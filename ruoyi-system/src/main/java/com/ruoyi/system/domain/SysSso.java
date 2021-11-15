package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.persistence.Transient;
import java.util.Date;

/**
 * sso集成应用对象 sys_sso
 * 
 * @author ruoyi
 * @date 2021-11-13
 */
public class SysSso extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 应用logo */
    @Excel(name = "应用logo")
    private String logo;

    /** 应用名称 */
    @Excel(name = "应用名称")
    private String appName;

    /** 应用登录url */
    @Excel(name = "应用登录url")
    private String ssoLogin;

    /** 状态（启用:1,停用:0） */
    @Excel(name = "状态", readConverterExp = "启=用:1,停用:0")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    @Transient
    private String imgUrl;

    private Boolean flag;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setLogo(String logo) 
    {
        this.logo = logo;
    }

    public String getLogo() 
    {
        return logo;
    }
    public void setAppName(String appName) 
    {
        this.appName = appName;
    }

    public String getAppName() 
    {
        return appName;
    }
    public void setSsoLogin(String ssoLogin) 
    {
        this.ssoLogin = ssoLogin;
    }

    public String getSsoLogin() 
    {
        return ssoLogin;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("logo", getLogo())
            .append("appName", getAppName())
            .append("ssoLogin", getSsoLogin())
            .append("status", getStatus())
            .toString();
    }
}
