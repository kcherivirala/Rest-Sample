package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.AttributeDao;
import com.fbr.Dao.Entities.AttributeDbType;
import com.fbr.domain.Attribute;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AttributeService {
    private static final Logger logger = Logger.getLogger(AttributeService.class);
    @Autowired
    private AttributeDao attributeDao;

    public Attribute addAttribute(Attribute attribute){
        AttributeDbType attributeDbEntry = getAttributeDbEntry(attribute);
        attributeDao.add(attributeDbEntry);

        attribute.setAttributeId(attributeDbEntry.getAttributeId());
        return attribute;
    }

    @Transactional
    public Attribute updateAttribute(Attribute attribute){
        AttributeDbType dbEntry = attributeDao.find(attribute.getAttributeId());
        dbEntry.setAttributeString(attribute.getAttributeString());
        dbEntry.setParentId(attribute.getParentId());

        attributeDao.update(dbEntry);
        return attribute;
    }

    public List<Attribute> getAllAttributes(){
        List<Attribute> list = new ArrayList<Attribute>();

        List<AttributeDbType> listDb = attributeDao.findAll();
        for(AttributeDbType attributeDbEntry: listDb){
            Attribute attribute = getAttribute(attributeDbEntry);
            list.add(attribute);
        }
        return list;
    }

    public Attribute getAtribute(String attrId){
        AttributeDbType attributeDbEntry = attributeDao.find(attrId);
        return getAttribute(attributeDbEntry);
    }

    public void delete(String attrId){
        attributeDao.delete(attrId);
    }

    public List<AttributeDbType> getAttributesByCompany(String companyId){
        return attributeDao.getAttributesByCompany(companyId);
    }

    private Attribute getAttribute(AttributeDbType attributeDbEntry){
        Attribute attribute = new Attribute();
        attribute.setAttributeId(attributeDbEntry.getAttributeId());
        attribute.setAttributeString(attributeDbEntry.getAttributeString());
        attribute.setParentId(attributeDbEntry.getParentId());

        return attribute;
    }

    private AttributeDbType getAttributeDbEntry(Attribute attribute){
        AttributeDbType attributeDbEntry = new AttributeDbType();
        String Id = UUID.randomUUID().toString();
        attributeDbEntry.setAttributeId(Id);
        attributeDbEntry.setAttributeString(attribute.getAttributeString());
        attributeDbEntry.setParentId(attribute.getParentId());

        return attributeDbEntry;
    }
}
