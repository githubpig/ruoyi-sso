package com.ruoyi.forum.aspect;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.CookieUtils;
import com.ruoyi.common.utils.Guid;
import com.ruoyi.forum.domain.ForumIn;
import com.ruoyi.forum.domain.ForumMessage;
import com.ruoyi.forum.domain.ForumQuestion;
import com.ruoyi.forum.service.IForumInService;
import com.ruoyi.forum.service.IForumMessageService;
import com.ruoyi.forum.service.IForumQuestionService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 每日签到切面
 */
@Aspect
@Component("forumScoreAspect")
public class ScoresAspect {

    @Autowired
    ISysUserService userService;
    @Autowired
    IForumInService forumInService;
    @Autowired
    IForumQuestionService forumQuestionService;
    @Autowired
    IForumMessageService forumMessageService;
    /**
     * 每日签到
     */
    @Pointcut("execution(* com.ruoyi.forum.controller.ForumController.userSign(..))")
    public void daySign() {
    }
    /**
     * 每日登陆
     */
    @Pointcut("execution(* com.ruoyi.forum.controller.ForumController.frontLogin(..))||execution(* com.ruoyi.*.controller.system.SysLoginController.ajaxLogin(..))")
    public void login() {
    }

    /**
     * 发的帖子被审核通过
     */
    @Pointcut("execution(* com.ruoyi.forum.controller.ForumQuestionController.batchPublish(..))")
    public void batchPublish() {
    }
    /**
     * 删除发帖
     */
    @Pointcut("execution(* com.ruoyi.forum.controller.ForumQuestionController.remove(..))")
    public void removeQuestion() {
    }

    @Pointcut("execution(* com.ruoyi.forum.controller.ForumController.setRecommend(..))")
    public void setRecommend() {
    }
    @Pointcut("execution(* com.ruoyi.forum.controller.ForumController.setGood(..))")
    public void setGood() {
    }
    @Pointcut("execution(* com.ruoyi.forum.controller.ForumController.setTop(..))")
    public void setTop() {
    }
    private static final Integer FORUM_IN_TYPE_LOGIN=1;//每日登录
    private static final Integer FORUM_IN_TYPE_SIGN=2;//每日签到
    private static final Integer FORUM_IN_TYPE_QUESTION=3;//发帖提问题
    private static final Integer FORUM_IN_TYPE_RECOMMEND=4;//发帖至为普通推荐
    private static final Integer FORUM_IN_TYPE_GOOD=5;//发帖至为精品
    private static final Integer FORUM_IN_TYPE_TOP=6;//发帖至顶
    @AfterReturning(returning = "ret", pointcut = "daySign()")
    public void doAfterSign(Object ret) throws Throwable {
        // 处理完请求，返回内容
        AjaxResult result = (AjaxResult) ret;
        if (result.isSuccess()) {
            Map<String, Object> m = (Map<String, Object>) result.get("data");
            int n = (Integer) m.get("dayCount");
            int score = 0;
            if (n == 1) {
                score = 2;
            } else if (n == 2) {
                score = 4;
            } else if (n == 3) {
                score = 6;
            } else if (n == 4) {
                score = 8;
            } else if (n >= 5) {
                score = 10;
            } else {
            }
            SysUser user = ShiroUtils.getSysUser();
            user.setScore(user.getScore() + score);
            userService.updateUserInfo(user);
            ShiroUtils.setSysUser(userService.selectUserById(user.getUserId()));

            //记录入账信息
            ForumIn forumIn=new ForumIn();
            forumIn.setUserId(user.getUserId().toString());
            forumIn.setForumInType(FORUM_IN_TYPE_SIGN);
            forumIn.setAmount(score+"积分");
            forumIn.setDescription("连续签到"+n+"天");
            forumInService.insertForumIn(forumIn);
        }
    }

    @AfterReturning(returning = "ret", pointcut = "login()")
    public void login(Object ret) throws Throwable {
        // 处理完请求，返回内容
        AjaxResult result = (AjaxResult) ret;
        if (result.isSuccess()) {
            Map<String, Object> m = (Map<String, Object>) result.get("data");
            Boolean todayLogin = (Boolean) m.get("todayLogin");//true表示今天第一次登录
            if(todayLogin){
                SysUser user = ShiroUtils.getSysUser();
                //记录入账信息
                ForumIn forumIn=new ForumIn();
                forumIn.setUserId(user.getUserId().toString());
                forumIn.setForumInType(FORUM_IN_TYPE_LOGIN);
                forumIn.setAmount("1积分");
                forumIn.setDescription("");
                forumInService.insertForumIn(forumIn);


                user.setScore(user.getScore()+1);
                userService.updateUserInfo(user);
                ShiroUtils.setSysUser(userService.selectUserById(user.getUserId()));
            }

            SysUser user = ShiroUtils.getSysUser();
            user.setLastLoginTime(new Date());
            userService.updateUserInfo(user);
            ShiroUtils.setSysUser(userService.selectUserById(user.getUserId()));
        }
    }

    //帖子审核通过+5积分
    @AfterReturning(returning = "ret", pointcut = "batchPublish()")
    public void doAfterBatchPublish(Object ret) throws Throwable {
        // 处理完请求，返回内容
        AjaxResult result = (AjaxResult) ret;
        if (result.isSuccess()) {

                List<String> idList = (List<String>) result.get("data");
                for(String id:idList){
                    ForumQuestion forumQuestion=forumQuestionService.selectForumQuestionById(id);
                    //记录入账信息
                    ForumIn forumIn=new ForumIn();
                    forumIn.setUserId(forumQuestion.getYhid());
                    forumIn.setForumInType(FORUM_IN_TYPE_QUESTION);
                    forumIn.setAmount("5积分");
                    forumIn.setDescription("ID:"+id);
                    forumInService.insertForumIn(forumIn);

                    SysUser user = userService.selectUserById(Long.valueOf(forumQuestion.getYhid()));
                    user.setScore(user.getScore()+5);
                    userService.updateUserInfo(user);


                    //发系统消息给用户
                    ForumMessage message=new ForumMessage();
                    message.setToId(forumQuestion.getYhid());
                    message.setToName(user.getUserName());
                    message.setContent("您有帖子被系统管理员审核通过!获得5积分。帖子ID:"+id);
                    message.setType("system");
                    message.setReadFlag(0);
                    message.setCreateTime(new Date());
                    message.setTitle("通知");
                    forumMessageService.insertForumMessage(message);

                }

        }
    }

    @AfterReturning(returning = "ret", pointcut = "setRecommend()")
    public void doAfterSetRecommend(Object ret) throws Throwable {
        // 处理完请求，返回内容
        AjaxResult result = (AjaxResult) ret;
        if (result.isSuccess()) {

            Map<String, Object> m = (Map<String, Object>) result.get("data");
            String id=(String) m.get("id");//帖子id
            ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(id);
            String yhid=forumQuestion.getYhid();
            //记录入账信息
            ForumIn forumIn=new ForumIn();
            forumIn.setUserId(yhid);
            forumIn.setForumInType(FORUM_IN_TYPE_RECOMMEND);
            forumIn.setAmount("5积分");//推荐+5分
            forumIn.setDescription("ID:"+id);
            forumInService.insertForumIn(forumIn);

            SysUser user = userService.selectUserById(Long.valueOf(yhid));
            user.setScore(user.getScore()+5);
            userService.updateUserInfo(user);


            //发系统消息给用户
            ForumMessage message=new ForumMessage();
            message.setToId(yhid);
            message.setToName(user.getUserName());
            message.setContent("您有帖子被系统管理员置顶!获得5积分。帖子ID:"+id);
            message.setType("system");
            message.setReadFlag(0);
            message.setCreateTime(new Date());
            message.setTitle("通知");
            forumMessageService.insertForumMessage(message);

        }
    }

    @AfterReturning(returning = "ret", pointcut = "setGood()")
    public void doAfterSetGood(Object ret) throws Throwable {
        // 处理完请求，返回内容
        AjaxResult result = (AjaxResult) ret;
        if (result.isSuccess()) {

            Map<String, Object> m = (Map<String, Object>) result.get("data");
            String id=(String) m.get("id");
            ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(id);
            String yhid=forumQuestion.getYhid();
            //记录入账信息
            ForumIn forumIn=new ForumIn();
            forumIn.setUserId(yhid);
            forumIn.setForumInType(FORUM_IN_TYPE_GOOD);
            forumIn.setAmount("10积分");//精品+10分
            forumIn.setDescription("ID:"+id);
            forumInService.insertForumIn(forumIn);

            SysUser user = userService.selectUserById(Long.valueOf(yhid));
            user.setScore(user.getScore()+10);
            userService.updateUserInfo(user);

            //发系统消息给用户
            ForumMessage message=new ForumMessage();
            message.setToId(yhid);
            message.setToName(user.getUserName());
            message.setContent("您有帖子被系统管理员置顶!获得10积分。帖子ID:"+id);
            message.setType("system");
            message.setReadFlag(0);
            message.setCreateTime(new Date());
            message.setTitle("通知");
            forumMessageService.insertForumMessage(message);

        }
    }

    @AfterReturning(returning = "ret", pointcut = "setTop()")
    public void doAfterSetTop(Object ret) throws Throwable {
        // 处理完请求，返回内容
        AjaxResult result = (AjaxResult) ret;
        if (result.isSuccess()) {

            Map<String, Object> m = (Map<String, Object>) result.get("data");
            String id=(String) m.get("id");
            ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(id);
            String yhid=forumQuestion.getYhid();
            //记录入账信息
            ForumIn forumIn=new ForumIn();
            forumIn.setUserId(yhid);
            forumIn.setForumInType(FORUM_IN_TYPE_TOP);
            forumIn.setAmount("15积分");//置顶+15分
            forumIn.setDescription("ID:"+id);
            forumInService.insertForumIn(forumIn);

            SysUser user = userService.selectUserById(Long.valueOf(yhid));
            user.setScore(user.getScore()+15);
            userService.updateUserInfo(user);
            //发系统消息给用户
            ForumMessage message=new ForumMessage();
            message.setToId(yhid);
            message.setToName(user.getUserName());
            message.setContent("您有帖子被系统管理员置顶!获得15积分。帖子ID:"+id);
            message.setType("system");
            message.setReadFlag(0);
            message.setCreateTime(new Date());
            message.setTitle("通知");
            forumMessageService.insertForumMessage(message);
        }
    }
}
