package com.ruoyi.forum.controller;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.ruoyi.cms.domain.ArticleTemplate;
import com.ruoyi.cms.service.IArticleTemplateService;
import com.ruoyi.cms.service.IEmailService;
import com.ruoyi.cms.service.SmsService;
import com.ruoyi.cms.util.CmsConstants;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.config.ServerConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.forum.domain.*;
import com.ruoyi.forum.service.*;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.shiro.token.MyUsernamePasswordToken;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 前台控制器
 */
@Controller
public class ForumController extends BaseController {

    @Autowired
    private ISysConfigService configService;
    @Autowired
    private SysPasswordService passwordService;
    @Autowired
    SmsService smsService;
    @Autowired
    ISysUserService userService;
    @Autowired
    ForumUserService forumUserService;
    @Autowired
    IEmailService emailService;
    @Autowired
    IForumQuestionService forumQuestionService;
    @Autowired
    IForumCategoryService forumCategoryService;
    @Autowired
    private IForumCommentService forumCommentService;
    @Autowired
    private IForumTagsService forumTagsService;
    @Autowired
    IForumOutService forumOutService;
    @Value("${front.register.deptId}")
    public  String  registerUserDeptId;//前台用户注册赋予的默认部门id
    @Value("${front.register.roleId}")
    public  String registerUserRoleId;//前台用户注册赋予的默认角色id

    private static Cache<String,Object> forumCache= CacheUtil.newTimedCache(1000*60*60*3);
    private static Cache<String,SysUser> userCache= CacheUtil.newTimedCache(1000*60*60*3);




    /***************************************登录，注册，发送验证码，找回密码相关****************  start  **************************/

    /**
     *  首页
     * @param mmap
     * @return
     */
    @GetMapping({"/forum","/forum/index"})
    public String index(ModelMap mmap)
    {
        mmap.addAttribute("pageUrl","/forum/index");
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null){
            mmap.addAttribute("user",user);
            int dayCount=0;
            ForumUserSign forumUserSign=forumUserService.selectUserSign(user.getUserId().intValue());
            if(forumUserSign==null){
                mmap.addAttribute("alreadySign",false);
                dayCount=0;
            }else{
                dayCount=forumUserSign.getCount();
                Date temp=forumUserSign.getSignTime();
                Date nowDate=new Date();
                if(isSameDay(temp,nowDate)){
                    mmap.addAttribute("alreadySign",true);
                }else{
                    Date yestaday=  DateUtils.addDays(nowDate,-1);
                    if(isSameDay(temp,yestaday)){
                        //上次签到是昨天
                        dayCount=forumUserSign.getCount();
                    }else{
                        //昨天没签到累计天数清零
                        dayCount=0;
                    }
                    mmap.addAttribute("alreadySign",false);
                }

            }
            mmap.addAttribute("dayCount",dayCount);
        }
        return "forum/index";

    }
    public static boolean isSameDay(Date date1, Date date2) {
        if(date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if(cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    /**
     * 前台登录页面
     * @param request
     * @param response
     * @return
     */
    @GetMapping({"/login","/forum/login"})
    public String login(HttpServletRequest request, HttpServletResponse response){
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null){
            return "redirect:/forum/index";//前台首页
        }
        // 如果是Ajax请求，返回Json字符串。
        if (ServletUtils.isAjaxRequest(request))
        {
            return ServletUtils.renderString(response, "{\"code\":\"1\",\"msg\":\"未登录或登录超时。请重新登录\"}");
        }

        return "forum/login";

    }

    /**
     * 前台登录页面iframe
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/login/frame")
    public String loginFrame(HttpServletRequest request, HttpServletResponse response){
        return "forum/account/login_frame";
    }

    /**
     *  短信登录发送手机验证码
     * @param request
     * @return
     */
    @GetMapping("/login/sms/send")
    @ResponseBody
    public AjaxResult frontSendSms(HttpServletRequest request, HttpSession session)
    {
        String phone =request.getParameter("phone");
        if(StringUtils.isNotEmpty(phone)){
            Map<String,String> params=new HashMap<>();
            TzCodeUtil util=new TzCodeUtil();
            String code =util.createCode();
            session.setAttribute(phone, code);
            params.put("#code#",code);
            AjaxResult result = smsService.sendByTemplate(CmsConstants.KEY_USER_SMS_LOGIN,phone,params);
            if(result.isSuccess()){
                return AjaxResult.success("验证码发送成功!");
            }else{
                return AjaxResult.error("验证码发送失败!");
            }
        }else{
            return AjaxResult.error("手机号不能为空!");
        }

    }

    /**
     * 前台用户登录账户检测
     * @param username
     * @return
     */
    @PostMapping("/checkAccount")
    @ResponseBody
    public AjaxResult checkAccount(String username)
    {
        boolean b=userService.checkAccountExist(username);
        if(b){
            return AjaxResult.success("账户已经注册!");
        }
        return AjaxResult.error("帐号未注册!");
    }


    /**
     * 前台用户登录手机号检测
     * @param phone
     * @return
     */
    @PostMapping("/checkPhone")
    @ResponseBody
    public AjaxResult checkPhone(String phone)
    {
        boolean b=userService.checkPhoneExist(phone);
        if(b){
            return AjaxResult.success("手机号已经注册!");
        }
        return AjaxResult.error("手机号未注册!");
    }
    /**
     * 检查手机号是否被绑定(phoneNumber,phoneFlag=1)
     * @param phone
     * @return
     */
    @PostMapping("/checkPhoneBind")
    @ResponseBody
    public AjaxResult checkPhoneBind(String phone)
    {
        SysUser user=userService.selectUserByPhoneNumber(phone);
        if(user!=null){
            return AjaxResult.success("手机号已被绑定!");
        }
        return AjaxResult.error("手机号未被绑定!");
    }
    /**
     * 检查邮箱是否被绑定
     * @param email
     * @return
     */
    @PostMapping("/checkEmailBind")
    @ResponseBody
    public AjaxResult checkEmailBind(String email)
    {
        SysUser user=userService.selectUserByEmail(email);
        if(user!=null){
            return AjaxResult.success("邮箱已被绑定!");
        }
        return AjaxResult.error("邮箱未被绑定!");
    }
    /**
     * 是否已经绑定手机(很多操作要求强制必须先绑定手机，所以有这个检查)
     * @return
     */
    @PostMapping("/nav/isBindPhone")
    @ResponseBody
    public AjaxResult isBindPhone()
    {
        SysUser user= ShiroUtils.getSysUser();
        boolean b=userService.isBindPhone(user);
        if(b){
            return AjaxResult.success("已经绑定手机号!");
        }
        return AjaxResult.error("未绑定手机号!");
    }
    @GetMapping("/nav/getUserInfo")
    @ResponseBody
    public AjaxResult getUserInfo(ModelMap mmap)
    {
        SysUser user= ShiroUtils.getSysUser();
        if(user!=null){
            Map data=new HashMap();
            data.put("pageUrl","/forum/u/"+user.getUserId());
            data.put("avatar",getAvatarPath(user));
            data.put("username",user.getUserName());
            data.put("memberType","0");//个人用户
            data.put("uid",user.getUserId().toString());
            return AjaxResult.success("获取用户信息成功!",data);
        }
        return AjaxResult.error("获取用户信息失败!");
    }
    public String getAvatarPath(SysUser user){
        if(StringUtils.isEmpty(user.getAvatar())){
            if("1".equals(user.getSex())){
                return "/forum/images/boy.png";
            }else{
                return "/forum/images/girl.png";
            }
        }
        return user.getAvatar();
    }
    /**
     * 前台用户手机短信登录
     * @param phone
     * @param code
     * @param rememberMe
     * @return
     */
    @PostMapping("/login/sms")
    @ResponseBody
    public AjaxResult frontLoginSms(String phone, String code, Boolean rememberMe, HttpSession session)
    {
        // 根据phone从session中取出发送的短信验证码，并与用户输入的验证码比较
        String sessionCode = (String) session.getAttribute(phone);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("短信登录请先发送验证码!");
        }
        if(!sessionCode.equals(code)){
            return AjaxResult.error("验证码错误!");
        }
        MyUsernamePasswordToken token = new MyUsernamePasswordToken(phone);

        Subject subject = SecurityUtils.getSubject();
        try
        {
            subject.login(token);
            //ServletUtils.setSmsLoginCookieFront(phone, rememberMe);

            SysUser user = userService.selectUserByLoginName(phone);

            if (user == null) {
                user=userService.selectUserByPhoneNumber(phone);
                if (user == null) {
                    throw new BusinessException("手机号未注册!");
                }
            }
            ServletUtils.setCookieUid(user.getUserId().toString());
            return success();
        }
        catch (AuthenticationException e)
        {
            String msg = "短信登录失败";
            if (StringUtils.isNotEmpty(e.getMessage()))
            {
                msg = e.getMessage();
            }
            return error(msg);
        }
    }
    /**
     * 前台用户登录
     * @param username
     * @param password
     * @param rememberMe
     * @return
     */
    @PostMapping("/login/front")
    @ResponseBody
    public AjaxResult frontLogin(String username, String password, Boolean rememberMe)
    {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        Subject subject = SecurityUtils.getSubject();
        try
        {
            subject.login(token);
            SysUser user= ShiroUtils.getSysUser();
            ServletUtils.setCookieUid(user.getUserId().toString());
            Map<String,Object> returnMap=new HashMap<String,Object>();

            if(user!=null){
                user=userService.selectUserById(user.getUserId());
                Date lastDate =   user.getLastLoginTime();
                if(lastDate==null){
                    returnMap.put("todayLogin",true);//今天是第一次登录
                    return AjaxResult.success("登录成功!",returnMap);
                }
                if(!isToday(lastDate)){
                    //不是今天的登录时间
                    returnMap.put("todayLogin",true);//今天是第一次登录
                    return AjaxResult.success("登录成功!",returnMap);
                }else{
                    returnMap.put("todayLogin",false);//今天是第n次登录
                }
            }


            return AjaxResult.success("登录成功!",returnMap);
        }
        catch (Exception e)
        {
            String msg = "用户或密码错误";
            if (StringUtils.isNotEmpty(e.getMessage()))
            {
                msg = e.getMessage();
            }
            return error(msg);
        }
    }


    /**
     * 判断时间是不是今天
     * @param date
     * @return    是返回true，不是返回false
     */
    private static boolean isToday(Date date) {
        //当前时间
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        //获取今天的日期
        String nowDay = sf.format(now);
        //对比的时间
        String day = sf.format(date);
        return day.equals(nowDay);
    }
    /**
     * 注册跳转
     * @param modelMap
     * @param request
     * @return
     */
    @GetMapping("/register")
    public String register(ModelMap modelMap, HttpServletRequest request)
    {
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null){
            modelMap.addAttribute("user",user);
        }
        String u=request.getParameter("u");//链接携带的推荐人用户
        if(StringUtils.isNotEmpty(u)){
            modelMap.addAttribute("invite_user_id",u);
        }
        return "forum/account/register";
    }
    /**
     * 前台注册用户
     * @param request
     * @param session
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    @Transactional
    public AjaxResult registUser(HttpServletRequest request, HttpSession session){
        String email=request.getParameter("email");
        String code=request.getParameter("code");
        String password=request.getParameter("password");

        if(StringUtils.isEmpty(email)){
            return AjaxResult.error("邮箱不能为空!");
        }
        if(StringUtils.isEmpty(password)){
            return AjaxResult.error("密码不能为空!");
        }

        String sessionCode=(String)session.getAttribute(email);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("请先发送验证码!");
        }
        if(!code.equals(sessionCode)){
            return AjaxResult.error("验证码不正确!");
        }

        SysUser user=new SysUser();
        user.setLoginName(email);
        user.setUserName(email);
        user.setEmail(email);
        //user.setPhonenumber(phone);
        user.setEmailFlag(1);
        user.setSalt(ShiroUtils.randomSalt());
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), password, user.getSalt()));
        user.setLoginIp(IpUtils.getIpAddr(request));
        user.setAvatar(getAvatarPath(user));
        user.setSex("1");
        user.setStatus("0");
        user.setDelFlag("0");
        Long[] roleIds={Long.valueOf(registerUserRoleId)};//前台用户注册赋予注册用户的角色和部门
        user.setRoleIds(roleIds);
        user.setDeptId(Long.valueOf(registerUserDeptId));
        int n = userService.insertUser(user);
        if(n>0){
            session.removeAttribute(email);
            String invite_user_id=request.getParameter("invite_user_id");
            if(StringUtils.isNotEmpty(invite_user_id)){
                String ip= IpUtils.getIpAddr(request);
                userService.insertUserInvite(user.getLoginName(),invite_user_id,ip);//插入用户邀请记录表
            }
            return  AjaxResult.success("注册成功!");
        }else{
            return  AjaxResult.error("注册失败!");
        }
    }


    /**
     * 用户注册发送手机验证码
     * @param request
     * @param session
     * @return
     */
    @PostMapping("/register/sendEmail")
    @ResponseBody
    public AjaxResult sendEmail(HttpServletRequest request, HttpSession session){

        String email=request.getParameter("email");
        Map<String,String> params=new HashMap<>();
        TzCodeUtil util=new TzCodeUtil();
        String code =util.createCode();
        session.setAttribute(email, code);
        params.put("#code#",code);
        String[] toEmail={email};
        boolean flag = emailService.sendEmailByTemplate(CmsConstants.KEY_USER_REGISTER,toEmail,params);

        if(flag){
            return AjaxResult.success("验证码发送成功!");
        }else{
            return AjaxResult.error("验证码发送失败!");
        }
    }

    /**
     * 用户注册发送手机验证码
     * @param request
     * @param session
     * @return
     */
    @PostMapping("/register/sendSms")
    @ResponseBody
    public AjaxResult sendSms(HttpServletRequest request, HttpSession session){

        String phone=request.getParameter("phone");
        Map<String,String> params=new HashMap<>();
        TzCodeUtil util=new TzCodeUtil();
        String code =util.createCode();
        session.setAttribute(phone, code);
        params.put("#code#",code);
        AjaxResult result = smsService.sendByTemplate(CmsConstants.KEY_USER_REGISTER,phone,params);
        if(result.isSuccess()){
            return AjaxResult.success("验证码发送成功!");
        }else{
            return AjaxResult.error("验证码发送失败!");
        }
    }

    /**
     * 忘记密码(重置密码)跳转
     * @param modelMap
     * @return
     */
    @GetMapping("/resetpwd")
    public String resetpwd(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null){
            modelMap.addAttribute("user",user);
        }
        return "forum/account/resetpwd";
    }

    /**
     * 忘记密码(重置密码)发送验证码
     * @return
     */
    @PostMapping("/resetpwd/sendCode")
    @ResponseBody
    public AjaxResult resetpwdSendCode(HttpServletRequest request, HttpSession session)
    {
        String account=request.getParameter("account");
        String type=request.getParameter("type");//验证码类型:email和sms
        if(StringUtils.isEmpty(account)){
            return AjaxResult.error("手机号/邮箱不能为空!");
        }
        if(StringUtils.isEmpty(type)){
            return AjaxResult.error("参数type不能为空!");
        }

        TzCodeUtil util=new TzCodeUtil();
        String code =util.createCode();
        session.setAttribute(account, code);
        Map<String,String> params=new HashMap<>();
        params.put("#code#",code);

        if("email".equals(type)){
            String[] toEmails={account};
            boolean flag=emailService.sendEmailByTemplate(CmsConstants.KEY_USER_RESET_PWD,toEmails,params);
            if(flag){
                return AjaxResult.success("验证码发送成功!");
            }
        }
        if("sms".equals(type)){
            AjaxResult result = smsService.sendByTemplate(CmsConstants.KEY_USER_RESET_PWD_SMS,account,params);
            if(result.isSuccess()){
                return AjaxResult.success("验证码发送成功!");
            }
        }
        return AjaxResult.error("验证码发送失败!");
    }

    /**
     * 找回密码
     * @param request
     * @param session
     * @return
     */
    @PostMapping("/resetpwd")
    @ResponseBody
    public AjaxResult resetpwdPost(HttpServletRequest request, HttpSession session){
        String account=request.getParameter("account");
        String code=request.getParameter("code");//验证码
        String password=request.getParameter("password");
        if(StringUtils.isEmpty(account)){
            return AjaxResult.error("手机号/邮箱不能为空!");
        }
        if(StringUtils.isEmpty(password)){
            return AjaxResult.error("密码不能为空!");
        }
        String sessionCode=(String)session.getAttribute(account);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("请先发送验证码!");
        }
        if(!code.equals(sessionCode)){
            return AjaxResult.error("验证码不正确!");
        }
        SysUser user=userService.selectUserByLoginName(account);
        if(user==null){
            user=userService.selectUserByPhoneNumber(account);
        }
        if(user==null){
            return AjaxResult.error("手机号/邮箱未注册!");
        }
        user.setSalt(ShiroUtils.randomSalt());
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), password, user.getSalt()));
        int n =userService.resetUserPwd(user);
        if(n>0){
            session.removeAttribute(account);
            return AjaxResult.success("密码重置成功!");
        }else{
            return AjaxResult.error("密码重置失败!");
        }
    }

    /**
     * 修改手机跳转
     * @param modelMap
     * @return
     */
    @GetMapping("/modifyphone_view")
    public String modifyphone_view(HttpServletRequest request, ModelMap modelMap)
    {

        String type=request.getParameter("type");

        SysUser user = ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        if(user!=null){
            modelMap.addAttribute("user",user);
            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());
            modelMap.addAttribute("phone",user.getPhonenumber());
        }
        if("2".equals(type)){
            modelMap.addAttribute("title","修改绑定手机");
            modelMap.addAttribute("type","2");
            modelMap.addAttribute("oldephone",user.getPhonenumber());
        }else{
            modelMap.addAttribute("title","绑定手机");
            modelMap.addAttribute("type","1");
        }
        return "forum/account/modifyphone_view";
    }
    /**
     * 修改手机-发送验证码
     * @return
     */
    @PostMapping("/modifyPhone/sendCode")
    @ResponseBody
    public AjaxResult modifyPhoneSendCode(HttpServletRequest request, HttpSession session)
    {
        String phone=request.getParameter("phone");
        String type=request.getParameter("type");
        if(StringUtils.isEmpty(phone)){
            return AjaxResult.error("手机号不能为空!");
        }
        TzCodeUtil util=new TzCodeUtil();
        String code =util.createCode();
        session.setAttribute(phone, code);
        Map<String,String> params=new HashMap<>();
        params.put("#code#",code);
        AjaxResult result;
        if("2".equals(type)){
            result = smsService.sendByTemplate(CmsConstants.KEY_USER_MODIFY_PHONE,phone,params);
        }else{
            result = smsService.sendByTemplate(CmsConstants.KEY_USER_BIND_PHONE,phone,params);
        }
        if(result.isSuccess()){
            return AjaxResult.success("验证码发送成功!");
        }
        return AjaxResult.error("验证码发送失败!");
    }
    @PostMapping("/modifyPhone")
    @ResponseBody
    public AjaxResult modifyPhone(HttpServletRequest request, HttpSession session){
        String phone=request.getParameter("phone");
        String code=request.getParameter("code");//验证码
        String type=request.getParameter("type");

        if(StringUtils.isEmpty(phone)){
            return AjaxResult.error("2".equals(type)?"新手机号不能为空!":"手机号不能为空!");
        }

        String sessionCode=(String)session.getAttribute(phone);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("请先发送验证码!");
        }
        if(!code.equals(sessionCode)){
            return AjaxResult.error("验证码不正确!");
        }
        SysUser user= ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());

        user.setPhonenumber(phone);
        user.setPhoneFlag(1);
        int n =userService.updateUserInfo(user);
        if(n>0){
            session.removeAttribute(phone);
            return AjaxResult.success("2".equals(type)?"修改绑定手机成功!":"绑定手机成功!");
        }else{
            return AjaxResult.error("2".equals(type)?"修改绑定手机失败!":"绑定手机失败!");
        }
    }


    /**
     * 修改手机跳转
     * @param modelMap
     * @return
     */
    @GetMapping("/modifypwd_view")
    public String modifypwd_view(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null){
            modelMap.addAttribute("user",user);
            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());
            modelMap.addAttribute("phone",user.getPhonenumber());
        }

        return "forum/account/modifypwd_view";
    }
    /**
     * 修改密码
     * @param request
     * @param session
     * @return
     */
    @PostMapping("/modifyPwd")
    @ResponseBody
    public AjaxResult modifyPwd(HttpServletRequest request, HttpSession session){
        String oldpwd=request.getParameter("oldpwd");
        String password=request.getParameter("password");
        if(StringUtils.isEmpty(oldpwd)){
            return AjaxResult.error("旧密码不能为空!");
        }
        if(StringUtils.isEmpty(password)){
            return AjaxResult.error("新密码不能为空!");
        }

        SysUser user = ShiroUtils.getSysUser();
        if (StringUtils.isNotEmpty(password) && passwordService.matches(user, oldpwd))
        {
            user.setSalt(ShiroUtils.randomSalt());
            user.setPassword(passwordService.encryptPassword(user.getLoginName(), password, user.getSalt()));
            if (userService.resetUserPwd(user) > 0)
            {
                ShiroUtils.setSysUser(userService.selectUserById(user.getUserId()));
                return AjaxResult.success("密码修改成功!");
            }
            return AjaxResult.error("修改密码失败!");
        }
        else
        {
            return error("修改密码失败，旧密码错误");
        }
    }
    /**
     * 身份证校验
     * @param modelMap
     * @return
     */
    @GetMapping("/idcard_view")
    public String idcard_view(HttpServletRequest request, ModelMap modelMap)
    {

        SysUser user = ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        if(user!=null){
            modelMap.addAttribute("user",user);
            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());
            modelMap.addAttribute("phone",user.getPhonenumber());
        }
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        modelMap.addAttribute("name",forumUser.getReal_name());
        modelMap.addAttribute("idCard",forumUser.getId_card());
        return "forum/account/idcard_view";
    }

    /**
     * 绑定或修改邮箱跳转
     * @param modelMap
     * @return
     */
    @GetMapping("/modifyemail_view")
    public String modifyemail_view(HttpServletRequest request, ModelMap modelMap)
    {
        String type=request.getParameter("type");

        SysUser user = ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        if(user!=null){
            modelMap.addAttribute("user",user);
            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());
            modelMap.addAttribute("phone",user.getPhonenumber());
        }
        if("2".equals(type)){
            modelMap.addAttribute("title","修改邮箱账号");
            modelMap.addAttribute("type","2");
            modelMap.addAttribute("oldemail",user.getEmail());
        }else{
            modelMap.addAttribute("title","绑定邮箱账号");
            modelMap.addAttribute("type","1");
        }
        return "forum/account/modifyemail_view";
    }
    /**
     * 修改邮箱-发送验证码
     * @return
     */
    @PostMapping("/modifyEmail/sendCode")
    @ResponseBody
    public AjaxResult modifyEmailSendCode(HttpServletRequest request, HttpSession session)
    {
        String email=request.getParameter("email");
        String type=request.getParameter("type");//1:绑定邮箱  2：修改邮箱
        if(StringUtils.isEmpty(email)){
            return AjaxResult.error("邮箱不能为空!");
        }

        TzCodeUtil util=new TzCodeUtil();
        String code =util.createCode();
        session.setAttribute(email, code);
        Map<String,String> params=new HashMap<>();
        params.put("#code#",code);
        boolean flag=false;
        String[] toEmails={email};
        if("2".equals(type)){
            flag=emailService.sendEmailByTemplate(CmsConstants.KEY_USER_MODIFY_EMAIL,toEmails,params);
        }else{
            flag=emailService.sendEmailByTemplate(CmsConstants.KEY_USER_BIND_EMAIL,toEmails,params);
        }
        if(flag){
            return AjaxResult.success("验证码发送成功!");
        }
        return AjaxResult.error("验证码发送失败!");
    }
    @PostMapping("/modifyEmail")
    @ResponseBody
    public AjaxResult modifyEmail(HttpServletRequest request, HttpSession session){
        String email=request.getParameter("email");
        String code=request.getParameter("code");//验证码
        String type=request.getParameter("type");
        if(StringUtils.isEmpty(email)){
            return AjaxResult.error("2".equals(type)?"新邮箱不能为空!":"邮箱不能为空!");
        }

        String sessionCode=(String)session.getAttribute(email);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("请先发送验证码!");
        }
        if(!code.equals(sessionCode)){
            return AjaxResult.error("验证码不正确!");
        }
        SysUser user= ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());

        user.setEmail(email);
        user.setEmailFlag(1);
        int n =userService.updateUserInfo(user);
        if(n>0){
            session.removeAttribute(email);
            return AjaxResult.success("2".equals(type)?"邮箱修改成功!":"邮箱绑定成功!");
        }else{
            return AjaxResult.error("2".equals(type)?"邮箱修改失败!":"邮箱绑定失败!");
        }
    }


    /**
     * 解绑手机号跳转
     * @param modelMap
     * @return
     */
    @GetMapping("/unbindphone_view")
    public String unbindphone_view(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        if(user!=null){
            modelMap.addAttribute("user",user);
            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());
            modelMap.addAttribute("phone",user.getPhonenumber());
        }

        return "forum/account/unbindphone_view";

    }
    /**
     * 解绑手机-发送验证码
     * @return
     */
    @PostMapping("/unbindPhone/sendCode")
    @ResponseBody
    public AjaxResult unbindPhoneSendCode(HttpServletRequest request, HttpSession session)
    {
        SysUser user= ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        String phone=user.getPhonenumber();
        TzCodeUtil util=new TzCodeUtil();
        String code =util.createCode();
        session.setAttribute(phone, code);
        Map<String,String> params=new HashMap<>();
        params.put("#code#",code);
        AjaxResult result=smsService.sendByTemplate(CmsConstants.KEY_USER_UNBIND_PHONE,phone,params);

        if(result.isSuccess()){
            return AjaxResult.success("验证码发送成功!");
        }
        return AjaxResult.error("验证码发送失败!");
    }

    @PostMapping("/unbindPhone")
    @ResponseBody
    public AjaxResult unbindPhone(HttpServletRequest request, HttpSession session){

        String code=request.getParameter("code");//验证码

        SysUser user= ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        String phone=user.getPhonenumber();

        String sessionCode=(String)session.getAttribute(phone);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("请先发送验证码!");
        }
        if(!code.equals(sessionCode)){
            return AjaxResult.error("验证码不正确!");
        }

        user.setPhonenumber("");
        user.setPhoneFlag(0);
        int n =userService.updateUserInfo(user);
        if(n>0){
            session.removeAttribute(phone);
            return AjaxResult.success("解绑手机成功!");
        }else{
            return AjaxResult.error("解绑手机失败!");
        }
    }

    /******************前台个人资料设置  start*******************************/
    /**
     * 个人设置跳转
     * @param modelMap
     * @return
     */
    @GetMapping("/forum/u/{userId}")
    public String userCenter(@PathVariable("userId")String userId, ModelMap modelMap)
    {
        SysUser  loginUser = ShiroUtils.getSysUser();
        modelMap.addAttribute("loginUser",loginUser);

        SysUser  user=userService.selectUserById(Long.valueOf(userId));
        if(user==null){
            return "forum/pages/404";
        }

        if(loginUser==null){
            modelMap.addAttribute("otherUser","1");
        }else{
            if(loginUser.getUserId().equals(user.getUserId())){
                modelMap.addAttribute("otherUser","0");//登录用户和访问用户同一个人
            }else{
                modelMap.addAttribute("otherUser","1");
            }

            //查询用户关注状态
            Integer focusCount=forumUserService.selectFocusFlag(loginUser.getUserId().toString(),userId);
            if(focusCount>0){
                //已经关注
                modelMap.addAttribute("focusFlag",true);
            }else{
                modelMap.addAttribute("focusFlag",false);
            }
            //添加访客记录
            if(!userId.equals(loginUser.getUserId().toString())){
                forumUserService.removeVisitUser(userId,loginUser.getUserId().toString());
                forumUserService.addVisitUser(userId,loginUser.getUserId().toString());
            }

        }
            //访客列表
            /*List<ForumUser> visitUsers = forumUserService.selectVisitUsers(userId);
            if(CollectionUtils.isNotEmpty(visitUsers)){
                modelMap.addAttribute("visitUsers",visitUsers);
                visitUsers.forEach(a->{
                    a.setTimeDesc(getTimePassedLong(a.getVisitTime()));
                });
            }*/

            //查询帖子数量
            int n=forumQuestionService.selectQuestionCount(userId);
            modelMap.addAttribute("questionCount",n);
            //查询粉丝数量
            int count=forumUserService.selectFansCount(userId);
            modelMap.addAttribute("fansCount",count);

            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());

            modelMap.addAttribute("email",user.getEmail());
            modelMap.addAttribute("emailFlag",user.getEmailFlag());
            modelMap.addAttribute("phone",user.getPhonenumber());
            modelMap.addAttribute("phoneFlag",user.getPhoneFlag());
            modelMap.addAttribute("score",user.getScore());
            modelMap.addAttribute("avatar",getAvatarPath(user));
            modelMap.addAttribute("createTime", user.getCreateTime());
            modelMap.addAttribute("sex",user.getSex());
            modelMap.addAttribute("description",user.getDescription());
            ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
            if(forumUser!=null){
                modelMap.addAttribute("user",forumUser);
                //学校highSchool
                String highSchool=forumUser.getHighSchool();
                if(StringUtils.isNotEmpty(highSchool) && highSchool.contains("#")){
                    String[] highSchoolArr=highSchool.split("#");
                    modelMap.addAttribute("highSchool",highSchoolArr[0]);
                    modelMap.addAttribute("highSchoolId",highSchoolArr[1]);
                }
                String homeCity=forumUser.getHomeCity();
                String nowCity=forumUser.getNowCity();
                if(StringUtils.isNotEmpty(homeCity)){
                    String[] homeCityArr=homeCity.split("#");
                    if(homeCityArr!=null&&homeCityArr.length>=3){
                        modelMap.addAttribute("homeProvinceValue",homeCityArr[0]);
                        modelMap.addAttribute("homeCityValue",homeCityArr[1]);
                        modelMap.addAttribute("homeDistrictValue",homeCityArr[2]);
                    }
                }
                if(StringUtils.isNotEmpty(homeCity)){
                    String[] nowCityArr=nowCity.split("#");
                    if(nowCityArr!=null&&nowCityArr.length>=3){
                        modelMap.addAttribute("nowProvinceValue",nowCityArr[0]);
                        modelMap.addAttribute("nowCityValue",nowCityArr[1]);
                        modelMap.addAttribute("nowDistrictValue",nowCityArr[2]);
                    }
                }
                //标签
                String str=forumUser.getLabel();
                if(StringUtils.isNotEmpty(str)){
                    String[] arr=str.split("#");
                    List<String> tags= Lists.newArrayList();
                    for(String s:arr){
                        if(StringUtils.isNotEmpty(s)){
                            tags.add(s);
                        }
                    }
                    modelMap.addAttribute("tags",tags);
                }
                //访问次数+1
                Object temp = forumCache.get("user_look_"+userId);
                boolean visitFlag=false;
                if(temp!=null){
                    visitFlag=(Boolean) temp;
                }
                if(!visitFlag){
                    forumUser.setLook(forumUser.getLook()+1);
                    forumUserService.updateUserInfo(forumUser);
                    forumCache.put("user_look_"+userId,true);
                }
                modelMap.addAttribute("look",forumUser.getLook());

            }else{
                forumUserService.insertNewForumUser(user.getUserId().intValue());
            }


        return "forum/account/userCenter";

    }
    @PostMapping("/forum/initVisitUserList/{userId}")
    @ResponseBody
    public AjaxResult initVisitUserList(@PathVariable("userId")String userId){
        //访客列表
        List<ForumUser> visitUsers = forumUserService.selectVisitUsers(userId);
        if(CollectionUtils.isNotEmpty(visitUsers)){
            visitUsers.forEach(a->{
                a.setTimeDesc(getTimePassedLong(a.getVisitTime()));
            });
        }
        return AjaxResult.success(visitUsers);
    }
    /**
     * 个人设置跳转
     * @param modelMap
     * @return
     */
    @GetMapping("/setting")
    public String setting(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        if(user!=null){

            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());

            modelMap.addAttribute("email",user.getEmail());
            modelMap.addAttribute("emailFlag",user.getEmailFlag());
            modelMap.addAttribute("phone",user.getPhonenumber());
            modelMap.addAttribute("phoneFlag",user.getPhoneFlag());
            modelMap.addAttribute("score",user.getScore());
            modelMap.addAttribute("avatar",getAvatarPath(user));
            modelMap.addAttribute("createTime", user.getCreateTime());
            modelMap.addAttribute("sex",user.getSex());
            modelMap.addAttribute("description",user.getDescription());
            ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
            if(forumUser!=null){
                modelMap.addAttribute("user",forumUser);
                //学校highSchool
                String highSchool=forumUser.getHighSchool();
                if(StringUtils.isNotEmpty(highSchool) && highSchool.contains("#")){
                    String[] highSchoolArr=highSchool.split("#");
                    modelMap.addAttribute("highSchool",highSchoolArr[0]);
                    modelMap.addAttribute("highSchoolId",highSchoolArr[1]);
                }
                String homeCity=forumUser.getHomeCity();
                String nowCity=forumUser.getNowCity();
                if(StringUtils.isNotEmpty(homeCity)){
                    String[] homeCityArr=homeCity.split("#");
                    if(homeCityArr!=null&&homeCityArr.length>=3){
                        modelMap.addAttribute("homeProvinceValue",homeCityArr[0]);
                        modelMap.addAttribute("homeCityValue",homeCityArr[1]);
                        modelMap.addAttribute("homeDistrictValue",homeCityArr[2]);
                    }
                }
                if(StringUtils.isNotEmpty(homeCity)){
                    String[] nowCityArr=nowCity.split("#");
                    if(nowCityArr!=null&&nowCityArr.length>=3){
                        modelMap.addAttribute("nowProvinceValue",nowCityArr[0]);
                        modelMap.addAttribute("nowCityValue",nowCityArr[1]);
                        modelMap.addAttribute("nowDistrictValue",nowCityArr[2]);
                    }
                }
                //标签
                String str=forumUser.getLabel();
                if(StringUtils.isNotEmpty(str)){
                    String[] arr=str.split("#");
                    List<String> tags= Lists.newArrayList();
                    for(String s:arr){
                        if(StringUtils.isNotEmpty(s)){
                            tags.add(s);
                        }
                    }
                    modelMap.addAttribute("tags",tags);
                }

            }else{
                forumUserService.insertNewForumUser(user.getUserId().intValue());
            }
        }

        return "forum/account/setting";

    }
    @PostMapping("/setting/avatar/upload")
    @ResponseBody
    public AjaxResult updateAvatar(@RequestParam("file") MultipartFile file)
    {
        SysUser currentUser = ShiroUtils.getSysUser();
        try
        {
            if (!file.isEmpty())
            {
                String avatar = FileUploadUtils.upload(Global.getAvatarPath(), file);

                return AjaxResult.success("头像上成功!",avatar);
            }
            return error();
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }
    @PostMapping("/setting/avatar")
    @ResponseBody
    public AjaxResult avatar(String avatarPath)
    {
        SysUser currentUser = ShiroUtils.getSysUser();
        try
        {
            if (StringUtils.isNotEmpty(avatarPath))
            {

                currentUser.setAvatar(avatarPath);
                if (userService.updateUserInfo(currentUser) > 0)
                {
                    ShiroUtils.setSysUser(userService.selectUserById(currentUser.getUserId()));
                    return success("头像保存成功!");
                }
            }
            return error("头像保存成功失败!参数不能为空!");
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }
    @PostMapping("/setting/basic")
    @ResponseBody
    public AjaxResult basic(HttpServletRequest request)
    {
        String sex=request.getParameter("sex");
        String homeCity=request.getParameter("homeCity");
        String nowCity=request.getParameter("nowCity");
        String job=request.getParameter("job");
        String description=request.getParameter("description");
        SysUser user= ShiroUtils.getSysUser();
        user.setSex(sex);
        user.setDescription(description);
        userService.updateUserInfo(user);
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        forumUser.setJob(job);
        forumUser.setHomeCity(homeCity);
        forumUser.setNowCity(nowCity);
        forumUserService.updateUserInfo(forumUser);
        return AjaxResult.success("保存成功!");
    }
    @PostMapping("/setting/detail")
    @ResponseBody
    public AjaxResult detail(HttpServletRequest request)
    {
        String school=request.getParameter("school");
        String height=request.getParameter("height");
        String weight=request.getParameter("weight");
        String education=request.getParameter("education");
        String minzhu=request.getParameter("minzhu");
        String xuexing=request.getParameter("xuexing");
        String marriage=request.getParameter("marriage");
        String salary=request.getParameter("salary");
        String children=request.getParameter("children");
        SysUser user= ShiroUtils.getSysUser();
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        forumUser.setHighSchool(school);
        if(StringUtils.isNotEmpty(height)){
            forumUser.setHeight(Integer.valueOf(height));
        }
        if(StringUtils.isNotEmpty(weight)){
            forumUser.setWeight(Integer.valueOf(weight));
        }
        if(StringUtils.isNotEmpty(xuexing)){
            forumUser.setXuexing(Integer.valueOf(xuexing));
        }

        forumUser.setEducation(education);
        if(StringUtils.isNotEmpty(minzhu)){
            forumUser.setMinzhu(Integer.valueOf(minzhu));
        }


        if(StringUtils.isNotEmpty(marriage)){
            forumUser.setMarriage(Integer.valueOf(marriage));
        }

        if(StringUtils.isNotEmpty(salary)){
            forumUser.setSalary(Integer.valueOf(salary));
        }

        forumUser.setChildren(Integer.valueOf(children));
        forumUserService.updateUserInfo(forumUser);
        return AjaxResult.success("保存成功!");
    }
    @PostMapping("/setting/contact")
    @ResponseBody
    public AjaxResult contact(HttpServletRequest request)
    {
        String qq=request.getParameter("qq");
        String qqPrivate=request.getParameter("qqPrivate");
        String wechat=request.getParameter("wechat");
        String wechatPrivate=request.getParameter("wechatPrivate");
        SysUser user= ShiroUtils.getSysUser();
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        forumUser.setQq(qq);
        forumUser.setQqPrivate(Integer.valueOf(qqPrivate));
        forumUser.setWx(wechat);
        forumUser.setWxPrivate(Integer.valueOf(wechatPrivate));
        forumUserService.updateUserInfo(forumUser);
        return AjaxResult.success("保存成功!");
    }

    @PostMapping("/setting/addTag")
    @ResponseBody
    public AjaxResult addTag(HttpServletRequest request)
    {
        String tag=request.getParameter("tag");
        SysUser user= ShiroUtils.getSysUser();
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        String str=forumUser.getLabel();
        if(StringUtils.isEmpty(str)){
            str+=tag;
        }else{
            str+=(str.endsWith("#")?"":"#")+tag;
        }

        forumUser.setLabel(str);
        forumUserService.updateUserInfo(forumUser);
        return AjaxResult.success("添加标签成功!");
    }

    @PostMapping("/setting/delTag")
    @ResponseBody
    public AjaxResult delTag(HttpServletRequest request)
    {
        String tag=request.getParameter("tag");
        SysUser user= ShiroUtils.getSysUser();
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        String str=forumUser.getLabel();
        String[] arr=str.split("#");
        String newStr="";
        for(String s:arr){
            if(!s.equals(tag)){
                newStr+=s+"#";
            }
        }
        forumUser.setLabel(newStr);
        forumUserService.updateUserInfo(forumUser);
        return AjaxResult.success("删除标签成功!");
    }

    @PostMapping("/setting/idCard")
    @ResponseBody
    public AjaxResult addIdCard(HttpServletRequest request)
    {
        String name=request.getParameter("name");
        String idCard=request.getParameter("idCard");
        Long userId= ShiroUtils.getUserId();
        ForumUser forumUser=new ForumUser();
        forumUser.setUserId(userId);
        forumUser.setReal_name(name);
        forumUser.setId_card(idCard);
        String xingzuo ="";
        try{
            xingzuo = CheckIdCardUtils.getConstellationById(idCard);
        }catch (Exception ex){}

        forumUser.setXingzuo(xingzuo);

        String shuxiang ="";
        try{
            shuxiang = CheckIdCardUtils.getZodiacById(idCard);
        }catch (Exception ex){}
        forumUser.setShuxiang(shuxiang);
        String birthday="";
        try{
            birthday = CheckIdCardUtils.getBirthDay(idCard);
        }catch (Exception ex){}
        forumUser.setBirthday(birthday);
        int n = forumUserService.updateUserInfo(forumUser);
        if(n>0){
            return AjaxResult.success("保存成功!");
        }else{
            return AjaxResult.success("保存失败!");
        }
    }

/******************前台zcool主题的个人资料设置  end *******************************/

    /**
     * 账号安全跳转
     * @param modelMap
     * @return
     */
    @GetMapping("/accountSafe")
    public String accountSafe(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        if(user!=null){
            modelMap.addAttribute("user",user);
            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());

            modelMap.addAttribute("email",user.getEmail());
            modelMap.addAttribute("phone",user.getPhonenumber());

            modelMap.addAttribute("emailFlag",user.getEmailFlag().toString());//邮箱绑定标志 1已绑定 0未绑定
            modelMap.addAttribute("phoneFlag",user.getPhoneFlag().toString());//手机绑定标志 1已绑定 0未绑定
        }


        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        if(forumUser!=null){
            modelMap.addAttribute("id_card",forumUser.getId_card());
        }
        return "forum/account/accountSafe";

    }

    /***************************************登录，注册，发送验证码，找回密码相关****************  end  **************************/

    /***************************************关于我们，用户协议，联系我们，帮助等协议相关****************  start  **************************/

    /**
     * 关于我们
     * @return
     */
    @GetMapping("/aboutUs")
    public String aboutUs(){
        return "forum/pages/aboutUs";
    }
    /**
     * 用户协议
     * @return
     */
    @GetMapping("/userAgreement")
    public String userAgreement(){
        return "forum/pages/userAgreement";
    }
    /**
     * 关于我们
     * @return
     */
    @GetMapping("/contactUs")
    public String contactUs(){
        return "forum/pages/contactUs";
    }

    /**
     * 帮助中心
     * @return
     */
    @GetMapping("/help")
    public String help(){
        return "forum/pages/help";
    }

    /**
     * 侵权申诉
     * @return
     */
    @GetMapping("/violationClaim")
    public String violationClaim(){
        return "forum/pages/violationClaim";
    }
    /**
     * 隐私政策
     * @return
     */
    @GetMapping("/policy")
    public String policy(){
        return "forum/pages/policy";
    }

    /**
     * 站点规则
     * @return
     */
    @GetMapping("/rule")
    public String rule(){
        return "forum/pages/rule";
    }

    /***************************************关于我们，用户协议，联系我们，帮助等协议相关****************  end  **************************/

    /***********************我的关注、我的收藏、我的帖子  start*****************************/

    /**
     * 提问题
     * @return
     */
    @GetMapping("/uploadQuestion")
    public String uploadQuestion(ModelMap modelMap){
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null) {
            modelMap.addAttribute("userId", user.getUserId().toString());
            modelMap.addAttribute("userName", user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        return "forum/uploadQuestion";
    }

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 上传附件
     */
    @PostMapping("/forum/uploadAttachment")
    @ResponseBody
    public AjaxResult uploadMaterial(MultipartFile file) throws Exception
    {
        try
        {
            // 上传并返回新文件名称
            String path = FileUploadUtils.upload(Global.getAttachPath(), file);
            String url = serverConfig.getUrl() + path;
            Map<String,Object> data=new HashMap();
            int width=FileUploadUtils.getImgWidth(file);
            int height=FileUploadUtils.getImgHeight(file);
            data.put("path",path);
            data.put("url",url);
            data.put("width",width);
            data.put("suffix",FileUploadUtils.getExtension(file));
            data.put("height",height);
            data.put("size",file.getSize());
            data.put("name",file.getOriginalFilename());
            return AjaxResult.success(data);
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }
    /**
     * 我的帖子
     * @return
     */
    @GetMapping("/myQuestion")
    public String myQuestion(ModelMap modelMap){
        SysUser user = ShiroUtils.getSysUser();
        //user=userService.selectUserById(user.getUserId());
        if(user!=null) {
            modelMap.addAttribute("userId", user.getUserId().toString());
            modelMap.addAttribute("userName", user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        return "forum/myQuestion";
    }
    /**
     * Ta的帖子
     * @return
     */
    @GetMapping("/forum/taQuestion/{userId}")
    public String taQuestion(@PathVariable("userId")String userId, ModelMap modelMap){

        SysUser  loginUser = ShiroUtils.getSysUser();
        modelMap.addAttribute("loginUser",loginUser);

        SysUser  user=userService.selectUserById(Long.valueOf(userId));
        if(user==null){
            return "forum/pages/404";
        }
        //查询帖子数量
        int n=forumQuestionService.selectQuestionCount(userId);
        modelMap.addAttribute("questionCount",n);
        //查询粉丝数量
        int count=forumUserService.selectFansCount(userId);
        modelMap.addAttribute("fansCount",count);

        if(loginUser==null){
            modelMap.addAttribute("otherUser","1");
        }else{
            if(loginUser.getUserId().equals(user.getUserId())){
                modelMap.addAttribute("otherUser","0");//登录用户和访问用户同一个人
            }else{
                modelMap.addAttribute("otherUser","1");
            }

            //查询用户关注状态
            Integer focusCount=forumUserService.selectFocusFlag(loginUser.getUserId().toString(),userId);
            if(focusCount>0){
                //已经关注
                modelMap.addAttribute("focusFlag",true);
            }else{
                modelMap.addAttribute("focusFlag",false);
            }

        }
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        if(forumUser!=null){
            //访问次数+1
            Object temp = forumCache.get("user_look_"+userId);
            boolean visitFlag=false;
            if(temp!=null){
                visitFlag=(Boolean) temp;
            }
            if(!visitFlag){
                forumUser.setLook(forumUser.getLook()+1);
                forumUserService.updateUserInfo(forumUser);
                forumCache.put("user_look_"+userId,true);
            }
            modelMap.addAttribute("look",forumUser.getLook());
        }

        modelMap.addAttribute("userId", userId);
        modelMap.addAttribute("userId",user.getUserId().toString());
        modelMap.addAttribute("userName",user.getUserName());
        modelMap.addAttribute("avatar",getAvatarPath(user));
        return "forum/account/taQuestion";
    }
    /**
     * 我的关注
     * @return
     */
    @GetMapping("/myFocus")
    public String myFocus(ModelMap modelMap){
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null) {
            modelMap.addAttribute("userId", user.getUserId().toString());
            modelMap.addAttribute("userName", user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        return "forum/myFocus";
    }
    /**
     * Ta的关注
     * @return
     */
    @GetMapping("/forum/taFocus/{userId}")
    public String taFocus(@PathVariable("userId")String userId, ModelMap modelMap){
        SysUser  loginUser = ShiroUtils.getSysUser();
        modelMap.addAttribute("loginUser",loginUser);

        SysUser  user=userService.selectUserById(Long.valueOf(userId));
        if(user==null){
            return "forum/pages/404";
        }
        //查询帖子数量
        int n=forumQuestionService.selectQuestionCount(userId);
        modelMap.addAttribute("questionCount",n);
        //查询粉丝数量
        int count=forumUserService.selectFansCount(userId);
        modelMap.addAttribute("fansCount",count);

        if(loginUser==null){
            modelMap.addAttribute("otherUser","1");
        }else{
            if(loginUser.getUserId().equals(user.getUserId())){
                modelMap.addAttribute("otherUser","0");//登录用户和访问用户同一个人
            }else{
                modelMap.addAttribute("otherUser","1");
            }

            //查询用户关注状态
            Integer focusCount=forumUserService.selectFocusFlag(loginUser.getUserId().toString(),userId);
            if(focusCount>0){
                //已经关注
                modelMap.addAttribute("focusFlag",true);
            }else{
                modelMap.addAttribute("focusFlag",false);
            }

        }
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        if(forumUser!=null){
            //访问次数+1
            Object temp = forumCache.get("user_look_"+userId);
            boolean visitFlag=false;
            if(temp!=null){
                visitFlag=(Boolean) temp;
            }
            if(!visitFlag){
                forumUser.setLook(forumUser.getLook()+1);
                forumUserService.updateUserInfo(forumUser);
                forumCache.put("user_look_"+userId,true);
            }
            modelMap.addAttribute("look",forumUser.getLook());
        }
        modelMap.addAttribute("userId", userId);


        modelMap.addAttribute("userId",user.getUserId().toString());
        modelMap.addAttribute("userName",user.getUserName());
        modelMap.addAttribute("avatar",getAvatarPath(user));
        return "forum/account/taFocus";
    }
    /**
     * 我的收藏
     * @return
     */
    @GetMapping("/myFavourite")
    public String myFavourite(ModelMap modelMap){
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null) {
            modelMap.addAttribute("userId", user.getUserId().toString());
            modelMap.addAttribute("userName", user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        return "forum/myFavourite";
    }
    /***********************我的关注、我的收藏、我的帖子  end*****************************/



    /**
     * 前台新增保存问题
     */

    @Log(title = "前台新增问题", businessType = BusinessType.INSERT)
    @PostMapping("/forumQuestion/addQuestion")
    @ResponseBody
    public AjaxResult addQuestion(ForumQuestion forumQuestion)
    {
        forumQuestion.setAvailable(0);
        int n=forumQuestionService.insertForumQuestion(forumQuestion);
        Map<String,Object> returnMap=new HashMap<String,Object>();
        returnMap.put("id",forumQuestion.getId());
        return AjaxResult.success("发帖成功!",returnMap);

    }
    /**
     * 前台新增保存问题
     */

    @Log(title = "前台修改问题", businessType = BusinessType.INSERT)
    @PostMapping("/forumQuestion/editQuestion")
    @ResponseBody
    public AjaxResult updateQuestion(ForumQuestion forumQuestion)
    {

        return toAjax(forumQuestionService.updateForumQuestion(forumQuestion));

    }
    /**
     * 前台修改问题
     */
    @GetMapping("/forumQuestion/editQuestion/{id}")
    public String editQuestion(@PathVariable("id") String id, ModelMap mmap)
    {
        ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(id);

        if(forumQuestion==null){
            return "forum/pages/404";
        }
        mmap.put("forumQuestion", forumQuestion);

        String categoryId=forumQuestion.getCategoryId();
        ForumCategory forumCategory= forumCategoryService.selectForumCategoryById(Long.valueOf(categoryId));
        mmap.put("forumCategory", forumCategory);
        String userId=ShiroUtils.getUserId().toString();
        if(!forumQuestion.getYhid().equals(userId)){
            return "forum/pages/404";
        }
        return "forum/question/editQuestion";
    }

    /**
     * 帖子详情
     * @param id
     * @param mmap
     * @return
     */
    @GetMapping("/forum/question/questionDetail/{id}")
    public String questionDetail(@PathVariable("id") String id, ModelMap mmap)
    {
        try{
            ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(id);
            if(forumQuestion==null){
                return "forum/pages/404";
            }
            //评论数
            int m=forumCommentService.selectCommentCount(id);
            forumQuestion.setCommentNum(m);
            String questionUserId=forumQuestion.getYhid();

            SysUser user = ShiroUtils.getSysUser();

            SysUser   questionUser =userService.selectUserById(Long.valueOf(questionUserId));
            mmap.addAttribute("userId", questionUserId);
            mmap.addAttribute("userName", questionUser.getUserName());
            mmap.addAttribute("avatar",getAvatarPath(questionUser));
            if(user!=null) {
                mmap.addAttribute("user", user);
                if(questionUserId.equals(user.getUserId().toString())){
                    mmap.addAttribute("other","0");
                }else{
                    mmap.addAttribute("other","1");
                }
            }else{
                mmap.addAttribute("other","1");
            }

            //查询评论数
            int n=forumCommentService.selectCommentCount(forumQuestion.getId().toString());
            forumQuestion.setCommentNum(n);
            mmap.put("forumQuestion", forumQuestion);
            if(user!=null) {
                //查询收藏状态
                String yhid=user.getUserId().toString();
                String qid=forumQuestion.getId().toString();
                Integer count = forumQuestionService.selectFavouriteFlag(yhid,qid);
                if(count==1){
                    //已经收藏
                    mmap.addAttribute("favouriteFlag","1");
                }else{
                    mmap.addAttribute("favouriteFlag","0");
                }
                //查询用户关注状态
                Integer focusCount=forumUserService.selectFocusFlag(yhid,forumQuestion.getYhid());
                if(focusCount>0){
                    //已经关注
                    mmap.addAttribute("focusFlag",true);
                }else{
                    mmap.addAttribute("focusFlag",false);
                }
            }else{
                mmap.addAttribute("favouriteFlag","0");
            }

            String categoryId=forumQuestion.getCategoryId();
            ForumCategory forumCategory= forumCategoryService.selectForumCategoryById(Long.valueOf(categoryId));
            mmap.put("forumCategory", forumCategory);
            return "forum/question/questionDetail";
        }catch (Exception ex){
            ex.printStackTrace();
            return "forum/pages/404";
        }

    }
    //付费下载压缩包资源
    @PostMapping("/forumQuestion/downloadPay/{questionId}")
    @ResponseBody
    public AjaxResult downloadPay(@PathVariable("questionId")String questionId)
    {
        SysUser sysUser=ShiroUtils.getSysUser();
        ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(questionId);
        if(forumQuestion!=null){
           int payType=forumQuestion.getPayType();
            int payCount=forumQuestion.getPayCount();
           if(payType==1){//积分下载
               int count = forumUserService.selectDownloadHisCount(sysUser.getUserId().toString(),forumQuestion.getAttachment());
               if(count==0){//没有下载过该资源
                   SysUser user = userService.selectUserById(Long.valueOf(sysUser.getUserId()));
                   if(user.getScore()<payCount){
                       return AjaxResult.error("积分不足以下载该资源!");
                   }
                   user.setScore(user.getScore()-payCount);
                   userService.updateUserInfo(user);
                   //记录积分消费记录
                   ForumOut forumOut=new ForumOut();
                   forumOut.setUserId(sysUser.getUserId().toString());
                   forumOut.setAmount("-"+payCount+"积分");
                   forumOut.setForumOutType(2);
                   forumOut.setDescription("下载资源,帖子ID:"+forumQuestion.getId());
                   forumOut.setOrderTime(new Date());
                   forumOutService.insertForumOut(forumOut);
                   //插入下载历史记录
                   forumUserService.insertDownloadHis(user.getUserId().toString(),forumQuestion.getAttachment());
               }
               return AjaxResult.success("获取下载地址成功!",forumQuestion.getAttachment());
           }
        }

        return error("参数错误!");
    }
    /**
     * 我的帖子列表
     */

    @PostMapping("/forumQuestion/myQuestionList")
    @ResponseBody
    public TableDataInfo myQuestionList(ForumQuestion forumQuestion)
    {
        Long yhid= ShiroUtils.getUserId();
        forumQuestion.setYhid(yhid.toString());
        //forumQuestion.setFront(1);//前台查询
        startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(forumQuestion);
        return getDataTable(list);
    }
    /**
     * Ta的帖子列表
     */

    @PostMapping("/forum/taQuestionList/{userId}")
    @ResponseBody
    public TableDataInfo taQuestionList(@PathVariable("userId") String userId)
    {
        ForumQuestion forumQuestion=new ForumQuestion();
        forumQuestion.setYhid(userId);
        forumQuestion.setFront(1);//前台查询
        startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(forumQuestion);
        return getDataTable(list);
    }
    /**
     * 我的关注列表
     */

    @PostMapping("/forum/taFocusList/{userId}")
    @ResponseBody
    public TableDataInfo taFocusList(@PathVariable("userId") String userId)
    {
        startPage();
        List<ForumUser> list = forumUserService.selectMyFocusList(userId);
        return getDataTable(list);
    }
    /**
     * 我的收藏问题列表
     */

    @PostMapping("/forumQuestion/myFavouriteList")
    @ResponseBody
    public TableDataInfo myFavouriteList(ForumQuestion forumQuestion)
    {
        Long yhid= ShiroUtils.getUserId();
        forumQuestion.setYhid(yhid.toString());
        List<ForumQuestion> list = forumQuestionService.selectMyFavouriteList(forumQuestion);
        return getDataTable(list);
    }
    /**
     * 收藏问题
     */
    @Log(title = "收藏问题", businessType = BusinessType.INSERT)
    @PostMapping( "/forumQuestion/addFavourite/{questionId}")
    @ResponseBody
    public AjaxResult addFavourite(@PathVariable("questionId")String questionId)
    {
        ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(questionId);

        String questionUserId=forumQuestion.getYhid();
        String yhid=ShiroUtils.getUserId().toString();
        if(questionUserId.equals(yhid)){
            //自己不能收藏自己的内容
            return AjaxResult.error("您不能收藏自己的内容!");
        }
        return toAjax(forumQuestionService.addFavourite(yhid,questionId));
    }
    /**
     * 取消收藏问题
     */
    @Log(title = "取消收藏问题", businessType = BusinessType.DELETE)
    @PostMapping( "/forumQuestion/removeFavourite/{questionId}")
    @ResponseBody
    public AjaxResult removeFavourite(@PathVariable("questionId")String questionId)
    {
        String yhid=ShiroUtils.getUserId().toString();
        return toAjax(forumQuestionService.removeFavourite(yhid,questionId));
    }


    @PostMapping("/forum/question/upVote/{questionId}")
    @ResponseBody
    public AjaxResult addUpVote(@PathVariable("questionId")String questionId,HttpServletRequest request){
        if(StringUtils.isEmpty(questionId)){
            return AjaxResult.error("系统错误!");
        }
        String ip= IpUtils.getIpAddr(request);
        Integer n=(Integer)forumCache.get(ip+"_question_upVote_"+questionId);
        if(n==null||n==0){
            forumQuestionService.upVote(questionId);
            forumCache.put(ip+"_question_upVote_"+questionId,1);
            return AjaxResult.success("点赞数+1");
        }else{
            forumCache.put(ip+"_question_upVote_"+questionId,n++);
            return  AjaxResult.error("您已点赞!");
        }
    }
    public static String getTimePassedLong(Date source) {

        if (source == null)
            return null;

        long nowTime = System.currentTimeMillis(); // 获取当前时间的毫秒数

        String msg = "";

        long dateDiff = nowTime - source.getTime();

        if (dateDiff >= 0) {
            long dateTemp1 = dateDiff  / 1000; // 秒
            long dateTemp2 = dateTemp1 / 60;   // 分钟
            long dateTemp3 = dateTemp2 / 60;   // 小时
            long dateTemp4 = dateTemp3 / 24;   // 天数
            long dateTemp5 = dateTemp4 / 30;   // 月数
            long dateTemp6 = dateTemp5 / 12;   // 年数
            if (dateTemp6 > 0)
                msg = dateTemp6 + "年前";
            else if (dateTemp5 > 0)
                msg = dateTemp5 + "个月前";
            else if (dateTemp4 > 0)
                msg = dateTemp4 + "天前";
            else if (dateTemp3 > 0)
                msg = dateTemp3 + "小时前";
            else if (dateTemp2 > 0)
                msg = dateTemp2 + "分钟前";
            else if (dateTemp1 > 0)
                msg = dateTemp1 + "秒前";
            else
                msg = "刚刚";
        }
        return msg;

    }

    /**
     * 帖子评论
     * @param tid
     * @return
     */
    @PostMapping("/forum/comments")
    @ResponseBody
    public AjaxResult comments(String tid){
        if(StringUtils.isEmpty(tid)){
            return AjaxResult.error("参数错误!");
        }
        ForumComment form=new ForumComment();
        form.setTid(tid);
        form.setType("question");
        startPage();
        List<ForumComment> list = forumCommentService.selectForumCommentList(form);
        list.stream().forEach(a->{
            String s=getTimePassedLong(a.getCreateTime());
            a.setTimeDesc(s);
        });
        Map<String,Object> data=new HashMap<>();
        data.put("total",new PageInfo(list).getTotal());
        data.put("rows",list);
        data.put("hasNextPage",new PageInfo(list).isHasNextPage());
        data.put("nextPage",new PageInfo(list).getNextPage());
        return AjaxResult.success(data);
    }

    @PostMapping("/forum/questionView")
    @ResponseBody
    public AjaxResult questionView(HttpServletRequest request,String questionId){
        if(StringUtils.isEmpty(questionId)){
            return AjaxResult.error("系统错误!");
        }
        String ip= IpUtils.getIpAddr(request);
        Integer n=(Integer)forumCache.get(ip+"_question_view_"+questionId);
        if(n==null||n==0){
            forumQuestionService.questionLook(questionId);
            forumCache.put(ip+"_question_view_"+questionId,1);
            return AjaxResult.success("浏览数+1");
        }else{
            forumCache.put(ip+"_question_view_"+questionId,n++);
            return  AjaxResult.error("您已浏览过!");
        }
    }


    @GetMapping("/forum/signRule")
    public String signRule()
    {
        return "forum/pages/signRule";
    }



    @PostMapping("/forum/comment/upVote")
    @ResponseBody
    public AjaxResult commentUpVote(HttpServletRequest request,String commentId){
        if(StringUtils.isEmpty(commentId)){
            return AjaxResult.error("系统错误!");
        }
        String ip= IpUtils.getIpAddr(request);
        Integer n=(Integer)forumCache.get(ip+"_comment_upVote_"+commentId);
        if(n==null||n==0){
            forumCommentService.upVote(commentId);
            forumCache.put(ip+"_comment_upVote_"+commentId,1);
            return AjaxResult.success("支持数+1");
        }else{
            forumCache.put(ip+"_comment_upVote_"+commentId,n++);
            return  AjaxResult.error("您已点赞!");
        }
    }

    /**
     * 每日签到
     * @return
     */
    @PostMapping("/userSign")
    @ResponseBody
    public AjaxResult userSign()
    {
        SysUser user=ShiroUtils.getSysUser();
        if(user!=null){
            Map<String,Object> returnMap=new HashMap<String,Object>();
            ForumUserSign forumUserSign=forumUserService.selectUserSign(user.getUserId().intValue());
            if(forumUserSign==null){
                forumUserSign=new ForumUserSign();
                forumUserSign.setCount(1);
                forumUserSign.setUserId(ShiroUtils.getUserId().toString());
                forumUserSign.setSignTime(new Date());
                forumUserService.insertUserSign(forumUserSign);
                returnMap.put("dayCount",1);
                return AjaxResult.success("签到成功!",returnMap);
            }else{
                Date temp=forumUserSign.getSignTime();
                Date nowDate=new Date();
                if(isSameDay(temp,nowDate)){
                    return AjaxResult.error("您今日已签到!");
                }else{
                  Date yestaday=  DateUtils.addDays(nowDate,-1);
                    if(isSameDay(temp,yestaday)){
                        //上次签到是昨天
                        forumUserSign.setSignTime(new Date());
                        forumUserSign.setCount(forumUserSign.getCount()+1);
                        forumUserService.updateUserSign(forumUserSign);
                        returnMap.put("dayCount",forumUserSign.getCount());
                        return AjaxResult.success("签到成功!",returnMap);
                    }else{
                        //昨天没签到累计天数清零

                        forumUserSign.setSignTime(new Date());
                        forumUserSign.setCount(1);
                        forumUserService.updateUserSign(forumUserSign);
                        returnMap.put("dayCount",1);
                        return AjaxResult.success("签到成功!",returnMap);
                    }
                }
            }
        }else{
            return AjaxResult.error("未登录!");
        }

    }

    /**
     * 入账记录
     * @return
     */
    @GetMapping("/myIn")
    public String myIn(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null) {
            modelMap.addAttribute("userId", user.getUserId().toString());
            modelMap.addAttribute("userName", user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        return "forum/myIn";
    }
    /**
     * 出账记录
     * @return
     */
    @GetMapping("/myOut")
    public String myOut(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null) {
            modelMap.addAttribute("userId", user.getUserId().toString());
            modelMap.addAttribute("userName", user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        return "forum/myOut";
    }

    /**
     * 搜索内容
     * 目前仅支持文章标题模糊搜索
     *
     * @param content
     * @param model
     * @return
     */
    @GetMapping("/forum/search")
    public String search(String content, Model model) {
        SysUser loginUser=ShiroUtils.getSysUser();
        if(loginUser!=null){
            model.addAttribute("user", loginUser);
        }
        model.addAttribute("content", content);
        ForumQuestion form = new ForumQuestion();
        form.setTitle(content.trim());
        model.addAttribute("content", content.trim());
        form.setFront(1);//前台查询
        //startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(form);
        list.forEach(a->{
            //评论数
            int m=forumCommentService.selectCommentCount(a.getId().toString());
            a.setCommentNum(m);
        });
        list.forEach(a->{
            //作者名称
            SysUser user=  userCache.get(a.getYhid());
            if(user==null){
                user= userService.selectUserById(Long.valueOf(a.getYhid()));
                userCache.put(a.getYhid(),user);
            }
            a.setAuthor(user.getUserName());
            a.setAvatar(getAvatarPath(user));
        });
        model.addAttribute("total", list.size());
        model.addAttribute("rows",list);
        return "forum/search";
    }

    /**
     * 分类列表
     *
     * @param categoryId
     * @param model
     * @return
     */
    @GetMapping("/forum/category/{categoryId}")
    public String categoryBy(@PathVariable("categoryId") String categoryId, Model model) {
        SysUser user=ShiroUtils.getSysUser();
        model.addAttribute("pageUrl", "/forum/category/"+categoryId);
        if (user != null) {
            model.addAttribute("user",user);
        }
        ForumCategory forumCategory=forumCategoryService.selectForumCategoryById(Long.valueOf(categoryId));
        if(forumCategory!=null){
            model.addAttribute("categoryName", forumCategory.getCategoryName());
        }
        ForumQuestion form = new ForumQuestion();
        form.setCategoryId(categoryId);
        model.addAttribute("categoryId", categoryId);
        form.setFront(1);//前台查询
        startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(form);

        list.forEach(a->{
            //评论数
            int m=forumCommentService.selectCommentCount(a.getId().toString());
            a.setCommentNum(m);
        });
        list.forEach(a->{
            //作者名称
            SysUser user2=  userCache.get(a.getYhid());
            if(user2==null){
                user2= userService.selectUserById(Long.valueOf(a.getYhid()));
                userCache.put(a.getYhid(),user2);
            }
            a.setAuthor(user2.getUserName());
            a.setAvatar(getAvatarPath(user2));
        });

        PageInfo pageInfo=new PageInfo(list);
        model.addAttribute("total", pageInfo.getTotal());
        model.addAttribute("pageNo", pageInfo.getPageNum());
        model.addAttribute("pageSize", pageInfo.getPageSize());
        model.addAttribute("totalPages", pageInfo.getPages());
        model.addAttribute("hasPrevious", pageInfo.isHasPreviousPage());
        model.addAttribute("hasNext", pageInfo.isHasNextPage());
        model.addAttribute("currentPage", pageInfo.getPageNum());
        model.addAttribute("prePage", pageInfo.getPrePage());
        model.addAttribute("nextPage", pageInfo.getNextPage());
        model.addAttribute("navNums", pageInfo.getNavigatepageNums());
        model.addAttribute("rows",list);
        return "forum/category";
    }
    @GetMapping("/forum/tag/{tagId}")
    public String tag(@PathVariable("tagId") String tagId, Model model) {
        SysUser user=ShiroUtils.getSysUser();
        model.addAttribute("pageUrl", "/forum/tag/"+tagId);
        if (user != null) {
            model.addAttribute("user",user);
        }
        ForumTags forumTag=forumTagsService.selectForumTagsById(Long.valueOf(tagId));
        if(forumTag!=null){
            model.addAttribute("tagName", forumTag.getTagName());
        }
        model.addAttribute("tagId", tagId);
        ForumQuestion form = new ForumQuestion();
        form.setTags(tagId);
        form.setFront(1);//前台查询
        startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(form);

        list.forEach(a->{
            //评论数
            int m=forumCommentService.selectCommentCount(a.getId().toString());
            a.setCommentNum(m);
        });
        list.forEach(a->{
            //作者名称
            SysUser user2=  userCache.get(a.getYhid());
            if(user2==null){
                user2= userService.selectUserById(Long.valueOf(a.getYhid()));
                userCache.put(a.getYhid(),user2);
            }
            a.setAuthor(user2.getUserName());
            a.setAvatar(getAvatarPath(user2));
        });

        PageInfo pageInfo=new PageInfo(list);
        model.addAttribute("total", pageInfo.getTotal());
        model.addAttribute("pageNo", pageInfo.getPageNum());
        model.addAttribute("pageSize", pageInfo.getPageSize());
        model.addAttribute("totalPages", pageInfo.getPages());
        model.addAttribute("hasPrevious", pageInfo.isHasPreviousPage());
        model.addAttribute("hasNext", pageInfo.isHasNextPage());
        model.addAttribute("currentPage", pageInfo.getPageNum());
        model.addAttribute("prePage", pageInfo.getPrePage());
        model.addAttribute("nextPage", pageInfo.getNextPage());
        model.addAttribute("navNums", pageInfo.getNavigatepageNums());
        model.addAttribute("rows",list);
        return "forum/tag";
    }
    @Log(title = "置顶", businessType = BusinessType.DELETE)
    @PostMapping( "/forumQuestion/setTop/{id}")
    @ResponseBody
    public AjaxResult setTop(@PathVariable("id")String id)
    {
        Map<String,Object> returnMap=new HashMap<String,Object>();
        returnMap.put("id",id);
        int n=forumQuestionService.setTop(id);
        return n>0?AjaxResult.success("帖子置顶成功!",returnMap):error("帖子置顶失败!");
    }
    @Log(title = "置推荐", businessType = BusinessType.DELETE)
    @PostMapping( "/forumQuestion/setRecommend/{id}")
    @ResponseBody
    public AjaxResult setRecommend(@PathVariable("id")String id)
    {
        Map<String,Object> returnMap=new HashMap<String,Object>();
        returnMap.put("id",id);
        int n=forumQuestionService.setRecommend(id);
        return n>0?AjaxResult.success("帖子置推荐成功!",returnMap):error("帖子置推荐失败!");
    }
    @Log(title = "置精品", businessType = BusinessType.DELETE)
    @PostMapping( "/forumQuestion/setGood/{id}")
    @ResponseBody
    public AjaxResult setGood(@PathVariable("id")String id)
    {
        Map<String,Object> returnMap=new HashMap<String,Object>();
        returnMap.put("id",id);
        int n=forumQuestionService.setGood(id);
        return n>0?AjaxResult.success("帖子置精品成功!",returnMap):error("帖子置精品失败!");
    }


    /**
     * 关注用户
     */
    @Log(title = "关注用户", businessType = BusinessType.INSERT)
    @PostMapping( "/forumQuestion/addFocus/{focus_user_id}")
    @ResponseBody
    public AjaxResult addFocus(@PathVariable("focus_user_id")String focus_user_id)
    {
        String yhid=ShiroUtils.getUserId().toString();
        if(focus_user_id.equals(yhid)){
            //自己不能关注自
            return AjaxResult.error("您不能关注自己!");
        }
        //查询用户关注状态
        Integer focusCount=forumUserService.selectFocusFlag(yhid,focus_user_id);
        if(focusCount>0){
            //已经关注
            return AjaxResult.error("您已经关注!");
        }

        return toAjax(forumUserService.addFocus(yhid,focus_user_id));
    }
    /**
     * 取消关注
     */
    @Log(title = "取消关注", businessType = BusinessType.DELETE)
    @PostMapping( "/forumQuestion/removeFocus/{focus_user_id}")
    @ResponseBody
    public AjaxResult removeFocus(@PathVariable("focus_user_id")String focus_user_id)
    {
        String yhid=ShiroUtils.getUserId().toString();
        if(focus_user_id.equals(yhid)){
            //自己不能关注自
            return AjaxResult.error("您不能取消关注自己!");
        }
        return toAjax(forumUserService.removeFocus(yhid,focus_user_id));
    }
    /**
     * 我的关注列表
     */

    @PostMapping("/forumQuestion/myFocusList")
    @ResponseBody
    public TableDataInfo myFocusList()
    {
        Long yhid= ShiroUtils.getUserId();
        startPage();
        List<ForumUser> list = forumUserService.selectMyFocusList(yhid.toString());
        return getDataTable(list);
    }
    /**
     * 我的粉丝
     * @return
     */
    @GetMapping("/myFans")
    public String myFans(ModelMap modelMap){
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null) {
            modelMap.addAttribute("userId", user.getUserId().toString());
            modelMap.addAttribute("userName", user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        return "forum/myFans";
    }
    /**
     * 我的粉丝列表
     */

    @PostMapping("/forumQuestion/myFansList")
    @ResponseBody
    public TableDataInfo selectMyFansList()
    {
        Long yhid= ShiroUtils.getUserId();
        startPage();
        List<ForumUser> list = forumUserService.selectMyFansList(yhid.toString());
        return getDataTable(list);
    }

    /**
     * Ta的帖子
     * @return
     */
    @GetMapping("/hisQuestions")
    public String hisQuestions(ModelMap modelMap){
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null) {
            modelMap.addAttribute("userId", user.getUserId().toString());
            modelMap.addAttribute("userName", user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        return "forum/hisQuestions";
    }
    /**
     * 关注的人的问题列表(Ta的动态)
     */

    @PostMapping("/forumQuestion/hisQuestions")
    @ResponseBody
    public TableDataInfo hisQuestions()
    {
        Long yhid= ShiroUtils.getUserId();
        startPage();
        List<ForumQuestion> list = forumUserService.selectHisQuestions(yhid.toString());
        return getDataTable(list);
    }

    @Autowired
    private IArticleTemplateService articleTemplateService;

    @GetMapping("/forumQuestion/articleTemplates")
    @ResponseBody
    public String list(ArticleTemplate articleTemplate)
    {
        List<ArticleTemplate> list = articleTemplateService.selectArticleTemplateList(articleTemplate);
        String html="";

        for(ArticleTemplate t:list){
            html+="<div class=\"stylemode"+t.getId()+"\" onclick=\"insertHtml("+t.getId()+")\">";
            html+=t.getContent();
            html+="</div>";
        }
        return html;
    }
}
