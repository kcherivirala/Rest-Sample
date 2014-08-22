package com.fbr.Dao.Attribute.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attributes")
public class AttributeDbType implements ProjectEntity<Integer> {
    @Column(name = "attribute_id", unique = true, nullable = false)
    @Id
    int attributeId;
    @Column(name = "attribute_string", nullable = false)
    String attributeString;
    @Column(name = "type")
    String type;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "attribute_id", referencedColumnName = "attribute_id")
    List<AttributeValuesDbType> attributeValues = new ArrayList<AttributeValuesDbType>();

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AttributeValuesDbType> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<AttributeValuesDbType> attributeValues) {
        this.attributeValues = attributeValues;
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
