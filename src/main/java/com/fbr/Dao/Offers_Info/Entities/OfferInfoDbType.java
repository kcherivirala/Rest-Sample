package com.fbr.Dao.Offers_Info.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "offers_info")
public class OfferInfoDbType implements ProjectEntity<OfferInfoPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "branchId", column = @Column(name = "branch_id", nullable = false)),
            @AttributeOverride(name = "offerId", column = @Column(name = "offer_id", nullable = false))})
    OfferInfoPrimaryKey id;

    @Column(name = "type")
    String type;
    @Column(name = "details")
    String details;

    @Column(name = "start_ts")
    Date startTS;
    @Column(name = "end_ts")
    Date endTS;

    @Column(name = "recurrence_day")
    int recurrenceDay;

    @Column(name = "recurrence")
    boolean recurrence;
    @Column(name = "hourly_push")
    boolean hourlyPush;
    @Column(name = "client_app_visibility")
    boolean clientAppVisibility;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getStartTS() {
        return startTS;
    }

    public void setStartTS(Date startTS) {
        this.startTS = startTS;
    }

    public Date getEndTS() {
        return endTS;
    }

    public void setEndTS(Date endTS) {
        this.endTS = endTS;
    }

    public int getRecurrenceDay() {
        return recurrenceDay;
    }

    public void setRecurrenceDay(int recurrenceDay) {
        this.recurrenceDay = recurrenceDay;
    }

    public boolean isRecurrence() {
        return recurrence;
    }

    public void setRecurrence(boolean recurrence) {
        this.recurrence = recurrence;
    }

    public boolean isHourlyPush() {
        return hourlyPush;
    }

    public void setHourlyPush(boolean hourlyPush) {
        this.hourlyPush = hourlyPush;
    }

    public boolean isClientAppVisibility() {
        return clientAppVisibility;
    }

    public void setClientAppVisibility(boolean clientAppVisibility) {
        this.clientAppVisibility = clientAppVisibility;
    }

    @Override
    public OfferInfoPrimaryKey getId() {
        return id;
    }

    @Override
    public void setId(OfferInfoPrimaryKey offersInfoPrimaryKey) {
        this.id = offersInfoPrimaryKey;
    }
}
