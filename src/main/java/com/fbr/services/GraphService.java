package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Graph.Entities.*;
import com.fbr.Dao.Graph.*;
import com.fbr.domain.Graph.Graph;
import com.fbr.domain.Graph.Trend;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
    public Graph updateGraph(String graphId, Graph graph) {
        GraphDbType graphDbEntry = graphsDao.find(graphId);
        if (!graph.getName().equals(graphDbEntry.getName())) {
            graphDbEntry.setName(graph.getName());
            graphsDao.update(graphDbEntry);
        }
        updateGraphAttributes(graphId, graph.getAttributeList());
        updateGraphFilters(graphId, graph.getFilterList());

        return graph;
    }

    @Transactional
    public void deleteGraph(String graphId) {
        GraphDbType graphDbEntry = graphsDao.find(graphId);

        graphsDao.delete(graphDbEntry);
        graphAttributesDao.deleteGraphAttributes(graphId);
        graphFiltersDao.deleteGraphFilters(graphId);
    }

    public List<Graph> getGraphs(int companyId) {
        List<GraphDbType> graphs = graphsDao.getGraphs(companyId);

        if (graphs.size() == 0) {
            return null;
        }

        List<String> graphIds = new ArrayList<String>(graphs.size());
        for (GraphDbType graph : graphs) {
            graphIds.add(graph.getGraphId());
        }

        List<GraphAttributesDbType> graphAttributes = graphAttributesDao.getGraphAttributes(graphIds);
        List<GraphFiltersDbType> graphFilters = graphFiltersDao.getGraphFilters(graphIds);

        return matchGraphsAndAttributes(graphs, graphAttributes, graphFilters);
    }

    public Graph getGraph(String graphId) {
        GraphDbType graphDbEntry = graphsDao.find(graphId);
        List<GraphDbType> list = new ArrayList<GraphDbType>(1);
        list.add(graphDbEntry);

        List<GraphAttributesDbType> graphAttributes = graphAttributesDao.getGraphAttributes(graphId);
        List<GraphFiltersDbType> graphFilters = graphFiltersDao.getGraphFilters(graphId);

        return matchGraphsAndAttributes(list, graphAttributes, graphFilters).get(0);
    }

    private void updateGraphAttributes(String graphId, List<Integer> attributeList) {
        List<GraphAttributesDbType> graphAttributesDbEntries = graphAttributesDao.getGraphAttributes(graphId);

        Collections.sort(graphAttributesDbEntries, Comparators.COMPARE_GRAPH_ATTRIBUTES);
        Collections.sort(attributeList);

        int dbIndex = 0, inIndex = 0;
        while (dbIndex < graphAttributesDbEntries.size() && inIndex < attributeList.size()) {
            int attribute = attributeList.get(inIndex);
            GraphAttributesDbType graphAttributesDbEntry = graphAttributesDbEntries.get(dbIndex);

            if (graphAttributesDbEntry.getId().getAttributeId() == attribute) {
                dbIndex++;
                inIndex++;
            } else if (graphAttributesDbEntry.getId().getAttributeId() < attribute) {
                graphAttributesDao.delete(graphAttributesDbEntry);
                dbIndex++;
            } else {
                //graphAttributesDbEntry.getId().getAttributeId() > attribute
                graphAttributesDao.add(Conversions.getGraphAttributeDbEntry(graphId, attribute));
                inIndex++;
            }
        }
        while (dbIndex < graphAttributesDbEntries.size()) {
            graphAttributesDao.delete(graphAttributesDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inIndex < attributeList.size()) {
            graphAttributesDao.add(Conversions.getGraphAttributeDbEntry(graphId, attributeList.get(inIndex)));
            inIndex++;
        }

    }

    private void updateGraphFilters(String graphId, List<Integer> filterList) {
        List<GraphFiltersDbType> graphFiltersDbEntries = graphFiltersDao.getGraphFilters(graphId);

        Collections.sort(graphFiltersDbEntries, Comparators.COMPARE_GRAPH_FILTERS);
        Collections.sort(filterList);

        int dbIndex = 0, inIndex = 0;
        while (dbIndex < graphFiltersDbEntries.size() && inIndex < filterList.size()) {
            int attribute = filterList.get(inIndex);
            GraphFiltersDbType graphFiltersDbEntry = graphFiltersDbEntries.get(dbIndex);

            if (graphFiltersDbEntry.getId().getAttributeId() == attribute) {
                dbIndex++;
                inIndex++;
            } else if (graphFiltersDbEntry.getId().getAttributeId() < attribute) {
                graphFiltersDao.delete(graphFiltersDbEntry);
                dbIndex++;
            } else {
                //graphAttributesDbEntry.getId().getAttributeId() > attribute
                graphFiltersDao.add(Conversions.getGraphFilterDbEntry(graphId, attribute));
                inIndex++;
            }
        }
        while (dbIndex < graphFiltersDbEntries.size()) {
            graphFiltersDao.delete(graphFiltersDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inIndex < filterList.size()) {
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


    /*

     */


    @Transactional
    public Trend addTrend(int companyId, Trend trend) {
        String id = UUID.randomUUID().toString();

        trendsDao.add(Conversions.getTrendDbEntry(id, companyId, trend.getName()));

        addTrendAttributes(id, trend.getAttributeList());
        addTrendFilters(id, trend.getFilterList());

        trend.setTrendId(id);
        return trend;
    }

    @Transactional
    public Trend updateTrend(String trendId, Trend trend) {
        TrendDbType trendDbEntry = trendsDao.find(trendId);
        if (!trend.getName().equals(trendDbEntry.getName())) {
            trendDbEntry.setName(trend.getName());
            trendsDao.update(trendDbEntry);
        }
        updateTrendAttributes(trendId, trend.getAttributeList());
        updateTrendFilters(trendId, trend.getFilterList());

        return trend;
    }

    @Transactional
    public void deleteTrend(String trendId) {
        TrendDbType trendDbEntry = trendsDao.find(trendId);

        trendsDao.delete(trendDbEntry);
        trendAttributesDao.deleteTrendAttributes(trendId);
        trendFiltersDao.deleteTrendFilters(trendId);
    }

    public List<Trend> getTrends(int companyId) {
        List<TrendDbType> trends = trendsDao.getTrends(companyId);

        if (trends.size() == 0) {
            return null;
        }

        List<String> trendIds = new ArrayList<String>(trends.size());
        for (TrendDbType trend : trends) {
            trendIds.add(trend.getTrendId());
        }

        List<TrendAttributesDbType> trendAttributes = trendAttributesDao.getTrendAttributes(trendIds);
        List<TrendFiltersDbType> trendFilters = trendFiltersDao.getTrendFilters(trendIds);

        return matchTrendsAndAttributes(trends, trendAttributes, trendFilters);
    }

    public Trend getTrend(String trendId) {
        TrendDbType trendDbEntry = trendsDao.find(trendId);
        List<TrendDbType> list = new ArrayList<TrendDbType>(1);
        list.add(trendDbEntry);

        List<TrendAttributesDbType> trendAttributes = trendAttributesDao.getTrendAttributes(trendId);
        List<TrendFiltersDbType> trendFilters = trendFiltersDao.getTrendFilters(trendId);

        return matchTrendsAndAttributes(list, trendAttributes, trendFilters).get(0);
    }

    private void updateTrendAttributes(String trendId, List<Integer> attributeList) {
        List<TrendAttributesDbType> trendAttributesDbEntries = trendAttributesDao.getTrendAttributes(trendId);

        Collections.sort(trendAttributesDbEntries, Comparators.COMPARE_TREND_ATTRIBUTES);
        Collections.sort(attributeList);

        int dbIndex = 0, inIndex = 0;
        while (dbIndex < trendAttributesDbEntries.size() && inIndex < attributeList.size()) {
            int attribute = attributeList.get(inIndex);
            TrendAttributesDbType trendAttributesDbEntry = trendAttributesDbEntries.get(dbIndex);

            if (trendAttributesDbEntry.getId().getAttributeId() == attribute) {
                dbIndex++;
                inIndex++;
            } else if (trendAttributesDbEntry.getId().getAttributeId() < attribute) {
                trendAttributesDao.delete(trendAttributesDbEntry);
                dbIndex++;
            } else {
                //trendAttributesDbEntry.getId().getAttributeId() > attribute
                trendAttributesDao.add(Conversions.getTrendAttributeDbEntry(trendId, attribute));
                inIndex++;
            }
        }
        while (dbIndex < trendAttributesDbEntries.size()) {
            trendAttributesDao.delete(trendAttributesDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inIndex < attributeList.size()) {
            trendAttributesDao.add(Conversions.getTrendAttributeDbEntry(trendId, attributeList.get(inIndex)));
            inIndex++;
        }

    }

    private void updateTrendFilters(String trendId, List<Integer> filterList) {
        List<TrendFiltersDbType> trendFiltersDbEntries = trendFiltersDao.getTrendFilters(trendId);

        Collections.sort(trendFiltersDbEntries, Comparators.COMPARE_TREND_FILTERS);
        Collections.sort(filterList);

        int dbIndex = 0, inIndex = 0;
        while (dbIndex < trendFiltersDbEntries.size() && inIndex < filterList.size()) {
            int attribute = filterList.get(inIndex);
            TrendFiltersDbType trendFiltersDbEntry = trendFiltersDbEntries.get(dbIndex);

            if (trendFiltersDbEntry.getId().getAttributeId() == attribute) {
                dbIndex++;
                inIndex++;
            } else if (trendFiltersDbEntry.getId().getAttributeId() < attribute) {
                trendFiltersDao.delete(trendFiltersDbEntry);
                dbIndex++;
            } else {
                //trendAttributesDbEntry.getId().getAttributeId() > attribute
                trendFiltersDao.add(Conversions.getTrendFilterDbEntry(trendId, attribute));
                inIndex++;
            }
        }
        while (dbIndex < trendFiltersDbEntries.size()) {
            trendFiltersDao.delete(trendFiltersDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inIndex < filterList.size()) {
            trendFiltersDao.add(Conversions.getTrendFilterDbEntry(trendId, filterList.get(inIndex)));
            inIndex++;
        }
    }

    private void addTrendAttributes(String trendId, List<Integer> attributeList) {
        for (int attributeId : attributeList) {
            trendAttributesDao.add(Conversions.getTrendAttributeDbEntry(trendId, attributeId));
        }
    }

    private void addTrendFilters(String trendId, List<Integer> filterList) {
        for (int attributeId : filterList) {
            trendFiltersDao.add(Conversions.getTrendFilterDbEntry(trendId, attributeId));
        }
    }

    private List<Trend> matchTrendsAndAttributes(List<TrendDbType> trends, List<TrendAttributesDbType> trendAttributes,
                                                 List<TrendFiltersDbType> trendFilters) {
        List<Trend> out = new ArrayList<Trend>(trends.size());

        Collections.sort(trends, Comparators.COMPARE_TRENDS);
        Collections.sort(trendAttributes, Comparators.COMPARE_TREND_ATTRIBUTES);
        Collections.sort(trendFilters, Comparators.COMPARE_TREND_FILTERS);

        int gIndex = 0, aIndex = 0, fIndex = 0;
        while (gIndex < trends.size()) {
            TrendDbType trendDbEntry = trends.get(gIndex);
            Trend trend = Conversions.getTrend(trendDbEntry);
            List<Integer> attributeList = new ArrayList<Integer>();
            List<Integer> filterList = new ArrayList<Integer>();

            trend.setAttributeList(attributeList);
            trend.setFilterList(filterList);

            while (aIndex < trendAttributes.size() && trend.getTrendId().equals(trendAttributes.get(aIndex).getId().getTrendId())) {
                attributeList.add(trendAttributes.get(aIndex).getId().getAttributeId());
                aIndex++;
            }

            while (fIndex < trendFilters.size() && trend.getTrendId().equals(trendFilters.get(fIndex).getId().getTrendId())) {
                filterList.add(trendFilters.get(fIndex).getId().getAttributeId());
                fIndex++;
            }
            gIndex++;
            out.add(trend);
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


        private static TrendDbType getTrendDbEntry(String trendId, int companyId, String name) {
            TrendDbType trendDbEntry = new TrendDbType();

            trendDbEntry.setCompanyId(companyId);
            trendDbEntry.setTrendId(trendId);
            trendDbEntry.setName(name);

            return trendDbEntry;
        }

        private static Trend getTrend(TrendDbType trendDbEntry) {
            Trend trend = new Trend();
            trend.setTrendId(trendDbEntry.getTrendId());
            trend.setName(trendDbEntry.getName());

            return trend;
        }

        private static TrendAttributesDbType getTrendAttributeDbEntry(String trendID, int attributeId) {
            TrendAttributesDbType entry = new TrendAttributesDbType();
            TrendAttributesPrimaryKey key = new TrendAttributesPrimaryKey();
            entry.setId(key);

            key.setAttributeId(attributeId);
            key.setTrendId(trendID);

            return entry;

        }

        private static TrendFiltersDbType getTrendFilterDbEntry(String trendID, int attributeId) {
            TrendFiltersDbType entry = new TrendFiltersDbType();
            TrendFiltersPrimaryKey key = new TrendFiltersPrimaryKey();
            entry.setId(key);

            key.setAttributeId(attributeId);
            key.setTrendId(trendID);

            return entry;
        }
    }
}


