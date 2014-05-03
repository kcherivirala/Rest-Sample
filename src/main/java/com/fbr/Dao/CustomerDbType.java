package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import javax.persistence.*;

@Entity
@Table(name = "customers")
public class CustomerDbType implements ProjectEntity<String> {
    @Column(name = "customer_id", unique = true, nullable = false)
    @Id
    String customerId;
    @Column(name = "phone", unique = true)
    String phone;
    @Column(name = "mail", unique = true)
    String mail;
    @Column(name = "name", unique = true)
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    @Transient
    public void setId(String s) {
        this.customerId = s;
    }

    @Override
    public String getId() {
        return this.customerId;
    }
}
