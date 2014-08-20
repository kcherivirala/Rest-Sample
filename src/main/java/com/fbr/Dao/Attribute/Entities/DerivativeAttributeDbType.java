package com.fbr.Dao.Attribute.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "derivative_attributes")
public class DerivativeAttributeDbType implements ProjectEntity<DerivativeAttributePrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false)),
            @AttributeOverride(name = "dependentAttribute", column = @Column(name = "dependent_attribute", nullable = false))})
    DerivativeAttributePrimaryKey id;

    @Column(name = "weight", nullable = false)
    int weight;

    public DerivativeAttributePrimaryKey getId() {
        return id;
    }

    public void setId(DerivativeAttributePrimaryKey id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
