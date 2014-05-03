package com.fbr.domain.Statistic;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class GraphStatistics {
    String graphId;
    List<ConstraintLevelStatistics> listConstraintLevelStatistics;

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public List<ConstraintLevelStatistics> getConstraintLevelStatistics() {
        return listConstraintLevelStatistics;
    }

    public void setConstraintLevelStatistics(List<ConstraintLevelStatistics> listConstraintLevelStatistics) {
        this.listConstraintLevelStatistics = listConstraintLevelStatistics;
    }
}
