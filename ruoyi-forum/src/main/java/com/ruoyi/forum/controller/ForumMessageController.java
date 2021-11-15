package com.ruoyi.forum.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.cms.service.IEmailService;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.forum.domain.ForumUser;
import com.ruoyi.forum.service.ForumUserService;
import com.ruoyi.forum.service.IForumQuestionService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
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
import com.ruoyi.forum.domain.ForumMessage;
import com.ruoyi.forum.service.IForumMessageService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * 消息Controller
 * 
 * @author ruoyi
 * @date 2021-03-24
 */
@Controller
@RequestMapping("/forum/message")
public class ForumMessageController extends BaseController
{
    private String prefix = "forum/message";

    @Autowired
    private IForumMessageService forumMessageService;
    @Autowired
    private ISysUserService userService;

    @Autowired
    ForumUserService forumUserService;
    @Autowired
    IEmailService emailService;
    @Autowired
    IForumQuestionService forumQuestionService;

    @RequiresPermissions("forum:message:view")
    @GetMapping()
    public String message()
    {
        return prefix + "/message";
    }

    /**
     * 查询消息列表
     */
    @RequiresPermissions("forum:message:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ForumMessage forumMessage)
    {
        startPage();
        List<ForumMessage> list = forumMessageService.selectForumMessageList(forumMessage);
        return getDataTable(list);
    }

    /**
     * 导出消息列表
     */
    @RequiresPermissions("forum:message:export")
    @Log(title = "消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ForumMessage forumMessage)
    {
        List<ForumMessage> list = forumMessageService.selectForumMessageList(forumMessage);
        ExcelUtil<ForumMessage> util = new ExcelUtil<ForumMessage>(ForumMessage.class);
        return util.exportExcel(list, "message");
    }

    /**
     * 新增消息
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存消息
     */
    @RequiresPermissions("forum:message:add")
    @Log(title = "消息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ForumMessage forumMessage)
    {
        return toAjax(forumMessageService.insertForumMessage(forumMessage));
    }

    /**
     * 修改消息
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        ForumMessage forumMessage = forumMessageService.selectForumMessageById(id);
        mmap.put("forumMessage", forumMessage);
        return prefix + "/edit";
    }

    /**
     * 修改保存消息
     */
    @RequiresPermissions("forum:message:edit")
    @Log(title = "消息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ForumMessage forumMessage)
    {
        return toAjax(forumMessageService.updateForumMessage(forumMessage));
    }

    /**
     * 删除消息
     */
    @RequiresPermissions("forum:message:remove")
    @Log(title = "消息", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(forumMessageService.deleteForumMessageByIds(ids));
    }


    /**
     * 通知
     * @param modelMap
     * @return
     */
    @GetMapping("/notice")
    public String notice(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        if(user!=null){
            modelMap.addAttribute("user",user);
            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        //查询帖子数量
        int n=forumQuestionService.selectQuestionCount(user.getUserId().toString());
        modelMap.addAttribute("questionCount",n);
        //查询粉丝数量
        int count=forumUserService.selectFansCount(user.getUserId().toString());
        modelMap.addAttribute("fansCount",count);
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        if(forumUser!=null){
            modelMap.addAttribute("look",forumUser.getLook());
        }
        return "forum/message/notice";
    }

    /**
     * 评论留言
     * @param modelMap
     * @return
     */
    @GetMapping("/comments")
    public String comments(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        if(user!=null){
            modelMap.addAttribute("user",user);
            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        //查询帖子数量
        int n=forumQuestionService.selectQuestionCount(user.getUserId().toString());
        modelMap.addAttribute("questionCount",n);
        //查询粉丝数量
        int count=forumUserService.selectFansCount(user.getUserId().toString());
        modelMap.addAttribute("fansCount",count);
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        if(forumUser!=null){
            modelMap.addAttribute("look",forumUser.getLook());
        }
        return "forum/message/comments";
    }
    /**
     * 评论留言
     * @param modelMap
     * @return
     */
    @GetMapping("/privateMessage")
    public String privateMessage(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        user=userService.selectUserById(user.getUserId());
        if(user!=null){
            modelMap.addAttribute("user",user);
            modelMap.addAttribute("userId",user.getUserId().toString());
            modelMap.addAttribute("userName",user.getUserName());
            modelMap.addAttribute("avatar",getAvatarPath(user));
        }
        //查询帖子数量
        int n=forumQuestionService.selectQuestionCount(user.getUserId().toString());
        modelMap.addAttribute("questionCount",n);
        //查询粉丝数量
        int count=forumUserService.selectFansCount(user.getUserId().toString());
        modelMap.addAttribute("fansCount",count);
        ForumUser forumUser=forumUserService.selectByUserId(user.getUserId());
        if(forumUser!=null){
            modelMap.addAttribute("look",forumUser.getLook());
        }

        return "forum/message/privateMessage";
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
    @PostMapping("/getMessages")
    @ResponseBody
    public TableDataInfo getMessages(ModelMap modelMap, HttpServletRequest request)
    {
        SysUser user=ShiroUtils.getSysUser();
        String msgType=request.getParameter("msgType");
        if("all".equals(msgType)){
            startPage();
            List<ForumMessage> list=forumMessageService.selectByAllType(user.getUserId().toString());
            list.forEach(a->{
                a.setTimeDesc(getTimePassedLong(a.getCreateTime()));
            });
            return getDataTable(list);
        }else {
            startPage();
            List<ForumMessage> list=forumMessageService.selectByMsgType(msgType,user.getUserId().toString());
            list.forEach(a->{
                a.setTimeDesc(getTimePassedLong(a.getCreateTime()));
            });
            return getDataTable(list);
        }
    }

    /**
     * 获取留言、评论
     * @return
     */
    @PostMapping("/getComments")
    @ResponseBody
    public TableDataInfo getComments()
    {
            String userId=ShiroUtils.getUserId().toString();
            startPage();
            List<ForumMessage> list=forumMessageService.selectComments(userId);
            list.forEach(a->{
                a.setTimeDesc(getTimePassedLong(a.getCreateTime()));
            });
            return getDataTable(list);
    }
    @PostMapping("/getPrivateMessage")
    @ResponseBody
    public TableDataInfo getPrivateMessage()
    {
        String userId=ShiroUtils.getUserId().toString();
        startPage();
        List<ForumMessage> list=forumMessageService.selectPrivateMessage(userId);
        list.forEach(a->{
            a.setTimeDesc(getTimePassedLong(a.getCreateTime()));
        });
        return getDataTable(list);
    }
    @ResponseBody
    @GetMapping("/countMessages")
    public AjaxResult countMessages(ModelMap modelMap)
    {
        SysUser user = ShiroUtils.getSysUser();
        if(user!=null){
            Map map=forumMessageService.countMessages(user.getUserId().toString());
            return AjaxResult.success(map);
        }
        return error("未登录!");

    }
    public static String getTimePassedLong(Date source) {

        if (source == null)
            return null;

        long nowTime = System.currentTimeMillis(); // 获取当前时间的毫秒数

        String msg = "";

        long dateDiff = nowTime - source.getTime();

        if (dateDiff >= 0) {
            long dateTemp1 = dateDiff  / 1000; // 秒
            long dateTemp2 = dateTemp1 / 60;   // 分钟
            long dateTemp3 = dateTemp2 / 60;   // 小时
            long dateTemp4 = dateTemp3 / 24;   // 天数
            long dateTemp5 = dateTemp4 / 30;   // 月数
            long dateTemp6 = dateTemp5 / 12;   // 年数
            if (dateTemp6 > 0)
                msg = dateTemp6 + "年前";
            else if (dateTemp5 > 0)
                msg = dateTemp5 + "个月前";
            else if (dateTemp4 > 0)
                msg = dateTemp4 + "天前";
            else if (dateTemp3 > 0)
                msg = dateTemp3 + "小时前";
            else if (dateTemp2 > 0)
                msg = dateTemp2 + "分钟前";
            else if (dateTemp1 > 0)
                msg = dateTemp1 + "秒前";
            else
                msg = "刚刚";
        }
        return msg;

    }

    @ResponseBody
    @GetMapping("/updateReadFlag")
    public AjaxResult updateReadFlag(String ids)
    {

      return   toAjax(forumMessageService.updateReadFlag(ids));

    }

}
