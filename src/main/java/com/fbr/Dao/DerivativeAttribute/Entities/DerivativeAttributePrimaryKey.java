package com.fbr.Dao.DerivativeAttribute.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DerivativeAttributePrimaryKey implements Serializable {
    @Column(name = "company_id", nullable = false)
    int companyId;
    @Column(name = "attribute_id", nullable = false)
    int attributeId;
    @Column(name = "dependent_attribute", nullable = false)
    int dependentAttribute;


    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public int getDependentAttribute() {
        return dependentAttribute;
    }

    public void setDependentAttribute(int dependentAttribute) {
        this.dependentAttribute = dependentAttribute;
    }
}
