package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.*;
import com.fbr.Dao.TrendAttributesDao;
import com.fbr.Dao.TrendFiltersDao;
import com.fbr.Dao.TrendsDao;
import com.fbr.domain.Trend;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("trendService")
public class TrendService {
    private static final Logger logger = Logger.getLogger(TrendService.class);
    @Autowired
    private TrendsDao trendsDao;
    @Autowired
    private TrendFiltersDao trendFiltersDao;
    @Autowired
    private TrendAttributesDao trendAttributesDao;

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

    private static class Comparators {
        private static Comparator<TrendDbType> COMPARE_TRENDS = new Comparator<TrendDbType>() {
            @Override
            public int compare(TrendDbType first, TrendDbType second) {
                return first.getTrendId().compareTo(second.getTrendId());
            }
        };

        private static Comparator<TrendAttributesDbType> COMPARE_TREND_ATTRIBUTES = new Comparator<TrendAttributesDbType>() {
            @Override
            public int compare(TrendAttributesDbType first, TrendAttributesDbType second) {
                if (first.getId().getTrendId().equals(second.getId().getTrendId())) {
                    return first.getId().getAttributeId() - second.getId().getAttributeId();
                }
                return first.getId().getTrendId().compareTo(second.getId().getTrendId());
            }
        };

        private static Comparator<TrendFiltersDbType> COMPARE_TREND_FILTERS = new Comparator<TrendFiltersDbType>() {
            @Override
            public int compare(TrendFiltersDbType first, TrendFiltersDbType second) {
                if (first.getId().getTrendId().equals(second.getId().getTrendId())) {
                    return first.getId().getAttributeId() - second.getId().getAttributeId();
                }
                return first.getId().getTrendId().compareTo(second.getId().getTrendId());
            }
        };
    }
}


