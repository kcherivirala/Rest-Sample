package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Company.Entities.BranchDbType;
import com.fbr.Dao.Company.Entities.CompanyDbType;
import com.fbr.Dao.Response.CustomerResponseDao;
import com.fbr.Dao.Response.Entities.CustomerResponseValuesDbType;
import com.fbr.Utilities.Comparators;
import com.fbr.Utilities.FeedbackUtilities;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Attribute.AttributeValue;
import com.fbr.domain.Graph.Graph;
import com.fbr.domain.Statistic.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("statisticsService")
public class StatisticsService {
    private static final Logger logger = Logger.getLogger(StatisticsService.class);
    @Autowired
    private CustomerResponseDao customerResponseDao;
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private GraphService graphService;
    @Autowired
    private CompanyService companyService;

    Timer timer;

    private List<CompanyData> listCompanyData;

    private static int DATA_DAY_COUNT = 30;                             //for trends API
    private static int DATA_MONTH_COUNT = 12;                           //for trends API

    private long HOURLY_REFRESH_INTERVAL = 24 * 60 * 60 * 1000;   //1day
    private long DAILY_REFRESH_INTERVAL = 60 * 60 * 1000;         //1 hour


    @PostConstruct
    public void init() {
        Calendar cal = Calendar.getInstance();
        int date = FeedbackUtilities.dateFromCal(cal);
        int month = FeedbackUtilities.monthFromDate(date);

        List<CompanyDbType> companies = companyService.getCompanyDbEntries();
        listCompanyData = new ArrayList<CompanyData>(companies.size());

        for (CompanyDbType company : companies) {
            try {
                listCompanyData.add(processPerCompanyResponses(company.getCompanyId(), company.getName(), date, month));
            } catch (Exception e) {
                logger.error("error initialising info for : " + company.getCompanyId());
            }
        }

        timer = new Timer();
        UpdateNormalGraphsTask updateNormalGraphsTask = new UpdateNormalGraphsTask();
        UpdateTrendGraphsTask updateTrendGraphsTask = new UpdateTrendGraphsTask();

        cal.add(Calendar.HOUR, 1);
        timer.schedule(updateTrendGraphsTask, cal.getTime(), HOURLY_REFRESH_INTERVAL);

        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        timer.schedule(updateNormalGraphsTask, cal.getTime(), DAILY_REFRESH_INTERVAL);
    }

    public void resetCompanyData(int companyId) throws Exception {
        try {
            logger.info("caching the statistics for company : " + companyId);
            int date = FeedbackUtilities.dateFromCal(Calendar.getInstance());
            int month = FeedbackUtilities.monthFromDate(date);

            CompanyDbType company = companyService.getCompanyDbEntry(companyId);
            CompanyData data = processPerCompanyResponses(company.getCompanyId(), company.getName(), date, month);

            int index = -1;
            for (int i = 0; i < listCompanyData.size(); i++) {
                if (listCompanyData.get(i).companyId == companyId) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                listCompanyData.add(data);
            } else {
                listCompanyData.remove(index);
                listCompanyData.add(data);
            }
            logger.info("done caching the statistics for company : " + companyId);
        } catch (Exception e) {
            throw new Exception("error resetting company Data : " + companyId + " : " + e.getMessage());
        }
    }

    public List<AttributeLevelStatistics> getGraphData(int companyId, String graphId, Map<String, Integer> mapOfFilters) throws Exception {
        try {
            logger.info("getting the statistics for companyId : " + companyId + " and graph : " + graphId);
            int index = getIndex(listCompanyData, companyId);
            CompanyData companyData = listCompanyData.get(index);

            index = getIndex(companyData.listGraphData, graphId);
            GraphData graphData = companyData.listGraphData.get(index);

            Graph graph = graphService.getGraph(companyId, graphId);

            List<AttributeLevelStatistics> listAttributeStatistics = new ArrayList<AttributeLevelStatistics>(graphData.mapOfAttributes.size());
            initialiseAttributeLevelStatistics(listAttributeStatistics, graphData.weigtedAttributes, FeedbackUtilities.dateFromCal(Calendar.getInstance()), graph.getType());
            populateFromGraphData(listAttributeStatistics, mapOfFilters, graphData);

            logger.info("done getting the statistics for companyId : " + companyId + " and graph : " + graphId);
            return listAttributeStatistics;
        } catch (Exception e) {
            logger.error("error getting graph data fro company : " + companyId + " and graphId : " + graphId + " : " + e.getMessage());
            throw new Exception("error getting graph data fro company : " + companyId + " and graphId : " + graphId + " : " + e.getMessage());
        }
    }

    public DashboardInfo getDashboardInfo(int companyId) throws Exception {
        try {
            DashboardInfo dashboardInfo = new DashboardInfo();

            CompanyData companyData = listCompanyData.get(getIndex(listCompanyData, companyId));

            double sumOfAvg = 0;
            for (BranchLevelDashboardData branchLevelDashboardData : companyData.listBranchLevelDashboardData) {
                dashboardInfo.setNpsNegative(dashboardInfo.getNpsNegative() + branchLevelDashboardData.countPplNps[0]);
                dashboardInfo.setNpsPassive(dashboardInfo.getNpsPassive() + branchLevelDashboardData.countPplNps[1]);
                dashboardInfo.setNpsPositive(dashboardInfo.getNpsPositive() + branchLevelDashboardData.countPplNps[2]);

                dashboardInfo.setCountResponsesTotal(dashboardInfo.getCountResponsesTotal() + branchLevelDashboardData.countResponsesTotal);
                dashboardInfo.setCountResponsesNegativeTotal(dashboardInfo.getCountResponsesNegativeTotal() + branchLevelDashboardData.countResponsesNegativeTotal);
                dashboardInfo.setCountResponsesPositiveTotal(dashboardInfo.getCountResponsesPositiveTotal() + branchLevelDashboardData.countResponsesPositiveTotal);

                dashboardInfo.setCountResponsesToday(dashboardInfo.getCountResponsesToday() + branchLevelDashboardData.countResponsesToday);
                dashboardInfo.setCountResponsesNegativeToday(dashboardInfo.getCountResponsesNegativeToday() + branchLevelDashboardData.countResponsesNegativeToday);
                dashboardInfo.setCountResponsesPositiveToday(dashboardInfo.getCountResponsesPositiveToday() + branchLevelDashboardData.countResponsesPositiveToday);

                sumOfAvg += branchLevelDashboardData.sumOfAvg;
            }
            dashboardInfo.setAvgRating(sumOfAvg / dashboardInfo.getCountResponsesTotal());
            return dashboardInfo;
        } catch (Exception e) {
            logger.error("error getting dashboard info fro company : " + companyId);
            e.printStackTrace();
            throw new Exception("error getting dashboard info fro company : " + companyId + " : " + e.getMessage());
        }
    }

    public DashboardInfo getDashboardInfo(int companyId, int branchId) throws Exception {
        try {
            DashboardInfo dashboardInfo = new DashboardInfo();
            CompanyData companyData = listCompanyData.get(getIndex(listCompanyData, companyId));
            BranchLevelDashboardData branchLevelDashboardData = companyData.listBranchLevelDashboardData.get(branchId);

            dashboardInfo.setNpsNegative(branchLevelDashboardData.countPplNps[0]);
            dashboardInfo.setNpsPassive(branchLevelDashboardData.countPplNps[1]);
            dashboardInfo.setNpsPositive(branchLevelDashboardData.countPplNps[2]);

            dashboardInfo.setCountResponsesTotal(branchLevelDashboardData.countResponsesTotal);
            dashboardInfo.setCountResponsesNegativeTotal(branchLevelDashboardData.countResponsesNegativeTotal);
            dashboardInfo.setCountResponsesPositiveTotal(branchLevelDashboardData.countResponsesPositiveTotal);

            dashboardInfo.setCountResponsesToday(branchLevelDashboardData.countResponsesToday);
            dashboardInfo.setCountResponsesNegativeToday(branchLevelDashboardData.countResponsesNegativeToday);
            dashboardInfo.setCountResponsesPositiveToday(branchLevelDashboardData.countResponsesPositiveToday);

            dashboardInfo.setAvgRating(branchLevelDashboardData.sumOfAvg / branchLevelDashboardData.countResponsesTotal);
            return dashboardInfo;
        } catch (Exception e) {
            logger.error("error getting dashboard info fro company : " + companyId + " and branch : " + branchId + " : " + e.getMessage());
            throw new Exception("error getting dashboard info fro company : " + companyId + " and branch : " + branchId + " : " + e.getMessage());
        }
    }

    /*      Functions to add data to the out list for teh get API   */

    private void populateFromGraphData(List<AttributeLevelStatistics> listAttributeStatistics, Map<String, Integer> mapOfFilters, GraphData graphData) {
        for (ConstraintLevelStatistics constraintLevelStatistics : graphData.graphLevelStatistics.getListConstraintLevelStatistics()) {
            if (checkInclude(constraintLevelStatistics.getConstraints(), mapOfFilters)) {
                int i = 0;

                for (AttributeLevelStatistics inStatistics : constraintLevelStatistics.getAttributeLevelStatistics()) {
                    AttributeLevelStatistics outStatistics = listAttributeStatistics.get(i);

                    copyFromDailyAttributeStatisticValues(outStatistics.getListDailyAttributeStatisticValues(), inStatistics.getListDailyAttributeStatisticValues());
                    copyFromMonthlyAttributeLevelStatisticValues(outStatistics.getListMonthlyAttributeLevelStatisticValues(), inStatistics.getListMonthlyAttributeLevelStatisticValues());

                    copyFromListCountPPl(outStatistics.getListCountPPl_7Days(), inStatistics.getListCountPPl_7Days());
                    copyFromListCountPPl(outStatistics.getListCountPPl_30Days(), inStatistics.getListCountPPl_30Days());
                    copyFromListCountPPl(outStatistics.getListCountPPl_365Days(), inStatistics.getListCountPPl_365Days());

                    i++;
                }
            }
        }
    }

    private boolean checkInclude(Map<String, Integer> map, Map<String, Integer> inputMap) {
        if (inputMap == null || inputMap.size() == 0) return true;


        for (String key : inputMap.keySet()) {
            if (!map.containsKey(key))
                return false;
            if (map.get(key) != inputMap.get(key))
                return false;
        }
        return true;
    }

    private void copyFromDailyAttributeStatisticValues(List<DailyAttributeStatisticValues> outList, List<DailyAttributeStatisticValues> inList) {
        if (outList == null) return;

        int k = 0;
        for (int i = 0; i < outList.size(); i++) {
            DailyAttributeStatisticValues out = outList.get(i);
            DailyAttributeStatisticValues in = inList.get(k);

            if (out.getDate() < in.getDate()) continue;

            if (out.getDate() == in.getDate()) {
                for (int j = 0; j < out.getListCountPPL().size(); j++) {
                    out.getListCountPPL().set(j, out.getListCountPPL().get(j) + in.getListCountPPL().get(j));
                }
            }
            k++;
        }
    }

    private void copyFromMonthlyAttributeLevelStatisticValues(List<MonthlyAttributeLevelStatisticValues> outList, List<MonthlyAttributeLevelStatisticValues> inList) {
        if (outList == null) return;

        int k = 0;
        for (int i = 0; i < outList.size(); i++) {
            MonthlyAttributeLevelStatisticValues out = outList.get(i);
            MonthlyAttributeLevelStatisticValues in = inList.get(k);

            if (out.getMonth() < in.getMonth()) continue;

            if (out.getMonth() == in.getMonth()) {
                for (int j = 0; j < out.getListCountPPL().size(); j++) {
                    out.getListCountPPL().set(j, out.getListCountPPL().get(j) + in.getListCountPPL().get(j));
                }
            }
            k++;
        }
    }

    private void copyFromListCountPPl(List<Integer> outList, List<Integer> inList) {
        if (outList == null) return;

        for (int i = 0; i < outList.size(); i++) {
            outList.set(i, outList.get(i) + inList.get(i));
        }
    }

    /*          refreshing the cache       */

    private void refreshHourlyResponses() {
        List<CompanyDbType> companies = companyService.getCompanyDbEntries();

        for (CompanyDbType company : companies) {
            try {
                int index = getIndex(listCompanyData, company.getCompanyId());
                if (index == -1) {
                    resetCompanyData(company.getCompanyId());
                } else {
                    refreshHourlyResponses(company.getCompanyId());
                }
            } catch (Exception e) {

            }
        }
    }

    public void refreshHourlyResponses(int companyId) {
        try {
            int index = getIndex(listCompanyData, companyId);

            CompanyData companyData = listCompanyData.get(index);
            Date currTimeStamp = companyData.lastUpdatedTimeStamp;
            List<CustomerResponseDao.CustomerResponseAndValues> listResponse = customerResponseDao.getResponses(companyId, currTimeStamp);
            Date lastTime = getLastDate(listResponse);

            for (GraphData graphData : companyData.listGraphData) {
                if (graphData.type.equals("trend")) {
                    addResponsesToTrendGraphsData(graphData, listResponse);
                    removeTrendGraphStatistics(graphData);
                } else if (graphData.type.equals("normal")) {
                    int currentDate = FeedbackUtilities.dateFromCal(Calendar.getInstance());
                    populateNormalGraphStatistics(graphData, listResponse, currentDate);
                }
            }
            collectNPS(companyData.listBranchLevelDashboardData, companyData.mapOfBranches, listResponse, attributeService.getNPSAttr(companyId));
            addCompanyLevelStats(companyData.listBranchLevelDashboardData, companyData.mapOfBranches, attributeService.getAttributesByCompany(companyId), listResponse);

            companyData.lastUpdatedTimeStamp = lastTime;
        } catch (Exception e) {
            logger.error("error adding new data to trends for company: " + companyId);
        }
    }

    private void addResponsesToTrendGraphsData(GraphData graphData, List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {
        int currentDate = FeedbackUtilities.dateFromCal(Calendar.getInstance());
        int currentMonth = FeedbackUtilities.monthFromDate(currentDate);

        int size = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(0).getAttributeLevelStatistics().get(0).getListDailyAttributeStatisticValues().size();
        int lastDate = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(0).getAttributeLevelStatistics().get(0).getListDailyAttributeStatisticValues().get(size - 1).getDate();
        int lastMonth = FeedbackUtilities.monthFromDate(lastDate);

        initialiseConstraintLevelStatistics(graphData.graphLevelStatistics.getListConstraintLevelStatistics(), graphData.weigtedAttributes, lastMonth, lastDate, currentDate, graphData.type);
        modifyMapOfDates(graphData.mapOfDates, lastDate, currentDate);
        modifyMapOfMonths(graphData.mapOfMonths, lastMonth, currentMonth);
        populateTrendGraphStatistics(graphData, listResponse);
    }

    private void removeTrendGraphStatistics(GraphData graphData) {
        for (ConstraintLevelStatistics constraintLevelStatistics : graphData.graphLevelStatistics.getListConstraintLevelStatistics()) {
            for (AttributeLevelStatistics attributeLevelStatistics : constraintLevelStatistics.getAttributeLevelStatistics()) {
                while (attributeLevelStatistics.getListDailyAttributeStatisticValues().size() > 30) {
                    int date = attributeLevelStatistics.getListDailyAttributeStatisticValues().get(0).getDate();
                    attributeLevelStatistics.getListDailyAttributeStatisticValues().remove(0);
                    graphData.mapOfDates.remove(date);
                }
                while (attributeLevelStatistics.getListMonthlyAttributeLevelStatisticValues().size() > 12) {
                    int month = attributeLevelStatistics.getListMonthlyAttributeLevelStatisticValues().get(0).getMonth();
                    attributeLevelStatistics.getListMonthlyAttributeLevelStatisticValues().remove(0);
                    graphData.mapOfMonths.remove(month);
                }
            }
        }

    }

    private void refreshDailyLevelResponses() {
        //refresh at 12 midnight
        List<CompanyDbType> companies = companyService.getCompanyDbEntries();

        for (CompanyDbType company : companies) {
            refreshDailyLevelResponses(company.getCompanyId());
        }
    }

    public void refreshDailyLevelResponses(int companyId) {
        try {
            int index = getIndex(listCompanyData, companyId);

            if (index == -1) {
                resetCompanyData(companyId);
            } else {
                CompanyData companyData = listCompanyData.get(index);

                //responses for the previous day
                Calendar cal = Calendar.getInstance();

                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 1);

                Date end = cal.getTime();
                cal.add(Calendar.DATE, -1);
                Date start = cal.getTime();

                List<CustomerResponseDao.CustomerResponseAndValues> listResponse = customerResponseDao.getResponses(companyId, start, end);

                //responses for 7th previous day
                cal.add(Calendar.DATE, -6);
                end = cal.getTime();
                cal.add(Calendar.DATE, -1);
                start = cal.getTime();
                List<CustomerResponseDao.CustomerResponseAndValues> listResponse7th = customerResponseDao.getResponses(companyId, start, end);

                //responses for 30th previous day
                cal.add(Calendar.DATE, -22);
                end = cal.getTime();
                cal.add(Calendar.DATE, -1);
                start = cal.getTime();
                List<CustomerResponseDao.CustomerResponseAndValues> listResponse30th = customerResponseDao.getResponses(companyId, start, end);

                //responses for 365th previous day
                cal.add(Calendar.DATE, -334);
                end = cal.getTime();
                cal.add(Calendar.DATE, -1);
                start = cal.getTime();
                List<CustomerResponseDao.CustomerResponseAndValues> listResponse365th = customerResponseDao.getResponses(companyId, start, end);

                for (GraphData graphData : companyData.listGraphData) {
                    if (graphData.type.equals("normal"))
                        updateNormalGraphsData(graphData, listResponse, listResponse7th, listResponse30th, listResponse365th);
                }

                removeNPS(companyData.listBranchLevelDashboardData, companyData.mapOfBranches, listResponse365th, attributeService.getNPSAttr(companyId));
                removeCompanyLevelStats(companyData.listBranchLevelDashboardData, companyData.mapOfBranches, attributeService.getAttributesByCompany(companyId), listResponse365th);
            }
        } catch (Exception e) {
            logger.error("error adding new data to normal graph for company: " + companyId);
        }
    }

    private void updateNormalGraphsData(GraphData graphData, List<CustomerResponseDao.CustomerResponseAndValues> listResponse,
                                        List<CustomerResponseDao.CustomerResponseAndValues> listResponse7, List<CustomerResponseDao.CustomerResponseAndValues> listResponse30,
                                        List<CustomerResponseDao.CustomerResponseAndValues> listResponse365) {
        int currentDate = FeedbackUtilities.dateFromCal(Calendar.getInstance());
        populateNormalGraphStatistics(graphData, listResponse, currentDate);
        reduceNormalGraphStatistics(graphData, listResponse7, listResponse30, listResponse365);
    }

    private void reduceNormalGraphStatistics(GraphData graphData, List<CustomerResponseDao.CustomerResponseAndValues> listResponse7,
                                             List<CustomerResponseDao.CustomerResponseAndValues> listResponse30, List<CustomerResponseDao.CustomerResponseAndValues> listResponse365) {

        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse7) {
            Map<String, Integer> mapFilter = getMapConstraintsFromResponse(graphData.filterAttributes, response);
            int constraintIndex = graphData.mapOFConstraints.get(mapFilter);

            for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                if (graphData.mapOfAttributes.get(responseValue.getId().getAttributeId()) == null) continue;

                int attributeIndex = graphData.mapOfAttributes.get(responseValue.getId().getAttributeId());
                int attributeValueIndex = getAttributeValueIndex(graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListAttributeValue(), responseValue.getObtainedValue());

                List<Integer> countPPL = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListCountPPl_7Days();
                countPPL.set(attributeValueIndex, countPPL.get(attributeValueIndex) - 1);

            }
        }

        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse30) {
            Map<String, Integer> mapFilter = getMapConstraintsFromResponse(graphData.filterAttributes, response);
            int constraintIndex = graphData.mapOFConstraints.get(mapFilter);

            for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                if (graphData.mapOfAttributes.get(responseValue.getId().getAttributeId()) == null) continue;

                int attributeIndex = graphData.mapOfAttributes.get(responseValue.getId().getAttributeId());
                int attributeValueIndex = getAttributeValueIndex(graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListAttributeValue(), responseValue.getObtainedValue());

                List<Integer> countPPL = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListCountPPl_30Days();
                countPPL.set(attributeValueIndex, countPPL.get(attributeValueIndex) - 1);

            }
        }

        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse365) {
            Map<String, Integer> mapFilter = getMapConstraintsFromResponse(graphData.filterAttributes, response);
            int constraintIndex = graphData.mapOFConstraints.get(mapFilter);

            for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                if (graphData.mapOfAttributes.get(responseValue.getId().getAttributeId()) == null) continue;

                int attributeIndex = graphData.mapOfAttributes.get(responseValue.getId().getAttributeId());
                int attributeValueIndex = getAttributeValueIndex(graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListAttributeValue(), responseValue.getObtainedValue());

                List<Integer> countPPL = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListCountPPl_365Days();
                countPPL.set(attributeValueIndex, countPPL.get(attributeValueIndex) - 1);

            }
        }
    }

    /*         building the cache      */

    private CompanyData processPerCompanyResponses(int companyId, String companyName, int currentDate, int currentMonth) throws Exception {
        try {
            List<CustomerResponseDao.CustomerResponseAndValues> listResponse = customerResponseDao.getResponses(companyId);
            Collections.sort(listResponse, Comparators.COMPARE_RESPONSES);

            List<Attribute> listAttribute = attributeService.getAttributesByCompany(companyId);    //already sorted list
            int npsAttributeID = attributeService.getNPSAttr(companyId);
            List<BranchDbType> branches = companyService.getDbBranches(companyId);
            Collections.sort(branches, Comparators.COMPARE_DB_BRANCHES);
            List<Graph> listGraph = graphService.getGraphs(companyId);

            List<GraphData> listGraphData = new ArrayList<GraphData>(listGraph.size());
            for (Graph graph : listGraph) {
                List<Attribute> filterAttributes = attributeService.getAttributesForIds(graph.getFilterList(), listAttribute);
                List<Attribute> weightedAttributes = attributeService.getAttributesForIds(graph.getAttributeList(), listAttribute);
                int constraints = numberOfConstraints(filterAttributes);

                List<ConstraintLevelStatistics> listConstraintLevelStatistics = new ArrayList<ConstraintLevelStatistics>(constraints * branches.size());
                populateConstraintLevels(listConstraintLevelStatistics, filterAttributes, branches);    //start with index of -1
                initialiseConstraintLevelStatistics(listConstraintLevelStatistics, weightedAttributes, currentDate, graph.getType());


                GraphLevelStatistics graphLevelStatistics = new GraphLevelStatistics();
                graphLevelStatistics.setGraphId(graph.getGraphId());
                graphLevelStatistics.setListConstraintLevelStatistics(listConstraintLevelStatistics);

                GraphData graphData = new GraphData();
                graphData.graphId = graph.getGraphId();
                graphData.type = graph.getType();
                graphData.graphLevelStatistics = graphLevelStatistics;
                graphData.mapOFConstraints = getMapOfConstraints(listConstraintLevelStatistics);
                graphData.mapOfAttributes = getMapOfAttributes(graph.getAttributeList());

                graphData.filterAttributes = filterAttributes;
                graphData.weigtedAttributes = weightedAttributes;

                if (graph.getType().equals("trend")) {
                    graphData.mapOfDates = new HashMap<Integer, Integer>(DATA_DAY_COUNT + 1);
                    int startDate = FeedbackUtilities.addToDate(currentDate, -1 * DATA_DAY_COUNT);
                    modifyMapOfDates(graphData.mapOfDates, startDate, currentDate);

                    graphData.mapOfMonths = new HashMap<Integer, Integer>(DATA_MONTH_COUNT + 1);
                    int startMonth = FeedbackUtilities.addToMonth(currentMonth, -1 * DATA_MONTH_COUNT);
                    modifyMapOfMonths(graphData.mapOfMonths, startMonth, currentMonth);
                }


                populateStatistics(graphData, listResponse);

                listGraphData.add(graphData);
            }

            CompanyData companyData = new CompanyData();
            companyData.companyId = companyId;
            companyData.companyName = companyName;
            companyData.listGraphData = listGraphData;
            companyData.lastUpdatedTimeStamp = new Date();
            companyData.listBranchLevelDashboardData = new ArrayList<BranchLevelDashboardData>(branches.size());
            companyData.mapOfBranches = getMapOfBranches(branches);

            initialiseBranchLevelDashBoard(companyData.listBranchLevelDashboardData, branches);
            collectNPS(companyData.listBranchLevelDashboardData, companyData.mapOfBranches, listResponse, npsAttributeID);
            addCompanyLevelStats(companyData.listBranchLevelDashboardData, companyData.mapOfBranches, listAttribute, listResponse);

            return companyData;
        } catch (Exception e) {
            logger.error("error processing per company responses : " + e.getMessage());
            throw new Exception("error processing per company responses : " + e.getMessage());
        }
    }

    /*            initialising the cache    */

    private void populateConstraintLevels(List<ConstraintLevelStatistics> listConstraintLevelStatistics, List<Attribute> filterAttributes, List<BranchDbType> branches) {
        Map<String, Integer> mapConstraints = new HashMap<String, Integer>();

        for (Attribute attribute : filterAttributes) {
            AttributeValue defaultAttrValue = new AttributeValue();
            defaultAttrValue.setMaxValue(-1);
            defaultAttrValue.setValue(-1);
            defaultAttrValue.setName("default");

            attribute.getAttributeValues().add(defaultAttrValue);
        }

        for (BranchDbType branch : branches) {
            mapConstraints.put("branch", branch.getId().getBranchId());
            populateConstraintLevels(listConstraintLevelStatistics, mapConstraints, filterAttributes, 0);
        }

        for (Attribute attribute : filterAttributes) {
            int index = attributeService.getAttributeValueIndex(attribute, -1);
            attribute.getAttributeValues().remove(index);
        }

    }

    private void populateConstraintLevels(List<ConstraintLevelStatistics> listConstraintLevelStatistics, Map<String, Integer> mapConstraints, List<Attribute> filterAttributes, int index) {
        Attribute attribute = filterAttributes.get(index);

        for (AttributeValue attributeValue : attribute.getAttributeValues()) {
            mapConstraints.put(attribute.getAttributeString(), attributeValue.getValue());

            if (index == filterAttributes.size() - 1) {
                //create a new MAP from mapConstraints so as to set in teh constraint.
                Map<String, Integer> newMap = createNewMap(mapConstraints);

                ConstraintLevelStatistics constraintLevelStatistics = new ConstraintLevelStatistics();
                constraintLevelStatistics.setConstraints(newMap);
                listConstraintLevelStatistics.add(constraintLevelStatistics);
            } else {
                populateConstraintLevels(listConstraintLevelStatistics, mapConstraints, filterAttributes, index + 1);
            }
        }
    }

    private void initialiseConstraintLevelStatistics(List<ConstraintLevelStatistics> listConstraintLevelStatistics, List<Attribute> weightedAttributes,
                                                     int currentDate, String graphType) {
        int startDate = FeedbackUtilities.addToDate(currentDate, -1 * DATA_DAY_COUNT);
        int currentMonth = FeedbackUtilities.monthFromDate(currentDate);
        int startMonth = FeedbackUtilities.addToMonth(currentMonth, -1 * DATA_MONTH_COUNT);

        initialiseConstraintLevelStatistics(listConstraintLevelStatistics, weightedAttributes, startMonth, startDate, currentDate, graphType);
    }

    private void initialiseConstraintLevelStatistics(List<ConstraintLevelStatistics> listConstraintLevelStatistics, List<Attribute> weightedAttributes,
                                                     int startMonth, int startDate, int currentDate, String graphType) {
        for (ConstraintLevelStatistics constraintLevelStatistics : listConstraintLevelStatistics) {
            if (constraintLevelStatistics.getAttributeLevelStatistics() == null) {
                List<AttributeLevelStatistics> listAttributeStatistics = new ArrayList<AttributeLevelStatistics>(weightedAttributes.size());
                constraintLevelStatistics.setAttributeLevelStatistics(listAttributeStatistics);
            }

            initialiseAttributeLevelStatistics(constraintLevelStatistics.getAttributeLevelStatistics(), weightedAttributes, startMonth, startDate, currentDate, graphType);
        }
    }

    private void initialiseAttributeLevelStatistics(List<AttributeLevelStatistics> listAttributeStatistics, List<Attribute> weightedAttributes, int currentDate, String graphType) {
        int startDate = FeedbackUtilities.addToDate(currentDate, -1 * DATA_DAY_COUNT);
        int currentMonth = FeedbackUtilities.monthFromDate(currentDate);
        int startMonth = FeedbackUtilities.addToMonth(currentMonth, -1 * DATA_MONTH_COUNT);

        initialiseAttributeLevelStatistics(listAttributeStatistics, weightedAttributes, startMonth, startDate, currentDate, graphType);
    }

    private void initialiseAttributeLevelStatistics(List<AttributeLevelStatistics> listAttributeStatistics, List<Attribute> weightedAttributes, int startMonth, int startDate, int currentDate, String graphType) {
        int currentMonth = FeedbackUtilities.monthFromDate(currentDate);

        for (Attribute attribute : weightedAttributes) {
            AttributeLevelStatistics attributeLevelStatistics;
            if (listAttributeStatistics.size() != weightedAttributes.size()) {
                attributeLevelStatistics = new AttributeLevelStatistics();

                attributeLevelStatistics.setAttributeId(attribute.getAttributeId());
                attributeLevelStatistics.setName(attribute.getAttributeString());
                attributeLevelStatistics.setListAttributeValue(attribute.getAttributeValues());

                listAttributeStatistics.add(attributeLevelStatistics);
            } else {
                attributeLevelStatistics = listAttributeStatistics.get(getAttributeStatisticsIndex(listAttributeStatistics, attribute.getAttributeId()));
            }

            if (graphType.equals("trend")) {
                if (attributeLevelStatistics.getListDailyAttributeStatisticValues() == null) {
                    List<DailyAttributeStatisticValues> listDailyAttributeStatisticValues = new ArrayList<DailyAttributeStatisticValues>(31);
                    attributeLevelStatistics.setListDailyAttributeStatisticValues(listDailyAttributeStatisticValues);
                }

                if (attributeLevelStatistics.getListMonthlyAttributeLevelStatisticValues() == null) {
                    List<MonthlyAttributeLevelStatisticValues> listMonthlyAttributeLevelStatisticValues = new ArrayList<MonthlyAttributeLevelStatisticValues>(12);
                    attributeLevelStatistics.setListMonthlyAttributeLevelStatisticValues(listMonthlyAttributeLevelStatisticValues);
                }

                initialiseDailyValues(attributeLevelStatistics.getListDailyAttributeStatisticValues(), startDate, currentDate, attribute.getAttributeValues().size());
                initialiseMonthlyValues(attributeLevelStatistics.getListMonthlyAttributeLevelStatisticValues(), startMonth, currentMonth, attribute.getAttributeValues().size());
            } else {
                List<Integer> listCountPPl_7Days = new ArrayList<Integer>(attribute.getAttributeValues().size());
                List<Integer> listCountPPl_30Days = new ArrayList<Integer>(attribute.getAttributeValues().size());
                List<Integer> listCountPPl_365Days = new ArrayList<Integer>(attribute.getAttributeValues().size());

                attributeLevelStatistics.setListCountPPl_7Days(listCountPPl_7Days);
                attributeLevelStatistics.setListCountPPl_30Days(listCountPPl_30Days);
                attributeLevelStatistics.setListCountPPl_365Days(listCountPPl_365Days);

                initialiseNormalAttributeStatisticValues(listCountPPl_7Days, listCountPPl_30Days, listCountPPl_365Days, attribute.getAttributeValues().size());
            }
        }
    }

    private void initialiseNormalAttributeStatisticValues(List<Integer> listCountPPl_7Days, List<Integer> listCountPPl_30Days, List<Integer> listCountPPl_365Days, int count) {
        for (int i = 0; i < count; i++) {
            listCountPPl_7Days.add(0);
            listCountPPl_30Days.add(0);
            listCountPPl_365Days.add(0);
        }
    }

    private void initialiseDailyValues(List<DailyAttributeStatisticValues> listDailyAttributeStatisticValues, int startDate, int currentDate, int noOfValues) {
        int date = FeedbackUtilities.nextDate(startDate);

        while (date <= currentDate) {
            DailyAttributeStatisticValues dailyAttributeStatisticValues = new DailyAttributeStatisticValues();
            dailyAttributeStatisticValues.setDate(date);
            List<Integer> listCountPPL = new ArrayList<Integer>(noOfValues);
            for (int i = 0; i < noOfValues; i++) {
                listCountPPL.add(0);
            }
            dailyAttributeStatisticValues.setListCountPPL(listCountPPL);

            listDailyAttributeStatisticValues.add(dailyAttributeStatisticValues);

            date = FeedbackUtilities.nextDate(date);
        }
    }

    private void initialiseMonthlyValues(List<MonthlyAttributeLevelStatisticValues> listMonthlyAttributeLevelStatisticValues, int startMonth, int currentMonth, int noOfValues) {
        int month = FeedbackUtilities.nextMonth(startMonth);

        while (month <= currentMonth) {
            MonthlyAttributeLevelStatisticValues monthlyAttributeLevelStatisticValues = new MonthlyAttributeLevelStatisticValues();
            monthlyAttributeLevelStatisticValues.setMonth(month);
            List<Integer> listCountPPL = new ArrayList<Integer>(noOfValues);
            for (int i = 0; i < noOfValues; i++) {
                listCountPPL.add(0);
            }
            monthlyAttributeLevelStatisticValues.setListCountPPL(listCountPPL);

            listMonthlyAttributeLevelStatisticValues.add(monthlyAttributeLevelStatisticValues);

            month = FeedbackUtilities.nextMonth(month);
        }
    }

    /*    populating the data in the cache */

    private void populateStatistics(GraphData graphData, List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {
        if (graphData.type.equals("trend"))
            populateTrendGraphStatistics(graphData, listResponse);
        else if (graphData.type.equals("normal"))
            populateNormalGraphStatistics(graphData, listResponse, FeedbackUtilities.dateFromCal(Calendar.getInstance()));
    }

    private void populateTrendGraphStatistics(GraphData graphData, List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {
        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
            Map<String, Integer> mapFilter = getMapConstraintsFromResponse(graphData.filterAttributes, response);
            int constraintIndex = graphData.mapOFConstraints.get(mapFilter);

            int date = FeedbackUtilities.dateFromCal(response.getResponse().getEndTimestamp());
            int month = FeedbackUtilities.monthFromDate(date);

            if (graphData.mapOfDates.get(date) == null || graphData.mapOfMonths.get(month) == null)
                continue;

            int dateIndex = graphData.mapOfDates.get(date);
            int monthIndex = graphData.mapOfMonths.get(month);

            for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                if (graphData.mapOfAttributes.get(responseValue.getId().getAttributeId()) == null) continue;

                int attributeIndex = graphData.mapOfAttributes.get(responseValue.getId().getAttributeId());
                int attributeValueIndex = getAttributeValueIndex(graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListAttributeValue(), responseValue.getObtainedValue());

                List<Integer> countPPL_daily = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListDailyAttributeStatisticValues().get(dateIndex).getListCountPPL();
                countPPL_daily.set(attributeValueIndex, countPPL_daily.get(attributeValueIndex) + 1);

                List<Integer> countPPL_Monthly = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListMonthlyAttributeLevelStatisticValues().get(monthIndex).getListCountPPL();
                countPPL_Monthly.set(attributeValueIndex, countPPL_Monthly.get(attributeValueIndex) + 1);
            }
        }
    }

    private void populateNormalGraphStatistics(GraphData graphData, List<CustomerResponseDao.CustomerResponseAndValues> listResponse, int currentDate) {
        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
            Map<String, Integer> mapFilter = getMapConstraintsFromResponse(graphData.filterAttributes, response);
            int constraintIndex = graphData.mapOFConstraints.get(mapFilter);

            int date = FeedbackUtilities.dateFromCal(response.getResponse().getEndTimestamp());

            //if todays response then ignore it
            if (date == currentDate) continue;

            for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                if (graphData.mapOfAttributes.get(responseValue.getId().getAttributeId()) == null) continue;

                int attributeIndex = graphData.mapOfAttributes.get(responseValue.getId().getAttributeId());
                int attributeValueIndex = getAttributeValueIndex(graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListAttributeValue(), responseValue.getObtainedValue());

                long differenceInDate = FeedbackUtilities.differenceInDates(currentDate, date);
                if (differenceInDate <= 7) {
                    List<Integer> countPPL = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListCountPPl_7Days();
                    countPPL.set(attributeValueIndex, countPPL.get(attributeValueIndex) + 1);
                }
                if (differenceInDate <= 30) {
                    List<Integer> countPPL = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListCountPPl_30Days();
                    countPPL.set(attributeValueIndex, countPPL.get(attributeValueIndex) + 1);
                }
                if (differenceInDate <= 365) {
                    List<Integer> countPPL = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListCountPPl_365Days();
                    countPPL.set(attributeValueIndex, countPPL.get(attributeValueIndex) + 1);
                }
            }
        }
    }

    /*      company-branch level dashboard statistics    */

    private void initialiseBranchLevelDashBoard(List<BranchLevelDashboardData> list, List<BranchDbType> branches) {
        for (BranchDbType branch : branches) {
            BranchLevelDashboardData branchLevelDashboardData = new BranchLevelDashboardData();
            branchLevelDashboardData.branchId = branch.getId().getBranchId();

            branchLevelDashboardData.countPplNps = new int[3];
            branchLevelDashboardData.countResponsesNegativeToday = branchLevelDashboardData.countResponsesToday = branchLevelDashboardData.countResponsesPositiveToday =
                    branchLevelDashboardData.countResponsesNegativeTotal = branchLevelDashboardData.countResponsesTotal = branchLevelDashboardData.countResponsesPositiveTotal = 0;
            branchLevelDashboardData.sumOfAvg = 0;

            list.add(branchLevelDashboardData);
        }
    }

    private void collectNPS(List<BranchLevelDashboardData> listBranchLevelDashboard, Map<Integer, Integer> mapOfBranches,
                            List<CustomerResponseDao.CustomerResponseAndValues> listResponse, int npsAttrId) {
        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
            int branchId = response.getResponse().getBranchId();
            BranchLevelDashboardData branchLevelDashboardData = listBranchLevelDashboard.get(mapOfBranches.get(branchId));
            if (branchLevelDashboardData != null) {
                for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                    if (responseValue.getId().getAttributeId() == npsAttrId) {
                        int value = responseValue.getObtainedValue(); //1,2,3
                        branchLevelDashboardData.countPplNps[value - 1]++;
                    }
                }
            }
        }
    }

    private void removeNPS(List<BranchLevelDashboardData> listBranchLevelDashboard, Map<Integer, Integer> mapOfBranches,
                           List<CustomerResponseDao.CustomerResponseAndValues> listResponse, int npsAttrId) {
        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
            int branchId = response.getResponse().getBranchId();
            BranchLevelDashboardData branchLevelDashboardData = listBranchLevelDashboard.get(mapOfBranches.get(branchId));
            if (branchLevelDashboardData != null) {
                for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                    if (responseValue.getId().getAttributeId() == npsAttrId) {
                        int value = responseValue.getObtainedValue(); //1,2,3
                        branchLevelDashboardData.countPplNps[value - 1]--;
                    }
                }
            }
        }
    }

    private void addCompanyLevelStats(List<BranchLevelDashboardData> listBranchLevelDashboard, Map<Integer, Integer> mapOfBranches,
                                      List<Attribute> listAttribute, List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {
        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
            int branchId = response.getResponse().getBranchId();
            BranchLevelDashboardData branchLevelDashboardData = listBranchLevelDashboard.get(mapOfBranches.get(branchId));

            if (branchLevelDashboardData != null) {
                int responseDate = FeedbackUtilities.dateFromCal(response.getResponse().getEndTimestamp());
                int today = FeedbackUtilities.dateFromCal(Calendar.getInstance());

                float avg = 0;
                int count = 0;
                for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                    if (isWeighted(responseValue.getId().getAttributeId(), listAttribute)) {
                        avg += responseValue.getObtainedValue();
                        count++;
                    }
                }
                avg = avg / count;

                branchLevelDashboardData.countResponsesTotal++;
                if (avg < 2) {
                    branchLevelDashboardData.countResponsesNegativeTotal++;
                } else if (avg > 3) {
                    branchLevelDashboardData.countResponsesPositiveTotal++;
                }

                if (responseDate == today) {
                    if (avg < 2) {
                        branchLevelDashboardData.countResponsesNegativeToday++;
                    } else if (avg > 3) {
                        branchLevelDashboardData.countResponsesPositiveToday++;
                    }
                    branchLevelDashboardData.countResponsesToday++;
                }

                branchLevelDashboardData.sumOfAvg += avg;
            }
        }
    }

    private void removeCompanyLevelStats(List<BranchLevelDashboardData> listBranchLevelDashboard, Map<Integer, Integer> mapOfBranches,
                                         List<Attribute> listAttribute, List<CustomerResponseDao.CustomerResponseAndValues> listResponse365) {
        for (BranchLevelDashboardData branchLevelDashboardData : listBranchLevelDashboard) {
            branchLevelDashboardData.countResponsesNegativeToday = branchLevelDashboardData.countResponsesTotal =
                    branchLevelDashboardData.countResponsesPositiveToday = 0;
        }

        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse365) {
            int branchId = response.getResponse().getBranchId();
            BranchLevelDashboardData branchLevelDashboardData = listBranchLevelDashboard.get(mapOfBranches.get(branchId));

            if (branchLevelDashboardData != null) {
                branchLevelDashboardData.countResponsesTotal--;
                float avg = 0;
                int count = 0;
                for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                    if (isWeighted(responseValue.getId().getAttributeId(), listAttribute)) {
                        avg += responseValue.getObtainedValue();
                        count++;
                    }
                }
                avg = avg / count;

                if (avg < 2) {
                    branchLevelDashboardData.countResponsesNegativeTotal--;
                } else if (avg > 3) {
                    branchLevelDashboardData.countResponsesPositiveTotal--;
                }

                branchLevelDashboardData.sumOfAvg -= avg;
            }
        }
    }

    /*      helper functions     */

    private boolean isWeighted(int attrId, List<Attribute> listAttribute) {
        for (Attribute attribute : listAttribute) {
            if (attribute.getAttributeId() == attrId) {
                if (attribute.getType().equals("weighted"))
                    return true;
                else
                    return false;
            }
        }
        return false;
    }

    private Map<String, Integer> getMapConstraintsFromResponse(List<Attribute> filterAttributes, CustomerResponseDao.CustomerResponseAndValues response) {
        Map<String, Integer> mapFilter = new HashMap<String, Integer>();
        mapFilter.put("branch", response.getResponse().getBranchId());

        for (Attribute attribute : filterAttributes) {
            mapFilter.put(attribute.getAttributeString(), -1);
        }

        for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
            for (Attribute attribute : filterAttributes) {
                if (attribute.getAttributeId() == responseValue.getId().getAttributeId()) {
                    mapFilter.put(attribute.getAttributeString(), responseValue.getObtainedValue());
                    break;
                }
            }
        }
        return mapFilter;
    }

    private static int getIndex(List<CompanyData> listCompanyData, int companyId) {
        int i = 0;
        for (CompanyData companyData : listCompanyData) {
            if (companyData.companyId == companyId)
                return i;
            i++;
        }
        return -1;
    }

    private static int getIndex(List<GraphData> listGraphData, String graphId) {
        int i = 0;
        for (GraphData graphData : listGraphData) {
            if (graphData.graphId.equals(graphId))
                return i;
            i++;
        }
        return -1;
    }

    private static int getAttributeStatisticsIndex(List<AttributeLevelStatistics> list, int attributeId) {
        int index = 0;
        for (AttributeLevelStatistics attributeLevelStatistics : list) {
            if (attributeLevelStatistics.getAttributeId() == attributeId) return index;
            index++;
        }
        return -1;
    }

    private int getAttributeValueIndex(List<AttributeValue> attributeValueList, int value) {
        int i = 0;
        for (AttributeValue attributeValue : attributeValueList) {
            if (value == attributeValue.getValue()) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private Map<String, Integer> createNewMap(Map<String, Integer> map) {
        Map<String, Integer> newMap = new HashMap<String, Integer>(map.size());
        for (String key : map.keySet()) {
            newMap.put(key, map.get(key));
        }
        return newMap;
    }

    private Map<Map<String, Integer>, Integer> getMapOfConstraints(List<ConstraintLevelStatistics> listConstraintLevelStatistics) {
        Map<Map<String, Integer>, Integer> mapOfMaps = new HashMap<Map<String, Integer>, Integer>();
        int index = 0;
        for (ConstraintLevelStatistics constraintLevelStatistics : listConstraintLevelStatistics) {
            mapOfMaps.put(constraintLevelStatistics.getConstraints(), index);
            index++;
        }

        return mapOfMaps;
    }

    private Map<Integer, Integer> getMapOfBranches(List<BranchDbType> branches) {
        Map<Integer, Integer> outMap = new HashMap<Integer, Integer>(branches.size());
        int i = 0;
        for (BranchDbType branch : branches) {
            outMap.put(branch.getId().getBranchId(), i);
            i++;
        }
        return outMap;
    }

    private Map<Integer, Integer> getMapOfAttributes(List<Attribute> attributes) {
        Map<Integer, Integer> outMap = new HashMap<Integer, Integer>(attributes.size());
        int i = 0;
        for (Attribute attribute : attributes) {
            outMap.put(attribute.getAttributeId(), i);
            i++;
        }
        return outMap;
    }

    private Map<Integer, Integer> modifyMapOfDates(Map<Integer, Integer> outMap, int startDate, int endDate) {
        int i = 0;
        int date = FeedbackUtilities.nextDate(startDate);
        while (date <= endDate) {
            outMap.put(date, i);
            date = FeedbackUtilities.nextDate(date);
            i++;
        }
        return outMap;
    }

    private Map<Integer, Integer> modifyMapOfMonths(Map<Integer, Integer> outMap, int startMonth, int endMonth) {
        int i = 0;
        int month = FeedbackUtilities.nextMonth(startMonth);
        while (month <= endMonth) {
            outMap.put(month, i);
            month = FeedbackUtilities.nextMonth(month);
            i++;
        }
        return outMap;
    }

    private Date getLastDate(List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {
        if (listResponse.size() == 0) return null;

        Date last = listResponse.get(0).getResponse().getEndTimestamp();
        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
            if (last.after(response.getResponse().getEndTimestamp()))
                last = response.getResponse().getEndTimestamp();
        }
        return last;
    }

    private int numberOfConstraints(List<Attribute> filterAttributes) {
        int constraints = 1;
        for (Attribute attribute : filterAttributes) {
            constraints *= attribute.getAttributeValues().size();
        }
        return constraints;
    }

    class GraphData {
        String graphId;
        String type;

        List<Attribute> weigtedAttributes;
        List<Attribute> filterAttributes;

        Map<Map<String, Integer>, Integer> mapOFConstraints;
        Map<Integer, Integer> mapOfDates;
        Map<Integer, Integer> mapOfMonths;
        Map<Integer, Integer> mapOfAttributes;

        GraphLevelStatistics graphLevelStatistics;
    }

    class CompanyData {
        int companyId;
        String companyName;
        Date lastUpdatedTimeStamp;

        Map<Integer, Integer> mapOfBranches;
        List<BranchLevelDashboardData> listBranchLevelDashboardData;

        List<GraphData> listGraphData;
    }

    class BranchLevelDashboardData {
        int branchId;

        double sumOfAvg;

        int[] countPplNps;
        int countResponsesTotal;
        int countResponsesNegativeTotal;
        int countResponsesPositiveTotal;

        int countResponsesToday;
        int countResponsesNegativeToday;
        int countResponsesPositiveToday;
    }

    class UpdateTrendGraphsTask extends TimerTask {

        @Override
        public void run() {
            refreshHourlyResponses();
        }
    }

    class UpdateNormalGraphsTask extends TimerTask {

        @Override
        public void run() {
            refreshDailyLevelResponses();
        }
    }
}




