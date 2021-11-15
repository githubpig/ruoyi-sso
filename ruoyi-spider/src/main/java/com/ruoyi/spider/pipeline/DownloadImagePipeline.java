package com.ruoyi.spider.pipeline;

import com.ruoyi.common.config.Global;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DownloadImagePipeline implements Pipeline {

    /**
     * 把字段是网络图片的下载到本地(而文章内容有网络图片的情况不做下载处理)
     * @param resultItems
     * @param task
     */
    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String,Object> map = resultItems.getAll();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key=entry.getKey();
            String value=String.valueOf(entry.getValue());
            if(FileUploadUtils.isImageUrl(value)){//是网络图片路径
                if(FileUploadUtils.checkImageUrlExists(value)){//网络图片存在
                    String path = downloadImage(value);
                    resultItems.getAll().put(key,path.equals("error")?value:path);//重新赋值
                }
            }
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
