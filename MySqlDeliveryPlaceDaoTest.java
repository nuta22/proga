package com.avseredyuk.carrental.dao.impl;

import com.avseredyuk.carrental.dao.impl.factory.MySqlDaoFactory;
import com.avseredyuk.carrental.domain.DeliveryPlace;
import com.avseredyuk.carrental.util.RandomUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static com.avseredyuk.carrental.util.Utils.*;

/**
 * Created by lenfer on 1/25/17.
 */
public class MySqlDeliveryPlaceDaoTest {
    public static final DeliveryPlace.DeliveryPlaceType PLACE_TYPE_2 = DeliveryPlace.DeliveryPlaceType.OFFICE;
    public static final String PLACE_NAME_2 = "Place Name";
    public static final String PLACE_ADDRESS_2 = "Place Address";
    private DeliveryPlace randomPlace;
    private List<DeliveryPlace> randomPlaces = new ArrayList<>();
    private MySqlDeliveryPlaceDao dao = MySqlDaoFactory.getInstance().getDeliveryPlaceDao();


    @Before
    public void setUp() throws Exception {
        resetDB();
        randomPlace = RandomUtil.getPlace();
        for(int i = 0; i < TOTAL_COUNT_ADDED; i++) {
            randomPlaces.add(RandomUtil.getPlace());
        }
    }

    @Test(expected = NullPointerException.class)
    public void persistNull() throws Exception{
        assertFalse(dao.persist(null));
    }

    @Test(expected = NullPointerException.class)
    public void persistNullElements() throws Exception{
        randomPlace.setType(null);
        randomPlace.setName(null);
        randomPlace.setAddress(null);
        assertFalse(dao.persist(randomPlace));
    }

    @Test
    public void persistValid() throws Exception {
        assertTrue(dao.persist(randomPlace));
        assertNotNull(randomPlace.getId());
    }

    @Test
    public void readValidId() throws Exception {
        assertTrue(dao.persist(randomPlace));
        DeliveryPlace place2 = dao.read(randomPlace.getId());
        assertEquals(randomPlace, place2);
    }

    @Test
    public void readInvalidId() throws Exception {
        assertNull(dao.read(NON_EXISTENT_ID));
    }

    @Test
    public void updateValidId() throws Exception {
        dao.persist(randomPlace);
        randomPlace.setName(PLACE_NAME_2);
        randomPlace.setAddress(PLACE_ADDRESS_2);
        randomPlace.setType(PLACE_TYPE_2);
        assertTrue(dao.update(randomPlace));
        assertEquals(randomPlace, dao.read(randomPlace.getId()));
    }

    @Test
    public void updateInvalidId() throws Exception {
        randomPlace.setId(NON_EXISTENT_ID);
        assertFalse(dao.update(randomPlace));
    }

    @Test
    public void deleteValidId() throws Exception {
        assertTrue(dao.persist(randomPlace));
        assertTrue(dao.delete(randomPlace));
        assertNull(dao.read(randomPlace.getId()));
    }

    @Test
    public void deleteInvalidId() throws Exception {
        randomPlace.setId(NON_EXISTENT_ID);
        assertFalse(dao.delete(randomPlace));
    }

    @Test
    public void findAll() throws Exception {
        randomPlaces.forEach(dao::persist);
        List<DeliveryPlace> foundPlaces = dao.findAll();
        assertEquals(TOTAL_COUNT_ADDED, foundPlaces.size());
    }

    @Test
    public void findAllValidRange() throws Exception {
        for (int i = FIND_RANGE_START; i < FIND_RANGE_SIZE; i++) {
            dao.persist(randomPlaces.get(i));
        }
        List<DeliveryPlace> foundPlaces = dao.findAll(FIND_RANGE_START, FIND_RANGE_SIZE);
        assertEquals(FIND_RANGE_SIZE, foundPlaces.size());
    }

    @Test
    public void findAllNegativeStartRange() throws Exception {
        randomPlaces.forEach(dao::persist);
        List<DeliveryPlace> places = dao.findAll(NEGATIVE_RANGE, POSITIVE_RANGE);
        assertNotNull(places);
        assertEquals(0, places.size());
    }

    @Test
    public void findAllNegativeSizeRange() throws Exception {
        randomPlaces.forEach(dao::persist);
        List<DeliveryPlace> places = dao.findAll(ZERO_RANGE, NEGATIVE_RANGE);
        assertNotNull(places);
        assertEquals(0, places.size());
    }

    @Test
    public void findAllBothNegativeRange() throws Exception {
        randomPlaces.forEach(dao::persist);
        List<DeliveryPlace> places = dao.findAll(NEGATIVE_RANGE, NEGATIVE_RANGE);
        assertNotNull(places);
        assertEquals(0, places.size());
    }

    @Test
    public void findAllZeroSizeRange() throws Exception {
        randomPlaces.forEach(dao::persist);
        List<DeliveryPlace> places = dao.findAll(POSITIVE_RANGE, ZERO_RANGE);
        assertNotNull(places);
        assertEquals(0, places.size());
    }

    @Test
    public void findAllGreaterThenExistsRange() throws Exception {
        randomPlaces.forEach(dao::persist);
        List<DeliveryPlace> places = dao.findAll(TOO_BIG_RANGE, TOO_BIG_RANGE);
        assertNotNull(places);
        assertEquals(0, places.size());
    }

    @Test
    public void getCount() throws Exception {
        for (int i = 0; i < TOTAL_COUNT_ADDED; i++) {
            assertTrue(dao.persist(randomPlace));
        }
        assertEquals(TOTAL_COUNT_ADDED, dao.getCount());
    }

}