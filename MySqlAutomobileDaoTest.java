package com.avseredyuk.carrental.dao.impl;

import com.avseredyuk.carrental.dao.impl.factory.MySqlDaoFactory;
import com.avseredyuk.carrental.domain.Automobile;
import com.avseredyuk.carrental.util.RandomUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.avseredyuk.carrental.util.Utils.*;
import static org.junit.Assert.*;

/**
 * Created by lenfer on 1/28/17.
 */
public class MySqlAutomobileDaoTest {
    public static final String AUTOMOBILE_MODEL_2 = "Yaris";
    public static final String AUTOMOBILE_MANUFACTURER_2 = "Toyota";
    public static final int AUTOMOBILE_YEAR_2 = 1999;
    public static final int AUTOMOBILE_CARGO_CAPACITY_2 = 3;
    public static final int AUTOMOBILE_PASSENGER_CAPACITY_2 = 4;
    public static final int AUTOMOBILE_DOORS_COUNT_2 = 5;
    public static final int AUTOMOBILE_PRICE_PER_DAY_2 = 123;
    private Automobile randomAutomobile;
    private List<Automobile> randomAutomobiles = new ArrayList<>();
    private MySqlAutomobileDao dao = MySqlDaoFactory.getInstance().getAutomobileDao();

    @Before
    public void setUp() throws Exception {
        resetDB();
        randomAutomobile = RandomUtil.getAutomobile();
        for(int i = 0; i < TOTAL_COUNT_ADDED; i++) {
            randomAutomobiles.add(RandomUtil.getAutomobile());
        }
    }

    @Test(expected = NullPointerException.class)
    public void persistNull() throws Exception{
        assertFalse(dao.persist(null));
    }

    @Test(expected = NullPointerException.class)
    public void persistNullElements() throws Exception{
        randomAutomobile.setDeliveryPlace(null);
        randomAutomobile.setPricePerDay(null);
        randomAutomobile.setDoorsCount(null);
        randomAutomobile.setPassengerCapacity(null);
        randomAutomobile.setCargoCapacity(null);
        randomAutomobile.setModel(null);
        randomAutomobile.setManufacturer(null);
        randomAutomobile.setYearOfProduction(null);
        randomAutomobile.setCategory(null);
        randomAutomobile.setFuel(null);
        randomAutomobile.setTransmission(null);
        assertFalse(dao.persist(randomAutomobile));
    }

    @Test
    public void persistValid() throws Exception {
        assertTrue(dao.persist(randomAutomobile));
        assertNotNull(randomAutomobile.getId());
    }

    @Test(expected = NullPointerException.class)
    public void persistWithNonPersistedPlace() throws Exception {
        randomAutomobile.setDeliveryPlace(RandomUtil.getPlace());
        dao.persist(randomAutomobile);
    }

    @Test
    public void readValidId() throws Exception {
        assertTrue(dao.persist(randomAutomobile));
        Automobile automobile2 = dao.read(randomAutomobile.getId());
        assertEquals(randomAutomobile, automobile2);
    }

    @Test
    public void readInvalidId() throws Exception {
        assertNull(dao.read(NON_EXISTENT_ID));
    }

    @Test
    public void updateValidId() throws Exception {
        dao.persist(randomAutomobile);
        randomAutomobile.setPricePerDay(AUTOMOBILE_PRICE_PER_DAY_2);
        randomAutomobile.setDoorsCount(AUTOMOBILE_DOORS_COUNT_2);
        randomAutomobile.setPassengerCapacity(AUTOMOBILE_PASSENGER_CAPACITY_2);
        randomAutomobile.setCargoCapacity(AUTOMOBILE_CARGO_CAPACITY_2);
        randomAutomobile.setModel(AUTOMOBILE_MODEL_2);
        randomAutomobile.setManufacturer(AUTOMOBILE_MANUFACTURER_2);
        randomAutomobile.setYearOfProduction(AUTOMOBILE_YEAR_2);
        assertTrue(dao.update(randomAutomobile));
        assertEquals(randomAutomobile, dao.read(randomAutomobile.getId()));
    }

    @Test
    public void updateInvalidId() throws Exception {
        randomAutomobile.setId(NON_EXISTENT_ID);
        assertFalse(dao.update(randomAutomobile));
    }

    @Test
    public void deleteValidId() throws Exception {
        assertTrue(dao.persist(randomAutomobile));
        assertTrue(dao.delete(randomAutomobile));
        assertNull(dao.read(randomAutomobile.getId()));
    }

    @Test
    public void deleteInvalidId() throws Exception {
        randomAutomobile.setId(NON_EXISTENT_ID);
        assertFalse(dao.delete(randomAutomobile));
    }

    @Test
    public void findAll() throws Exception {
        randomAutomobiles.forEach(dao::persist);
        List<Automobile> foundAutomobiles = dao.findAll();
        assertEquals(TOTAL_COUNT_ADDED, foundAutomobiles.size());
    }

    @Test
    public void findAllValidRange() throws Exception {
        for (int i = FIND_RANGE_START; i < FIND_RANGE_SIZE; i++) {
            dao.persist(randomAutomobiles.get(i));
        }
        List<Automobile> foundAutomobiles = dao.findAll(FIND_RANGE_START, FIND_RANGE_SIZE);
        assertEquals(FIND_RANGE_SIZE, foundAutomobiles.size());
    }

    @Test
    public void findAllNegativeStartRange() throws Exception {
        randomAutomobiles.forEach(dao::persist);
        List<Automobile> automobiles = dao.findAll(NEGATIVE_RANGE, POSITIVE_RANGE);
        assertNotNull(automobiles);
        assertEquals(0, automobiles.size());
    }

    @Test
    public void findAllNegativeSizeRange() throws Exception {
        randomAutomobiles.forEach(dao::persist);
        List<Automobile> automobiles = dao.findAll(ZERO_RANGE, NEGATIVE_RANGE);
        assertNotNull(automobiles);
        assertEquals(0, automobiles.size());
    }

    @Test
    public void findAllBothNegativeRange() throws Exception {
        randomAutomobiles.forEach(dao::persist);
        List<Automobile> automobiles = dao.findAll(NEGATIVE_RANGE, NEGATIVE_RANGE);
        assertNotNull(automobiles);
        assertEquals(0, automobiles.size());
    }

    @Test
    public void findAllZeroSizeRange() throws Exception {
        randomAutomobiles.forEach(dao::persist);
        List<Automobile> automobiles = dao.findAll(POSITIVE_RANGE, ZERO_RANGE);
        assertNotNull(automobiles);
        assertEquals(0, automobiles.size());
    }

    @Test
    public void findAllGreaterThenExistsRange() throws Exception {
        randomAutomobiles.forEach(dao::persist);
        List<Automobile> automobiles = dao.findAll(TOO_BIG_RANGE, TOO_BIG_RANGE);
        assertNotNull(automobiles);
        assertEquals(0, automobiles.size());
    }

    @Test
    public void getCount() throws Exception {
        for (int i = 0; i < TOTAL_COUNT_ADDED; i++) {
            assertTrue(dao.persist(randomAutomobile));
        }
        assertEquals(TOTAL_COUNT_ADDED, dao.getCount());
    }

}