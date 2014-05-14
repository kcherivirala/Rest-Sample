package com.fbr.domain.Statistic;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;
import java.util.Map;

public class ConstraintLevelStatistics {
    Map<String, Integer> constraints;
    List<AttributeLevelStatistics> listAttributeLevelStatistics;

    public Map<String, Integer> getConstraints() {
        return constraints;
    }

    public void setConstraints(Map<String, Integer> constraints) {
        this.constraints = constraints;
    }

    public List<AttributeLevelStatistics> getAttributeLevelStatistics() {
        return listAttributeLevelStatistics;
    }

    public void setAttributeLevelStatistics(List<AttributeLevelStatistics> listAttributeLevelStatistics) {
        this.listAttributeLevelStatistics = listAttributeLevelStatistics;
    }
}
