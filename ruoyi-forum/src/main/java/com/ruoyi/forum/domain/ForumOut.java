package com.ruoyi.forum.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 出账记录对象 forum_out
 * 
 * @author ruoyi
 * @date 2021-03-19
 */
public class ForumOut extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单编号 */
    @Excel(name = "订单编号")
    private String id;

    @Excel(name = "用户ID")
    private String userId;

    /** 入账类型 */
    @Excel(name = "入账类型")
    private Integer forumOutType;

    /** 出账说明 */
    @Excel(name = "出账说明")
    private String description;

    /** 金额 */
    @Excel(name = "金额")
    private String amount;

    /** 订单时间 */
    @Excel(name = "订单时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderTime;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setForumOutType(Integer forumOutType) 
    {
        this.forumOutType = forumOutType;
    }

    public Integer getForumOutType() 
    {
        return forumOutType;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("forumOutType", getForumOutType())
            .append("description", getDescription())
            .append("amount", getAmount())
            .append("orderTime", getOrderTime())
            .toString();
    }
}
