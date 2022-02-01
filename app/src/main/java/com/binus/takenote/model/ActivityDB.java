/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 * Generated by the CloudDB ObjectType compiler.  DO NOT EDIT!
 */
package com.binus.takenote.model;

import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.Text;
import com.huawei.agconnect.cloud.database.annotations.DefaultValue;
import com.huawei.agconnect.cloud.database.annotations.EntireEncrypted;
import com.huawei.agconnect.cloud.database.annotations.NotNull;
import com.huawei.agconnect.cloud.database.annotations.Indexes;
import com.huawei.agconnect.cloud.database.annotations.PrimaryKeys;

import java.util.Date;

/**
 * Definition of ObjectType ActivityDB.
 *
 * @since 2022-02-01
 */
@PrimaryKeys({"id"})
@Indexes({"activityindex:id"})
public final class ActivityDB extends CloudDBZoneObject {
    private String id;

    @NotNull
    @DefaultValue(stringValue = "-")
    private String title;

    @NotNull
    @DefaultValue(stringValue = "-")
    private String description;

    private Date date;

    @NotNull
    @DefaultValue(stringValue = "-")
    private String userId;

    public ActivityDB() {
        super(ActivityDB.class);
        this.title = "-";
        this.description = "-";
        this.userId = "-";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}
