package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * bug对象 sys_bug
 *
 * @author ruoyi
 * @date 2021-07-15
 */
public class SysBug extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 用户ID */
    private String userId;

    /** 用户 */
    @Excel(name = "用户")
    private String userName;

    /** bug出处 */
    @Excel(name = "bug出处")
    private String bugPlace;

    /** 版本号 */
    @Excel(name = "版本号")
    private String version;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 截图 */
    private String imgs;

    /** 详情 */
    private String detail;

    /** 审核状态 */
    @Excel(name = "审核状态")
    private Integer auditState;
    @Excel(name = "积分")
    private Integer score;
    /** 审核说明 */
    @Excel(name = "审核说明")
    private String auditDesc;

    /** 审核时间 */
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditTime;

    /** 审核人ID */
    private String auditUserId;

    /** 审核人 */
    @Excel(name = "审核人")
    private String auditUserName;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
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
    public void setBugPlace(String bugPlace)
    {
        this.bugPlace = bugPlace;
    }

    public String getBugPlace()
    {
        return bugPlace;
    }
    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setImgs(String imgs)
    {
        this.imgs = imgs;
    }

    public String getImgs()
    {
        return imgs;
    }
    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    public String getDetail()
    {
        return detail;
    }
    public void setAuditState(Integer auditState)
    {
        this.auditState = auditState;
    }

    public Integer getAuditState()
    {
        return auditState;
    }
    public void setAuditDesc(String auditDesc)
    {
        this.auditDesc = auditDesc;
    }

    public String getAuditDesc()
    {
        return auditDesc;
    }
    public void setAuditTime(Date auditTime)
    {
        this.auditTime = auditTime;
    }

    public Date getAuditTime()
    {
        return auditTime;
    }
    public void setAuditUserId(String auditUserId)
    {
        this.auditUserId = auditUserId;
    }

    public String getAuditUserId()
    {
        return auditUserId;
    }
    public void setAuditUserName(String auditUserName)
    {
        this.auditUserName = auditUserName;
    }

    public String getAuditUserName()
    {
        return auditUserName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("bugPlace", getBugPlace())
            .append("version", getVersion())
            .append("description", getDescription())
            .append("imgs", getImgs())
            .append("detail", getDetail())
            .append("auditState", getAuditState())
            .append("score", getScore())
            .append("auditDesc", getAuditDesc())
            .append("auditTime", getAuditTime())
            .append("auditUserId", getAuditUserId())
            .append("auditUserName", getAuditUserName())
            .append("createTime", getCreateTime())
            .toString();
    }
}
