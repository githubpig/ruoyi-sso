package com.ruoyi.spider.pipeline.component;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.service.IGeneralService;
import org.apache.commons.collections.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理抓取的gitee捐赠用户列表
 */
@Component
public class GiteeDonatePipeline  implements Pipeline {

    private IGeneralService generalService= SpringUtils.getBean(IGeneralService.class);
    private static final String tableName="cms_donate";
    @Override
    public void process(ResultItems resultItems, Task task) {
        String tableName="cms_donate";
        Map<String,Object> data = resultItems.getAll();
        String html=String.valueOf(data.get("list"));
        html=html.replaceAll("\\n","");
        Document document= Jsoup.parse(html);
        if(document!=null){
            Elements rows=document.select("tbody > tr");
            for(Element row:rows){
                Map<String,Object> map=new HashMap<>();
                Elements tds=row.select("td");
                String amount=tds.get(0).html();
                String account=tds.get(2).children().first().html();
                String msg=tds.get(3).html();
                String donate_time=tds.get(4).html();
                map.put("platform","gitee");
                map.put("uid",account);
                map.put("amount",amount);
                map.put("msg",msg);
                map.put("donate_time",donate_time);
                map.put("create_time", DateUtils.getTime());
                /*String avatar="";
                String url="https://gitee.com/"+account;
                Document avatarDoc=Jsoup.parse(url);
                if(avatarDoc!=null){
                    avatar=avatarDoc.select("div.users__personal-avatar > a > img").first().attr("src");
                }
                map.put("avatar",avatar);*/
                //先查询数据库
                Map queryMap=new HashMap();
                queryMap.put("uid",account);
                queryMap.put("donate_time",donate_time);
                List<Map> list=generalService.selectByMap(tableName,queryMap);
                if(CollectionUtils.isEmpty(list)){
                    generalService.generalInsert(tableName,map);
                }

            }

        }
    }
}
