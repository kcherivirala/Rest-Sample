package com.fbr.Dao.Cache;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.domain.Attribute.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("cacheJdbc")
public class CacheJdbcClient {
    @Autowired
    JdbcTemplate jdbcTemplate;


    public void createTable(int companyId, List<Attribute> filterAttributes) {
        String sql = getCreateTableString(companyId, filterAttributes);
        jdbcTemplate.execute(sql);
    }

    public void deleteTable(int companyId) {
        String sql = getDeleteTableString(companyId);
        jdbcTemplate.execute(sql);
    }

    public void addEntry(int companyId, CacheDbEntry cacheDbEntry) {
        String insertString = getInsertString(companyId, cacheDbEntry);
        jdbcTemplate.update(insertString);
    }

    public void updateEntry(int companyId, CacheDbEntry cacheDbEntry) {
        String updateString = getUpdateString(companyId, cacheDbEntry);
        jdbcTemplate.update(updateString);
    }

    public List<CacheDbEntry> getEntries(int companyId, int branchId, Map<Integer, Integer> mapFilter, List<Attribute> filterAttributes) {
        String sql = getSearchString(companyId, branchId, mapFilter, -1, -1);
        List<Map<String, Object>> resultSet = jdbcTemplate.queryForList(sql);

        return processResultSet(resultSet, filterAttributes);
    }

    public List<CacheDbEntry> getEntries(int companyId, Map<Integer, Integer> mapFilter, List<Attribute> filterAttributes) {
        String sql = getSearchString(companyId, -1, mapFilter, -1, -1);
        List<Map<String, Object>> resultSet = jdbcTemplate.queryForList(sql);

        return processResultSet(resultSet, filterAttributes);

    }

    public List<CacheDbEntry> getEntries(int companyId, int branchId, Map<Integer, Integer> mapFilter, int start, int end, List<Attribute> filterAttributes) {
        String sql = getSearchString(companyId, branchId, mapFilter, start, end);
        List<Map<String, Object>> resultSet = jdbcTemplate.queryForList(sql);

        return processResultSet(resultSet, filterAttributes);
    }

    public List<CacheDbEntry> getEntries(int companyId, Map<Integer, Integer> mapFilter, int start, int end, List<Attribute> filterAttributes) {
        String sql = getSearchString(companyId, -1, mapFilter, start, end);
        List<Map<String, Object>> resultSet = jdbcTemplate.queryForList(sql);

        return processResultSet(resultSet, filterAttributes);
    }

    /*                    Private Functions     */

    private String getCreateTableString(int companyId, List<Attribute> filterAttributes) {
        String filterString = "branch_id integer";
        String primaryKey = "branch_id";

        if (filterAttributes != null) {
            for (Attribute attribute : filterAttributes) {
                filterString += ", filter_attribute_" + attribute.getAttributeId() + " integer";
                primaryKey += ",filter_attribute_" + attribute.getAttributeId();
            }
        }
        filterString += ", date integer";
        primaryKey += ",date,weighted_attribute_id";

        filterString += ", weighted_attribute_id integer"
                + ", count_1 integer"
                + ", count_2 integer"
                + ", count_3 integer"
                + ", count_4 integer"
                + ", count_5 integer";

        return "create table cache_company_" + companyId + " (" + filterString + ", Primary Key (" + primaryKey + "))";
    }

    private String getDeleteTableString(int companyId) {
        return "drop table cache_company_" + companyId;
    }

    private String getUpdateTableString(int companyId, List<Attribute> filterAttributesOld, List<Attribute> filterAttributesNew){
        return "";
    }

    private String getSearchString(int companyId, int branchId, Map<Integer, Integer> mapFilters, int startDate, int endDate) {
        String searchString = " where ";
        boolean first = true;

        if (branchId != -1) {
            searchString += " branch_id = " + branchId;
            first = false;
        }

        if (mapFilters != null && mapFilters.size() > 0) {
            for (Integer key : mapFilters.keySet()) {
                if (first) {
                    searchString += "filter_attribute_" + key + " = " + mapFilters.get(key);
                    first = false;
                } else {
                    searchString += " and filter_attribute_" + key + " = " + mapFilters.get(key);
                }
            }
        }

        if (startDate != -1 && first) {
            searchString += " date >= " + startDate;
            first = false;
        } else if (startDate != -1) {
            searchString += " and date >= " + startDate;
        }

        if (endDate != -1 && first) {
            searchString += " date <= " + endDate;
        } else if (endDate != -1) {
            searchString += " and date <= " + endDate;
        }


        return "select * from cache_company_" + companyId + searchString;
    }

    private String getInsertString(int companyId, CacheDbEntry cacheDbEntry) {
        String insertString = "insert into cache_company_" + companyId;

        String attributes = "branch_id";
        String values = "" + cacheDbEntry.getBranchId();

        for (int key : cacheDbEntry.mapOfFilters.keySet()) {
            attributes += ", filter_attribute_" + key;
            values += ", " + cacheDbEntry.mapOfFilters.get(key);
        }

        attributes += ", date";
        values += ", " + cacheDbEntry.getDate();

        attributes += ", weighted_attribute_id" + ", count_1" + ", count_2" + ", count_3" + ", count_4" + ", count_5";
        values += ", " + cacheDbEntry.getWeightedAttributeId()
                + ", " + cacheDbEntry.getCount_1()
                + ", " + cacheDbEntry.getCount_2()
                + ", " + cacheDbEntry.getCount_3()
                + ", " + cacheDbEntry.getCount_4()
                + ", " + cacheDbEntry.getCount_5();

        return insertString + "(" + attributes + ") values (" + values + ")";
    }

    private String getUpdateString(int companyId, CacheDbEntry cacheDbEntry) {
        String insertString = "update cache_company_" + companyId + " set ";

        String attributes = "branch_id = " + cacheDbEntry.getBranchId();
        String constraint = "branch_id = " + cacheDbEntry.getBranchId();

        for (int key : cacheDbEntry.mapOfFilters.keySet()) {
            attributes += ", filter_attribute_" + key + " = " + cacheDbEntry.mapOfFilters.get(key);
            constraint += " and filter_attribute_" + key + " = " + cacheDbEntry.mapOfFilters.get(key);
        }

        attributes += ", date = " + cacheDbEntry.getDate();
        constraint += " and date = " + cacheDbEntry.getDate();

        attributes += ", weighted_attribute_id = " + cacheDbEntry.getWeightedAttributeId()
                + ", count_1 = " + cacheDbEntry.getCount_1()
                + ", count_2 = " + cacheDbEntry.getCount_2()
                + ", count_3 = " + cacheDbEntry.getCount_3()
                + ", count_4 = " + cacheDbEntry.getCount_4()
                + ", count_5 = " + cacheDbEntry.getCount_5();

        return insertString + attributes + " where " + constraint;
    }

    /*                   Object mapper         */

    private List<CacheDbEntry> processResultSet(List<Map<String, Object>> resultSet, List<Attribute> filterAttributes) {
        List<CacheDbEntry> outList = new ArrayList<CacheDbEntry>(resultSet.size());

        for (Map<String, Object> result : resultSet) {
            CacheDbEntry dbEntry = new CacheDbEntry();

            dbEntry.setBranchId((Integer) result.get("branch_id"));
            dbEntry.setDate((Integer) result.get("date"));

            Map<Integer, Integer> map = new HashMap<Integer, Integer>(filterAttributes.size());
            for (Attribute attribute : filterAttributes) {
                map.put(attribute.getAttributeId(), (Integer) result.get("filter_attribute_" + attribute.getAttributeId()));
            }

            dbEntry.setWeightedAttributeId((Integer) result.get("weighted_attribute_id"));
            dbEntry.setCount_1((Integer) result.get("count_1"));
            dbEntry.setCount_2((Integer) result.get("count_2"));
            dbEntry.setCount_3((Integer) result.get("count_3"));
            dbEntry.setCount_4((Integer) result.get("count_4"));
            dbEntry.setCount_5((Integer) result.get("count_5"));

            outList.add(dbEntry);
        }

        return outList;
    }
}

/*
  List<Attribute> filterAttributes = new ArrayList<Attribute>(2);
        Attribute attribute = new Attribute();
        attribute.setAttributeId(3);
        filterAttributes.add(attribute);

        Attribute attribute2 = new Attribute();
        attribute2.setAttributeId(11);
        filterAttributes.add(attribute2);


        try{
        cacheJdbcClient.createTable(1, filterAttributes);
        }catch(Exception e){
            System.out.println();
        }


        //insertion

        for(int i=20140701;i<20140718;i++){
            CacheDbEntry dbEntry = new CacheDbEntry();
            dbEntry.setBranchId(1);
            dbEntry.setDate(i);

            Map<Integer, Integer> mapOfFilters = new HashMap<Integer, Integer>(2);
            mapOfFilters.put(3, 1);
            mapOfFilters.put(11, 1);
            dbEntry.setMapOfFilters(mapOfFilters);

            dbEntry.setWeightedAttributeId(1);
            dbEntry.setCount_1(1);
            dbEntry.setCount_2(2);
            dbEntry.setCount_3(3);
            dbEntry.setCount_4(4);
            dbEntry.setCount_5(5);

            try{
            cacheJdbcClient.addEntry(1, dbEntry);
            }catch(Exception e){
                System.out.println();
            }
        }

 */
