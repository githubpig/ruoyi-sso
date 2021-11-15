package com.ruoyi.blog.job;

import com.ruoyi.cms.domain.FriendLink;
import com.ruoyi.cms.service.CmsService;
import com.ruoyi.cms.service.IFriendLinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * 每天凌晨0点检查友链能否正常打开，否则设置该友链状态为不可用
 */
@Component("openFriendLinkJob")
public class OpenFriendLinkJob {
    private static final Logger log = LoggerFactory.getLogger(OpenFriendLinkJob.class);

    @Autowired
    CmsService cmsService;

    @Autowired
    IFriendLinkService friendLinkService;

    private static boolean openUrlWithTimeOut(String urlString){
        long lo = System.currentTimeMillis();
        int timeOutMillSeconds=5000;
        URL url;
        try {
            url = new URL(urlString);
            URLConnection co =  url.openConnection();
            co.setConnectTimeout(timeOutMillSeconds);
            co.connect();
            System.out.println("连接可用");
            return true;
        } catch (Exception e1) {
            System.out.println("连接打不开!");
            url = null;
        }
        System.out.println(System.currentTimeMillis()-lo);
        return false;
    }

    /**
     * 测试友链是否正常能打开，否则将状态改为不可用
     */
    public void testOpenFriendLink()
    {
        boolean flag=false;
        FriendLink friendLink=new FriendLink();
        friendLink.setAuditState(1);
        List<FriendLink> list = friendLinkService.selectFriendLinkList(friendLink);
        for(FriendLink link:list){
            boolean b=openUrlWithTimeOut(link.getLink());
            if(!b){
                flag=true;
                link.setAuditState(0);
                friendLinkService.updateFriendLink(link);
            }
        }
        if(flag){
            cmsService.clearCache();
        }
    }
}
