package com.ruoyi.forum.controller;

import java.util.List;
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
import com.ruoyi.forum.domain.ForumTags;
import com.ruoyi.forum.service.IForumTagsService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 论坛标签Controller
 * 
 * @author ruoyi
 * @date 2021-03-12
 */
@Controller
@RequestMapping("/forum/tags")
public class ForumTagsController extends BaseController
{
    private String prefix = "forum/tags";

    @Autowired
    private IForumTagsService forumTagsService;

    @RequiresPermissions("forum:tags:view")
    @GetMapping()
    public String tags()
    {
        return prefix + "/tags";
    }

    /**
     * 查询论坛标签列表
     */
    @RequiresPermissions("forum:tags:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ForumTags forumTags)
    {
        startPage();
        List<ForumTags> list = forumTagsService.selectForumTagsList(forumTags);
        return getDataTable(list);
    }

    /**
     * 导出论坛标签列表
     */
    @RequiresPermissions("forum:tags:export")
    @Log(title = "论坛标签", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ForumTags forumTags)
    {
        List<ForumTags> list = forumTagsService.selectForumTagsList(forumTags);
        ExcelUtil<ForumTags> util = new ExcelUtil<ForumTags>(ForumTags.class);
        return util.exportExcel(list, "tags");
    }

    /**
     * 新增论坛标签
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存论坛标签
     */
    @RequiresPermissions("forum:tags:add")
    @Log(title = "论坛标签", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ForumTags forumTags)
    {
        return toAjax(forumTagsService.insertForumTags(forumTags));
    }

    /**
     * 修改论坛标签
     */
    @GetMapping("/edit/{tagId}")
    public String edit(@PathVariable("tagId") Long tagId, ModelMap mmap)
    {
        ForumTags forumTags = forumTagsService.selectForumTagsById(tagId);
        mmap.put("forumTags", forumTags);
        return prefix + "/edit";
    }

    /**
     * 修改保存论坛标签
     */
    @RequiresPermissions("forum:tags:edit")
    @Log(title = "论坛标签", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ForumTags forumTags)
    {
        return toAjax(forumTagsService.updateForumTags(forumTags));
    }

    /**
     * 删除论坛标签
     */
    @RequiresPermissions("forum:tags:remove")
    @Log(title = "论坛标签", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(forumTagsService.deleteForumTagsByIds(ids));
    }
}
