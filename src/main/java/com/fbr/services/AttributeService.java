package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.AttributeDao;
import com.fbr.Dao.AttributeValuesDao;
import com.fbr.Dao.Entities.AttributeDbType;
import com.fbr.Dao.Entities.AttributeValuesDbType;
import com.fbr.Dao.Entities.AttributeValuesPrimaryKey;
import com.fbr.domain.Answer;
import com.fbr.domain.Attribute;
import com.fbr.domain.AttributeValue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AttributeService {
    private static final Logger logger = Logger.getLogger(AttributeService.class);
    @Autowired
    private AttributeDao attributeDao;
    @Autowired
    private AttributeValuesDao attributeValuesDao;

    @Transactional
    public Attribute addAttributeAndValues(Attribute attribute) {
        AttributeDbType attributeDbEntry = getAttributeDbEntry(attribute);
        attributeDao.add(attributeDbEntry);

        for (AttributeValue attributeValue : attribute.getAttributeValues()) {
            addAttributeValueDbEntry(attributeDbEntry.getAttributeId(), attributeValue);
        }
        attribute.setAttributeId(attributeDbEntry.getAttributeId());
        return attribute;
    }

    @Transactional
    public Attribute updateAttributeAndValues(int attributeId, Attribute attribute) {
        AttributeDbType dbEntry = attributeDao.find(attributeId);
        List<AttributeValuesDbType> attributeValuesDbEntries = attributeValuesDao.getAttributeValues(attributeId);

        updateAttributeDbEntry(dbEntry, attribute);
        updateAttributeValues(dbEntry.getAttributeId(), attributeValuesDbEntries, attribute);
        return attribute;
    }

    @Transactional
    public void deleteAttributeAndValues(int attrId) {
        attributeDao.delete(attrId);
        attributeValuesDao.deleteAttributeValues(attrId);

    }

    public List<Attribute> getAttributeAndValues() {
        List<AttributeDbType> attributeDbEntries = attributeDao.findAll();
        List<AttributeValuesDbType> attributeValuesDbEntries = attributeValuesDao.findAll();

        return matchAttributesAndValues(attributeDbEntries, attributeValuesDbEntries);
    }

    public Attribute getAttributeAndValues(int attrId) {
        AttributeDbType attributeDbEntry = attributeDao.find(attrId);
        List<AttributeValuesDbType> attributeValuesDbEntries = attributeValuesDao.getAttributeValues(attrId);
        return matchAttributeAndValues(attributeDbEntry, attributeValuesDbEntries);
    }




    public List<Attribute> getAttributesByCompany(int companyId) {
        List<AttributeDbType> attributeList = getDbAttributesByCompany(companyId);
        List<AttributeValuesDbType> attributeValuesList = getDbAttributeValuesByCompany(companyId);

        return matchAttributesAndValues(attributeList, attributeValuesList);
    }

    public List<AttributeDbType> getDbAttributesByCompany(int companyId) {
        return attributeDao.getAttributesByCompany(companyId);
    }

    public List<AttributeValuesDbType> getDbAttributeValuesByCompany(int companyId) {
        return attributeValuesDao.getAttributeValuesByCompany(companyId);
    }



    private Attribute getAttribute(AttributeDbType attributeDbEntry) {
        Attribute attribute = new Attribute();
        attribute.setAttributeId(attributeDbEntry.getAttributeId());
        attribute.setAttributeString(attributeDbEntry.getAttributeString());
        attribute.setParentId(attributeDbEntry.getParentId());
        attribute.setType(attributeDbEntry.getType());

        return attribute;
    }

    private AttributeValue getAttributeValue(AttributeValuesDbType attributeValuesDbEntry) {
        AttributeValue attributeValue = new AttributeValue();

        attributeValue.setMaxValue(attributeValuesDbEntry.getMaxValue());
        attributeValue.setName(attributeValuesDbEntry.getName());
        attributeValue.setValue(attributeValuesDbEntry.getId().getValue());

        return attributeValue;
    }

    private AttributeDbType getAttributeDbEntry(Attribute attribute) {
        AttributeDbType attributeDbEntry = new AttributeDbType();
        int Id = attributeDao.getMaxAttributeIdValue() + 1;
        attributeDbEntry.setAttributeId(Id);
        attributeDbEntry.setAttributeString(attribute.getAttributeString());
        attributeDbEntry.setParentId(attribute.getParentId());
        attributeDbEntry.setType(attribute.getType());

        return attributeDbEntry;
    }

    private AttributeValuesDbType getAttributeValueDbEntry(int attributeId, AttributeValue attributeValue) {
        AttributeValuesDbType attributeValuesDbEntry = new AttributeValuesDbType();
        AttributeValuesPrimaryKey id = new AttributeValuesPrimaryKey();
        attributeValuesDbEntry.setId(id);

        id.setAttributeId(attributeId);
        id.setValue(attributeValue.getValue());

        attributeValuesDbEntry.setMaxValue(attributeValue.getMaxValue());
        attributeValuesDbEntry.setName(attributeValue.getName());

        return attributeValuesDbEntry;
    }



    private void updateAttributeDbEntry(AttributeDbType attributeDbEntry, Attribute attribute) {
        attributeDbEntry.setAttributeString(attribute.getAttributeString());
        attributeDbEntry.setParentId(attribute.getParentId());
        attributeDbEntry.setType(attribute.getType());

        attributeDao.update(attributeDbEntry);
    }

    private void updateAttributeValueDbEntry(AttributeValuesDbType attributeValueDbEntry, AttributeValue attributeValue){
        boolean updated = false;
        if(attributeValueDbEntry.getMaxValue()!=attributeValue.getMaxValue()){
            attributeValueDbEntry.setMaxValue(attributeValue.getMaxValue());
            updated = true;
        }

        if(!attributeValueDbEntry.getName().equals(attributeValue.getName())){
            attributeValueDbEntry.setName(attributeValue.getName());
            updated = true;
        }
        if (updated)
            attributeValuesDao.update(attributeValueDbEntry);
    }



    private void addAttributeValueDbEntry(int attributeId, AttributeValue attributeValue){
        AttributeValuesDbType attributeValuesDbEntry = getAttributeValueDbEntry(attributeId, attributeValue);
        attributeValuesDao.add(attributeValuesDbEntry);
    }

    private void deleteAttributeValueDbEntry(AttributeValuesDbType attributeValuesDbEntry){
        attributeValuesDao.delete(attributeValuesDbEntry);
    }

    private void updateAttributeValues(int attributeId, List<AttributeValuesDbType> attributeValuesDbEntries, Attribute attribute) {
        List<AttributeValue> inputValues = attribute.getAttributeValues();
        Collections.sort(attributeValuesDbEntries, COMPARE_ATTRIBUTE_VALUES);
        Collections.sort(inputValues, COMPARE_DOMAIN_ATTRIBUTE_VALUES);

        int dbIndex = 0, inputIndex = 0;
        while (dbIndex < attributeValuesDbEntries.size() && inputIndex < inputValues.size()) {
            AttributeValuesDbType attributeValueDbEntry = attributeValuesDbEntries.get(dbIndex);
            AttributeValue inputValue = inputValues.get(inputIndex);

            if (inputValue.getValue() == attributeValueDbEntry.getId().getValue()) {
                updateAttributeValueDbEntry(attributeValueDbEntry, inputValue);
                dbIndex++;
                inputIndex++;
            } else if (inputValue.getValue() < attributeValueDbEntry.getId().getValue()) {
                addAttributeValueDbEntry(attributeId, inputValue);
                inputIndex++;
            } else {
                deleteAttributeValueDbEntry(attributeValueDbEntry);
                dbIndex++;
            }
        }
        while (dbIndex < attributeValuesDbEntries.size()) {
            deleteAttributeValueDbEntry(attributeValuesDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inputIndex < inputValues.size()) {
            addAttributeValueDbEntry(attributeId, inputValues.get(inputIndex));
            inputIndex++;
        }

    }

    private Attribute matchAttributeAndValues(AttributeDbType attributeDbEntry, List<AttributeValuesDbType> attributeValuesList){
        List<AttributeDbType> list = new ArrayList<AttributeDbType>(1);
        list.add(attributeDbEntry);
        return matchAttributesAndValues(list, attributeValuesList).get(0);
    }

    private List<Attribute> matchAttributesAndValues(List<AttributeDbType> attributeList, List<AttributeValuesDbType> attributeValuesList) {
        List<Attribute> out = new ArrayList<Attribute>();
        Collections.sort(attributeList, COMPARE_ATTRIBUTES);
        Collections.sort(attributeValuesList, COMPARE_ATTRIBUTE_VALUES);

        int aIndex = 0, vIndex = 0;
        while (aIndex < attributeList.size()) {
            AttributeDbType attributeDbEntry = attributeList.get(aIndex);
            int attributeId = attributeDbEntry.getAttributeId();

            Attribute outAttribute = getAttribute(attributeDbEntry);
            List<AttributeValue> valueList = new ArrayList<AttributeValue>();
            outAttribute.setAttributeValues(valueList);

            while (vIndex < attributeValuesList.size() && attributeId == attributeValuesList.get(vIndex).getId().getAttributeId()) {
                AttributeValue attributeValue = getAttributeValue(attributeValuesList.get(vIndex));
                valueList.add(attributeValue);
                vIndex++;
            }
            out.add(outAttribute);
            aIndex++;
        }

        return out;
    }


    private static Comparator<AttributeDbType> COMPARE_ATTRIBUTES = new Comparator<AttributeDbType>() {
        @Override
        public int compare(AttributeDbType first, AttributeDbType second) {
            return first.getAttributeId() - second.getAttributeId();
        }
    };

    private static Comparator<AttributeValuesDbType> COMPARE_ATTRIBUTE_VALUES = new Comparator<AttributeValuesDbType>() {
        @Override
        public int compare(AttributeValuesDbType first, AttributeValuesDbType second) {
            if (first.getId().getAttributeId() == second.getId().getAttributeId())
                return first.getId().getValue() - second.getId().getValue();
            else
                return first.getId().getAttributeId() - second.getId().getAttributeId();
        }
    };

    private static Comparator<AttributeValue> COMPARE_DOMAIN_ATTRIBUTE_VALUES = new Comparator<AttributeValue>() {
        @Override
        public int compare(AttributeValue first, AttributeValue second) {
            return first.getValue() - second.getValue();
        }
    };
}
