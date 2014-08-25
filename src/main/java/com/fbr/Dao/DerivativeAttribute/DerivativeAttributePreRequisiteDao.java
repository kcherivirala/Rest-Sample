package com.fbr.Dao.DerivativeAttribute;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.DerivativeAttribute.Entities.DerivativeAttributePreRequisiteDbType;
import com.fbr.Dao.DerivativeAttribute.Entities.DerivativeAttributePreRequisitePrimaryKey;
import com.fbr.Dao.ProjectDaoImpl;

public class DerivativeAttributePreRequisiteDao extends ProjectDaoImpl<DerivativeAttributePreRequisiteDbType, DerivativeAttributePreRequisitePrimaryKey> {
    public DerivativeAttributePreRequisiteDao() {
        this.entityClass = DerivativeAttributePreRequisiteDbType.class;
    }
}
