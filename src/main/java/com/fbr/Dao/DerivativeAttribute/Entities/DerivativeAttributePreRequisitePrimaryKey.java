package com.fbr.Dao.DerivativeAttribute.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import javax.persistence.Column;
import java.io.Serializable;

public class DerivativeAttributePreRequisitePrimaryKey implements Serializable {
    @Column(name = "company_id", nullable = false)
    int companyId;
    @Column(name = "attribute_id", nullable = false)
    int attributeId;
    @Column(name = "dependent_attribute", nullable = false)
    int dependentAttribute;
    @Column(name = "prerequisite_attribute", nullable = false)
    int prerequisiteAttribute;


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

    public int getPrerequisiteAttribute() {
        return prerequisiteAttribute;
    }

    public void setPrerequisiteAttribute(int prerequisiteAttribute) {
        this.prerequisiteAttribute = prerequisiteAttribute;
    }
}
