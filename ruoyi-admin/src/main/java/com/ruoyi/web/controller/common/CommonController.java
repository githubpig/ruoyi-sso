package com.ruoyi.web.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.cms.domain.Attachment;
import com.ruoyi.cms.service.IAttachmentService;
import com.ruoyi.common.utils.Guid;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.spider.pipeline.DownloadImagePipeline;
import com.ruoyi.spider.pipeline.component.ComponentPipelineManager;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.IGeneralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.config.ServerConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用请求处理
 *
 * @author ruoyi
 */
@Controller
public class CommonController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private IAttachmentService attachmentService;
    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("common/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response)
    {
        try
        {
            if (!FileUtils.checkAllowDownload(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }

            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = Global.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);

            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求
     */
    @PostMapping("/common/upload")
    @ResponseBody
    public AjaxResult uploadFile(MultipartFile file, HttpServletRequest request) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = Global.getUploadPath();
            // 上传并返回新文件名称
            String url = FileUploadUtils.upload(filePath, file);
            //String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("fileName", file.getOriginalFilename());
            ajax.put("url", url);
            String attachmentFlag=request.getParameter("attachment");
            if("1".equals(attachmentFlag)||"true".equals(attachmentFlag)){
                Attachment attachment=new Attachment();
                //String guid=Guid.get();
                //attachment.setAttachId(guid);
                SysUser user= ShiroUtils.getSysUser();
                attachment.setUserId(user.getUserId().toString());
                String zid=request.getParameter("zid");
                if(StringUtils.isEmpty(zid)){
                    zid="";
                }
                attachment.setZid(zid);
                attachment.setFileType(FileUploadUtils.getExtension(file));
                attachment.setFileName(file.getName());
                attachment.setFileUrl(url);
                attachment.setSize(file.getSize());
                attachmentService.insertAttachment(attachment);
                ajax.put("attachment", "1");
                ajax.put("id", attachment.getAttachId());
            }
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }
    @PostMapping("/common/upload/get")
    @ResponseBody
    public AjaxResult getUploadFile(HttpServletRequest request) throws Exception
    {
        String id=request.getParameter("id");
        if(StringUtils.isNotEmpty(id)){
          Attachment attachment = attachmentService.selectAttachmentById(id);
          if(attachment!=null){
              Map<String,Object> data=new HashMap();
              data.put("url",attachment.getFileUrl());
              return AjaxResult.success(data);
          }
        }
        return AjaxResult.error();
    }
    @PostMapping("/common/upload/delete")
    @ResponseBody
    public AjaxResult deleteUploadFile(HttpServletRequest request)
    {
        String id=request.getParameter("id");
        if(StringUtils.isNotEmpty(id)){
           Attachment attachment = attachmentService.selectAttachmentById(id);
           if(attachment!=null){
               String url=attachment.getFileUrl();
               try {
                   FileUploadUtils.deleteFile(url);
               } catch (Exception e) {
                   e.printStackTrace();
               }
               attachmentService.deleteAttachmentById(id);
           }
        }else{
            String url=request.getParameter("url");
            if(StringUtils.isNotEmpty(url)){
                try {
                    FileUploadUtils.deleteFile(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return AjaxResult.success();
    }
    /**
     * 本地资源通用下载
     */
    @GetMapping("/common/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        if (!FileUtils.checkAllowDownload(resource))
        {
            throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", resource));
        }
        // 本地资源路径
        String localPath = Global.getProfile();
        // 数据库资源地址
        String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
        // 下载名称
        String downloadName = StringUtils.substringAfterLast(downloadPath, "/");

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileUtils.setAttachmentResponseHeader(response, downloadName);

        FileUtils.writeBytes(downloadPath, response.getOutputStream());
    }
    @Autowired
    IGeneralService generalService;
    @GetMapping("/common/test")
    @ResponseBody
    public AjaxResult test(String tableName,String columnName){
        boolean b =  generalService.isAutoColumn(tableName,columnName);
        List<String> list= generalService.selectPrimaryKeys(tableName);
        Map data=new HashMap();
        data.put("b",b);
        data.put("list",list);
        return  AjaxResult.success(data);
    }
    @GetMapping("/common/test2")
    @ResponseBody
    public AjaxResult test2(String url){
        DownloadImagePipeline.downloadImage(url);
        return  AjaxResult.success(url);
    }
    @GetMapping("/common/test3")
    @ResponseBody
    public AjaxResult test3(String url){
        return  AjaxResult.success(ComponentPipelineManager.getPipelineList());
    }
}
