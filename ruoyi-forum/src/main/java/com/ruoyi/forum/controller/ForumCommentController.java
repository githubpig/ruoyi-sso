package com.ruoyi.forum.controller;

import java.util.List;

import com.ruoyi.common.utils.IpUtils;
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
import com.ruoyi.forum.domain.ForumComment;
import com.ruoyi.forum.service.IForumCommentService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.domain.Ztree;

import javax.servlet.http.HttpServletRequest;

/**
 * 评论管理Controller
 * 
 * @author ruoyi
 * @date 2021-03-15
 */
@Controller
@RequestMapping("/forum/comment")
public class ForumCommentController extends BaseController
{
    private String prefix = "forum/comment";

    @Autowired
    private IForumCommentService forumCommentService;

    @RequiresPermissions("forum:comment:view")
    @GetMapping()
    public String comment()
    {
        return prefix + "/comment";
    }

    /**
     * 查询评论管理树列表
     */
    @RequiresPermissions("forum:comment:list")
    @PostMapping("/list")
    @ResponseBody
    public List<ForumComment> list(ForumComment forumComment)
    {
        List<ForumComment> list = forumCommentService.selectForumCommentList_Back(forumComment);
        return list;
    }

    /**
     * 导出评论管理列表
     */
    @RequiresPermissions("forum:comment:export")
    @Log(title = "评论管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ForumComment forumComment)
    {
        List<ForumComment> list = forumCommentService.selectForumCommentList(forumComment);
        ExcelUtil<ForumComment> util = new ExcelUtil<ForumComment>(ForumComment.class);
        return util.exportExcel(list, "comment");
    }

    /**
     * 新增评论管理
     */
    @GetMapping(value = { "/add/{id}", "/add/" })
    public String add(@PathVariable(value = "id", required = false) Long id, ModelMap mmap)
    {
        if (StringUtils.isNotNull(id))
        {
            mmap.put("forumComment", forumCommentService.selectForumCommentById(id));
        }
        return prefix + "/add";
    }

    /**
     * 新增保存评论管理
     */
    @RequiresPermissions("forum:comment:add")
    @Log(title = "评论管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ForumComment forumComment, HttpServletRequest request)

    {
        String ip= IpUtils.getIpAddr(request);
        forumComment.setIp(ip);
        SysUser user= ShiroUtils.getSysUser();
        forumComment.setUserId(user.getUserId().toString());
        forumComment.setUserName(user.getUserName());
        forumComment.setAvatar(getAvatarPath(user));
        return toAjax(forumCommentService.insertForumComment(forumComment));
    }
    public String getAvatarPath(SysUser user){
        if(StringUtils.isEmpty(user.getAvatar())){
            if("1".equals(user.getSex())){
                return "/forum/images/boy.png";
            }else{
                return "/forum/images/girl.png";
            }
        }
        return user.getAvatar();
    }
    /**
     * 修改评论管理
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        ForumComment forumComment = forumCommentService.selectForumCommentById(id);
        mmap.put("forumComment", forumComment);
        return prefix + "/edit";
    }

    /**
     * 修改保存评论管理
     */
    @RequiresPermissions("forum:comment:edit")
    @Log(title = "评论管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ForumComment forumComment)
    {
        return toAjax(forumCommentService.updateForumComment(forumComment));
    }

    /**
     * 删除
     */
    @RequiresPermissions("forum:comment:remove")
    @Log(title = "评论管理", businessType = BusinessType.DELETE)
    @GetMapping("/remove/{id}")
    @ResponseBody
    public AjaxResult remove(@PathVariable("id") Long id)
    {
        return toAjax(forumCommentService.deleteForumCommentById(id));
    }

    /**
     * 选择评论管理树
     */
    @GetMapping(value = { "/selectCommentTree/{id}", "/selectCommentTree/" })
    public String selectCommentTree(@PathVariable(value = "id", required = false) Long id, ModelMap mmap)
    {
        if (StringUtils.isNotNull(id))
        {
            mmap.put("forumComment", forumCommentService.selectForumCommentById(id));
        }
        return prefix + "/tree";
    }

    /**
     * 加载评论管理树列表
     */
    @GetMapping("/treeData")
    @ResponseBody
    public List<Ztree> treeData()
    {
        List<Ztree> ztrees = forumCommentService.selectForumCommentTree();
        return ztrees;
    }

}
