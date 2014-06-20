package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Customer.CustomerDao;
import com.fbr.Dao.Customer.Entities.CustomerDbType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {
    private static final Logger logger = Logger.getLogger(ResponseService.class);
    @Autowired
    private CustomerDao customerDao;

    public CustomerDbType addCustomerInfo(String mail, String phone, String name) throws Exception {
        try {
            logger.info("adding customer info for : " + mail + " phone : " + phone + " name : " + name);
            if (mail == null) return null;

            CustomerDbType customerDbEntry = customerDao.getCustomerWithMail(mail);
            if (customerDbEntry != null) {
                logger.debug("already existing customer info : " + customerDbEntry.getCustomerId());
                return customerDbEntry;
            } else {
                customerDbEntry = getCustomerDbEntry(mail, phone, name);
                logger.debug("creating new customer : " + customerDbEntry.getCustomerId());
                customerDao.add(customerDbEntry);
                return customerDbEntry;
            }
        } catch (Exception e) {
            logger.error("error creating customer : " + mail + " : " + e.getMessage());
            throw new Exception("error creating customer : " + mail + " : " + e.getMessage());
        }
    }

    private CustomerDbType getCustomerDbEntry(String mail, String phone, String name) {
        CustomerDbType customerDbEntry = new CustomerDbType();
        customerDbEntry.setMail(mail);
        customerDbEntry.setCustomerId(UUID.randomUUID().toString());
        customerDbEntry.setPhone(phone);
        customerDbEntry.setName(name);

        return customerDbEntry;
    }
}
