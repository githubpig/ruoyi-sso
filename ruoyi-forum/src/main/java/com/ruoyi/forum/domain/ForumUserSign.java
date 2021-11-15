package com.ruoyi.forum.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

public class ForumUserSign extends BaseEntity {

    private Integer id;

    private String userId;

    private Integer count;

    private Date signTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getSignTime() {
        return signTime;
    }

    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }
}
