package com.ruoyi.forum.controller;

import java.util.List;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.forum.domain.ForumReport;
import com.ruoyi.forum.service.IForumReportService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 举报信息Controller
 * 
 * @author ruoyi
 * @date 2021-03-15
 */
@Controller
@RequestMapping("/forum/report")
public class ForumReportController extends BaseController
{
    private String prefix = "forum/report";

    @Autowired
    private IForumReportService forumReportService;


    @RequiresPermissions("forum:report:view")
    @GetMapping()
    public String report()
    {
        return prefix + "/report";
    }

    /**
     * 查询举报信息列表
     */
    @RequiresPermissions("forum:report:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ForumReport forumReport)
    {
        startPage();
        List<ForumReport> list = forumReportService.selectForumReportList(forumReport);
        return getDataTable(list);
    }

    /**
     * 导出举报信息列表
     */
    @RequiresPermissions("forum:report:export")
    @Log(title = "举报信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ForumReport forumReport)
    {
        List<ForumReport> list = forumReportService.selectForumReportList(forumReport);
        ExcelUtil<ForumReport> util = new ExcelUtil<ForumReport>(ForumReport.class);
        return util.exportExcel(list, "report");
    }

    /**
     * 新增举报信息
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }
    /**
     * 新增保存举报信息
     */
    @RequiresPermissions("forum:report:add")
    @Log(title = "举报信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ForumReport forumReport)
    {

        return toAjax(forumReportService.insertForumReport(forumReport));
    }
    /**
     * 新增保存举报信息
     */
    @Log(title = "举报信息", businessType = BusinessType.INSERT)
    @PostMapping("/addReport/{targetId}")
    @ResponseBody
    public AjaxResult addReport(@PathVariable("targetId") String targetId,ForumReport forumReport)
    {
        String userId= ShiroUtils.getUserId().toString();

        int count=forumReportService.selectExist(targetId,userId);
        if(count>0){
            return AjaxResult.error("您已举报过!");
        }
        int n=forumReportService.insertForumReport(forumReport);
        return toAjax(n);
    }

    /**
     * 修改举报信息
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        ForumReport forumReport = forumReportService.selectForumReportById(id);
        mmap.put("forumReport", forumReport);
        return prefix + "/edit";
    }

    /**
     * 修改保存举报信息
     */
    @RequiresPermissions("forum:report:edit")
    @Log(title = "举报信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ForumReport forumReport)
    {
        return toAjax(forumReportService.updateForumReport(forumReport));
    }

    /**
     * 删除举报信息
     */
    @RequiresPermissions("forum:report:remove")
    @Log(title = "举报信息", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(forumReportService.deleteForumReportByIds(ids));
    }

    @Log(title = "处理举报信息", businessType = BusinessType.UPDATE)
    @PostMapping("/dealForumReport")
    @ResponseBody
    public AjaxResult dealForumReport(ForumReport forumReport)
    {
        return toAjax(forumReportService.dealForumReport(forumReport));
    }

}
