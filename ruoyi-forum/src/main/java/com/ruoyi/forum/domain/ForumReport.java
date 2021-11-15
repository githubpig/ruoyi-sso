package com.ruoyi.forum.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 举报信息对象 forum_report
 * 
 * @author ruoyi
 * @date 2021-03-15
 */
public class ForumReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 模块 */
    @Excel(name = "模块")
    private String module;

    /** 举报类型 */
    @Excel(name = "举报类型")
    private Integer reportType;

    /** 被举报目标id */
    @Excel(name = "被举报目标id")
    private String targetId;

    /** 举报人 */
    private String reportYhid;

    /** 信息 */
    private String message;

    /** 处理标志 */
    @Excel(name = "处理标志")
    private Integer dealFlag;

    /** 处理时间 */
    @Excel(name = "处理时间")
    private String dealTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setModule(String module) 
    {
        this.module = module;
    }

    public String getModule() 
    {
        return module;
    }
    public void setReportType(Integer reportType) 
    {
        this.reportType = reportType;
    }

    public Integer getReportType() 
    {
        return reportType;
    }
    public void setTargetId(String targetId) 
    {
        this.targetId = targetId;
    }

    public String getTargetId() 
    {
        return targetId;
    }
    public void setReportYhid(String reportYhid) 
    {
        this.reportYhid = reportYhid;
    }

    public String getReportYhid() 
    {
        return reportYhid;
    }
    public void setMessage(String message) 
    {
        this.message = message;
    }

    public String getMessage() 
    {
        return message;
    }
    public void setDealFlag(Integer dealFlag) 
    {
        this.dealFlag = dealFlag;
    }

    public Integer getDealFlag() 
    {
        return dealFlag;
    }
    public void setDealTime(String dealTime) 
    {
        this.dealTime = dealTime;
    }

    public String getDealTime() 
    {
        return dealTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("module", getModule())
            .append("reportType", getReportType())
            .append("targetId", getTargetId())
            .append("reportYhid", getReportYhid())
            .append("message", getMessage())
            .append("dealFlag", getDealFlag())
            .append("dealTime", getDealTime())
            .toString();
    }
}
