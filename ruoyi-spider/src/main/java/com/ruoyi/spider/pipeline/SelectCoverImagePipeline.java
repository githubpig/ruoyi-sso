package com.ruoyi.spider.pipeline;

import com.ruoyi.common.config.Global;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.util.HtmlUtils;
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

/**
 * 抽取内容详情的第一张图片作为封面图片
 */
public class SelectCoverImagePipeline implements Pipeline {

    private static final String CONTENT_FIELD="content,detail";
    private static final String COVER_IMG_FIELD="cover_img";
    /**
     * @param resultItems
     * @param task
     */
    @Override
    public void process(ResultItems resultItems, Task task) {
        String[] fields=CONTENT_FIELD.split(",");
        Map<String,Object> data = resultItems.getAll();
        String coverImg="";
        for(String field:fields) {
            if (data.containsKey(field)) {
                if(StringUtils.isEmpty(coverImg)){
                    String value = String.valueOf(data.get(field));
                    if(StringUtils.isNotEmpty(value)){
                        Document doc = Jsoup.parse(value);
                        if (doc != null) {
                            List<String> imgUrls = new ArrayList<String>();
                            Elements elements = doc.select("img");
                            if (elements != null && elements.size() > 0) {
                                Element first = elements.first();
                                coverImg = first.attr("src");
                            }
                        }
                    }
                }
            }
        }
        if(StringUtils.isNotEmpty(coverImg)&&coverImg.startsWith(Constants.RESOURCE_PREFIX)){//此时在内容中已经下载了该图片
            resultItems.getAll().put(COVER_IMG_FIELD,coverImg);
            return;
        }
        if(StringUtils.isNotEmpty(coverImg)&&FileUploadUtils.isImageUrl(coverImg)&&FileUploadUtils.checkImageUrlExists(coverImg)){
            String newPath=downloadImage(coverImg);
            resultItems.getAll().put(COVER_IMG_FIELD,newPath.equals("error")?coverImg:newPath);
        }

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
