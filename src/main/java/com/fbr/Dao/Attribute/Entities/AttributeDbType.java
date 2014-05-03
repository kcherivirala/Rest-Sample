package com.fbr.Dao.Attribute.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "attributes")
public class AttributeDbType implements ProjectEntity<Integer> {
    @Column(name = "attribute_id", unique = true, nullable = false)
    @Id
    int attributeId;
    @Column(name = "attribute_string", nullable = false)
    String attributeString;
    @Column(name = "parent_id")
    int parentId;
    @Column(name = "type")
    String type;

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeString() {
        return attributeString;
    }

    public void setAttributeString(String attributeString) {
        this.attributeString = attributeString;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    @Transient
    public void setId(Integer s) {
        this.attributeId = s;
    }

    @Override
    public Integer getId() {
        return this.attributeId;
    }
}
