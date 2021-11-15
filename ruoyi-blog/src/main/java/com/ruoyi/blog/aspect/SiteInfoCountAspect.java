package com.ruoyi.blog.aspect;

import com.ruoyi.cms.config.CmsConfig;
import com.ruoyi.cms.domain.FriendLink;
import com.ruoyi.cms.service.CmsService;
import com.ruoyi.cms.service.IEmailService;
import com.ruoyi.cms.util.CmsConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
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
public class SiteInfoCountAspect {
    @Autowired
    CmsService cmsService;

    @Pointcut("execution(* com.ruoyi.blog.controller.BlogController.saveComments(..))||execution(* com.ruoyi.blog.controller.BlogController.*View(..))||execution(* com.ruoyi.cms.controller.ArticleController.addSave(..))")
    public void siteInfoCount(){}
    @AfterReturning(returning = "ret", pointcut = "siteInfoCount()")
    public void doAfterApplyFriendLink(Object ret) throws Throwable {
        // 处理完请求，返回内容
        AjaxResult result=(AjaxResult)ret;
        if(result.isSuccess()){
            cmsService.clearCountCache();
        }
    }
}
