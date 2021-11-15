package com.ruoyi.blog.job;

import com.ruoyi.cms.service.CmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 方便手工清除前台缓存
 */
@Component("clearCacheJob")
public class ClearCacheJob {

    @Autowired
    CmsService cmsService;

    public void clearCache()
    {
        System.out.println("==========触发清空缓存===========");
        cmsService.clearCache();
    }
}
