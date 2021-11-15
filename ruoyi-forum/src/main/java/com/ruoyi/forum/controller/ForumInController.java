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
import com.ruoyi.forum.domain.ForumIn;
import com.ruoyi.forum.service.IForumInService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 入账记录Controller
 * 
 * @author ruoyi
 * @date 2021-03-19
 */
@Controller
@RequestMapping("/forum/in")
public class ForumInController extends BaseController
{
    private String prefix = "forum/in";

    @Autowired
    private IForumInService forumInService;

    @RequiresPermissions("forum:in:view")
    @GetMapping()
    public String in()
    {
        return prefix + "/in";
    }

    /**
     * 查询入账记录列表
     */
    @RequiresPermissions("forum:in:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ForumIn forumIn)
    {
        startPage();
        List<ForumIn> list = forumInService.selectForumInList(forumIn);
        return getDataTable(list);
    }

    /**
     * 导出入账记录列表
     */
    @RequiresPermissions("forum:in:export")
    @Log(title = "入账记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ForumIn forumIn)
    {
        List<ForumIn> list = forumInService.selectForumInList(forumIn);
        ExcelUtil<ForumIn> util = new ExcelUtil<ForumIn>(ForumIn.class);
        return util.exportExcel(list, "in");
    }

    /**
     * 新增入账记录
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存入账记录
     */
    @RequiresPermissions("forum:in:add")
    @Log(title = "入账记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ForumIn forumIn)
    {
        return toAjax(forumInService.insertForumIn(forumIn));
    }

    /**
     * 修改入账记录
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap)
    {
        ForumIn forumIn = forumInService.selectForumInById(id);
        mmap.put("forumIn", forumIn);
        return prefix + "/edit";
    }

    /**
     * 修改保存入账记录
     */
    @RequiresPermissions("forum:in:edit")
    @Log(title = "入账记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ForumIn forumIn)
    {
        return toAjax(forumInService.updateForumIn(forumIn));
    }

    /**
     * 删除入账记录
     */
    @RequiresPermissions("forum:in:remove")
    @Log(title = "入账记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(forumInService.deleteForumInByIds(ids));
    }
}
