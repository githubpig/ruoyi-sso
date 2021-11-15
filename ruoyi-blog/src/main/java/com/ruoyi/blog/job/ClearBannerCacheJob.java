package com.ruoyi.blog.job;

import com.ruoyi.blog.controller.BlogController;
import com.ruoyi.cms.service.CmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 方便手工清除前台缓存
 */
@Component("clearBannerCacheJob")
public class ClearBannerCacheJob {

    @Autowired
    BlogController blogController;

    public void clearBannerCache()
    {
        System.out.println("==========触发清空banner缓存===========");
        blogController.clearBannerCache();
    }
}
