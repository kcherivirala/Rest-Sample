package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.*;
import com.fbr.Dao.*;
import com.fbr.domain.Graph;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Transactional
    public Graph addGraph(int companyId, Graph graph) {
        String id = UUID.randomUUID().toString();

        graphsDao.add(Conversions.getGraphDbEntry(id, companyId, graph.getName()));

        addGraphAttributes(id, graph.getAttributeList());
        addGraphFilters(id, graph.getFilterList());

        graph.setGraphId(id);
        return graph;
    }

    @Transactional
    public Graph updateGraph(String graphId, Graph graph){
        GraphDbType graphDbEntry = graphsDao.find(graphId);
        if(!graph.getName().equals(graphDbEntry.getName())){
            graphDbEntry.setName(graph.getName());
            graphsDao.update(graphDbEntry);
        }
        updateGraphAttributes(graphId, graph.getAttributeList());
        updateGraphFilters(graphId, graph.getFilterList());

        return graph;
    }

    @Transactional
    public void deleteGraph(String graphId){
        GraphDbType graphDbEntry = graphsDao.find(graphId);

        graphsDao.delete(graphDbEntry);
        graphAttributesDao.deleteGraphAttributes(graphId);
        graphFiltersDao.deleteGraphFilters(graphId);
    }

    public List<Graph> getGraphs(int companyId) {
        List<GraphDbType> graphs = graphsDao.getGraphs(companyId);

        List<String> graphIds = new ArrayList<String>(graphs.size());
        for (GraphDbType graph : graphs) {
            graphIds.add(graph.getGraphId());
        }

        List<GraphAttributesDbType> graphAttributes = graphAttributesDao.getGraphAttributes(graphIds);
        List<GraphFiltersDbType> graphFilters = graphFiltersDao.getGraphFilters(graphIds);

        return matchGraphsAndAttributes(graphs, graphAttributes, graphFilters);
    }

    public Graph getGraph(String graphId){
        GraphDbType graphDbEntry = graphsDao.find(graphId);
        List<GraphDbType> list = new ArrayList<GraphDbType>(1);
        list.add(graphDbEntry);

        List<GraphAttributesDbType> graphAttributes = graphAttributesDao.getGraphAttributes(graphId);
        List<GraphFiltersDbType> graphFilters = graphFiltersDao.getGraphFilters(graphId);

        return matchGraphsAndAttributes(list, graphAttributes, graphFilters).get(0);
    }

    private void updateGraphAttributes(String graphId, List<Integer> attributeList){
        List<GraphAttributesDbType> graphAttributesDbEntries = graphAttributesDao.getGraphAttributes(graphId);

        Collections.sort(graphAttributesDbEntries, Comparators.COMPARE_GRAPH_ATTRIBUTES);
        Collections.sort(attributeList);

        int dbIndex = 0, inIndex = 0;
        while(dbIndex<graphAttributesDbEntries.size() && inIndex< attributeList.size()){
            int attribute = attributeList.get(inIndex);
            GraphAttributesDbType graphAttributesDbEntry = graphAttributesDbEntries.get(dbIndex);

            if(graphAttributesDbEntry.getId().getAttributeId() == attribute){
                dbIndex++;
                inIndex++;
            } else if (graphAttributesDbEntry.getId().getAttributeId() < attribute){
                graphAttributesDao.delete(graphAttributesDbEntry);
                dbIndex++;
            } else {
                //graphAttributesDbEntry.getId().getAttributeId() > attribute
                graphAttributesDao.add(Conversions.getGraphAttributeDbEntry(graphId, attribute));
                inIndex++;
            }
        }
        while(dbIndex<graphAttributesDbEntries.size()){
            graphAttributesDao.delete(graphAttributesDbEntries.get(dbIndex));
            dbIndex++;
        }
        while(inIndex< attributeList.size()){
            graphAttributesDao.add(Conversions.getGraphAttributeDbEntry(graphId, attributeList.get(inIndex)));
            inIndex++;
        }

    }

    private void updateGraphFilters(String graphId, List<Integer> filterList){
        List<GraphFiltersDbType> graphFiltersDbEntries = graphFiltersDao.getGraphFilters(graphId);

        Collections.sort(graphFiltersDbEntries, Comparators.COMPARE_GRAPH_FILTERS);
        Collections.sort(filterList);

        int dbIndex = 0, inIndex = 0;
        while(dbIndex<graphFiltersDbEntries.size() && inIndex< filterList.size()){
            int attribute = filterList.get(inIndex);
            GraphFiltersDbType graphFiltersDbEntry = graphFiltersDbEntries.get(dbIndex);

            if(graphFiltersDbEntry.getId().getAttributeId() == attribute){
                dbIndex++;
                inIndex++;
            } else if (graphFiltersDbEntry.getId().getAttributeId() < attribute){
                graphFiltersDao.delete(graphFiltersDbEntry);
                dbIndex++;
            } else {
                //graphAttributesDbEntry.getId().getAttributeId() > attribute
                graphFiltersDao.add(Conversions.getGraphFilterDbEntry(graphId, attribute));
                inIndex++;
            }
        }
        while(dbIndex<graphFiltersDbEntries.size()){
            graphFiltersDao.delete(graphFiltersDbEntries.get(dbIndex));
            dbIndex++;
        }
        while(inIndex< filterList.size()){
            graphFiltersDao.add(Conversions.getGraphFilterDbEntry(graphId, filterList.get(inIndex)));
            inIndex++;
        }
    }

    private void addGraphAttributes(String graphId, List<Integer> attributeList) {
        for (int attributeId : attributeList) {
            graphAttributesDao.add(Conversions.getGraphAttributeDbEntry(graphId, attributeId));
        }
    }

    private void addGraphFilters(String graphId, List<Integer> filterList) {
        for (int attributeId : filterList) {
            graphFiltersDao.add(Conversions.getGraphFilterDbEntry(graphId, attributeId));
        }
    }

    private List<Graph> matchGraphsAndAttributes(List<GraphDbType> graphs, List<GraphAttributesDbType> graphAttributes,
                                                 List<GraphFiltersDbType> graphFilters) {
        List<Graph> out = new ArrayList<Graph>(graphs.size());

        Collections.sort(graphs, Comparators.COMPARE_GRAPHS);
        Collections.sort(graphAttributes, Comparators.COMPARE_GRAPH_ATTRIBUTES);
        Collections.sort(graphFilters, Comparators.COMPARE_GRAPH_FILTERS);

        int gIndex = 0, aIndex = 0, fIndex = 0;
        while (gIndex < graphs.size()) {
            GraphDbType graphDbEntry = graphs.get(gIndex);
            Graph graph = Conversions.getGraph(graphDbEntry);
            List<Integer> attributeList = new ArrayList<Integer>();
            List<Integer> filterList = new ArrayList<Integer>();

            graph.setAttributeList(attributeList);
            graph.setFilterList(filterList);

            while (aIndex < graphAttributes.size() && graph.getGraphId().equals(graphAttributes.get(aIndex).getId().getGraphId())) {
                attributeList.add(graphAttributes.get(aIndex).getId().getAttributeId());
                aIndex++;
            }

            while (fIndex < graphFilters.size() && graph.getGraphId().equals(graphFilters.get(fIndex).getId().getGraphId())) {
                filterList.add(graphFilters.get(fIndex).getId().getAttributeId());
                fIndex++;
            }
            gIndex++;
            out.add(graph);
        }
        return out;
    }

    private static class Conversions {
        private static GraphDbType getGraphDbEntry(String graphId, int companyId, String name) {
            GraphDbType graphDbEntry = new GraphDbType();

            graphDbEntry.setCompanyId(companyId);
            graphDbEntry.setGraphId(graphId);
            graphDbEntry.setName(name);

            return graphDbEntry;
        }

        private static Graph getGraph(GraphDbType graphDbEntry) {
            Graph graph = new Graph();
            graph.setGraphId(graphDbEntry.getGraphId());
            graph.setName(graphDbEntry.getName());

            return graph;
        }

        private static GraphAttributesDbType getGraphAttributeDbEntry(String graphID, int attributeId) {
            GraphAttributesDbType entry = new GraphAttributesDbType();
            GraphAttributesPrimaryKey key = new GraphAttributesPrimaryKey();
            entry.setId(key);

            key.setAttributeId(attributeId);
            key.setGraphId(graphID);

            return entry;

        }

        private static GraphFiltersDbType getGraphFilterDbEntry(String graphID, int attributeId) {
            GraphFiltersDbType entry = new GraphFiltersDbType();
            GraphFiltersPrimaryKey key = new GraphFiltersPrimaryKey();
            entry.setId(key);

            key.setAttributeId(attributeId);
            key.setGraphId(graphID);

            return entry;
        }
    }

    private static class Comparators {
        private static Comparator<GraphDbType> COMPARE_GRAPHS = new Comparator<GraphDbType>() {
            @Override
            public int compare(GraphDbType first, GraphDbType second) {
                return first.getGraphId().compareTo(second.getGraphId());
            }
        };

        private static Comparator<GraphAttributesDbType> COMPARE_GRAPH_ATTRIBUTES = new Comparator<GraphAttributesDbType>() {
            @Override
            public int compare(GraphAttributesDbType first, GraphAttributesDbType second) {
                if (first.getId().getGraphId().equals(second.getId().getGraphId())) {
                    return first.getId().getAttributeId() - second.getId().getAttributeId();
                }
                return first.getId().getGraphId().compareTo(second.getId().getGraphId());
            }
        };

        private static Comparator<GraphFiltersDbType> COMPARE_GRAPH_FILTERS = new Comparator<GraphFiltersDbType>() {
            @Override
            public int compare(GraphFiltersDbType first, GraphFiltersDbType second) {
                if (first.getId().getGraphId().equals(second.getId().getGraphId())) {
                    return first.getId().getAttributeId() - second.getId().getAttributeId();
                }
                return first.getId().getGraphId().compareTo(second.getId().getGraphId());
            }
        };
    }
}


