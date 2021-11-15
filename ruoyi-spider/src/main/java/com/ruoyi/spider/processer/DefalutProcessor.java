package com.ruoyi.spider.processer;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.spider.MyConfigurableSpider;
import com.ruoyi.spider.domain.SpiderConfig;
import com.ruoyi.spider.domain.SpiderField;
import com.ruoyi.spider.downloader.HttpClientDownloader;
import com.ruoyi.spider.pipeline.DownloadContentImagePipeline;
import com.ruoyi.spider.pipeline.DownloadImagePipeline;
import com.ruoyi.spider.pipeline.GeneralDbPipeline;
import com.ruoyi.spider.pipeline.SelectCoverImagePipeline;
import com.ruoyi.spider.scheduler.CountDownScheduler;
import com.ruoyi.system.service.IGeneralService;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 爬虫入口
 */
public class DefalutProcessor extends AbstractProcessor {

    private IGeneralService generalService= SpringUtils.getBean(IGeneralService.class);

    public DefalutProcessor(SpiderConfig config) {
        super(config);
    }

    public DefalutProcessor(SpiderConfig config, String uuid) {
        super(config, uuid);
    }

    /**
     * 运行爬虫并返回结果
     *
     * @return
     */
    @Override
    public CopyOnWriteArrayList<LinkedHashMap<String, String>> execute() {
        List<String> errors = this.validateModel(config);
        if (CollectionUtils.isNotEmpty(errors)) {
            logger.warn("校验不通过！请依据下方提示，检查输入参数是否正确......");
            for (String error : errors) {
                logger.warn(">> " + error);
            }
            return null;
        }
        List<SpiderField> fields = config.getFieldsList();
        if(CollectionUtils.isEmpty(fields)){
            logger.warn("校验不通过！爬虫字段对应规则未配置!!!");
            return null;
        }
        //设置用户自定义pipeline
        if(StringUtils.isNotEmpty(config.getUserDefinePipeline())){
            String[] arr=config.getUserDefinePipeline().split(",");
            if(arr!=null&&arr.length>0){
                for(String str:arr){
                    if(StringUtils.isNotEmpty(str)){
                        char[]chars = str.toCharArray();
                        chars[0] += 32;
                        str=String.valueOf(chars);
                        Pipeline pipeline = SpringUtils.getBean(str);
                       if(pipeline!=null){
                           config.addPipeline(pipeline);
                       }
                    }
                }
            }
        }
        CopyOnWriteArrayList<LinkedHashMap<String, String>> datas = new CopyOnWriteArrayList<>();
        MyConfigurableSpider spider = MyConfigurableSpider.create(this, config, uuid);

        spider.addUrl(config.getEntryUrlsList().toArray(new String[0]))
                .setPipelines(config.getPipelineList())
                .setDownloader(new HttpClientDownloader())
                .setScheduler(new CountDownScheduler(config))
                .thread(config.getThreadCount().intValue())
                .addPipeline((resultItems, task) -> this.processData(resultItems, datas, spider)); // 收集数据

        //设置抓取代理IP
        if (config.getUseProxy()==1 && !CollectionUtils.isEmpty(config.getProxyList())) {
            HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
            SimpleProxyProvider provider = SimpleProxyProvider.from(config.getProxyList().toArray(new Proxy[0]));
            httpClientDownloader.setProxyProvider(provider);
            spider.setDownloader(httpClientDownloader);
        }
        //高级设置1:DownloadImagePipeline  2:DownloadContentImagePipeline  3:SelectCoverImagePipeline
        if(StringUtils.isNotEmpty(config.getSpiderHighSetting())){
            String[] arr=config.getSpiderHighSetting().split(",");
            List<String> list=Arrays.asList(arr);
            if(list.contains("1")){
                //下载单张图片到本地
                spider.addPipeline(new DownloadImagePipeline());
            }
            if(list.contains("2")){
                //下载内容详情里的图片到本地
                spider.addPipeline(new DownloadContentImagePipeline());
            }
            if(list.contains("3")){
                //选取内容详情图片的第一张作为封面图片
                spider.addPipeline(new SelectCoverImagePipeline());
            }
        }

        //控制台输出管道
        if(config.getShowLog()==1){
            spider.addPipeline(new ConsolePipeline());
        }
        //数据库输出管道
        if(config.getSaveDb()==1){
            spider.addPipeline(new GeneralDbPipeline(config.getTableName(),generalService));
        }

        // 启动爬虫
        spider.run();
        return datas;
    }


}
