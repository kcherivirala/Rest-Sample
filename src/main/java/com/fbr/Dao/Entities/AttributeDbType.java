package com.fbr.Dao.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "attributes")
public class AttributeDbType implements ProjectEntity<String>{
    @Column(name = "attribute_id", unique = true, nullable = false)
    @Id
    String attributeId;
    @Column(name = "attribute_string", nullable = false)
    String attributeString;
    @Column(name = "parent_id")
    String parentId;

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeString() {
        return attributeString;
    }

    public void setAttributeString(String attributeString) {
        this.attributeString = attributeString;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    @Transient
    public void setId(String s) {
        this.attributeId = s;
    }

    @Override
    public String getId(){
        return this.attributeId;
    }
}
