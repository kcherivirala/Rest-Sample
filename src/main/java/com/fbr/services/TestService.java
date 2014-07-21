package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Cache.CacheJdbcClient;
import com.fbr.domain.Attribute.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("testService")
public class TestService {
    @Autowired
    private CacheJdbcClient cacheJdbcClient;

    public Attribute test() {
        /*
        List<Attribute> filterAttributes = new ArrayList<Attribute>(2);
        Attribute attribute = new Attribute();
        attribute.setAttributeId(3);
        filterAttributes.add(attribute);

        Attribute attribute2 = new Attribute();
        attribute2.setAttributeId(11);
        filterAttributes.add(attribute2);

        Map<Integer, Integer> map = new HashMap<Integer, Integer>(2);
        map.put(3, 1);
        map.put(11, 1);
        try {
            List<CacheDbEntry> list = cacheJdbcClient.getEntries(1, 1, map, filterAttributes);
            list = cacheJdbcClient.getEntries(1, map, filterAttributes);
            list = cacheJdbcClient.getEntries(1, 1, map, 20140701, 20140705, filterAttributes);
            list = cacheJdbcClient.getEntries(1, map, 20140701, 20140705, filterAttributes);
        } catch (Exception e) {
            System.out.println();
        }
        */

        List<Attribute> filtersOld = new ArrayList<Attribute>(1);
        List<Attribute> filtersNew = new ArrayList<Attribute>(2);

        Attribute one = new Attribute();
        one.setAttributeId(3);
        Attribute two = new Attribute();
        two.setAttributeId(11);

        filtersOld.add(one);
        filtersNew.add(one);
        filtersNew.add(two);

        try {
            cacheJdbcClient.updateTable(1, filtersOld, filtersNew);
        } catch (Exception e) {
            System.out.println();
        }


        return null;
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


}
