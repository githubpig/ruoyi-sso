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
import com.ruoyi.forum.domain.ForumOut;
import com.ruoyi.forum.service.IForumOutService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 出账记录Controller
 * 
 * @author ruoyi
 * @date 2021-03-19
 */
@Controller
@RequestMapping("/forum/out")
public class ForumOutController extends BaseController
{
    private String prefix = "forum/out";

    @Autowired
    private IForumOutService forumOutService;

    @RequiresPermissions("forum:out:view")
    @GetMapping()
    public String out()
    {
        return prefix + "/out";
    }

    /**
     * 查询出账记录列表
     */
    @RequiresPermissions("forum:out:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ForumOut forumOut)
    {
        startPage();
        List<ForumOut> list = forumOutService.selectForumOutList(forumOut);
        return getDataTable(list);
    }

    /**
     * 导出出账记录列表
     */
    @RequiresPermissions("forum:out:export")
    @Log(title = "出账记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ForumOut forumOut)
    {
        List<ForumOut> list = forumOutService.selectForumOutList(forumOut);
        ExcelUtil<ForumOut> util = new ExcelUtil<ForumOut>(ForumOut.class);
        return util.exportExcel(list, "out");
    }

    /**
     * 新增出账记录
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存出账记录
     */
    @RequiresPermissions("forum:out:add")
    @Log(title = "出账记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ForumOut forumOut)
    {
        return toAjax(forumOutService.insertForumOut(forumOut));
    }

    /**
     * 修改出账记录
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap)
    {
        ForumOut forumOut = forumOutService.selectForumOutById(id);
        mmap.put("forumOut", forumOut);
        return prefix + "/edit";
    }

    /**
     * 修改保存出账记录
     */
    @RequiresPermissions("forum:out:edit")
    @Log(title = "出账记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ForumOut forumOut)
    {
        return toAjax(forumOutService.updateForumOut(forumOut));
    }

    /**
     * 删除出账记录
     */
    @RequiresPermissions("forum:out:remove")
    @Log(title = "出账记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(forumOutService.deleteForumOutByIds(ids));
    }
}
