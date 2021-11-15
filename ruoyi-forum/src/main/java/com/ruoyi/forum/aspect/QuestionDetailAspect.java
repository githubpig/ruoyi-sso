package com.ruoyi.forum.aspect;

import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.forum.domain.ForumQuestion;
import com.ruoyi.forum.service.IForumQuestionService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class QuestionDetailAspect {
    @Autowired
    IForumQuestionService forumQuestionService;
    /**
     * 帖子详情
     */
    @Pointcut("execution(* com.ruoyi.forum.controller.ForumController.questionDetail(..))")
    public void questionDetail() {
    }
    private static final int HIDE_TYPE_LOGIN=1;
    @Before("questionDetail()")
    public void doBefore(JoinPoint point) throws Throwable
    {
        Object[] params =  point.getArgs();
        String id=(String)params[0];
        ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(id);
        int n=forumQuestion.getHideType();
        SysUser user= ShiroUtils.getSysUser();
        if(user==null&&n==HIDE_TYPE_LOGIN){
            throw new BusinessException("请登陆后再浏览该帖子!");
        }
    }
}
