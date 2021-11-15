package com.ruoyi.plugs.thirdlogin.qq;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.IpUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.plugs.thirdlogin.domain.ThirdPartyUser;
import com.ruoyi.plugs.thirdlogin.domain.ThirdOauth;
import com.ruoyi.plugs.thirdlogin.service.IThirdOauthService;
import com.ruoyi.plugs.thirdlogin.service.ThirdLoginService;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class QQThirdLogin extends BaseController {

    @Value("${third.login.qq.authorize_url}")
    public  String  AUTHORIZE_URL;
    @Value("${third.login.qq.client_id}")
    public  String CLIENT_ID;
    @Value("${third.login.qq.client_secret}")
    public  String CLIENT_SECRET;
    @Value("${third.login.qq.redirect_uri}")
    public  String REDIRECT_URL;
    @Value("${third.login.qq.scope}")
    public  String SCOPE;
    @Value("${third.login.to_bind_url}")
    public  String BIND_URL;
    @Autowired
    ISysUserService userService;
    @Autowired
    private SysPasswordService passwordService;

    @Value("${front.register.deptId}")
    public  String  registerUserDeptId;//前台用户注册赋予的默认部门id
    @Value("${front.register.roleId}")
    public  String registerUserRoleId;//前台用户注册赋予的默认角色id

    @Autowired
    private IThirdOauthService thirdOauthService;
    @Autowired
    private ThirdLoginService thirdLoginService;

    @GetMapping(value = "/thirdLogin/qq")
    public void qq(HttpServletRequest request, HttpServletResponse response){
        String authorizeUrl = getRedirectUrl(request);
        try {
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<script>");
            out.println("window.open ('"+authorizeUrl+"','_top')");
            out.println("</script>");
            out.println("</html>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @RequestMapping(value = "/thirdLogin/unbind/qq")
    @ResponseBody
    public AjaxResult unbindQQ(HttpServletRequest request, HttpServletResponse response){
        SysUser user= ShiroUtils.getSysUser();
        if(user!=null){
            String userId=user.getUserId().toString();
            String type="qq";
            int n = thirdOauthService.deleteThirdOauthByUserIdAndLoginType(userId,type);
            if(n>0){
                return AjaxResult.success("解绑QQ账号成功!");
            }
        }
        return error();
    }
    @RequestMapping(value = "/thirdLogin/qq/callback")
    public String qqCallback(HttpServletRequest request) throws Exception {

        String host = request.getHeader("host");
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String redirect = request.getParameter("redirect");
        if(StringUtils.isEmpty(code)){
            return "redirect:/thirdLogin/qq";
        }
        //获取用户AccessToken
        AjaxResult ajaxResult = this.getQQTokenAndOpenid(code,host);
        if(ajaxResult.isSuccess()) {
            Map resMap = (Map) ajaxResult.get("data");
            String openid=String.valueOf(resMap.get("openid"));

            if(StringUtils.isEmpty(openid)){
                System.out.println("============QQ第三方登录未获取到openid!");
                return "redirect:/thirdLogin/qq";
            }
            //根据openid获取用户信息
            String access_token=String.valueOf(resMap.get("access_token"));
            ThirdPartyUser thirdPartyUser = this.getQQUserinfo(access_token,openid);
            if(thirdPartyUser!=null){
                //查询第三方登录
                ThirdOauth form=new ThirdOauth();
                form.setLoginType("qq");
                form.setOpenid(thirdPartyUser.getOpenid());
                List<ThirdOauth> list=thirdOauthService.selectThirdOauthList(form);
                if(CollectionUtils.isEmpty(list)){
                    //未绑定平台用户，去引导绑定用户
                    if(StringUtils.isEmpty(BIND_URL)){
                        //如果为空，自动绑定用户
                        SysUser user = registThirdUser(thirdPartyUser);
                        insertThirdOauth(thirdPartyUser,user.getUserId().toString());
                        list=thirdOauthService.selectThirdOauthList(form);
                    }else{
                        //跳转到绑定用户页面
                        return "redirect:"+BIND_URL;
                    }
                }
                //登录
                ThirdOauth thirdOauth=list.get(0);
                String userId=thirdOauth.getUserId();//本平台的用户ID
                AjaxResult ajaxResult1 = thirdLoginService.loginNoPwd(Long.valueOf(userId));
                if(ajaxResult1.isSuccess()){
                    //返回
                    if(StringUtils.isEmpty(redirect)){
                        return "redirect:/";
                    }else{
                        return "redirect:"+redirect;
                    }
                }else{
                    return "redirect:/thirdLogin/qq";
                }
            }
        }
        return "redirect:/thirdLogin/qq";
    }


    private SysUser registThirdUser(ThirdPartyUser thirdPartyUser){
        HttpServletRequest request= ServletUtils.getRequest();
        SysUser user=new SysUser();
        user.setLoginName(thirdPartyUser.getAccount());
        user.setUserName(thirdPartyUser.getUserName());
        user.setSalt(ShiroUtils.randomSalt());
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), "88888888", user.getSalt()));
        user.setLoginIp(IpUtils.getIpAddr(request));
        user.setAvatar(thirdPartyUser.getAvatarUrl());
        user.setSex(thirdPartyUser.getGender());
        user.setStatus("0");
        user.setDelFlag("0");
        Long[] roleIds={Long.valueOf(registerUserRoleId)};//前台用户注册赋予注册用户的角色和部门
        user.setRoleIds(roleIds);
        user.setDeptId(Long.valueOf(registerUserDeptId));
        int n = userService.insertUser(user);
        if(n>0){
            String invite_user_id=request.getParameter("invite_user_id");
            if(StringUtils.isNotEmpty(invite_user_id)){
                String ip= IpUtils.getIpAddr(request);
                userService.insertUserInvite(user.getLoginName(),invite_user_id,ip);//插入用户邀请记录表
            }
            return  user;
        }else{
            return  null;
        }
    }
    private void insertThirdOauth(ThirdPartyUser thirdPartyUser,String userId){
        ThirdOauth thirdOauth=new ThirdOauth();
        thirdOauth.setOpenid(thirdPartyUser.getOpenid());
        thirdOauth.setLoginType(thirdPartyUser.getProvider());
        thirdOauth.setBindTime(DateUtils.getTime());
        thirdOauth.setUserId(userId);
        ThirdOauth form=new ThirdOauth();
        form.setOpenid(thirdPartyUser.getOpenid());
        form.setLoginType(thirdPartyUser.getProvider());
        form.setUserId(userId);
        List<ThirdOauth>  list = thirdOauthService.selectThirdOauthList(form);
        if(CollectionUtils.isEmpty(list)){
            thirdOauthService.insertThirdOauth(thirdOauth);
        }

    }

    private ThirdPartyUser getQQUserinfo(String token, String openid){
        ThirdPartyUser user = new ThirdPartyUser();
        user.setProvider("qq");
        user.setOpenid(openid);
        user.setToken(token);
        String userInfoUrl="https://graph.qq.com/user/get_user_info";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token" , token));
        params.add(new BasicNameValuePair("oauth_consumer_key" , CLIENT_ID));
        params.add(new BasicNameValuePair("openid" , openid));
        String res=httpPost(userInfoUrl,params);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if (StringUtils.isNotEmpty(res)&&jsonObject.getIntValue("ret")==0) {
            user.setUserName(jsonObject.getString("nickname"));
            String img = jsonObject.getString("figureurl_qq_2");
            if (img == null || "".equals(img)) {
                img = jsonObject.getString("figureurl_qq_1");
            }
            user.setAvatarUrl(img);
            String sex = jsonObject.getString("gender");
            if ("女".equals(sex)) {
                user.setGender("0");
            } else {
                user.setGender("1");
            }
            return user;
        }
        return null;
    }
    private AjaxResult getQQTokenAndOpenid(String code, String host) throws Exception {

        Map<String, String> map = new HashMap<String, String>();

        String redirectUrl="http://"+host+REDIRECT_URL;
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type" , "authorization_code"));
        params.add(new BasicNameValuePair("client_id" , CLIENT_ID));
        params.add(new BasicNameValuePair("client_secret" , CLIENT_SECRET));
        params.add(new BasicNameValuePair("code" , code));
        params.add(new BasicNameValuePair("redirect_uri",redirectUrl));
        String url = "https://graph.qq.com/oauth2.0/token";
        String result = httpPost(url,params);
        if(StringUtils.isNotEmpty(result)&&result.contains("access_token")){
            JSONObject jsonObject=JSONObject.parseObject(result);
            String openIdUrl ="https://graph.qq.com/oauth2.0/me";
            openIdUrl = openIdUrl + "?access_token=" + jsonObject.getString("access_token");
            List<NameValuePair> openParams = new ArrayList<>();
            openParams.add(new BasicNameValuePair("access_token" , jsonObject.getString("access_token")));
            result=httpPost(openIdUrl,openParams);
            if(result.contains("(")&&result.contains(")")){
                int i = result.indexOf("(");
                int j = result.indexOf(")");
                result = result.substring(i + 1, j);
            }
            JSONObject openidObj = JSONObject.parseObject(result);
            map.put("access_token",jsonObject.getString("access_token"));
            map.put("openid",openidObj.getString("openid"));
            return AjaxResult.success(map);
        }
        return AjaxResult.error("qq获取token失败!");
    }
    private String httpPost(String url,List<NameValuePair> params) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        try {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent" , "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0");
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        response = httpClient.execute(httpPost);
        String result = EntityUtils.toString(response.getEntity());
        System.out.println(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
    private String getRedirectUrl(HttpServletRequest request) {
        String url = "";
        String host = request.getHeader("host");
        String redirectUrl="http://"+host+REDIRECT_URL;
        String state=request.getParameter("state");
        String state_str= StringUtils.isEmpty(state)?"":"&state="+state;
        url=AUTHORIZE_URL.replaceAll("\\[client_id\\]",CLIENT_ID).replaceAll("\\[redirect_uri\\]",redirectUrl).replaceAll("\\[scope\\]",SCOPE)+state_str;
        return url;
    }

}
