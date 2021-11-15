package com.ruoyi.cms.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 捐赠记录对象 cms_donate
 * 
 * @author ruoyi
 * @date 2021-07-16
 */
public class Donate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 平台 */
    @Excel(name = "平台")
    private String platform;

    /** 平台用户ID */
    @Excel(name = "平台用户ID")
    private String uid;

    /** 头像 */
    @Excel(name = "头像")
    private String avatar;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 留言 */
    @Excel(name = "留言")
    private String msg;

    /** 捐赠时间 */
    @Excel(name = "捐赠时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date donateTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPlatform(String platform) 
    {
        this.platform = platform;
    }

    public String getPlatform() 
    {
        return platform;
    }
    public void setUid(String uid) 
    {
        this.uid = uid;
    }

    public String getUid() 
    {
        return uid;
    }
    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setMsg(String msg) 
    {
        this.msg = msg;
    }

    public String getMsg() 
    {
        return msg;
    }
    public void setDonateTime(Date donateTime) 
    {
        this.donateTime = donateTime;
    }

    public Date getDonateTime() 
    {
        return donateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("platform", getPlatform())
            .append("uid", getUid())
            .append("avatar", getAvatar())
            .append("amount", getAmount())
            .append("msg", getMsg())
            .append("donateTime", getDonateTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
