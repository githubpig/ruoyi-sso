package com.ruoyi.forum.job;

import com.ruoyi.cms.service.CmsService;
import com.ruoyi.forum.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 方便手工清除前台缓存
 */
@Component("clearForumCacheJob")
public class ClearForumCacheJob {

    @Autowired
    ForumService forumService;

    public void clearCache()
    {
        System.out.println("==========触发清空论坛缓存===========");
        forumService.clearCache();
    }
}
