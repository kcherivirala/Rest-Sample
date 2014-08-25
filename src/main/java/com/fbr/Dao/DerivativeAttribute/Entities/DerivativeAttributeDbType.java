package com.fbr.Dao.DerivativeAttribute.Entities;

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "company_id", referencedColumnName = "company_id"),
            @JoinColumn(name = "attribute_id", referencedColumnName = "attribute_id"),
            @JoinColumn(name = "dependent_attribute", referencedColumnName = "dependent_attribute")
    })
    List<DerivativeAttributePreRequisiteDbType> attributeValues = new ArrayList<DerivativeAttributePreRequisiteDbType>();

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

    public List<DerivativeAttributePreRequisiteDbType> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<DerivativeAttributePreRequisiteDbType> attributeValues) {
        this.attributeValues = attributeValues;
    }
}
