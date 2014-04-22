package com.fbr.Dao.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "attribute_values")
public class AttributeValuesDbType implements ProjectEntity<AttributeValuesPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "value", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false))})
    AttributeValuesPrimaryKey id;

    @Column(name = "max_value", nullable = false)
    int maxValue;
    @Column(name = "name")
    String name;

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Transient
    public void setId(AttributeValuesPrimaryKey s) {
        this.id = s;
    }

    @Override
    public AttributeValuesPrimaryKey getId() {
        return this.id;
    }
}
