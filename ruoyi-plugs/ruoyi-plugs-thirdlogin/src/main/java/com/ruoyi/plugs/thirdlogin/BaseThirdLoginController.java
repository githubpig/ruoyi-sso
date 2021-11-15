package com.ruoyi.plugs.thirdlogin;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.shiro.service.SysLoginService;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.plugs.thirdlogin.domain.ThirdOauth;
import com.ruoyi.plugs.thirdlogin.domain.ThirdPartyUser;
import com.ruoyi.plugs.thirdlogin.service.IThirdOauthService;
import com.ruoyi.plugs.thirdlogin.service.ThirdLoginService;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public abstract class BaseThirdLoginController extends BaseController {

    @Value("${third.login.to_bind_url}")
    public  String BIND_URL;
    @Value("${front.register.deptId}")
    public  String  registerUserDeptId;//前台用户注册赋予的默认部门id
    @Value("${front.register.roleId}")
    public  String registerUserRoleId;//前台用户注册赋予的默认角色id

    @Autowired
    public ISysUserService userService;
    @Autowired
    public SysPasswordService passwordService;
    @Autowired
    public IThirdOauthService thirdOauthService;
    @Autowired
    public ThirdLoginService thirdLoginService;
    @Autowired
    private SysLoginService sysLoginService;


    public abstract void bindSaveCallBack(SysUser user);

    @GetMapping(value = "/thirdLogin/bind")
    public String bind(HttpServletRequest request, ModelMap modelMap){
        String type=request.getParameter("type");
        String openid=request.getParameter("openid");
        String successUri=request.getParameter("successUri");
        String thirdAccount=request.getParameter("thirdAccount");
        if(StringUtils.isNotEmpty(type)&& StringUtils.isNotEmpty(openid)){
            modelMap.addAttribute("type",type);
            modelMap.addAttribute("openid",openid);
            modelMap.addAttribute("successUri",successUri);
            modelMap.addAttribute("thirdAccount",thirdAccount);
        }else{
            modelMap.addAttribute("errMsg","绑定账号参数异常!");
        }
        return "thirdlogin/bind";
    }
    @PostMapping(value = "/thirdLogin/bindSave")
    @ResponseBody
    public AjaxResult bindSave(HttpServletRequest request){
        String type=request.getParameter("type");
        String openid=request.getParameter("openid");
        String account=request.getParameter("account");
        String pwd=request.getParameter("pwd");
        String thirdAccount=request.getParameter("thirdAccount");
        if(StringUtils.isEmpty(account)){
            return AjaxResult.error("绑定账户名不能为空!");
        }
        if(StringUtils.isEmpty(pwd)){
            return AjaxResult.error("绑定账户密码不能为空!");
        }
        if(StringUtils.isEmpty(type)|| StringUtils.isEmpty(openid)){
            return AjaxResult.error("绑定账户参数异常!");
        }
        String access_token=String.valueOf(request.getSession().getAttribute("access_token"));
        SysUser user=sysLoginService.login(account,pwd,false);
        user.setThirdAccount(thirdAccount);//设置第三方登录账号
        ThirdPartyUser thirdPartyUser=new ThirdPartyUser();
        thirdPartyUser.setOpenid(openid);
        thirdPartyUser.setProvider(type);
        if(StringUtils.isNotEmpty(access_token)){
            thirdPartyUser.setAccessToken(access_token);
        }
        int n=insertThirdOauth(thirdPartyUser,user.getUserId().toString());
        if(n>0){
            bindSaveCallBack(user);
        }
        AjaxResult result=thirdLoginService.loginNoPwd(user.getUserId());
        if(result.isSuccess()){
            return AjaxResult.success("绑定成功,正在进行跳转!");
        }else{
            return AjaxResult.error("绑定成功,但登陆失败!");
        }

    }
    @RequestMapping(value = "/thirdLogin/unbind/{type}")
    @ResponseBody
    public AjaxResult unbind(HttpServletRequest request, HttpServletResponse response, @PathVariable("type")String type){
        SysUser user= ShiroUtils.getSysUser();
        if(user!=null){
            String userId=user.getUserId().toString();
            int n = thirdOauthService.deleteThirdOauthByUserIdAndLoginType(userId,type);
            if(n>0){
                return AjaxResult.success("解绑账号成功!");
            }
        }
        return error();
    }


    public int insertThirdOauth(ThirdPartyUser thirdPartyUser, String userId){
        int n=0;
        ThirdOauth thirdOauth=new ThirdOauth();
        thirdOauth.setOpenid(thirdPartyUser.getOpenid());
        thirdOauth.setLoginType(thirdPartyUser.getProvider());
        thirdOauth.setBindTime(DateUtils.getTime());
        thirdOauth.setUserId(userId);
        thirdOauth.setAccessToken(thirdPartyUser.getAccessToken());
        ThirdOauth form=new ThirdOauth();
        form.setOpenid(thirdPartyUser.getOpenid());
        form.setLoginType(thirdPartyUser.getProvider());
        form.setUserId(userId);
        List<ThirdOauth>  list = thirdOauthService.selectThirdOauthList(form);
        if(CollectionUtils.isEmpty(list)){
           n= thirdOauthService.insertThirdOauth(thirdOauth);
        }
        return n;
    }


}
