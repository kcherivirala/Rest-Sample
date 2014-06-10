package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Attribute.AttributeDao;
import com.fbr.Dao.Attribute.AttributeValuesDao;
import com.fbr.Dao.Attribute.Entities.AttributeDbType;
import com.fbr.Dao.Attribute.Entities.AttributeValuesDbType;
import com.fbr.Dao.Attribute.Entities.AttributeValuesPrimaryKey;
import com.fbr.Utilities.Comparators;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Attribute.AttributeValue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AttributeService {
    private static final Logger logger = Logger.getLogger(AttributeService.class);
    @Autowired
    private AttributeDao attributeDao;
    @Autowired
    private AttributeValuesDao attributeValuesDao;

    @Transactional
    public Attribute addAttributeAndValues(Attribute attribute) {
        int id = attributeDao.getMaxAttributeIdValue() + 1;
        AttributeDbType attributeDbEntry = Conversions.getAttributeDbEntry(id, attribute);
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
        updateAttributeValues(dbEntry.getAttributeId(), attributeValuesDbEntries, attribute.getAttributeValues());
        return attribute;
    }

    @Transactional
    public void deleteAttributeAndValues(int attrId) {
        attributeDao.delete(attrId);
        attributeValuesDao.deleteAttributeValues(attrId);

    }

    public List<Attribute> getAttributeAndValues() {
        try {
            List<AttributeDbType> attributeDbEntries = attributeDao.findAll();
            List<AttributeValuesDbType> attributeValuesDbEntries = attributeValuesDao.findAll();

            return matchAttributesAndValues(attributeDbEntries, attributeValuesDbEntries);
        } catch (Exception e) {
            System.out.println();
            return null;
        }
    }

    public Attribute getAttributeAndValues(int attrId) {
        AttributeDbType attributeDbEntry = attributeDao.find(attrId);
        List<AttributeValuesDbType> attributeValuesDbEntries = attributeValuesDao.getAttributeValues(attrId);
        return matchAttributeAndValues(attributeDbEntry, attributeValuesDbEntries);
    }

    public List<Attribute> getAttributesByCompany(int companyId) {
        List<AttributeDbType> attributeList = attributeDao.getAttributesByCompany(companyId);
        List<AttributeValuesDbType> attributeValuesList = attributeValuesDao.getAttributeValuesByCompany(companyId);

        return matchAttributesAndValues(attributeList, attributeValuesList);
    }

    public List<AttributeDbType> getDbAttributesByCompany(int companyId) {
        return attributeDao.getAttributesByCompany(companyId);
    }

    public List<Attribute> getAttributes(List<Attribute> listFiltersId, List<Attribute> listAttribute) {
        Collections.sort(listFiltersId, Comparators.COMPARE_DOMAIN_ATTRIBUTES);
        List<Attribute> out = new ArrayList<Attribute>(listFiltersId.size());

        int idIndex = 0, attrIndex = 0;
        while (idIndex < listFiltersId.size() && attrIndex < listAttribute.size()) {
            int id = listFiltersId.get(idIndex).getAttributeId();
            Attribute attribute = listAttribute.get(attrIndex);

            if (id == attribute.getAttributeId()) {
                out.add(attribute);
                idIndex++;
                attrIndex++;
            } else if (id < attribute.getAttributeId()) {
                idIndex++;
            } else {
                attrIndex++;
            }
        }
        return out;
    }

    public int getAttributeValueIndex(Attribute attribute, AttributeValue attributeValue) {
        int index = -1;
        int i = 0;
        for (AttributeValue attrValue : attribute.getAttributeValues()) {
            if (attrValue.getValue() == attributeValue.getValue())
                index = i;
            i++;
        }
        return index;
    }

    public int getAttributeValueIndex(Attribute attribute, int attributeValue) {
        int i = 0;
        for (AttributeValue attrValue : attribute.getAttributeValues()) {
            if (attrValue.getValue() == attributeValue)
                return i;
            i++;
        }
        return -1;
    }

    public Map<String, Integer> getMapOfFilters(int companyId, Map<String, String> map) {
        if (map == null || map.size() == 0)
            return null;

        Map<String, Integer> outMap = new HashMap<String, Integer>();
        List<Attribute> listAttribute = getAttributesByCompany(companyId);

        for (Attribute attribute : listAttribute) {
            String attrValString = map.get(attribute.getAttributeString());
            if (attrValString != null) {
                int attrValue = getAttributeValue(attribute.getAttributeValues(), attrValString);
                outMap.put(attribute.getAttributeString(), attrValue);
            }
        }
        return outMap;
    }

    /*          Private functions           */

    private void updateAttributeDbEntry(AttributeDbType attributeDbEntry, Attribute attribute) {
        attributeDbEntry.setAttributeString(attribute.getAttributeString());
        attributeDbEntry.setParentId(attribute.getParentId());
        attributeDbEntry.setType(attribute.getType());

        attributeDao.update(attributeDbEntry);
    }

    private void updateAttributeValueDbEntry(AttributeValuesDbType attributeValueDbEntry, AttributeValue attributeValue) {
        boolean updated = false;
        if (attributeValueDbEntry.getMaxValue() != attributeValue.getMaxValue()) {
            attributeValueDbEntry.setMaxValue(attributeValue.getMaxValue());
            updated = true;
        }

        if (!attributeValueDbEntry.getName().equals(attributeValue.getName())) {
            attributeValueDbEntry.setName(attributeValue.getName());
            updated = true;
        }
        if (updated)
            attributeValuesDao.update(attributeValueDbEntry);
    }

    private void addAttributeValueDbEntry(int attributeId, AttributeValue attributeValue) {
        AttributeValuesDbType attributeValuesDbEntry = Conversions.getAttributeValueDbEntry(attributeId, attributeValue);
        attributeValuesDao.add(attributeValuesDbEntry);
    }

    private void deleteAttributeValueDbEntry(AttributeValuesDbType attributeValuesDbEntry) {
        attributeValuesDao.delete(attributeValuesDbEntry);
    }

    private void updateAttributeValues(int attributeId, List<AttributeValuesDbType> attributeValuesDbEntries, List<AttributeValue> inputValues) {
        Collections.sort(attributeValuesDbEntries, Comparators.COMPARE_ATTRIBUTE_VALUES);
        Collections.sort(inputValues, Comparators.COMPARE_DOMAIN_ATTRIBUTE_VALUES);

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

    private Attribute matchAttributeAndValues(AttributeDbType attributeDbEntry, List<AttributeValuesDbType> attributeValuesList) {
        List<AttributeDbType> list = new ArrayList<AttributeDbType>(1);
        list.add(attributeDbEntry);
        return matchAttributesAndValues(list, attributeValuesList).get(0);
    }

    private List<Attribute> matchAttributesAndValues(List<AttributeDbType> attributeList, List<AttributeValuesDbType> attributeValuesList) {
        List<Attribute> out = new ArrayList<Attribute>();
        Collections.sort(attributeList, Comparators.COMPARE_ATTRIBUTES);
        Collections.sort(attributeValuesList, Comparators.COMPARE_ATTRIBUTE_VALUES);

        int aIndex = 0, vIndex = 0;
        while (aIndex < attributeList.size()) {
            AttributeDbType attributeDbEntry = attributeList.get(aIndex);
            int attributeId = attributeDbEntry.getAttributeId();

            Attribute outAttribute = Conversions.getAttribute(attributeDbEntry);
            List<AttributeValue> valueList = new ArrayList<AttributeValue>();
            outAttribute.setAttributeValues(valueList);

            while (vIndex < attributeValuesList.size() && attributeId == attributeValuesList.get(vIndex).getId().getAttributeId()) {
                AttributeValue attributeValue = Conversions.getAttributeValue(attributeValuesList.get(vIndex));
                valueList.add(attributeValue);
                vIndex++;
            }
            out.add(outAttribute);
            aIndex++;
        }

        return out;
    }

    private int getAttributeValue(List<AttributeValue> attributeValueList, String attrValueString) {
        for (AttributeValue attributeValue : attributeValueList) {
            if (attrValueString.equals(attributeValue.getName())) {
                return attributeValue.getValue();
            }
        }
        return -1;
    }

    public static class Conversions {
        public static Attribute getAttribute(AttributeDbType attributeDbEntry) {
            Attribute attribute = new Attribute();
            attribute.setAttributeId(attributeDbEntry.getAttributeId());
            attribute.setAttributeString(attributeDbEntry.getAttributeString());
            attribute.setParentId(attributeDbEntry.getParentId());
            attribute.setType(attributeDbEntry.getType());

            return attribute;
        }

        public static AttributeValue getAttributeValue(AttributeValuesDbType attributeValuesDbEntry) {
            AttributeValue attributeValue = new AttributeValue();

            attributeValue.setMaxValue(attributeValuesDbEntry.getMaxValue());
            attributeValue.setName(attributeValuesDbEntry.getName());
            attributeValue.setValue(attributeValuesDbEntry.getId().getValue());

            return attributeValue;
        }

        public static AttributeDbType getAttributeDbEntry(int attributeId, Attribute attribute) {
            AttributeDbType attributeDbEntry = new AttributeDbType();
            attributeDbEntry.setAttributeId(attributeId);
            attributeDbEntry.setAttributeString(attribute.getAttributeString());
            attributeDbEntry.setParentId(attribute.getParentId());
            attributeDbEntry.setType(attribute.getType());

            return attributeDbEntry;
        }

        public static AttributeValuesDbType getAttributeValueDbEntry(int attributeId, AttributeValue attributeValue) {
            AttributeValuesDbType attributeValuesDbEntry = new AttributeValuesDbType();
            AttributeValuesPrimaryKey id = new AttributeValuesPrimaryKey();
            attributeValuesDbEntry.setId(id);

            id.setAttributeId(attributeId);
            id.setValue(attributeValue.getValue());

            attributeValuesDbEntry.setMaxValue(attributeValue.getMaxValue());
            attributeValuesDbEntry.setName(attributeValue.getName());

            return attributeValuesDbEntry;
        }
    }
}
