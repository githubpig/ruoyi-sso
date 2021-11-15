package com.ruoyi.cms.controller;

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
import com.ruoyi.cms.domain.Donate;
import com.ruoyi.cms.service.IDonateService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 捐赠记录Controller
 * 
 * @author ruoyi
 * @date 2021-07-16
 */
@Controller
@RequestMapping("/cms/donate")
public class DonateController extends BaseController
{
    private String prefix = "cms/donate";

    @Autowired
    private IDonateService donateService;

    @RequiresPermissions("cms:donate:view")
    @GetMapping()
    public String donate()
    {
        return prefix + "/donate";
    }

    /**
     * 查询捐赠记录列表
     */
    @RequiresPermissions("cms:donate:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Donate donate)
    {
        startPage();
        List<Donate> list = donateService.selectDonateList(donate);
        return getDataTable(list);
    }

    /**
     * 导出捐赠记录列表
     */
    @RequiresPermissions("cms:donate:export")
    @Log(title = "捐赠记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Donate donate)
    {
        List<Donate> list = donateService.selectDonateList(donate);
        ExcelUtil<Donate> util = new ExcelUtil<Donate>(Donate.class);
        return util.exportExcel(list, "donate");
    }

    /**
     * 新增捐赠记录
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存捐赠记录
     */
    @RequiresPermissions("cms:donate:add")
    @Log(title = "捐赠记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Donate donate)
    {
        return toAjax(donateService.insertDonate(donate));
    }

    /**
     * 修改捐赠记录
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        Donate donate = donateService.selectDonateById(id);
        mmap.put("donate", donate);
        return prefix + "/edit";
    }

    /**
     * 修改保存捐赠记录
     */
    @RequiresPermissions("cms:donate:edit")
    @Log(title = "捐赠记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Donate donate)
    {
        return toAjax(donateService.updateDonate(donate));
    }

    /**
     * 删除捐赠记录
     */
    @RequiresPermissions("cms:donate:remove")
    @Log(title = "捐赠记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(donateService.deleteDonateByIds(ids));
    }
}
