package com.ruoyi.plugs.thirdlogin.gitee;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.IpUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.plugs.thirdlogin.BaseThirdLoginController;
import com.ruoyi.plugs.thirdlogin.domain.ThirdOauth;
import com.ruoyi.plugs.thirdlogin.domain.ThirdPartyUser;
import com.ruoyi.system.domain.SysUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GiteeThirdLoginController extends BaseThirdLoginController {

    @Value("${third.login.gitee.authorize_url}")
    public  String  AUTHORIZE_URL;
    @Value("${third.login.gitee.client_id}")
    public  String CLIENT_ID;
    @Value("${third.login.gitee.client_secret}")
    public  String CLIENT_SECRET;
    @Value("${third.login.gitee.redirect_uri}")
    public  String REDIRECT_URL;
    @Value("${third.login.gitee.scope}")
    public  String SCOPE;

    @GetMapping(value = "/thirdLogin/gitee")
    public void gitee(HttpServletRequest request, HttpServletResponse response){
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

    @RequestMapping(value = "/thirdLogin/gitee/callback")
    public String giteeCallback(HttpServletRequest request) throws Exception {
        String host = request.getHeader("host");
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String redirect = request.getParameter("redirect");
        if(StringUtils.isEmpty(code)){
            return "redirect:/thirdLogin/gitee";
        }
        //获取用户AccessToken
        AjaxResult ajaxResult = this.getAccessToken(code,host);
        if(ajaxResult.isSuccess()){
            JSONObject jsonObject=(JSONObject)ajaxResult.get("data");
            String access_token = jsonObject.getString("access_token");
            if(StringUtils.isEmpty(access_token)){
                System.out.println("==>/thirdLogin/gitee/callback=======获得access_token为空!");
                return "redirect:/thirdLogin/gitee";
            }
            //获取用户
            ThirdPartyUser thirdPartyUser = this.geAccessTokentUserInfo(access_token);
            if(thirdPartyUser!=null){
                thirdPartyUser.setAccessToken(access_token);
                //查询第三方登录
                ThirdOauth form=new ThirdOauth();
                form.setLoginType("gitee");
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
                        String url=BIND_URL+"?type=gitee&openid="+thirdPartyUser.getOpenid()+"&successUri="+redirect+"&thirdAccount="+thirdPartyUser.getAccount();
                        request.getSession().setAttribute("access_token",access_token);
                        return "redirect:"+url;
                    }
                }
                //已经绑定过
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
                    return "redirect:/thirdLogin/gitee";
                }
            }
            System.out.println("==>/thirdLogin/gitee/callback=======获得ThirdPartyUser为空!");
            return "redirect:/thirdLogin/gitee";
        }
        return "redirect:/thirdLogin/gitee";
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
        user.setSex("1");
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

    /**
     * 根据用户授权code获取授权token
     */
    private AjaxResult getAccessToken(String code, String host) throws Exception {
        String redirectUrl="http://"+host+REDIRECT_URL;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type" , "authorization_code"));
        params.add(new BasicNameValuePair("code" , code));
        params.add(new BasicNameValuePair("client_id" , CLIENT_ID));
        params.add(new BasicNameValuePair("redirect_uri",redirectUrl));
        params.add(new BasicNameValuePair("client_secret" , CLIENT_SECRET));
        CloseableHttpResponse response = null;
        try {
            String url = "https://gitee.com/oauth/token";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("User-Agent" , "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0");
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println(result);
            JSONObject jsonObject=JSONObject.parseObject(result);
            return AjaxResult.success(jsonObject);
        } catch (Exception e) {
            return AjaxResult.error("gitee获取token失败!"+e.getMessage());
        } finally {
            response.close();
            httpClient.close();
        }
    }

    /**
     * 根据授权token获取对应的用户详细信息
     */
    private ThirdPartyUser geAccessTokentUserInfo(String accessToken) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String url = "https://gitee.com/api/v5/user?access_token=" + accessToken;
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent" , "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0");
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject=JSONObject.parseObject(result);
            String loginAccount=jsonObject.getString("login");
            if(StringUtils.isNotEmpty(loginAccount)){
                ThirdPartyUser thirdPartyUser=new ThirdPartyUser();
                thirdPartyUser.setAccount(loginAccount);
                thirdPartyUser.setAvatarUrl(jsonObject.getString("avatar_url").contains("no_portrait.png")?"":jsonObject.getString("avatar_url"));
                thirdPartyUser.setOpenid(jsonObject.getString("id"));
                thirdPartyUser.setProvider("gitee");
                thirdPartyUser.setUserName(jsonObject.getString("name"));
                thirdPartyUser.setToken(accessToken);
                return thirdPartyUser;
            }
        } catch (Exception e) {
        } finally {
            response.close();
            httpClient.close();
        }
        return null;
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

    @Override
    public void bindSaveCallBack(SysUser user) {

    }
}
