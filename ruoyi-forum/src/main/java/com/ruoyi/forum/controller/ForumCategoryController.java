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
import com.ruoyi.forum.domain.ForumCategory;
import com.ruoyi.forum.service.IForumCategoryService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 论坛栏目Controller
 * 
 * @author ruoyi
 * @date 2021-03-17
 */
@Controller
@RequestMapping("/forum/category")
public class ForumCategoryController extends BaseController
{
    private String prefix = "forum/category";

    @Autowired
    private IForumCategoryService forumCategoryService;

    @RequiresPermissions("forum:category:view")
    @GetMapping()
    public String category()
    {
        return prefix + "/category";
    }

    /**
     * 查询论坛栏目列表
     */
    @RequiresPermissions("forum:category:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ForumCategory forumCategory)
    {
        startPage();
        List<ForumCategory> list = forumCategoryService.selectForumCategoryList(forumCategory);
        return getDataTable(list);
    }

    /**
     * 导出论坛栏目列表
     */
    @RequiresPermissions("forum:category:export")
    @Log(title = "论坛栏目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ForumCategory forumCategory)
    {
        List<ForumCategory> list = forumCategoryService.selectForumCategoryList(forumCategory);
        ExcelUtil<ForumCategory> util = new ExcelUtil<ForumCategory>(ForumCategory.class);
        return util.exportExcel(list, "category");
    }

    /**
     * 新增论坛栏目
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存论坛栏目
     */
    @RequiresPermissions("forum:category:add")
    @Log(title = "论坛栏目", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ForumCategory forumCategory)
    {
        return toAjax(forumCategoryService.insertForumCategory(forumCategory));
    }

    /**
     * 修改论坛栏目
     */
    @GetMapping("/edit/{categoryId}")
    public String edit(@PathVariable("categoryId") Long categoryId, ModelMap mmap)
    {
        ForumCategory forumCategory = forumCategoryService.selectForumCategoryById(categoryId);
        mmap.put("forumCategory", forumCategory);
        return prefix + "/edit";
    }

    /**
     * 修改保存论坛栏目
     */
    @RequiresPermissions("forum:category:edit")
    @Log(title = "论坛栏目", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ForumCategory forumCategory)
    {
        return toAjax(forumCategoryService.updateForumCategory(forumCategory));
    }

    /**
     * 删除论坛栏目
     */
    @RequiresPermissions("forum:category:remove")
    @Log(title = "论坛栏目", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(forumCategoryService.deleteForumCategoryByIds(ids));
    }
}
