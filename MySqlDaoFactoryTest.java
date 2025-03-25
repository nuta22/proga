package com.avseredyuk.carrental.dao.impl;

import com.avseredyuk.carrental.dao.impl.factory.MySqlDaoFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by lenfer on 1/25/17.
 */
public class MySqlDaoFactoryTest {
    @Test
    public void getAutomobileDao() throws Exception {
        assertNotNull(MySqlDaoFactory.getInstance().getAutomobileDao());
    }

    @Test
    public void getUserDao() throws Exception {
        assertNotNull(MySqlDaoFactory.getInstance().getUserDao());
    }

    @Test
    public void getDamageDao() throws Exception {
        assertNotNull(MySqlDaoFactory.getInstance().getDamageDao());
    }

    @Test
    public void getOrderDao() throws Exception {
        assertNotNull(MySqlDaoFactory.getInstance().getOrderDao());
    }

    @Test
    public void getDeliveryPlaceDao() throws Exception {
        assertNotNull(MySqlDaoFactory.getInstance().getDeliveryPlaceDao());
    }

}