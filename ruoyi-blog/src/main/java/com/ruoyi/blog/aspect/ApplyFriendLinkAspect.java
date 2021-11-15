package com.ruoyi.blog.aspect;

import com.ruoyi.cms.config.CmsConfig;
import com.ruoyi.cms.domain.FriendLink;
import com.ruoyi.cms.service.IEmailService;
import com.ruoyi.cms.util.CmsConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.TimeUuidUtil;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.domain.CostTime;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 申请友情链接邮件通知管理员切面
 *
 */
@Aspect
@Component
public class ApplyFriendLinkAspect {
    @Autowired
    IEmailService emailService;
    // 配置织入点
    @Pointcut("execution(* com.ruoyi.blog.controller.BlogController.saveFriendLink(..))")
    public void afterApplyFriendLink(){}

    @AfterReturning(returning = "ret", pointcut = "afterApplyFriendLink()")
    public void doAfterApplyFriendLink(Object ret) throws Throwable {
        // 处理完请求，返回内容
        AjaxResult result=(AjaxResult)ret;
        if(result.isSuccess()){
            FriendLink friendLink=(FriendLink) result.get("data");

            String email= CmsConfig.getFromEmail();
            String[] toEmails={email};//给管理员发送邮件
            Map<String,String> params=new HashMap<>();
            params.put("#name#",friendLink.getName());
            params.put("#email#",friendLink.getEmail());
            params.put("#link#",friendLink.getLink());
            params.put("#description#",friendLink.getDescription());
            boolean flag=emailService.sendEmailByTemplate(CmsConstants.KEY_USER_APPLY_FRIEND_LINK,toEmails,params);
            if(flag){
                System.out.println("申请友情链接通知管理员成功!");
            }

        }
    }
}
