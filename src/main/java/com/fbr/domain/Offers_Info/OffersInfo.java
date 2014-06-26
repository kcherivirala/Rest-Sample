package com.fbr.domain.Offers_Info;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.Date;

public class OffersInfo {
    int branchId;
    int offerId;

    String type;
    String details;

    Date start;
    Date end;

    int recurrenceDay;
    boolean recurrence;
    boolean hourlyPush;
    boolean clientAppVisibility;

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
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
}
