package com.ruoyi.plugs.thirdlogin.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class ThirdOauth extends BaseEntity {

    private Long id;
    private String userId;
    private String openid;
    private String loginType;
    private String accessToken;
    private String bindTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getBindTime() {
        return bindTime;
    }

    public void setBindTime(String bindTime) {
        this.bindTime = bindTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "ThirdOauth{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", openid='" + openid + '\'' +
                ", loginType='" + loginType + '\'' +
                ", bindTime='" + bindTime + '\'' +
                '}';
    }
}
