package com.fbr.domain.Response;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.Date;
import java.util.List;

public class Response {
    String email;
    String name;
    String phone;

    Date startDate;
    Date endDate;

    List<AttributeTuple> attributeTuples;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<AttributeTuple> getAttributeTuples() {
        return attributeTuples;
    }

    public void setAttributeTuples(List<AttributeTuple> attributeTuples) {
        this.attributeTuples = attributeTuples;
    }


}
