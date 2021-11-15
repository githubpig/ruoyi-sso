package com.ruoyi.forum.controller;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.cms.domain.Tags;
import com.ruoyi.cms.util.CmsConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.forum.domain.ForumCategory;
import com.ruoyi.forum.domain.ForumTags;
import com.ruoyi.forum.service.IForumCategoryService;
import com.ruoyi.forum.service.IForumTagsService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.forum.domain.ForumQuestion;
import com.ruoyi.forum.service.IForumQuestionService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 问题Controller
 * 
 * @author ruoyi
 * @date 2021-03-12
 */
@Controller
@RequestMapping("/forum/question")
public class ForumQuestionController extends BaseController
{
    private String prefix = "forum/question";

    @Autowired
    private IForumQuestionService forumQuestionService;
    @Autowired
    private ISysConfigService configService;
    @Autowired
    private IForumTagsService forumTagsService;
    @Autowired
    IForumCategoryService forumCategoryService;

    private String getEditorType(){
        return configService.selectConfigByKey(CmsConstants.KEY_EDITOR_TYPE);
    }


    @RequiresPermissions("forum:question:view")
    @GetMapping()
    public String question()
    {
        return prefix + "/question";
    }

    /**
     * 查询问题列表
     */
    @RequiresPermissions("forum:question:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ForumQuestion forumQuestion)
    {
        startPage();
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList_Back(forumQuestion);
        return getDataTable(list);
    }


    /**
     * 导出问题列表
     */
    @RequiresPermissions("forum:question:export")
    @Log(title = "问题", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ForumQuestion forumQuestion)
    {
        List<ForumQuestion> list = forumQuestionService.selectForumQuestionList(forumQuestion);
        ExcelUtil<ForumQuestion> util = new ExcelUtil<ForumQuestion>(ForumQuestion.class);
        return util.exportExcel(list, "question");
    }

    /**
     * 新增问题
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        List<ForumTags> tags=forumTagsService.selectForumTagsAll();
        mmap.put("tags",tags);
        String editorType = getEditorType();
        if(CmsConstants.EDITOR_TYPE_EDITORMD.equals(editorType)){
            return prefix + "/add_editormd";
        }else{
            return prefix + "/add";
        }
    }

    /**
     * 新增保存问题
     */
    @RequiresPermissions("forum:question:add")
    @Log(title = "问题", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ForumQuestion forumQuestion)
    {
        return toAjax(forumQuestionService.insertForumQuestion(forumQuestion));
    }


    /**
     * 修改问题
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap)
    {
        ForumQuestion forumQuestion = forumQuestionService.selectForumQuestionById(id);
        mmap.put("forumQuestion", forumQuestion);
        String tagIds=forumQuestion.getTags();
        List<ForumTags> tags= forumTagsService.selectSelectedForumTagsAll(tagIds);
        mmap.put("tags", tags);
        String editorType = getEditorType();
        if(CmsConstants.EDITOR_TYPE_EDITORMD.equals(editorType)){
            return prefix + "/edit_editormd";
        }else{
            return prefix + "/edit";
        }
    }

    /**
     * 修改保存问题
     */
    @RequiresPermissions("forum:question:edit")
    @Log(title = "问题", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ForumQuestion forumQuestion)
    {
        return toAjax(forumQuestionService.updateForumQuestion(forumQuestion));
    }
    @Log(title = "更新问题状态", businessType = BusinessType.UPDATE)
    @PostMapping("/updateAvailable")
    @ResponseBody
    public AjaxResult updateAvailable(ForumQuestion forumQuestion)
    {
        return toAjax(forumQuestionService.updateAvailable(forumQuestion));
    }
    /**
     * 删除问题
     */
    @RequiresPermissions("forum:question:remove")
    @Log(title = "问题", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(forumQuestionService.deleteForumQuestionByIds(ids));
    }

    @ResponseBody
    @RequestMapping(value = "/batchPublish/{ids}", method = RequestMethod.POST)
    public AjaxResult batchPublish(@PathVariable String[] ids) {
        try{
            List<String> idList=new ArrayList<>();
            for(String id:ids){
                ForumQuestion forumQuestion=forumQuestionService.selectForumQuestionById(id);
                if(forumQuestion.getAvailable()!=1){
                    forumQuestion.setAvailable(1);
                    forumQuestionService.updateAvailable(forumQuestion);
                    idList.add(id);
                }
            }
            return AjaxResult.success("批量审核成功！",idList);
        }catch (Exception e){
            return error("批量审核失败！");
        }
    }
    private static final int HIDE_TYPE_ANON=0;
    private static final int HIDE_TYPE_LOGIN=1;
    /**
     * 判断帖子权限
     */
    @PostMapping( "/checkAuth/{id}")
    @ResponseBody
    public AjaxResult checkAuth(@PathVariable("id") String id)
    {
        ForumQuestion forumQuestion=forumQuestionService.selectForumQuestionById(id);
        int authCode=forumQuestion.getHideType();
        SysUser user= ShiroUtils.getSysUser();
        if(authCode==HIDE_TYPE_ANON){
            return AjaxResult.success("可以访问帖子!");
        }
        if(user==null&&authCode==HIDE_TYPE_LOGIN){
            return AjaxResult.error("请登陆后再浏览该帖子!");
        }

        return AjaxResult.success("可以访问帖子!");
    }

}
