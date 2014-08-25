package com.fbr.Dao.DerivativeAttribute.Entities;

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
            @AttributeOverride(name = "dependentAttribute", column = @Column(name = "dependent_attribute", nullable = false)),
            @AttributeOverride(name = "prerequisiteAttribute", column = @Column(name = "prerequisite_attribute", nullable = false))})
    DerivativeAttributePreRequisitePrimaryKey id;

    @Column(name = "prerequisite_attribute_value", nullable = false)
    int prerequisiteAttributeValue;

    public DerivativeAttributePreRequisitePrimaryKey getId() {
        return id;
    }

    public void setId(DerivativeAttributePreRequisitePrimaryKey id) {
        this.id = id;
    }

    public int getPrerequisiteAttributeValue() {
        return prerequisiteAttributeValue;
    }

    public void setPrerequisiteAttributeValue(int prerequisiteAttributeValue) {
        this.prerequisiteAttributeValue = prerequisiteAttributeValue;
    }
}