package com.ruoyi.web.controller.system;

import com.ruoyi.cms.util.CmsConstants;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysBug;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysBugService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * bugController
 *
 * @author ruoyi
 * @date 2021-07-15
 */
@Controller
@RequestMapping("/system/bug")
public class SysBugController extends BaseController
{
    private String prefix = "system/bug";

    @Autowired
    private ISysBugService sysBugService;

    @RequiresPermissions("system:bug:view")
    @GetMapping()
    public String bug()
    {
        return prefix + "/bug";
    }

    /**
     * 查询bug列表
     */
    @RequiresPermissions("system:bug:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysBug sysBug)
    {
        startPage();
        List<SysBug> list = sysBugService.selectSysBugList(sysBug);
        return getDataTable(list);
    }

    /**
     * 导出bug列表
     */
    @RequiresPermissions("system:bug:export")
    @Log(title = "bug", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysBug sysBug)
    {
        List<SysBug> list = sysBugService.selectSysBugList(sysBug);
        ExcelUtil<SysBug> util = new ExcelUtil<SysBug>(SysBug.class);
        return util.exportExcel(list, "bug");
    }

    /**
     * 新增bug
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存bug
     */
    @RequiresPermissions("system:bug:add")
    @Log(title = "bug", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SysBug sysBug)
    {
        SysUser user= ShiroUtils.getSysUser();
        sysBug.setUserId(user.getUserId().toString());
        sysBug.setUserName(user.getUserName());
        return toAjax(sysBugService.insertSysBug(sysBug));
    }

    /**
     * 修改bug
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        SysBug sysBug = sysBugService.selectSysBugById(id);
        mmap.put("sysBug", sysBug);
        return prefix + "/edit";
    }

    /**
     * 修改保存bug
     */
    @RequiresPermissions("system:bug:edit")
    @Log(title = "bug", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysBug sysBug)
    {
        if(sysBug.getAuditState()!=null){
            SysUser user=ShiroUtils.getSysUser();
            sysBug.setAuditUserId(user.getUserId().toString());
            sysBug.setAuditUserName(user.getUserName());
            sysBug.setAuditTime(new Date());
        }
        sysBugService.updateSysBug(sysBug);
        return AjaxResult.success(sysBug);
    }

    /**
     * 删除bug
     */
    @RequiresPermissions("system:bug:remove")
    @Log(title = "bug", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(sysBugService.deleteSysBugByIds(ids));
    }
}
