package com.ruoyi.forum.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.TreeEntity;

/**
 * 评论管理对象 forum_comment
 * 
 * @author ruoyi
 * @date 2021-03-15
 */
public class ForumComment extends TreeEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 父ID */
    private Long pid;

    /** 目标ID */
    private String tid;

    /** 评论类型 */
    private String type;

    /** 用户id */
    private String userId;

    /** 用户名称 */
    @Excel(name = "用户名称")
    private String userName;

    /** 用户头像 */
    @Excel(name = "用户头像")
    private String avatar;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 点赞数 */
    private Long upVote;

    /** 反对数 */
    private Long downVote;

    /** QQ */
    @Excel(name = "QQ")
    private String qq;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** IP */
    @Excel(name = "IP")
    private String ip;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 状态 */
    @Excel(name = "状态")
    private Integer available;

    private String timeDesc;//扩展字段，时间过去多长时间的描述
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPid(Long pid) 
    {
        this.pid = pid;
    }

    public Long getPid() 
    {
        return pid;
    }
    public void setTid(String tid) 
    {
        this.tid = tid;
    }

    public String getTid() 
    {
        return tid;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setUpVote(Long upVote) 
    {
        this.upVote = upVote;
    }

    public Long getUpVote() 
    {
        return upVote;
    }
    public void setDownVote(Long downVote) 
    {
        this.downVote = downVote;
    }

    public Long getDownVote() 
    {
        return downVote;
    }
    public void setQq(String qq) 
    {
        this.qq = qq;
    }

    public String getQq() 
    {
        return qq;
    }
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }
    public void setAvailable(Integer available) 
    {
        this.available = available;
    }

    public Integer getAvailable() 
    {
        return available;
    }

    public String getTimeDesc() {
        return timeDesc;
    }

    public void setTimeDesc(String timeDesc) {
        this.timeDesc = timeDesc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("pid", getPid())
            .append("tid", getTid())
            .append("type", getType())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("avatar", getAvatar())
            .append("content", getContent())
            .append("upVote", getUpVote())
            .append("downVote", getDownVote())
            .append("qq", getQq())
            .append("email", getEmail())
            .append("ip", getIp())
            .append("address", getAddress())
            .append("createTime", getCreateTime())
            .append("available", getAvailable())
            .toString();
    }
}
