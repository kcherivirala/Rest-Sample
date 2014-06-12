package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Graph.Entities.*;
import com.fbr.Dao.Graph.GraphAttributesDao;
import com.fbr.Dao.Graph.GraphFiltersDao;
import com.fbr.Dao.Graph.GraphsDao;
import com.fbr.Utilities.Comparators;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Graph.Graph;
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
    private AttributeService attributeService;

    @Transactional
    public Graph addGraph(int companyId, Graph graph) {
        logger.info("adding graph for company : " + companyId + " and graph : " + graph.getName());
        String id = UUID.randomUUID().toString();

        graphsDao.add(Conversions.getGraphDbEntry(id, companyId, graph));

        addGraphAttributes(id, graph.getAttributeList());
        addGraphFilters(id, graph.getFilterList());

        graph.setGraphId(id);
        logger.info("done adding graph for company : " + companyId + " and graph : " + graph.getName());
        return graph;
    }

    @Transactional
    public Graph updateGraph(int companyId, String graphId, Graph graph) {
        logger.info("updating graph for company : " + companyId + " and graph : " + graph.getName());
        GraphDbType graphDbEntry = graphsDao.find(graphId);
        if (!graph.getName().equals(graphDbEntry.getName())) {
            graphDbEntry.setName(graph.getName());
            graphsDao.update(graphDbEntry);
        }
        updateGraphAttributes(graphId, graph.getAttributeList());
        updateGraphFilters(graphId, graph.getFilterList());

        logger.info("done updating graph for company : " + companyId + " and graph : " + graph.getName());
        return graph;
    }

    @Transactional
    public void deleteGraph(int companyId, String graphId) {
        logger.info("deleting graph for company : " + companyId + " and graph : " + graphId);
        GraphDbType graphDbEntry = graphsDao.find(graphId);

        graphsDao.delete(graphDbEntry);
        graphAttributesDao.deleteGraphAttributes(graphId);
        graphFiltersDao.deleteGraphFilters(graphId);
        logger.info("done deleting graph for company : " + companyId + " and graph : " + graphId);
    }

    public List<Graph> getGraphs(int companyId) {
        logger.info("getting graphs for company : " + companyId);
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
        List<Attribute> attributeDbEntries = attributeService.getAttributesByCompany(companyId);

        List<Graph> out = matchGraphsAndAttributes(graphs, graphAttributes, graphFilters, attributeDbEntries);
        logger.info("done getting graphs for company : " + companyId);
        return out;
    }

    public Graph getGraph(int companyId, String graphId) {
        logger.info("getting graph for company : " + companyId + " and graph : " + graphId);
        GraphDbType graphDbEntry = graphsDao.find(graphId);
        List<GraphDbType> list = new ArrayList<GraphDbType>(1);
        list.add(graphDbEntry);

        List<GraphAttributesDbType> graphAttributes = graphAttributesDao.getGraphAttributes(graphId);
        List<GraphFiltersDbType> graphFilters = graphFiltersDao.getGraphFilters(graphId);
        List<Attribute> attributes = attributeService.getAttributesByCompany(graphDbEntry.getCompanyId());

        Graph out = matchGraphsAndAttributes(list, graphAttributes, graphFilters, attributes).get(0);
        logger.info("done getting graph for company : " + companyId + " and graph : " + graphId);
        return out;
    }

    /* private functions */

    private void updateGraphAttributes(String graphId, List<Attribute> attributeList) {
        List<GraphAttributesDbType> graphAttributesDbEntries = graphAttributesDao.getGraphAttributes(graphId);

        Collections.sort(graphAttributesDbEntries, Comparators.COMPARE_GRAPH_ATTRIBUTES);
        Collections.sort(attributeList, Comparators.COMPARE_DOMAIN_ATTRIBUTES);

        int dbIndex = 0, inIndex = 0;
        while (dbIndex < graphAttributesDbEntries.size() && inIndex < attributeList.size()) {
            int attribute = attributeList.get(inIndex).getAttributeId();
            GraphAttributesDbType graphAttributesDbEntry = graphAttributesDbEntries.get(dbIndex);

            if (graphAttributesDbEntry.getId().getAttributeId() == attribute) {
                dbIndex++;
                inIndex++;
            } else if (graphAttributesDbEntry.getId().getAttributeId() < attribute) {
                logger.debug("deleting graph attribute : (" + graphId + "," + graphAttributesDbEntry.getId().getAttributeId() + ")");
                graphAttributesDao.delete(graphAttributesDbEntry);
                dbIndex++;
            } else {
                //graphAttributesDbEntry.getId().getAttributeId() > attribute
                logger.debug("adding graph attribute : (" + graphId + "," + attribute + ")");
                graphAttributesDao.add(Conversions.getGraphAttributeDbEntry(graphId, attribute));
                inIndex++;
            }
        }
        while (dbIndex < graphAttributesDbEntries.size()) {
            logger.debug("deleting graph attribute : (" + graphId + "," + graphAttributesDbEntries.get(dbIndex).getId().getAttributeId() + ")");
            graphAttributesDao.delete(graphAttributesDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inIndex < attributeList.size()) {
            logger.debug("adding graph attribute : (" + graphId + "," + attributeList.get(inIndex) + ")");
            graphAttributesDao.add(Conversions.getGraphAttributeDbEntry(graphId, attributeList.get(inIndex).getAttributeId()));
            inIndex++;
        }

    }

    private void updateGraphFilters(String graphId, List<Attribute> filterList) {
        List<GraphFiltersDbType> graphFiltersDbEntries = graphFiltersDao.getGraphFilters(graphId);

        Collections.sort(graphFiltersDbEntries, Comparators.COMPARE_GRAPH_FILTERS);
        Collections.sort(filterList, Comparators.COMPARE_DOMAIN_ATTRIBUTES);

        int dbIndex = 0, inIndex = 0;
        while (dbIndex < graphFiltersDbEntries.size() && inIndex < filterList.size()) {
            int attribute = filterList.get(inIndex).getAttributeId();
            GraphFiltersDbType graphFiltersDbEntry = graphFiltersDbEntries.get(dbIndex);

            if (graphFiltersDbEntry.getId().getAttributeId() == attribute) {
                dbIndex++;
                inIndex++;
            } else if (graphFiltersDbEntry.getId().getAttributeId() < attribute) {
                logger.debug("deleting graph attribute : (" + graphId + "," + graphFiltersDbEntry.getId().getAttributeId() + ")");
                graphFiltersDao.delete(graphFiltersDbEntry);
                dbIndex++;
            } else {
                //graphAttributesDbEntry.getId().getAttributeId() > attribute
                logger.debug("adding graph attribute : (" + graphId + "," + attribute + ")");
                graphFiltersDao.add(Conversions.getGraphFilterDbEntry(graphId, attribute));
                inIndex++;
            }
        }
        while (dbIndex < graphFiltersDbEntries.size()) {
            logger.debug("deleting graph attribute : (" + graphId + "," + graphFiltersDbEntries.get(dbIndex).getId().getAttributeId() + ")");
            graphFiltersDao.delete(graphFiltersDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inIndex < filterList.size()) {
            logger.debug("adding graph attribute : (" + graphId + "," + filterList.get(inIndex) + ")");
            graphFiltersDao.add(Conversions.getGraphFilterDbEntry(graphId, filterList.get(inIndex).getAttributeId()));
            inIndex++;
        }
    }

    private void addGraphAttributes(String graphId, List<Attribute> attributeList) {
        for (Attribute attribute : attributeList) {
            graphAttributesDao.add(Conversions.getGraphAttributeDbEntry(graphId, attribute.getAttributeId()));
        }
    }

    private void addGraphFilters(String graphId, List<Attribute> filterList) {
        for (Attribute attribute : filterList) {
            graphFiltersDao.add(Conversions.getGraphFilterDbEntry(graphId, attribute.getAttributeId()));
        }
    }

    private List<Graph> matchGraphsAndAttributes(List<GraphDbType> graphs, List<GraphAttributesDbType> graphAttributes,
                                                 List<GraphFiltersDbType> graphFilters, List<Attribute> attributes) {
        List<Graph> out = new ArrayList<Graph>(graphs.size());

        Collections.sort(graphs, Comparators.COMPARE_GRAPHS);
        Collections.sort(graphAttributes, Comparators.COMPARE_GRAPH_ATTRIBUTES);
        Collections.sort(graphFilters, Comparators.COMPARE_GRAPH_FILTERS);

        int gIndex = 0, aIndex = 0, fIndex = 0;
        while (gIndex < graphs.size()) {
            int dbIndex = 0;
            GraphDbType graphDbEntry = graphs.get(gIndex);
            Graph graph = Conversions.getGraph(graphDbEntry);
            List<Attribute> attributeList = new ArrayList<Attribute>();
            List<Attribute> filterList = new ArrayList<Attribute>();

            graph.setAttributeList(attributeList);
            graph.setFilterList(filterList);

            while (dbIndex < attributes.size()) {
                Attribute attribute = attributes.get(dbIndex);
                if (aIndex < graphAttributes.size() && graph.getGraphId().equals(graphAttributes.get(aIndex).getId().getGraphId()) && graphAttributes.get(aIndex).getId().getAttributeId() == attribute.getAttributeId()) {
                    attributeList.add(attribute);
                    aIndex++;
                } else if (aIndex < graphAttributes.size() && graph.getGraphId().equals(graphAttributes.get(aIndex).getId().getGraphId()) && graphAttributes.get(aIndex).getId().getAttributeId() < attribute.getAttributeId()) {
                    aIndex++;
                } else if (fIndex < graphFilters.size() && graph.getGraphId().equals(graphFilters.get(fIndex).getId().getGraphId()) && graphFilters.get(fIndex).getId().getAttributeId() == attribute.getAttributeId()) {
                    filterList.add(attribute);
                    fIndex++;
                } else if (fIndex < graphFilters.size() && graph.getGraphId().equals(graphFilters.get(fIndex).getId().getGraphId()) && graphFilters.get(fIndex).getId().getAttributeId() < attribute.getAttributeId()) {
                    fIndex++;
                }
                dbIndex++;
            }
            while (aIndex < graphAttributes.size() && graph.getGraphId().equals(graphAttributes.get(aIndex).getId().getGraphId())) {
                aIndex++;
            }
            while (fIndex < graphFilters.size() && graph.getGraphId().equals(graphFilters.get(fIndex).getId().getGraphId())) {
                fIndex++;
            }
            gIndex++;
            out.add(graph);
        }
        return out;
    }


    public static class Conversions {
        private static GraphDbType getGraphDbEntry(String graphId, int companyId, Graph graph) {
            GraphDbType graphDbEntry = new GraphDbType();

            graphDbEntry.setCompanyId(companyId);
            graphDbEntry.setGraphId(graphId);
            graphDbEntry.setName(graph.getName());
            graphDbEntry.setType(graph.getType());

            return graphDbEntry;
        }

        private static Graph getGraph(GraphDbType graphDbEntry) {
            Graph graph = new Graph();
            graph.setGraphId(graphDbEntry.getGraphId());
            graph.setName(graphDbEntry.getName());
            graph.setType(graphDbEntry.getType());

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
}


