package com.fbr.Dao.Customer.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

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

    @Column(name = "age", unique = true)
    int age;
    @Column(name = "sex", unique = true)
    boolean sex;
    @Column(name = "profession", nullable = false)
    int profession;

    @Column(name = "country", nullable = false)
    int country;
    @Column(name = "state", nullable = false)
    int state;
    @Column(name = "city", nullable = false)
    int city;

    @Column(name = "password", unique = true)
    String password;


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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getProfession() {
        return profession;
    }

    public void setProfession(int profession) {
        this.profession = profession;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
