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
 * ???????????????
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
    public  String  registerUserDeptId;//???????????????????????????????????????id
    @Value("${front.register.roleId}")
    public  String registerUserRoleId;//???????????????????????????????????????id

    private static Cache<String,Object> forumCache= CacheUtil.newTimedCache(1000*60*60*3);
    private static Cache<String,SysUser> userCache= CacheUtil.newTimedCache(1000*60*60*3);




    /***************************************??????????????????????????????????????????????????????****************  start  **************************/

    /**
     *  ??????
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
                        //?????????????????????
                        dayCount=forumUserSign.getCount();
                    }else{
                        //?????????????????????????????????
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
     * ??????????????????
     * @param request
     * @param response
     * @return
     */
    @GetMapping({"/login","/forum/login"})
    public String login(HttpServletRequest request, HttpServletResponse response){
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null){
            return "redirect:/forum/index";//????????????
        }
        // ?????????Ajax???????????????Json????????????
        if (ServletUtils.isAjaxRequest(request))
        {
            return ServletUtils.renderString(response, "{\"code\":\"1\",\"msg\":\"??????????????????????????????????????????\"}");
        }

        return "forum/login";

    }

    /**
     * ??????????????????iframe
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/login/frame")
    public String loginFrame(HttpServletRequest request, HttpServletResponse response){
        return "forum/account/login_frame";
    }

    /**
     *  ?????????????????????????????????
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
                return AjaxResult.success("?????????????????????!");
            }else{
                return AjaxResult.error("?????????????????????!");
            }
        }else{
            return AjaxResult.error("?????????????????????!");
        }

    }

    /**
     * ??????????????????????????????
     * @param username
     * @return
     */
    @PostMapping("/checkAccount")
    @ResponseBody
    public AjaxResult checkAccount(String username)
    {
        boolean b=userService.checkAccountExist(username);
        if(b){
            return AjaxResult.success("??????????????????!");
        }
        return AjaxResult.error("???????????????!");
    }


    /**
     * ?????????????????????????????????
     * @param phone
     * @return
     */
    @PostMapping("/checkPhone")
    @ResponseBody
    public AjaxResult checkPhone(String phone)
    {
        boolean b=userService.checkPhoneExist(phone);
        if(b){
            return AjaxResult.success("?????????????????????!");
        }
        return AjaxResult.error("??????????????????!");
    }
    /**
     * ??????????????????????????????(phoneNumber,phoneFlag=1)
     * @param phone
     * @return
     */
    @PostMapping("/checkPhoneBind")
    @ResponseBody
    public AjaxResult checkPhoneBind(String phone)
    {
        SysUser user=userService.selectUserByPhoneNumber(phone);
        if(user!=null){
            return AjaxResult.success("?????????????????????!");
        }
        return AjaxResult.error("?????????????????????!");
    }
    /**
     * ???????????????????????????
     * @param email
     * @return
     */
    @PostMapping("/checkEmailBind")
    @ResponseBody
    public AjaxResult checkEmailBind(String email)
    {
        SysUser user=userService.selectUserByEmail(email);
        if(user!=null){
            return AjaxResult.success("??????????????????!");
        }
        return AjaxResult.error("??????????????????!");
    }
    /**
     * ????????????????????????(?????????????????????????????????????????????????????????????????????)
     * @return
     */
    @PostMapping("/nav/isBindPhone")
    @ResponseBody
    public AjaxResult isBindPhone()
    {
        SysUser user= ShiroUtils.getSysUser();
        boolean b=userService.isBindPhone(user);
        if(b){
            return AjaxResult.success("?????????????????????!");
        }
        return AjaxResult.error("??????????????????!");
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
            data.put("memberType","0");//????????????
            data.put("uid",user.getUserId().toString());
            return AjaxResult.success("????????????????????????!",data);
        }
        return AjaxResult.error("????????????????????????!");
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
     * ??????????????????????????????
     * @param phone
     * @param code
     * @param rememberMe
     * @return
     */
    @PostMapping("/login/sms")
    @ResponseBody
    public AjaxResult frontLoginSms(String phone, String code, Boolean rememberMe, HttpSession session)
    {
        // ??????phone???session????????????????????????????????????????????????????????????????????????
        String sessionCode = (String) session.getAttribute(phone);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("?????????????????????????????????!");
        }
        if(!sessionCode.equals(code)){
            return AjaxResult.error("???????????????!");
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
                    throw new BusinessException("??????????????????!");
                }
            }
            ServletUtils.setCookieUid(user.getUserId().toString());
            return success();
        }
        catch (AuthenticationException e)
        {
            String msg = "??????????????????";
            if (StringUtils.isNotEmpty(e.getMessage()))
            {
                msg = e.getMessage();
            }
            return error(msg);
        }
    }
    /**
     * ??????????????????
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
                    returnMap.put("todayLogin",true);//????????????????????????
                    return AjaxResult.success("????????????!",returnMap);
                }
                if(!isToday(lastDate)){
                    //???????????????????????????
                    returnMap.put("todayLogin",true);//????????????????????????
                    return AjaxResult.success("????????????!",returnMap);
                }else{
                    returnMap.put("todayLogin",false);//????????????n?????????
                }
            }


            return AjaxResult.success("????????????!",returnMap);
        }
        catch (Exception e)
        {
            String msg = "?????????????????????";
            if (StringUtils.isNotEmpty(e.getMessage()))
            {
                msg = e.getMessage();
            }
            return error(msg);
        }
    }


    /**
     * ???????????????????????????
     * @param date
     * @return    ?????????true???????????????false
     */
    private static boolean isToday(Date date) {
        //????????????
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        //?????????????????????
        String nowDay = sf.format(now);
        //???????????????
        String day = sf.format(date);
        return day.equals(nowDay);
    }
    /**
     * ????????????
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
        String u=request.getParameter("u");//??????????????????????????????
        if(StringUtils.isNotEmpty(u)){
            modelMap.addAttribute("invite_user_id",u);
        }
        return "forum/account/register";
    }
    /**
     * ??????????????????
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
            return AjaxResult.error("??????????????????!");
        }
        if(StringUtils.isEmpty(password)){
            return AjaxResult.error("??????????????????!");
        }

        String sessionCode=(String)session.getAttribute(email);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("?????????????????????!");
        }
        if(!code.equals(sessionCode)){
            return AjaxResult.error("??????????????????!");
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
        Long[] roleIds={Long.valueOf(registerUserRoleId)};//??????????????????????????????????????????????????????
        user.setRoleIds(roleIds);
        user.setDeptId(Long.valueOf(registerUserDeptId));
        int n = userService.insertUser(user);
        if(n>0){
            session.removeAttribute(email);
            String invite_user_id=request.getParameter("invite_user_id");
            if(StringUtils.isNotEmpty(invite_user_id)){
                String ip= IpUtils.getIpAddr(request);
                userService.insertUserInvite(user.getLoginName(),invite_user_id,ip);//???????????????????????????
            }
            return  AjaxResult.success("????????????!");
        }else{
            return  AjaxResult.error("????????????!");
        }
    }


    /**
     * ?????????????????????????????????
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
            return AjaxResult.success("?????????????????????!");
        }else{
            return AjaxResult.error("?????????????????????!");
        }
    }

    /**
     * ?????????????????????????????????
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
            return AjaxResult.success("?????????????????????!");
        }else{
            return AjaxResult.error("?????????????????????!");
        }
    }

    /**
     * ????????????(????????????)??????
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
     * ????????????(????????????)???????????????
     * @return
     */
    @PostMapping("/resetpwd/sendCode")
    @ResponseBody
    public AjaxResult resetpwdSendCode(HttpServletRequest request, HttpSession session)
    {
        String account=request.getParameter("account");
        String type=request.getParameter("type");//???????????????:email???sms
        if(StringUtils.isEmpty(account)){
            return AjaxResult.error("?????????/??????????????????!");
        }
        if(StringUtils.isEmpty(type)){
            return AjaxResult.error("??????type????????????!");
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
                return AjaxResult.success("?????????????????????!");
            }
        }
        if("sms".equals(type)){
            AjaxResult result = smsService.sendByTemplate(CmsConstants.KEY_USER_RESET_PWD_SMS,account,params);
            if(result.isSuccess()){
                return AjaxResult.success("?????????????????????!");
            }
        }
        return AjaxResult.error("?????????????????????!");
    }

    /**
     * ????????????
     * @param request
     * @param session
     * @return
     */
    @PostMapping("/resetpwd")
    @ResponseBody
    public AjaxResult resetpwdPost(HttpServletRequest request, HttpSession session){
        String account=request.getParameter("account");
        String code=request.getParameter("code");//?????????
        String password=request.getParameter("password");
        if(StringUtils.isEmpty(account)){
            return AjaxResult.error("?????????/??????????????????!");
        }
        if(StringUtils.isEmpty(password)){
            return AjaxResult.error("??????????????????!");
        }
        String sessionCode=(String)session.getAttribute(account);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("?????????????????????!");
        }
        if(!code.equals(sessionCode)){
            return AjaxResult.error("??????????????????!");
        }
        SysUser user=userService.selectUserByLoginName(account);
        if(user==null){
            user=userService.selectUserByPhoneNumber(account);
        }
        if(user==null){
            return AjaxResult.error("?????????/???????????????!");
        }
        user.setSalt(ShiroUtils.randomSalt());
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), password, user.getSalt()));
        int n =userService.resetUserPwd(user);
        if(n>0){
            session.removeAttribute(account);
            return AjaxResult.success("??????????????????!");
        }else{
            return AjaxResult.error("??????????????????!");
        }
    }

    /**
     * ??????????????????
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
            modelMap.addAttribute("title","??????????????????");
            modelMap.addAttribute("type","2");
            modelMap.addAttribute("oldephone",user.getPhonenumber());
        }else{
            modelMap.addAttribute("title","????????????");
            modelMap.addAttribute("type","1");
        }
        return "forum/account/modifyphone_view";
    }
    /**
     * ????????????-???????????????
     * @return
     */
    @PostMapping("/modifyPhone/sendCode")
    @ResponseBody
    public AjaxResult modifyPhoneSendCode(HttpServletRequest request, HttpSession session)
    {
        String phone=request.getParameter("phone");
        String type=request.getParameter("type");
        if(StringUtils.isEmpty(phone)){
            return AjaxResult.error("?????????????????????!");
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
            return AjaxResult.success("?????????????????????!");
        }
        return AjaxResult.error("?????????????????????!");
    }
    @PostMapping("/modifyPhone")
    @ResponseBody
    public AjaxResult modifyPhone(HttpServletRequest request, HttpSession session){
        String phone=request.getParameter("phone");
        String code=request.getParameter("code");//?????????
        String type=request.getParameter("type");

        if(StringUtils.isEmpty(phone)){
            return AjaxResult.error("2".equals(type)?"????????????????????????!":"?????????????????????!");
        }

        String sessionCode=(String)session.getAttribute(phone);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("?????????????????????!");
        }
        if(!code.equals(sessionCode)){
            return AjaxResult.error("??????????????????!");
        }
        SysUser user= ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());

        user.setPhonenumber(phone);
        user.setPhoneFlag(1);
        int n =userService.updateUserInfo(user);
        if(n>0){
            session.removeAttribute(phone);
            return AjaxResult.success("2".equals(type)?"????????????????????????!":"??????????????????!");
        }else{
            return AjaxResult.error("2".equals(type)?"????????????????????????!":"??????????????????!");
        }
    }


    /**
     * ??????????????????
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
     * ????????????
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
            return AjaxResult.error("?????????????????????!");
        }
        if(StringUtils.isEmpty(password)){
            return AjaxResult.error("?????????????????????!");
        }

        SysUser user = ShiroUtils.getSysUser();
        if (StringUtils.isNotEmpty(password) && passwordService.matches(user, oldpwd))
        {
            user.setSalt(ShiroUtils.randomSalt());
            user.setPassword(passwordService.encryptPassword(user.getLoginName(), password, user.getSalt()));
            if (userService.resetUserPwd(user) > 0)
            {
                ShiroUtils.setSysUser(userService.selectUserById(user.getUserId()));
                return AjaxResult.success("??????????????????!");
            }
            return AjaxResult.error("??????????????????!");
        }
        else
        {
            return error("????????????????????????????????????");
        }
    }
    /**
     * ???????????????
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
     * ???????????????????????????
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
            modelMap.addAttribute("title","??????????????????");
            modelMap.addAttribute("type","2");
            modelMap.addAttribute("oldemail",user.getEmail());
        }else{
            modelMap.addAttribute("title","??????????????????");
            modelMap.addAttribute("type","1");
        }
        return "forum/account/modifyemail_view";
    }
    /**
     * ????????????-???????????????
     * @return
     */
    @PostMapping("/modifyEmail/sendCode")
    @ResponseBody
    public AjaxResult modifyEmailSendCode(HttpServletRequest request, HttpSession session)
    {
        String email=request.getParameter("email");
        String type=request.getParameter("type");//1:????????????  2???????????????
        if(StringUtils.isEmpty(email)){
            return AjaxResult.error("??????????????????!");
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
            return AjaxResult.success("?????????????????????!");
        }
        return AjaxResult.error("?????????????????????!");
    }
    @PostMapping("/modifyEmail")
    @ResponseBody
    public AjaxResult modifyEmail(HttpServletRequest request, HttpSession session){
        String email=request.getParameter("email");
        String code=request.getParameter("code");//?????????
        String type=request.getParameter("type");
        if(StringUtils.isEmpty(email)){
            return AjaxResult.error("2".equals(type)?"?????????????????????!":"??????????????????!");
        }

        String sessionCode=(String)session.getAttribute(email);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("?????????????????????!");
        }
        if(!code.equals(sessionCode)){
            return AjaxResult.error("??????????????????!");
        }
        SysUser user= ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());

        user.setEmail(email);
        user.setEmailFlag(1);
        int n =userService.updateUserInfo(user);
        if(n>0){
            session.removeAttribute(email);
            return AjaxResult.success("2".equals(type)?"??????????????????!":"??????????????????!");
        }else{
            return AjaxResult.error("2".equals(type)?"??????????????????!":"??????????????????!");
        }
    }


    /**
     * ?????????????????????
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
     * ????????????-???????????????
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
            return AjaxResult.success("?????????????????????!");
        }
        return AjaxResult.error("?????????????????????!");
    }

    @PostMapping("/unbindPhone")
    @ResponseBody
    public AjaxResult unbindPhone(HttpServletRequest request, HttpSession session){

        String code=request.getParameter("code");//?????????

        SysUser user= ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        String phone=user.getPhonenumber();

        String sessionCode=(String)session.getAttribute(phone);
        if(StringUtils.isEmpty(sessionCode)){
            return AjaxResult.error("?????????????????????!");
        }
        if(!code.equals(sessionCode)){
            return AjaxResult.error("??????????????????!");
        }

        user.setPhonenumber("");
        user.setPhoneFlag(0);
        int n =userService.updateUserInfo(user);
        if(n>0){
            session.removeAttribute(phone);
            return AjaxResult.success("??????????????????!");
        }else{
            return AjaxResult.error("??????????????????!");
        }
    }

    /******************????????????????????????  start*******************************/
    /**
     * ??????????????????
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
                modelMap.addAttribute("otherUser","0");//???????????????????????????????????????
            }else{
                modelMap.addAttribute("otherUser","1");
            }

            //????????????????????????
            Integer focusCount=forumUserService.selectFocusFlag(loginUser.getUserId().toString(),userId);
            if(focusCount>0){
                //????????????
                modelMap.addAttribute("focusFlag",true);
            }else{
                modelMap.addAttribute("focusFlag",false);
            }
            //??????????????????
            if(!userId.equals(loginUser.getUserId().toString())){
                forumUserService.removeVisitUser(userId,loginUser.getUserId().toString());
                forumUserService.addVisitUser(userId,loginUser.getUserId().toString());
            }

        }
            //????????????
            /*List<ForumUser> visitUsers = forumUserService.selectVisitUsers(userId);
            if(CollectionUtils.isNotEmpty(visitUsers)){
                modelMap.addAttribute("visitUsers",visitUsers);
                visitUsers.forEach(a->{
                    a.setTimeDesc(getTimePassedLong(a.getVisitTime()));
                });
            }*/

            //??????????????????
            int n=forumQuestionService.selectQuestionCount(userId);
            modelMap.addAttribute("questionCount",n);
            //??????????????????
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
                //??????highSchool
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
                //??????
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
                //????????????+1
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
        //????????????
        List<ForumUser> visitUsers = forumUserService.selectVisitUsers(userId);
        if(CollectionUtils.isNotEmpty(visitUsers)){
            visitUsers.forEach(a->{
                a.setTimeDesc(getTimePassedLong(a.getVisitTime()));
            });
        }
        return AjaxResult.success(visitUsers);
    }
    /**
     * ??????????????????
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
                //??????highSchool
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
                //??????
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

                return AjaxResult.success("???????????????!",avatar);
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
                    return success("??????????????????!");
                }
            }
            return error("????????????????????????!??????????????????!");
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
        return AjaxResult.success("????????????!");
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
        return AjaxResult.success("????????????!");
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
        return AjaxResult.success("????????????!");
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
        return AjaxResult.success("??????????????????!");
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
        return AjaxResult.success("??????????????????!");
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
            return AjaxResult.success("????????????!");
        }else{
            return AjaxResult.success("????????????!");
        }
    }

/******************??????zcool???????????????????????????  end *******************************/

    /**
     * ??????????????????
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

            modelMap.addAttribute("emailFlag",user.getEmailFlag().toString());//?????????????????? 1????????? 0?????????
            modelMap.addAttribute("phoneFlag",user.getPhoneFlag().toString());//?????????????????? 1????????? 0?????????
        }


        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        if(forumUser!=null){
            modelMap.addAttribute("id_card",forumUser.getId_card());
        }
        return "forum/account/accountSafe";

    }

    /***************************************??????????????????????????????????????????????????????****************  end  **************************/

    /***************************************??????????????????????????????????????????????????????????????????****************  start  **************************/

    /**
     * ????????????
     * @return
     */
    @GetMapping("/aboutUs")
    public String aboutUs(){
        return "forum/pages/aboutUs";
    }
    /**
     * ????????????
     * @return
     */
    @GetMapping("/userAgreement")
    public String userAgreement(){
        return "forum/pages/userAgreement";
    }
    /**
     * ????????????
     * @return
     */
    @GetMapping("/contactUs")
    public String contactUs(){
        return "forum/pages/contactUs";
    }

    /**
     * ????????????
     * @return
     */
    @GetMapping("/help")
    public String help(){
        return "forum/pages/help";
    }

    /**
     * ????????????
     * @return
     */
    @GetMapping("/violationClaim")
    public String violationClaim(){
        return "forum/pages/violationClaim";
    }
    /**
     * ????????????
     * @return
     */
    @GetMapping("/policy")
    public String policy(){
        return "forum/pages/policy";
    }

    /**
     * ????????????
     * @return
     */
    @GetMapping("/rule")
    public String rule(){
        return "forum/pages/rule";
    }

    /***************************************??????????????????????????????????????????????????????????????????****************  end  **************************/

    /***********************??????????????????????????????????????????  start*****************************/

    /**
     * ?????????
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
     * ????????????
     */
    @PostMapping("/forum/uploadAttachment")
    @ResponseBody
    public AjaxResult uploadMaterial(MultipartFile file) throws Exception
    {
        try
        {
            // ??????????????????????????????
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
     * ????????????
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
     * Ta?????????
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
        //??????????????????
        int n=forumQuestionService.selectQuestionCount(userId);
        modelMap.addAttribute("questionCount",n);
        //??????????????????
        int count=forumUserService.selectFansCount(userId);
        modelMap.addAttribute("fansCount",count);

        if(loginUser==null){
            modelMap.addAttribute("otherUser","1");
        }else{
            if(loginUser.getUserId().equals(user.getUserId())){
                modelMap.addAttribute("otherUser","0");//???????????????????????????????????????
            }else{
                modelMap.addAttribute("otherUser","1");
            }

            //????????????????????????
            Integer focusCount=forumUserService.selectFocusFlag(loginUser.getUserId().toString(),userId);
            if(focusCount>0){
                //????????????
                modelMap.addAttribute("focusFlag",true);
            }else{
                modelMap.addAttribute("focusFlag",false);
            }

        }
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        if(forumUser!=null){
            //????????????+1
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
     * ????????????
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
     * Ta?????????
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
        //??????????????????
        int n=forumQuestionService.selectQuestionCount(userId);
        modelMap.addAttribute("questionCount",n);
        //??????????????????
        int count=forumUserService.selectFansCount(userId);
        modelMap.addAttribute("fansCount",count);

        if(loginUser==null){
            modelMap.addAttribute("otherUser","1");
        }else{
            if(loginUser.getUserId().equals(user.getUserId())){
                modelMap.addAttribute("otherUser","0");//???????????????????????????????????????
            }else{
                modelMap.addAttribute("otherUser","1");
            }

            //????????????????????????
            Integer focusCount=forumUserService.selectFocusFlag(loginUser.getUserId().toString(),userId);
            if(focusCount>0){
                //????????????
                modelMap.addAttribute("focusFlag",true);
            }else{
                modelMap.addAttribute("focusFlag",false);
            }

        }
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        if(forumUser!=null){
            //????????????+1
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
     * ????????????
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
    /***********************??????????????????????????????????????????  end*****************************/



    /**
     * ????????????????????????
     */

    @Log(title = "??????????????????", businessType = BusinessType.INSERT)
    @PostMapping("/forumQuestion/addQuestion")
    @ResponseBody
    public AjaxResult addQuestion(ForumQuestion forumQuestion)
    {
        forumQuestion.setAvailable(0);
        int n=forumQuestionService.insertForumQuestion(forumQuestion);
        Map<String,Object> returnMap=new HashMap<String,Object>();
        returnMap.put("id",forumQuestion.getId());
        return AjaxResult.success("????????????!",returnMap);

    }
    /**
     * ????????????????????????
     */

    @Log(title = "??????????????????", businessType = BusinessType.INSERT)
    @PostMapping("/forumQuestion/editQuestion")
    @ResponseBody
    public AjaxResult updateQuestion(ForumQuestion forumQuestion)
    {

        return toAjax(forumQuestionService.updateForumQuestion(forumQuestion));

    }
    /**
     * ??????????????????
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
     * ????????????
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
            //?????????
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

            //???????????????
            int n=forumCommentService.selectCommentCount(forumQuestion.getId().toString());
            forumQuestion.setCommentNum(n);
            mmap.put("forumQuestion", forumQuestion);
            if(user!=null) {
                //??????????????????
                String yhid=user.getUserId().toString();
                String qid=forumQuestion.getId().toString();
                Integer count = forumQuestionService.selectFavouriteFlag(yhid,qid);
                if(count==1){
                    //????????????
                    mmap.addAttribute("favouriteFlag","1");
                }else{
                    mmap.addAttribute("favouriteFlag","0");
                }
                //????????????????????????
                Integer focusCount=forumUserService.selectFocusFlag(yhid,forumQuestion.getYhid());
                if(focusCount>0){
                    //????????????
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
    //???????????????????????????
    @PostMapping("/forumQuestion/downloadPay/{questionId}")
    @ResponseBody
    public AjaxResult downloadPay(@PathVariable("questionId")String questionId)
    {
        SysUser sysUser=ShiroUtils.getSysUser();
        ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(questionId);
        if(forumQuestion!=null){
           int payType=forumQuestion.getPayType();
            int payCount=forumQuestion.getPayCount();
           if(payType==1){//????????????
               int count = forumUserService.selectDownloadHisCount(sysUser.getUserId().toString(),forumQuestion.getAttachment());
               if(count==0){//????????????????????????
                   SysUser user = userService.selectUserById(Long.valueOf(sysUser.getUserId()));
                   if(user.getScore()<payCount){
                       return AjaxResult.error("??????????????????????????????!");
                   }
                   user.setScore(user.getScore()-payCount);
                   userService.updateUserInfo(user);
                   //????????????????????????
                   ForumOut forumOut=new ForumOut();
                   forumOut.setUserId(sysUser.getUserId().toString());
                   forumOut.setAmount("-"+payCount+"??????");
                   forumOut.setForumOutType(2);
                   forumOut.setDescription("????????????,??????ID:"+forumQuestion.getId());
                   forumOut.setOrderTime(new Date());
                   forumOutService.insertForumOut(forumOut);
                   //????????????????????????
                   forumUserService.insertDownloadHis(user.getUserId().toString(),forumQuestion.getAttachment());
               }
               return AjaxResult.success("????????????????????????!",forumQuestion.getAttachment());
           }
        }

        return error("????????????!");
    }
    /**
     * ??????????????????
     */

    @PostMapping("/forumQuestion/myQuestionList")
    @ResponseBody
    public TableDataInfo myQuestionList(ForumQuestion forumQuestion)
    {
        Long yhid= ShiroUtils.getUserId();
        forumQuestion.setYhid(yhid.toString());
        //forumQuestion.setFront(1);//????????????
        startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(forumQuestion);
        return getDataTable(list);
    }
    /**
     * Ta???????????????
     */

    @PostMapping("/forum/taQuestionList/{userId}")
    @ResponseBody
    public TableDataInfo taQuestionList(@PathVariable("userId") String userId)
    {
        ForumQuestion forumQuestion=new ForumQuestion();
        forumQuestion.setYhid(userId);
        forumQuestion.setFront(1);//????????????
        startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(forumQuestion);
        return getDataTable(list);
    }
    /**
     * ??????????????????
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
     * ????????????????????????
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
     * ????????????
     */
    @Log(title = "????????????", businessType = BusinessType.INSERT)
    @PostMapping( "/forumQuestion/addFavourite/{questionId}")
    @ResponseBody
    public AjaxResult addFavourite(@PathVariable("questionId")String questionId)
    {
        ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(questionId);

        String questionUserId=forumQuestion.getYhid();
        String yhid=ShiroUtils.getUserId().toString();
        if(questionUserId.equals(yhid)){
            //?????????????????????????????????
            return AjaxResult.error("??????????????????????????????!");
        }
        return toAjax(forumQuestionService.addFavourite(yhid,questionId));
    }
    /**
     * ??????????????????
     */
    @Log(title = "??????????????????", businessType = BusinessType.DELETE)
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
            return AjaxResult.error("????????????!");
        }
        String ip= IpUtils.getIpAddr(request);
        Integer n=(Integer)forumCache.get(ip+"_question_upVote_"+questionId);
        if(n==null||n==0){
            forumQuestionService.upVote(questionId);
            forumCache.put(ip+"_question_upVote_"+questionId,1);
            return AjaxResult.success("?????????+1");
        }else{
            forumCache.put(ip+"_question_upVote_"+questionId,n++);
            return  AjaxResult.error("????????????!");
        }
    }
    public static String getTimePassedLong(Date source) {

        if (source == null)
            return null;

        long nowTime = System.currentTimeMillis(); // ??????????????????????????????

        String msg = "";

        long dateDiff = nowTime - source.getTime();

        if (dateDiff >= 0) {
            long dateTemp1 = dateDiff  / 1000; // ???
            long dateTemp2 = dateTemp1 / 60;   // ??????
            long dateTemp3 = dateTemp2 / 60;   // ??????
            long dateTemp4 = dateTemp3 / 24;   // ??????
            long dateTemp5 = dateTemp4 / 30;   // ??????
            long dateTemp6 = dateTemp5 / 12;   // ??????
            if (dateTemp6 > 0)
                msg = dateTemp6 + "??????";
            else if (dateTemp5 > 0)
                msg = dateTemp5 + "?????????";
            else if (dateTemp4 > 0)
                msg = dateTemp4 + "??????";
            else if (dateTemp3 > 0)
                msg = dateTemp3 + "?????????";
            else if (dateTemp2 > 0)
                msg = dateTemp2 + "?????????";
            else if (dateTemp1 > 0)
                msg = dateTemp1 + "??????";
            else
                msg = "??????";
        }
        return msg;

    }

    /**
     * ????????????
     * @param tid
     * @return
     */
    @PostMapping("/forum/comments")
    @ResponseBody
    public AjaxResult comments(String tid){
        if(StringUtils.isEmpty(tid)){
            return AjaxResult.error("????????????!");
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
            return AjaxResult.error("????????????!");
        }
        String ip= IpUtils.getIpAddr(request);
        Integer n=(Integer)forumCache.get(ip+"_question_view_"+questionId);
        if(n==null||n==0){
            forumQuestionService.questionLook(questionId);
            forumCache.put(ip+"_question_view_"+questionId,1);
            return AjaxResult.success("?????????+1");
        }else{
            forumCache.put(ip+"_question_view_"+questionId,n++);
            return  AjaxResult.error("???????????????!");
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
            return AjaxResult.error("????????????!");
        }
        String ip= IpUtils.getIpAddr(request);
        Integer n=(Integer)forumCache.get(ip+"_comment_upVote_"+commentId);
        if(n==null||n==0){
            forumCommentService.upVote(commentId);
            forumCache.put(ip+"_comment_upVote_"+commentId,1);
            return AjaxResult.success("?????????+1");
        }else{
            forumCache.put(ip+"_comment_upVote_"+commentId,n++);
            return  AjaxResult.error("????????????!");
        }
    }

    /**
     * ????????????
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
                return AjaxResult.success("????????????!",returnMap);
            }else{
                Date temp=forumUserSign.getSignTime();
                Date nowDate=new Date();
                if(isSameDay(temp,nowDate)){
                    return AjaxResult.error("??????????????????!");
                }else{
                  Date yestaday=  DateUtils.addDays(nowDate,-1);
                    if(isSameDay(temp,yestaday)){
                        //?????????????????????
                        forumUserSign.setSignTime(new Date());
                        forumUserSign.setCount(forumUserSign.getCount()+1);
                        forumUserService.updateUserSign(forumUserSign);
                        returnMap.put("dayCount",forumUserSign.getCount());
                        return AjaxResult.success("????????????!",returnMap);
                    }else{
                        //?????????????????????????????????

                        forumUserSign.setSignTime(new Date());
                        forumUserSign.setCount(1);
                        forumUserService.updateUserSign(forumUserSign);
                        returnMap.put("dayCount",1);
                        return AjaxResult.success("????????????!",returnMap);
                    }
                }
            }
        }else{
            return AjaxResult.error("?????????!");
        }

    }

    /**
     * ????????????
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
     * ????????????
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
     * ????????????
     * ???????????????????????????????????????
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
        form.setFront(1);//????????????
        //startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(form);
        list.forEach(a->{
            //?????????
            int m=forumCommentService.selectCommentCount(a.getId().toString());
            a.setCommentNum(m);
        });
        list.forEach(a->{
            //????????????
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
     * ????????????
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
        form.setFront(1);//????????????
        startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(form);

        list.forEach(a->{
            //?????????
            int m=forumCommentService.selectCommentCount(a.getId().toString());
            a.setCommentNum(m);
        });
        list.forEach(a->{
            //????????????
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
        form.setFront(1);//????????????
        startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(form);

        list.forEach(a->{
            //?????????
            int m=forumCommentService.selectCommentCount(a.getId().toString());
            a.setCommentNum(m);
        });
        list.forEach(a->{
            //????????????
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
    @Log(title = "??????", businessType = BusinessType.DELETE)
    @PostMapping( "/forumQuestion/setTop/{id}")
    @ResponseBody
    public AjaxResult setTop(@PathVariable("id")String id)
    {
        Map<String,Object> returnMap=new HashMap<String,Object>();
        returnMap.put("id",id);
        int n=forumQuestionService.setTop(id);
        return n>0?AjaxResult.success("??????????????????!",returnMap):error("??????????????????!");
    }
    @Log(title = "?????????", businessType = BusinessType.DELETE)
    @PostMapping( "/forumQuestion/setRecommend/{id}")
    @ResponseBody
    public AjaxResult setRecommend(@PathVariable("id")String id)
    {
        Map<String,Object> returnMap=new HashMap<String,Object>();
        returnMap.put("id",id);
        int n=forumQuestionService.setRecommend(id);
        return n>0?AjaxResult.success("?????????????????????!",returnMap):error("?????????????????????!");
    }
    @Log(title = "?????????", businessType = BusinessType.DELETE)
    @PostMapping( "/forumQuestion/setGood/{id}")
    @ResponseBody
    public AjaxResult setGood(@PathVariable("id")String id)
    {
        Map<String,Object> returnMap=new HashMap<String,Object>();
        returnMap.put("id",id);
        int n=forumQuestionService.setGood(id);
        return n>0?AjaxResult.success("?????????????????????!",returnMap):error("?????????????????????!");
    }


    /**
     * ????????????
     */
    @Log(title = "????????????", businessType = BusinessType.INSERT)
    @PostMapping( "/forumQuestion/addFocus/{focus_user_id}")
    @ResponseBody
    public AjaxResult addFocus(@PathVariable("focus_user_id")String focus_user_id)
    {
        String yhid=ShiroUtils.getUserId().toString();
        if(focus_user_id.equals(yhid)){
            //?????????????????????
            return AjaxResult.error("?????????????????????!");
        }
        //????????????????????????
        Integer focusCount=forumUserService.selectFocusFlag(yhid,focus_user_id);
        if(focusCount>0){
            //????????????
            return AjaxResult.error("???????????????!");
        }

        return toAjax(forumUserService.addFocus(yhid,focus_user_id));
    }
    /**
     * ????????????
     */
    @Log(title = "????????????", businessType = BusinessType.DELETE)
    @PostMapping( "/forumQuestion/removeFocus/{focus_user_id}")
    @ResponseBody
    public AjaxResult removeFocus(@PathVariable("focus_user_id")String focus_user_id)
    {
        String yhid=ShiroUtils.getUserId().toString();
        if(focus_user_id.equals(yhid)){
            //?????????????????????
            return AjaxResult.error("???????????????????????????!");
        }
        return toAjax(forumUserService.removeFocus(yhid,focus_user_id));
    }
    /**
     * ??????????????????
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
     * ????????????
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
     * ??????????????????
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
     * Ta?????????
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
     * ???????????????????????????(Ta?????????)
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
