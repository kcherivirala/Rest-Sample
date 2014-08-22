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
import com.fbr.Dao.Company.Entities.CompanyDbType;
import com.fbr.Utilities.Comparators;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Attribute.AttributeValue;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class AttributeService {
    private static final Logger logger = Logger.getLogger(AttributeService.class);
    @Autowired
    private AttributeDao attributeDao;
    @Autowired
    private AttributeValuesDao attributeValuesDao;
    @Autowired
    private CompanyService companyService;

    private Map<Integer, CompanyAttributeData> mapCompanyAttributes;

    @PostConstruct
    public void init() {
        try {
            List<CompanyDbType> companies = companyService.getCompanyDbEntries();
            mapCompanyAttributes = new HashMap<Integer, CompanyAttributeData>(companies.size());

            for (CompanyDbType company : companies) {
                mapCompanyAttributes.put(company.getCompanyId(), getCompanyAttributeData(company.getCompanyId()));
            }
        } catch (Exception e) {
            logger.error("failed to initialise the attributes for companies" + e.getMessage());
        }
    }

    @Transactional
    public Attribute addAttributeAndValues(Attribute attribute) throws Exception {
        try {
            logger.info("adding new attribute : " + attribute.getAttributeString() + " and count of values : " + attribute.getAttributeValues().size());
            int id = attributeDao.getMaxAttributeIdValue() + 1;
            AttributeDbType attributeDbEntry = Conversions.getAttributeDbEntry(id, attribute);
            attributeDao.add(attributeDbEntry);

            for (AttributeValue attributeValue : attribute.getAttributeValues()) {
                attributeValuesDao.add(Conversions.getAttributeValueDbEntry(id, attributeValue));
            }
            attribute.setAttributeId(attributeDbEntry.getAttributeId());
            logger.info("done adding : " + attribute.getAttributeString() + " and count of values : " + attribute.getAttributeValues().size());
            return attribute;
        } catch (Exception e) {
            logger.error("error adding attribute and values : " + attribute.getAttributeString() + " : " + e.getMessage());
            throw new Exception("error adding attribute and values : " + attribute.getAttributeString() + " : " + e.getMessage());
        }
    }

    @Transactional
    public Attribute updateAttributeAndValues(int attributeId, Attribute attribute) throws Exception {
        try {
            logger.info("updating new attribute : " + attribute.getAttributeString() + " and count of values : " + attribute.getAttributeValues().size());
            AttributeDbType dbEntry = attributeDao.find(attributeId);

            updateAttributeDbEntry(dbEntry, attribute);
            logger.info("done updating : " + attribute.getAttributeString() + " and count of values : " + attribute.getAttributeValues().size());
            return attribute;
        } catch (Exception e) {
            logger.error("error updating attribute : " + attribute.getAttributeId() + " : " + e.getMessage());
            throw new Exception("error updating attribute : " + attribute.getAttributeId() + " : " + e.getMessage());
        }
    }

    @Transactional
    public void deleteAttributeAndValues(int attrId) throws Exception {
        try {
            logger.info("deleting attribute : " + attrId);
            attributeValuesDao.deleteAttributeValues(attrId);
            attributeDao.delete(attrId);
            logger.info("done deleting attribute : " + attrId);
        } catch (Exception e) {
            logger.error("error deleting attribute : " + attrId + " : " + e.getMessage());
            throw new Exception("error deleting attribute : " + attrId + " : " + e.getMessage());
        }
    }

    @Transactional
    public List<Attribute> getAttributeAndValues() throws Exception {
        try {
            logger.info("getting all attributes and values");
            List<AttributeDbType> attributeDbEntries = attributeDao.findAll();

            logger.info("done getting all attributes and values");
            return Conversions.getAttributes(attributeDbEntries);
        } catch (Exception e) {
            logger.error("error getting attributes : " + e.getMessage());
            throw new Exception("error getting attributes : " + e.getMessage());
        }
    }

    @Transactional
    public Attribute getAttributeAndValues(int attrId) throws Exception {
        try {
            logger.info("getting all attributes and values for : " + attrId);
            AttributeDbType attributeDbEntry = attributeDao.find(attrId);

            Attribute out = Conversions.getAttribute(attributeDbEntry);
            logger.info("done getting all attributes and values for : " + attrId);
            return out;
        } catch (Exception e) {
            logger.error("error getting : " + attrId + " : " + e.getMessage());
            throw new Exception("error getting : " + attrId + " : " + e.getMessage());
        }
    }


    /*      Internal functions    */


    public List<Attribute> getAttributesByCompany(int companyId) {
        if (mapCompanyAttributes.get(companyId) == null) resetCompanyAttributes(companyId);
        return mapCompanyAttributes.get(companyId).attributeList;
    }

    public List<Attribute> getWeightedAttributes(int companyId) {
        List<Attribute> list = getAttributesByCompany(companyId);
        List<Attribute> out = new ArrayList<Attribute>();

        for (Attribute attribute : list) {
            if (attribute.getType().equals("weighted")) {
                out.add(attribute);
            }
        }
        return out;
    }

    public List<Attribute> getFilterAttributes(int companyId) {
        List<Attribute> list = getAttributesByCompany(companyId);
        List<Attribute> out = new ArrayList<Attribute>();

        for (Attribute attribute : list) {
            if (attribute.getType().equals("non-weighted")) {
                out.add(attribute);
            }
        }
        return out;
    }

    public List<Attribute> getAttributesForIds(List<Attribute> listFiltersId, List<Attribute> listAttribute) {
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

    public int getAttributeValueIndex(Attribute attribute, int attributeValue) {
        int i = 0;
        for (AttributeValue attrValue : attribute.getAttributeValues()) {
            if (attrValue.getValue() == attributeValue)
                return i;
            i++;
        }
        return -1;
    }

    public Map<String, Integer> getMapOfInputArgumentFilters(int companyId, Map<String, String> map) {
        if (map == null || map.size() == 0)
            return null;

        Map<String, Integer> outMap = new HashMap<String, Integer>();
        if (map.containsKey("branch")) {
            logger.debug("adding branch in the argument filter");
            outMap.put("branch", Integer.parseInt(map.get("branch")));
        }
        List<Attribute> listAttribute = getAttributesByCompany(companyId);

        for (Attribute attribute : listAttribute) {
            String attrValString = map.get(attribute.getAttributeString());
            if (attrValString != null) {
                logger.debug("adding attribute : " + attribute.getAttributeId() + " to the argument filters");
                int attrValue = getAttributeValue(attribute.getAttributeValues(), attrValString);
                outMap.put(attribute.getAttributeString(), attrValue);
            }
        }
        return outMap;
    }

    public void resetCompanyAttributes(int companyId) {
        mapCompanyAttributes.put(companyId, getCompanyAttributeData(companyId));
    }

    public int getNPSAttr(int companyId) {
        List<Attribute> list = getAttributesByCompany(companyId);
        for (Attribute attribute : list) {
            if (attribute.getAttributeString().equals("NPS"))
                return attribute.getAttributeId();
        }
        return -1;
    }

    /*          Private functions           */

    private void updateAttributeDbEntry(AttributeDbType attributeDbEntry, Attribute attribute) {
        logger.debug("modifying attribute : " + attributeDbEntry.getAttributeId());
        attributeDbEntry.setAttributeString(attribute.getAttributeString());
        attributeDbEntry.setType(attribute.getType());

        attributeDao.update(attributeDbEntry);

        updateAttributeValues(attributeDbEntry.getAttributeId(), attributeDbEntry.getAttributeValues(), attribute.getAttributeValues());
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

    private void updateAttributeValues(int attributeId, List<AttributeValuesDbType> attributeValuesDbEntries, List<AttributeValue> inputValues) {
        Collections.sort(attributeValuesDbEntries, Comparators.COMPARE_ATTRIBUTE_VALUES);
        Collections.sort(inputValues, Comparators.COMPARE_DOMAIN_ATTRIBUTE_VALUES);

        int dbIndex = 0, inputIndex = 0;
        while (dbIndex < attributeValuesDbEntries.size() && inputIndex < inputValues.size()) {
            AttributeValuesDbType attributeValueDbEntry = attributeValuesDbEntries.get(dbIndex);
            AttributeValue inputValue = inputValues.get(inputIndex);

            if (inputValue.getValue() == attributeValueDbEntry.getId().getValue()) {
                logger.debug("modifying : (" + attributeId + "," + inputValue.getValue() + ")");
                updateAttributeValueDbEntry(attributeValueDbEntry, inputValue);
                dbIndex++;
                inputIndex++;
            } else if (inputValue.getValue() < attributeValueDbEntry.getId().getValue()) {
                logger.debug("adding : (" + attributeId + "," + inputValue.getValue() + ")");
                attributeValuesDao.add(Conversions.getAttributeValueDbEntry(attributeId, inputValue));
                inputIndex++;
            } else {
                logger.debug("deleting : (" + attributeId + "," + inputValue.getValue() + ")");
                deleteAttributeValueDbEntry(attributeValueDbEntry);
                dbIndex++;
            }
        }
        while (dbIndex < attributeValuesDbEntries.size()) {
            logger.debug("deleting : (" + attributeId + "," + attributeValuesDbEntries.get(dbIndex).getId().getValue() + ")");
            deleteAttributeValueDbEntry(attributeValuesDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inputIndex < inputValues.size()) {
            AttributeValue inputValue = inputValues.get(inputIndex);
            logger.debug("adding : (" + attributeId + "," + inputValues.get(inputIndex).getValue() + ")");
            attributeValuesDao.add(Conversions.getAttributeValueDbEntry(attributeId, inputValue));
            inputIndex++;
        }

    }

    private void deleteAttributeValueDbEntry(AttributeValuesDbType attributeValuesDbEntry) {
        attributeValuesDao.delete(attributeValuesDbEntry);
    }

    private int getAttributeValue(List<AttributeValue> attributeValueList, String attrValueString) {
        for (AttributeValue attributeValue : attributeValueList) {
            if (attrValueString.equals(attributeValue.getName())) {
                return attributeValue.getValue();
            }
        }
        return -1;
    }

    @Transactional
    private List<Attribute> calculateAttributesByCompany(int companyId) {
        List<AttributeDbType> attributeList = attributeDao.getAttributesByCompany(companyId);
        return Conversions.getAttributes(attributeList);
    }

    private CompanyAttributeData getCompanyAttributeData(int companyId) {
        logger.info("getting attribute data for company : " + companyId);
        CompanyAttributeData companyAttributeData = new CompanyAttributeData();
        companyAttributeData.companyId = companyId;
        companyAttributeData.attributeList = calculateAttributesByCompany(companyId);

        return companyAttributeData;
    }

    public static class Conversions {
        public static List<Attribute> getAttributes(List<AttributeDbType> attributeList) {
            List<Attribute> out = new ArrayList<Attribute>(attributeList.size());
            for (AttributeDbType entry : attributeList) {
                out.add(getAttribute(entry));
            }
            return out;
        }

        public static Attribute getAttribute(AttributeDbType attributeDbEntry) {
            Attribute attribute = new Attribute();
            attribute.setAttributeId(attributeDbEntry.getAttributeId());
            attribute.setAttributeString(attributeDbEntry.getAttributeString());
            attribute.setType(attributeDbEntry.getType());

            Hibernate.initialize(attributeDbEntry.getAttributeValues());

            List<AttributeValue> list = new ArrayList<AttributeValue>(attributeDbEntry.getAttributeValues().size());
            attribute.setAttributeValues(list);

            for (AttributeValuesDbType value : attributeDbEntry.getAttributeValues()) {
                list.add(getAttributeValue(value));
            }

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

    class CompanyAttributeData {
        int companyId;
        List<Attribute> attributeList;
    }
}
