package com.ruoyi.forum.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消息对象 forum_message
 * 
 * @author ruoyi
 * @date 2021-03-24
 */
public class ForumMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 发送者ID */
    @Excel(name = "发送者ID")
    private String fromId;

    /** 发送者名称 */
    @Excel(name = "发送者名称")
    private String fromName;

    /** 接受者ID */
    @Excel(name = "接受者ID")
    private String toId;

    /** 接受者名称 */
    @Excel(name = "接受者名称")
    private String toName;

    /** 消息类型 */
    @Excel(name = "消息类型")
    private String type;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 消息内容 */
    private String content;

    /** null */
    @Excel(name = "null")
    private Integer readFlag;

    private String timeDesc;//扩展字段，时间过去多久

    public String getTimeDesc() {
        return timeDesc;
    }

    public void setTimeDesc(String timeDesc) {
        this.timeDesc = timeDesc;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setFromId(String fromId) 
    {
        this.fromId = fromId;
    }

    public String getFromId() 
    {
        return fromId;
    }
    public void setFromName(String fromName) 
    {
        this.fromName = fromName;
    }

    public String getFromName() 
    {
        return fromName;
    }
    public void setToId(String toId) 
    {
        this.toId = toId;
    }

    public String getToId() 
    {
        return toId;
    }
    public void setToName(String toName) 
    {
        this.toName = toName;
    }

    public String getToName() 
    {
        return toName;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setReadFlag(Integer readFlag) 
    {
        this.readFlag = readFlag;
    }

    public Integer getReadFlag() 
    {
        return readFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("fromId", getFromId())
            .append("fromName", getFromName())
            .append("toId", getToId())
            .append("toName", getToName())
            .append("type", getType())
            .append("title", getTitle())
            .append("content", getContent())
            .append("readFlag", getReadFlag())
            .append("createTime", getCreateTime())
            .toString();
    }
}
