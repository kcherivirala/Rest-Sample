package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Offers_Info.Entities.OfferInfoDbType;
import com.fbr.Dao.Offers_Info.Entities.OfferInfoPrimaryKey;
import com.fbr.Dao.Offers_Info.OfferInfoDao;
import com.fbr.domain.Offers_Info.OffersInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OffersService {
    private static final Logger logger = Logger.getLogger(OffersService.class);
    @Autowired
    private OfferInfoDao offersInfoDao;

    @Transactional
    public OffersInfo addOffersInfo(int companyId, OffersInfo offersInfo) throws Exception {
        try {
            int offerId = offersInfoDao.getMaxIdValue(companyId) + 1;
            OfferInfoDbType offerInfoDbType = Conversions.getOfferInfoDbEntry(companyId, offersInfo.getBranchId(), offerId, offersInfo);
            offersInfoDao.add(offerInfoDbType);

            offersInfo.setOfferId(offerId);
            return offersInfo;
        } catch (Exception e) {
            logger.error("error adding offers for : " + companyId + " and : " + offersInfo.getBranchId() + " : " + e.getMessage());
            throw new Exception("error adding offers for : " + companyId + " and : " + offersInfo.getBranchId() + " : " + e.getMessage());
        }
    }

    @Transactional
    public OffersInfo updateOffersInfo(int companyId, int offerId, OffersInfo offersInfo) throws Exception {
        try {
            OfferInfoPrimaryKey id = Conversions.getOfferInfoPrimaryKey(companyId, offerId);
            OfferInfoDbType dbEntry = offersInfoDao.find(id);

            updateOfferInfoDbEntry(dbEntry, offersInfo);

            return offersInfo;
        } catch (Exception e) {
            logger.error("error updating offers for : " + companyId + " and : " + offersInfo.getBranchId() + " : " + e.getMessage());
            throw new Exception("error updating offers for : " + companyId + " and : " + offersInfo.getBranchId() + " : " + e.getMessage());
        }

    }


    public List<OffersInfo> getOffersInfo() throws Exception {
        try {
            List<OfferInfoDbType> listDbEntries = offersInfoDao.findAll();
            List<OffersInfo> out = new ArrayList<OffersInfo>(listDbEntries.size());

            for (OfferInfoDbType dbEntry : listDbEntries) {
                out.add(Conversions.getOffersInfo(dbEntry));
            }
            return out;
        } catch (Exception e) {
            logger.error("error getting all offers for : " + e.getMessage());
            throw new Exception("error getting all offers for : " + e.getMessage());
        }
    }

    public List<OffersInfo> getOffersInfo(int companyId) throws Exception {
        try {
            List<OfferInfoDbType> listDbEntries = offersInfoDao.getOffersAndInfo(companyId);
            List<OffersInfo> out = new ArrayList<OffersInfo>(listDbEntries.size());

            for (OfferInfoDbType dbEntry : listDbEntries) {
                out.add(Conversions.getOffersInfo(dbEntry));
            }
            return out;
        } catch (Exception e) {
            logger.error("error getting offers for : " + companyId + " and : " + " : " + e.getMessage());
            throw new Exception("error getting offers for : " + companyId + " and : " + " : " + e.getMessage());
        }
    }

    public List<OffersInfo> getOffersInfo(int companyId, int branchId) throws Exception {
        try {
            List<OfferInfoDbType> listDbEntries = offersInfoDao.getOffersAndInfo(companyId, branchId);
            List<OffersInfo> out = new ArrayList<OffersInfo>(listDbEntries.size());

            for (OfferInfoDbType dbEntry : listDbEntries) {
                out.add(Conversions.getOffersInfo(dbEntry));
            }
            return out;
        } catch (Exception e) {
            logger.error("error getting offers for : " + companyId + " and : " + " : " + e.getMessage());
            throw new Exception("error getting offers for : " + companyId + " and : " + " : " + e.getMessage());
        }
    }

    public OffersInfo getOfferOrInfo(int companyId, int offerId) throws Exception {
        try {
            OfferInfoDbType dbEntry = offersInfoDao.find(Conversions.getOfferInfoPrimaryKey(companyId, offerId));
            OffersInfo out = Conversions.getOffersInfo(dbEntry);
            return out;
        } catch (Exception e) {
            logger.error("error getting offer for : " + companyId + " and : offer id" + offerId + " : " + e.getMessage());
            throw new Exception("error getting offers for : " + companyId + " and : offer id" + offerId + " : " + e.getMessage());
        }
    }

    /*    private functions    */

    private void updateOfferInfoDbEntry(OfferInfoDbType dbEntry, OffersInfo offersInfo) {
        boolean updated = false;
        if (!dbEntry.getType().equals(offersInfo.getType())) {
            dbEntry.setType(offersInfo.getType());
            updated = true;
        }
        if (!dbEntry.getDetails().equals(offersInfo.getDetails())) {
            dbEntry.setDetails(offersInfo.getDetails());
            updated = true;
        }
        if (!dbEntry.getStartTS().equals(offersInfo.getStart())) {
            dbEntry.setStartTS(offersInfo.getStart());
            updated = true;
        }
        if (!dbEntry.getEndTS().equals(offersInfo.getEnd())) {
            dbEntry.setEndTS(offersInfo.getEnd());
            updated = true;
        }
        if (dbEntry.getRecurrenceDay() != offersInfo.getRecurrenceDay()) {
            dbEntry.setRecurrenceDay(offersInfo.getRecurrenceDay());
            updated = true;
        }
        if (dbEntry.isRecurrence() != offersInfo.isRecurrence()) {
            dbEntry.setRecurrence(offersInfo.isRecurrence());
            updated = true;
        }
        if (dbEntry.isHourlyPush() != offersInfo.isHourlyPush()) {
            dbEntry.setHourlyPush(offersInfo.isHourlyPush());
            updated = true;
        }
        if (dbEntry.isClientAppVisibility() != offersInfo.isClientAppVisibility()) {
            dbEntry.setClientAppVisibility(offersInfo.isClientAppVisibility());
            updated = true;
        }


        if (updated == true) {
            offersInfoDao.update(dbEntry);
        }
    }

    private static class Conversions {
        public static OfferInfoPrimaryKey getOfferInfoPrimaryKey(int companyId, int offerId) {
            OfferInfoPrimaryKey id = new OfferInfoPrimaryKey();

            id.setCompanyId(companyId);
            id.setOfferId(offerId);
            return id;
        }

        public static OfferInfoDbType getOfferInfoDbEntry(int companyId, int branchId, int offerId, OffersInfo offersInfo) {
            OfferInfoDbType dbEntry = new OfferInfoDbType();

            dbEntry.setId(getOfferInfoPrimaryKey(companyId, offerId));

            dbEntry.setType(offersInfo.getType());
            dbEntry.setDetails(offersInfo.getDetails());
            dbEntry.setBranchId(branchId);

            dbEntry.setStartTS(offersInfo.getStart());
            dbEntry.setEndTS(offersInfo.getEnd());

            dbEntry.setRecurrenceDay(offersInfo.getRecurrenceDay());
            dbEntry.setRecurrence(offersInfo.isRecurrence());
            dbEntry.setHourlyPush(offersInfo.isHourlyPush());
            dbEntry.setClientAppVisibility(offersInfo.isClientAppVisibility());

            return dbEntry;
        }

        public static OffersInfo getOffersInfo(OfferInfoDbType dbEntry) {
            OffersInfo offersInfo = new OffersInfo();

            offersInfo.setBranchId(dbEntry.getBranchId());
            offersInfo.setOfferId(dbEntry.getId().getOfferId());
            offersInfo.setType(dbEntry.getType());
            offersInfo.setDetails(dbEntry.getDetails());

            offersInfo.setStart(dbEntry.getStartTS());
            offersInfo.setEnd(dbEntry.getEndTS());

            offersInfo.setRecurrenceDay(dbEntry.getRecurrenceDay());
            offersInfo.setRecurrence(dbEntry.isRecurrence());
            offersInfo.setHourlyPush(dbEntry.isHourlyPush());
            offersInfo.setClientAppVisibility(dbEntry.isClientAppVisibility());

            return offersInfo;
        }
    }
}
