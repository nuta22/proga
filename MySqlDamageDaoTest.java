package com.avseredyuk.carrental.dao.impl;

import com.avseredyuk.carrental.dao.impl.factory.MySqlDaoFactory;
import com.avseredyuk.carrental.domain.Damage;
import com.avseredyuk.carrental.util.RandomUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static com.avseredyuk.carrental.util.Utils.*;

/**
 * Created by lenfer on 1/28/17.
 */
public class MySqlDamageDaoTest {
    public static final String DAMAGE_DESCRIPTION_2 = "Totally wrecked";
    private Damage randomDamage;
    private List<Damage> randomDamages = new ArrayList<>();
    private MySqlDamageDao dao = MySqlDaoFactory.getInstance().getDamageDao();

    @Before
    public void setUp() throws Exception {
        resetDB();
        randomDamage = RandomUtil.getDamage();
        for(int i = 0; i < TOTAL_COUNT_ADDED; i++) {
            randomDamages.add(RandomUtil.getDamage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void persistNull() throws Exception{
        assertFalse(dao.persist(null));
    }

    @Test(expected = NullPointerException.class)
    public void persistNullElements() throws Exception{
        randomDamage.setDamageSum(null);
        randomDamage.setDescription(null);
        assertFalse(dao.persist(randomDamage));
    }

    @Test
    public void persistValid() throws Exception {
        assertTrue(dao.persist(randomDamage));
        assertNotNull(randomDamage.getId());
    }

    @Test
    public void readValidId() throws Exception {
        assertTrue(dao.persist(randomDamage));
        Damage damage2 = dao.read(randomDamage.getId());
        assertEquals(randomDamage, damage2);
    }

    @Test
    public void readInvalidId() throws Exception {
        assertNull(dao.read(NON_EXISTENT_ID));
    }

    @Test
    public void updateValidId() throws Exception {
        dao.persist(randomDamage);
        randomDamage.setDescription(DAMAGE_DESCRIPTION_2);
        assertTrue(dao.update(randomDamage));
        assertEquals(randomDamage, dao.read(randomDamage.getId()));
    }

    @Test
    public void updateInvalidId() throws Exception {
        randomDamage.setId(NON_EXISTENT_ID);
        assertFalse(dao.update(randomDamage));
    }

    @Test
    public void deleteValidId() throws Exception {
        assertTrue(dao.persist(randomDamage));
        assertTrue(dao.delete(randomDamage));
        assertNull(dao.read(randomDamage.getId()));
    }

    @Test
    public void deleteInvalidId() throws Exception {
        randomDamage.setId(NON_EXISTENT_ID);
        assertFalse(dao.delete(randomDamage));
    }

    @Test
    public void findAll() throws Exception {
        randomDamages.forEach(dao::persist);
        List<Damage> foundDamages = dao.findAll();
        assertEquals(TOTAL_COUNT_ADDED, foundDamages.size());
    }

    @Test
    public void findAllValidRange() throws Exception {
        for (int i = FIND_RANGE_START; i < FIND_RANGE_SIZE; i++) {
            dao.persist(randomDamages.get(i));
        }
        List<Damage> foundDamages = dao.findAll(FIND_RANGE_START, FIND_RANGE_SIZE);
        assertEquals(FIND_RANGE_SIZE, foundDamages.size());
    }

    @Test
    public void findAllNegativeStartRange() throws Exception {
        randomDamages.forEach(dao::persist);
        List<Damage> damages = dao.findAll(NEGATIVE_RANGE, POSITIVE_RANGE);
        assertNotNull(damages);
        assertEquals(0, damages.size());
    }

    @Test
    public void findAllNegativeSizeRange() throws Exception {
        randomDamages.forEach(dao::persist);
        List<Damage> damages = dao.findAll(ZERO_RANGE, NEGATIVE_RANGE);
        assertNotNull(damages);
        assertEquals(0, damages.size());
    }

    @Test
    public void findAllBothNegativeRange() throws Exception {
        randomDamages.forEach(dao::persist);
        List<Damage> damages = dao.findAll(NEGATIVE_RANGE, NEGATIVE_RANGE);
        assertNotNull(damages);
        assertEquals(0, damages.size());
    }

    @Test
    public void findAllZeroSizeRange() throws Exception {
        randomDamages.forEach(dao::persist);
        List<Damage> damages = dao.findAll(POSITIVE_RANGE, ZERO_RANGE);
        assertNotNull(damages);
        assertEquals(0, damages.size());
    }

    @Test
    public void findAllGreaterThenExistsRange() throws Exception {
        randomDamages.forEach(dao::persist);
        List<Damage> damages = dao.findAll(TOO_BIG_RANGE, TOO_BIG_RANGE);
        assertNotNull(damages);
        assertEquals(0, damages.size());
    }

    @Test
    public void getCount() throws Exception {
        for (int i = 0; i < TOTAL_COUNT_ADDED; i++) {
            assertTrue(dao.persist(randomDamage));
        }
        assertEquals(TOTAL_COUNT_ADDED, dao.getCount());
    }

}