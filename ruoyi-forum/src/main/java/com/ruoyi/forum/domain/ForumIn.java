package com.ruoyi.forum.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 入账记录对象 forum_in
 * 
 * @author ruoyi
 * @date 2021-03-19
 */
public class ForumIn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单编号 */
    @Excel(name = "订单编号")
    private String id;

    @Excel(name = "用户ID")
    private String userId;

    /** 入账类型 */
    @Excel(name = "入账类型")
    private Integer forumInType;

    /** 入账说明 */
    @Excel(name = "入账说明")
    private String description;

    /** 金额 */
    @Excel(name = "金额")
    private String amount;

    /** 订单时间 */
    @Excel(name = "订单时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setForumInType(Integer forumInType) 
    {
        this.forumInType = forumInType;
    }

    public Integer getForumInType() 
    {
        return forumInType;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setAmount(String amount) 
    {
        this.amount = amount;
    }

    public String getAmount() 
    {
        return amount;
    }
    public void setOrderTime(Date orderTime) 
    {
        this.orderTime = orderTime;
    }

    public Date getOrderTime() 
    {
        return orderTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("forumInType", getForumInType())
            .append("description", getDescription())
            .append("amount", getAmount())
            .append("orderTime", getOrderTime())
            .toString();
    }
}
