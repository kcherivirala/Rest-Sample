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
    public Graph addGraph(int companyId, Graph graph) throws Exception {
        logger.info("adding graph for company : " + companyId + " and graph : " + graph.getName());
        String id = UUID.randomUUID().toString();

        if (!check(graph.getAttributeList(), graph.getFilterList(), attributeService.getAttributesByCompany(companyId))) {
            logger.error("graph contains attributes not part of the company : " + graph.getName());
            throw new Exception("graph contains attributes not part of the company : " + graph.getName());
        }
        try {
            graphsDao.add(Conversions.getGraphDbEntry(id, companyId, graph));

            addGraphAttributes(id, graph.getAttributeList());
            addGraphFilters(id, graph.getFilterList());

            graph.setGraphId(id);
            logger.info("done adding graph for company : " + companyId + " and graph : " + graph.getName());
            return graph;
        } catch (Exception e) {
            logger.error("error creating graph : " + graph.getName() + " : " + e.getMessage());
            throw new Exception("error creating graph : " + graph.getName() + " : " + e.getMessage());
        }
    }

    @Transactional
    public Graph updateGraph(int companyId, String graphId, Graph graph) throws Exception {
        logger.info("updating graph for company : " + companyId + " and graph : " + graph.getName());

        if (!check(graph.getAttributeList(), graph.getFilterList(), attributeService.getAttributesByCompany(companyId))) {
            logger.error("graph contains attributes not part of the company : " + graph.getName());
            throw new Exception("graph contains attributes not part of the company : " + graph.getName());
        }

        try {
            GraphDbType graphDbEntry = graphsDao.find(graphId);
            if (!graph.getName().equals(graphDbEntry.getName())) {
                graphDbEntry.setName(graph.getName());
                graphsDao.update(graphDbEntry);
            }

            updateGraphAttributes(graphId, graphDbEntry.getGraphAttributes(), graph.getAttributeList());
            updateGraphFilters(graphId, graphDbEntry.getGraphFilters(), graph.getFilterList());

            logger.info("done updating graph for company : " + companyId + " and graph : " + graph.getName());
            return graph;
        } catch (Exception e) {
            logger.error("error updating graph : " + graph.getName() + " : " + e.getMessage());
            throw new Exception("error updating graph : " + graph.getName() + " : " + e.getMessage());
        }
    }

    @Transactional
    public void deleteGraph(int companyId, String graphId) throws Exception {
        try {
            logger.info("deleting graph for company : " + companyId + " and graph : " + graphId);
            GraphDbType graphDbEntry = graphsDao.find(graphId);
            graphAttributesDao.deleteGraphAttributes(graphId);
            graphFiltersDao.deleteGraphFilters(graphId);
            graphsDao.delete(graphDbEntry);
            logger.info("done deleting graph for company : " + companyId + " and graph : " + graphId);
        } catch (Exception e) {
            logger.error("error deleting graph : " + graphId + " : " + e.getMessage());
            throw new Exception("error deleting graph : " + graphId + " : " + e.getMessage());
        }
    }

    @Transactional
    public List<Graph> getGraphs(int companyId) throws Exception {
        try {
            logger.info("getting graphs for company : " + companyId);
            List<GraphDbType> graphs = graphsDao.getGraphs(companyId);
            List<Attribute> attributeDbEntries = attributeService.getAttributesByCompany(companyId);

            List<Graph> out = Conversions.getGraphs(graphs, attributeDbEntries);
            logger.info("done getting graphs for company : " + companyId);
            return out;
        } catch (Exception e) {
            logger.error("error getting graphs : " + e.getMessage());
            throw new Exception("error getting graphs : " + e.getMessage());
        }
    }

    @Transactional
    public Graph getGraph(int companyId, String graphId) throws Exception {
        try {
            logger.info("getting graph for company : " + companyId + " and graph : " + graphId);
            GraphDbType graphDbEntry = graphsDao.find(graphId);
            List<Attribute> attributes = attributeService.getAttributesByCompany(graphDbEntry.getCompanyId());

            Graph out = Conversions.getGraph(graphDbEntry, attributes);
            logger.info("done getting graph for company : " + companyId + " and graph : " + graphId);
            return out;
        } catch (Exception e) {
            logger.error("error getting graph : " + graphId + " : " + e.getMessage());
            throw new Exception("error getting graph : " + graphId + " : " + e.getMessage());
        }
    }

    /* private functions */

    private void updateGraphAttributes(String graphId, List<GraphAttributesDbType> graphAttributesDbEntries, List<Attribute> attributeList) {
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

    private void updateGraphFilters(String graphId, List<GraphFiltersDbType> graphFiltersDbEntries, List<Attribute> filterList) {
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

    private boolean check(List<Attribute> attributeList, List<Attribute> filterList, List<Attribute> attributesOfCompany) {
        Collections.sort(attributeList, Comparators.COMPARE_DOMAIN_ATTRIBUTES);
        Collections.sort(filterList, Comparators.COMPARE_DOMAIN_ATTRIBUTES);
        Collections.sort(attributesOfCompany, Comparators.COMPARE_DOMAIN_ATTRIBUTES);

        int i = 0, j = 0;
        for (Attribute attribute : attributesOfCompany) {
            if (attribute.getAttributeId() == attributeList.get(i).getAttributeId()) {
                i++;
            } else if (attribute.getAttributeId() == filterList.get(j).getAttributeId()) {
                j++;
            } else if (attribute.getAttributeId() > attributeList.get(i).getAttributeId() || attribute.getAttributeId() > filterList.get(j).getAttributeId()) {
                return false;
            }
        }
        return true;
    }


    public static class Conversions {
        private static int getIndex(int attrId, List<Attribute> list) {
            int index = -1;
            int i = 0;
            for (Attribute attribute : list) {
                if (attribute.getAttributeId() == attrId) {
                    index = i;
                    break;
                }
                i++;
            }
            return index;
        }

        private static GraphDbType getGraphDbEntry(String graphId, int companyId, Graph graph) {
            GraphDbType graphDbEntry = new GraphDbType();

            graphDbEntry.setCompanyId(companyId);
            graphDbEntry.setGraphId(graphId);
            graphDbEntry.setName(graph.getName());
            graphDbEntry.setType(graph.getType());

            return graphDbEntry;
        }

        private static List<Graph> getGraphs(List<GraphDbType> graphs, List<Attribute> attributes) throws Exception {
            List<Graph> out = new ArrayList<Graph>(graphs.size());
            for (GraphDbType graphDbType : graphs) {
                out.add(getGraph(graphDbType, attributes));
            }
            return out;
        }

        private static Graph getGraph(GraphDbType graphDbEntry, List<Attribute> attributes) throws Exception {
            Graph graph = new Graph();
            graph.setGraphId(graphDbEntry.getGraphId());
            graph.setName(graphDbEntry.getName());
            graph.setType(graphDbEntry.getType());

            List<Attribute> graphAttributes = new ArrayList<Attribute>(graphDbEntry.getGraphAttributes().size());
            List<Attribute> graphFilters = new ArrayList<Attribute>(graphDbEntry.getGraphFilters().size());

            graph.setAttributeList(graphAttributes);
            graph.setFilterList(graphFilters);

            for (GraphAttributesDbType graphAttribute : graphDbEntry.getGraphAttributes()) {
                int index = getIndex(graphAttribute.getId().getAttributeId(), attributes);
                if (index == -1) throw new Exception("graph contains attributes which are not assigned yet");
                graphAttributes.add(attributes.get(index));
            }

            for (GraphFiltersDbType graphFilter : graphDbEntry.getGraphFilters()) {
                int index = getIndex(graphFilter.getId().getAttributeId(), attributes);
                if (index == -1) throw new Exception("graph contains attributes which are not assigned yet");
                graphFilters.add(attributes.get(index));
            }

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


