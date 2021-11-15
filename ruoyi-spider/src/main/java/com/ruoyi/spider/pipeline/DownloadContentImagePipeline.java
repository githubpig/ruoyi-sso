package com.ruoyi.spider.pipeline;

import cn.hutool.core.thread.ThreadUtil;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.thread.MultiThreadHandler;
import com.ruoyi.common.utils.thread.exception.ChildThreadException;
import com.ruoyi.common.utils.thread.parallel.ParallelTaskWithThreadPool;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class DownloadContentImagePipeline implements Pipeline {

    private static final String CONTENT_FIELD="content,detail";
    /**
     * @param resultItems
     * @param task
     */
    @Override
    public void process(ResultItems resultItems, Task task) {
        String[] fields=CONTENT_FIELD.split(",");
        Map<String,Object> data = resultItems.getAll();

        for(String field:fields) {
            if (data.containsKey(field)) {
                String value = String.valueOf(data.get(field));
                if (StringUtils.isNotEmpty(value)) {//只下载文章正文的图片
                    Document doc = Jsoup.parse(value);
                    if (doc != null) {
                        List<String> imgUrls = new ArrayList<String>();
                        Elements srcLinks = doc.select("img");
                        for (Element link : srcLinks) {
                            //:剔除标签，只剩链接路径
                            String imagesPath = link.attr("src");
                            imgUrls.add(imagesPath);
                        }
                        if (CollectionUtils.isNotEmpty(imgUrls)) {
                            //多线程下载图片
                            String newValue = threadDownloadImage(imgUrls,value);
                            //重新赋值
                            resultItems.getAll().put(field,newValue);
                        }
                    }
                }
            }
        }
    }

    public String threadDownloadImage(List<String> imgUrls,String contentValue){
        ExecutorService service = ThreadUtil.newExecutor(3);
        MultiThreadHandler handler = new ParallelTaskWithThreadPool(service);
        Runnable task = null;
        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
        for(String url:imgUrls){
            task=new Runnable() {
                @Override
                public void run() {
                    if(FileUploadUtils.isImageUrl(url)) {//是网络图片路径
                        if (FileUploadUtils.checkImageUrlExists(url)) {//网络图片存在
                            String path = downloadImage(url);
                            resultMap.put(url,!"error".equals(path)?path:url);
                        }
                    }
                }
            };
            handler.addTask(task);
        }
        try {
            handler.run();
        } catch (ChildThreadException e) {
            e.printStackTrace();
        }
        service.shutdown();
        //表里result结果，把下载图片成功的图片替换成本地路径
        for(Map.Entry entry:resultMap.entrySet()){
            String key=String.valueOf(entry.getKey());
            String value=String.valueOf(entry.getValue());
            contentValue=contentValue.replaceAll(key,value);
        }
        return contentValue;
    }
    public static String downloadImage(String url){
        String fileName = extractFilename(url);
        String baseDir=Global.getUploadPath();
        File file = null;
        try {
            file = getAbsoluteFile(baseDir, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStream in = entity.getContent();
        try {
            FileOutputStream fout = new FileOutputStream(file);
            int l = -1;
            byte[] tmp = new byte[1024];
            while ((l = in.read(tmp)) != -1) {
                fout.write(tmp,0,l);
            }
            fout.flush();
            fout.close();
        } finally {
            in.close();
        }
        }catch (Exception ex){
            ex.printStackTrace();
            return "error";
        }
        String pathFileName = getPathFileName(baseDir, fileName);
        return pathFileName;
    }
    public static final String extractFilename(String url)
    {
        String name=url.substring(url.lastIndexOf("/")+1);
        String fileName = DateUtils.datePath() + "/" + name;
        return fileName;
    }
    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException
    {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.getParentFile().exists())
        {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists())
        {
            desc.createNewFile();
        }
        return desc;
    }
    public static final String getPathFileName(String uploadDir, String fileName)
    {
        int dirLastIndex = Global.getProfile().length() + 1;
        String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
        String pathFileName = Constants.RESOURCE_PREFIX + "/" + currentDir + "/" + fileName;
        return pathFileName;
    }
}
