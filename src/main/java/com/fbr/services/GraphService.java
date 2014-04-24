package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.*;
import com.fbr.Dao.Entities.*;
import com.fbr.domain.Graph;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("graphService")
public class GraphService {
    private static final Logger logger = Logger.getLogger(GraphService.class);
    @Autowired
    private GraphsDao graphsDao;
    @Autowired
    private GraphFiltersDao graphFiltersDao;
    @Autowired
    private GraphAttributesDao graphAttributesDao;
    @Autowired
    private TrendsDao trendsDao;
    @Autowired
    private TrendFiltersDao trendFiltersDao;
    @Autowired
    private TrendAttributesDao trendAttributesDao;

    public Graph addGraph(int companyId, Graph graph) {
        String id = UUID.randomUUID().toString();
        GraphDbType graphDbEntry = new GraphDbType();

        graphDbEntry.setCompanyId(companyId);
        graphDbEntry.setGraphId(id);
        graphDbEntry.setName(graph.getName());

        addGraphAttributes(id, graph.getAttributeList());
        addGraphFilters(id, graph.getFilterList());
        return graph;
    }

    private void addGraphAttributes(String graphId, List<Integer> attributeList){
        for(int attributeId: attributeList){
            graphAttributesDao.add(getGraphAttributeDbEntry(graphId, attributeId));
        }
    }

    private GraphAttributesDbType getGraphAttributeDbEntry(String graphID, int attributeId){
        GraphAttributesDbType entry = new GraphAttributesDbType();
        GraphAttributesPrimaryKey key = new GraphAttributesPrimaryKey();
        entry.setId(key);

        key.setAttributeId(attributeId);
        key.setGraphId(graphID);

        return entry;

    }

    private void addGraphFilters(String graphId, List<Integer> filterList){
        for(int attributeId: filterList){
            graphFiltersDao.add(getGraphFilterDbEntry(graphId, attributeId));
        }
    }

    private GraphFiltersDbType getGraphFilterDbEntry(String graphID, int attributeId){
        GraphFiltersDbType entry = new GraphFiltersDbType();
        GraphFiltersPrimaryKey key = new GraphFiltersPrimaryKey();
        entry.setId(key);

        key.setAttributeId(attributeId);
        key.setGraphId(graphID);

        return entry;
    }
}
