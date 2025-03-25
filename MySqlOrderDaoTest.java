package com.avseredyuk.carrental.dao.impl;

import com.avseredyuk.carrental.dao.impl.factory.MySqlDaoFactory;
import com.avseredyuk.carrental.domain.Order;
import com.avseredyuk.carrental.domain.User;
import com.avseredyuk.carrental.util.RandomUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static com.avseredyuk.carrental.util.DateUtil.*;
import static com.avseredyuk.carrental.util.Utils.*;

/**
 * Created by lenfer on 1/28/17.
 */
public class MySqlOrderDaoTest {
    private Order randomOrder;
    private List<Order> randomOrders = new ArrayList<>();
    private MySqlOrderDao dao = MySqlDaoFactory.getInstance().getOrderDao();

    @Before
    public void setUp() throws Exception {
        resetDB();
        randomOrder = RandomUtil.getOrder();
        for(int i = 0; i < TOTAL_COUNT_ADDED; i++) {
            randomOrders.add(RandomUtil.getOrder());
        }
    }

    @Test(expected = NullPointerException.class)
    public void persistNull() throws Exception{
        assertFalse(dao.persist(null));
    }

    @Test(expected = NullPointerException.class)
    public void persistNullElements() throws Exception{
        randomOrder.setSum(null);
        randomOrder.setDamage(null);
        randomOrder.setUser(null);
        randomOrder.setAutomobile(null);
        randomOrder.setCreated(null);
        randomOrder.setDateFrom(null);
        randomOrder.setDateTo(null);
        randomOrder.setPlaceFrom(null);
        randomOrder.setPlaceTo(null);
        randomOrder.setStatus(null);
        assertFalse(dao.persist(randomOrder));
    }

    @Test
    public void persistNullDamage() throws Exception {
        randomOrder.setDamage(null);
        assertTrue(dao.persist(randomOrder));
    }

    @Test
    public void persistValid() throws Exception {
        assertTrue(dao.persist(randomOrder));
        assertNotNull(randomOrder.getId());
    }

    @Test(expected = NullPointerException.class)
    public void persistWithNonPersistedPlaces() throws Exception {
        randomOrder.setPlaceTo(RandomUtil.getPlace());
        randomOrder.setPlaceFrom(RandomUtil.getPlace());
        dao.persist(randomOrder);
    }

    @Test(expected = NullPointerException.class)
    public void persistWithNonPersistedAutomobile() throws Exception {
        randomOrder.setAutomobile(RandomUtil.getAutomobile());
        dao.persist(randomOrder);
    }

    @Test(expected = NullPointerException.class)
    public void persistWithNonPersistedUser() throws Exception {
        randomOrder.setUser(RandomUtil.getUser());
        dao.persist(randomOrder);
    }

    @Test
    public void readValidId() throws Exception {
        assertTrue(dao.persist(randomOrder));
        Order order2 = dao.read(randomOrder.getId());
        assertEquals(randomOrder.getAutomobile(), order2.getAutomobile());
        assertEquals(randomOrder.getPlaceFrom(), order2.getPlaceFrom());
        assertEquals(randomOrder.getPlaceTo(), order2.getPlaceTo());
        assertEquals(randomOrder.getDamage(), order2.getDamage());
        assertFalse(equalsDates(randomOrder.getCreated(), order2.getCreated()));
        assertTrue(equalsDates(randomOrder.getDateFrom(), order2.getDateFrom()));
        assertTrue(equalsDates(randomOrder.getDateTo(), order2.getDateTo()));
        assertEquals(randomOrder.getStatus(), order2.getStatus());
        assertEquals(randomOrder.getSum(), order2.getSum());
    }

    @Test
    public void readInvalidId() throws Exception {
        assertNull(dao.read(NON_EXISTENT_ID));
    }

    @Test
    public void updateValidId() throws Exception {
        Date savedDate = randomOrder.getCreated();
        dao.persist(randomOrder);
        int orderID = randomOrder.getId();
        randomOrder = RandomUtil.getOrder();
        randomOrder.setId(orderID);
        assertTrue(dao.update(randomOrder));
        Order order2 = dao.read(randomOrder.getId());
        assertEquals(randomOrder.getAutomobile(), order2.getAutomobile());
        assertEquals(randomOrder.getPlaceFrom(), order2.getPlaceFrom());
        assertEquals(randomOrder.getPlaceTo(), order2.getPlaceTo());
        assertEquals(randomOrder.getDamage(), order2.getDamage());
        assertFalse(equalsDates(randomOrder.getCreated(), order2.getCreated()));
        assertFalse(equalsDates(savedDate, order2.getCreated()));
        assertTrue(equalsDates(randomOrder.getDateFrom(), order2.getDateFrom()));
        assertTrue(equalsDates(randomOrder.getDateTo(), order2.getDateTo()));
        assertEquals(randomOrder.getStatus(), order2.getStatus());
        assertEquals(randomOrder.getSum(), order2.getSum());
    }

    @Test
    public void updateInvalidId() throws Exception {
        randomOrder.setId(NON_EXISTENT_ID);
        assertFalse(dao.update(randomOrder));
    }

    @Test
    public void deleteValidId() throws Exception {
        assertTrue(dao.persist(randomOrder));
        assertTrue(dao.delete(randomOrder));
        assertNull(dao.read(randomOrder.getId()));
    }

    @Test
    public void deleteInvalidId() throws Exception {
        randomOrder.setId(NON_EXISTENT_ID);
        assertFalse(dao.delete(randomOrder));
    }

    @Test
    public void findAll() throws Exception {
        randomOrders.forEach(dao::persist);
        List<Order> foundOrders = dao.findAll();
        assertEquals(TOTAL_COUNT_ADDED, foundOrders.size());
    }

    @Test
    public void findAllValidRange() throws Exception {
        for (int i = FIND_RANGE_START; i < FIND_RANGE_SIZE; i++) {
            dao.persist(randomOrders.get(i));
        }
        List<Order> foundOrders = dao.findAll(FIND_RANGE_START, FIND_RANGE_SIZE);
        assertEquals(FIND_RANGE_SIZE, foundOrders.size());
    }

    @Test
    public void findAllNegativeStartRange() throws Exception {
        randomOrders.forEach(dao::persist);
        List<Order> damages = dao.findAll(NEGATIVE_RANGE, POSITIVE_RANGE);
        assertNotNull(damages);
        assertEquals(0, damages.size());
    }

    @Test
    public void findAllNegativeSizeRange() throws Exception {
        randomOrders.forEach(dao::persist);
        List<Order> damages = dao.findAll(ZERO_RANGE, NEGATIVE_RANGE);
        assertNotNull(damages);
        assertEquals(0, damages.size());
    }

    @Test
    public void findAllBothNegativeRange() throws Exception {
        randomOrders.forEach(dao::persist);
        List<Order> damages = dao.findAll(NEGATIVE_RANGE, NEGATIVE_RANGE);
        assertNotNull(damages);
        assertEquals(0, damages.size());
    }

    @Test
    public void findAllZeroSizeRange() throws Exception {
        randomOrders.forEach(dao::persist);
        List<Order> damages = dao.findAll(POSITIVE_RANGE, ZERO_RANGE);
        assertNotNull(damages);
        assertEquals(0, damages.size());
    }

    @Test
    public void findAllGreaterThenExistsRange() throws Exception {
        randomOrders.forEach(dao::persist);
        List<Order> damages = dao.findAll(TOO_BIG_RANGE, TOO_BIG_RANGE);
        assertNotNull(damages);
        assertEquals(0, damages.size());
    }

    @Test
    public void getCount() throws Exception {
        for (int i = 0; i < TOTAL_COUNT_ADDED; i++) {
            assertTrue(dao.persist(randomOrder));
        }
        assertEquals(TOTAL_COUNT_ADDED, dao.getCount());
    }

    @Test
    public void countAllByUserSortedByDateRange() throws Exception {
        assertTrue(dao.persist(randomOrder));
        User user = randomOrder.getUser();
        assertTrue(dao.delete(randomOrder));
        randomOrders.forEach(o -> {
            o.setUser(user);
            assertTrue(dao.persist(o));
        });
        assertEquals(TOTAL_COUNT_ADDED, dao.countAllByUser(user));
    }

}