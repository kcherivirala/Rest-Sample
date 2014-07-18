package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Cache.CacheDbEntry;
import com.fbr.Dao.Cache.CacheJdbcClient;
import com.fbr.domain.Attribute.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("testService")
public class TestService {
    @Autowired
    private CacheJdbcClient cacheJdbcClient;

    public Attribute test(){
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

        return null;
    }

}
