package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruoyi.cms.domain.Attachment;
import com.ruoyi.cms.service.IAttachmentService;
import com.ruoyi.common.utils.StringUtils;
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
import com.ruoyi.system.domain.SysSso;
import com.ruoyi.system.service.ISysSsoService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * sso集成应用Controller
 * 
 * @author ruoyi
 * @date 2021-11-13
 */
@Controller
@RequestMapping("/system/sso")
public class SysSsoController extends BaseController
{
    private String prefix = "system/sso";

    @Autowired
    private ISysSsoService sysSsoService;

    @Autowired
    private IAttachmentService attachmentService;

    @RequiresPermissions("system:sso:view")
    @GetMapping()
    public String sso()
    {
        return prefix + "/sso";
    }

    /**
     * 查询sso集成应用列表
     */
    @RequiresPermissions("system:sso:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysSso sysSso)
    {
        startPage();
        List<SysSso> list = sysSsoService.selectSysSsoList(sysSso);
        for (SysSso sso : list) {
            if(StringUtils.isNotEmpty(sso.getLogo())){
                String[] split = sso.getLogo().split(",");
                Attachment attachment = attachmentService.selectAttachmentById(split[split.length-1]);
                String fileUrl = attachment.getFileUrl();
                if(fileUrl.startsWith("/")){
                    fileUrl = fileUrl.substring(1,fileUrl.length());
                }
                sso.setImgUrl(fileUrl);
            }
        }
        return getDataTable(list);
    }
    /**
     * 导出sso集成应用列表
     */
    @RequiresPermissions("system:sso:export")
    @Log(title = "sso集成应用", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysSso sysSso)
    {
        List<SysSso> list = sysSsoService.selectSysSsoList(sysSso);
        ExcelUtil<SysSso> util = new ExcelUtil<SysSso>(SysSso.class);
        return util.exportExcel(list, "sso");
    }

    /**
     * 新增sso集成应用
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存sso集成应用
     */
    @RequiresPermissions("system:sso:add")
    @Log(title = "sso集成应用", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SysSso sysSso)
    {
        return toAjax(sysSsoService.insertSysSso(sysSso));
    }

    /**
     * 修改sso集成应用
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap)
    {
        SysSso sysSso = sysSsoService.selectSysSsoById(id);
        mmap.put("sysSso", sysSso);
        return prefix + "/edit";
    }

    /**
     * 修改保存sso集成应用
     */
    @RequiresPermissions("system:sso:edit")
    @Log(title = "sso集成应用", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysSso sysSso)
    {
        return toAjax(sysSsoService.updateSysSso(sysSso));
    }

    /**
     * 删除sso集成应用
     */
    @RequiresPermissions("system:sso:remove")
    @Log(title = "sso集成应用", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(sysSsoService.deleteSysSsoByIds(ids));
    }
}
