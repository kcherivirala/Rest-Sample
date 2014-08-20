package com.fbr.Dao.Attribute.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "derviate_attribute_prerequisites")
public class DerivativeAttributePreRequisiteDbType implements ProjectEntity<DerivativeAttributePreRequisitePrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false)),
            @AttributeOverride(name = "dependentAttribute", column = @Column(name = "dependent_attribute", nullable = false))})
    DerivativeAttributePreRequisitePrimaryKey id;

    @Column(name = "attribute_value", nullable = false)
    int attributeValue;

    public DerivativeAttributePreRequisitePrimaryKey getId() {
        return id;
    }

    public void setId(DerivativeAttributePreRequisitePrimaryKey id) {
        this.id = id;
    }

    public int getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(int attributeValue) {
        this.attributeValue = attributeValue;
    }
}