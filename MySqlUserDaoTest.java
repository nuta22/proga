package com.avseredyuk.carrental.dao.impl;

import com.avseredyuk.carrental.dao.impl.factory.MySqlDaoFactory;
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
 * Created by lenfer on 1/27/17.
 */
public class MySqlUserDaoTest {
    public static final String USER_NAME_2 = "Vasya";
    public static final String USER_SURNAME_2 = "Pupkin";
    public static final String USER_EMAIL_2 = "qwertywww@mailattoilet.com";
    public static final String USER_LOGIN_2 = "uberkiller1488";
    public static final String USER_PASSWORD_2 = "qwerty";
    public static final User.Role USER_ROLE_2 = User.Role.ADMINISTRATOR;
    private User randomUser;
    private List<User> randomUsers = new ArrayList<>();
    private MySqlUserDao dao = MySqlDaoFactory.getInstance().getUserDao();

    @Before
    public void setUp() throws Exception {
        resetDB();
        randomUser = RandomUtil.getUser();
        for(int i = 0; i < TOTAL_COUNT_ADDED; i++) {
            randomUsers.add(RandomUtil.getUser());
        }
    }

    @Test(expected = NullPointerException.class)
    public void persistNull() throws Exception{
        assertFalse(dao.persist(null));
    }

    @Test(expected = NullPointerException.class)
    public void persistNullElements() throws Exception{
        randomUser.setEmail(null);
        randomUser.setLogin(null);
        randomUser.setPassword(null);
        randomUser.setName(null);
        randomUser.setSurname(null);
        randomUser.setRegistered(null);
        randomUser.setRole(null);
        assertFalse(dao.persist(randomUser));
    }

    @Test
    public void persistValid() throws Exception {
        assertTrue(dao.persist(randomUser));
        assertNotNull(randomUser.getId());
    }

    @Test
    public void persistNonUniqueLogin() throws Exception {
        assertTrue(dao.persist(randomUser));
        randomUser.setEmail(USER_EMAIL_2);
        assertFalse(dao.persist(randomUser));
    }

    @Test
    public void persistNonUniqueEmail() throws Exception {
        assertTrue(dao.persist(randomUser));
        randomUser.setLogin(USER_LOGIN_2);
        assertFalse(dao.persist(randomUser));
    }

    @Test
    public void persistNonUniqueBoth() throws Exception {
        assertTrue(dao.persist(randomUser));
        assertFalse(dao.persist(randomUser));
    }

    @Test
    public void readValidId() throws Exception {
        assertTrue(dao.persist(randomUser));
        User user = dao.read(randomUser.getId());
        assertNull(dao.read(NON_EXISTENT_ID));
        assertEquals(randomUser.getName(), user.getName());
        assertEquals(randomUser.getSurname(), user.getSurname());
        assertEquals(randomUser.getEmail(), user.getEmail());
        assertEquals(randomUser.getLogin(), user.getLogin());
        assertEquals(randomUser.getPassword(), user.getPassword());
        assertEquals(randomUser.getRole(), user.getRole());
        assertTrue(equalsDates(randomUser.getRegistered(), user.getRegistered()));
    }

    @Test
    public void readInvalidId() throws Exception {
        assertNull(dao.read(NON_EXISTENT_ID));
    }

    @Test
    public void updateValidId() throws Exception {
        Date savedOriginalDate = randomUser.getRegistered();
        dao.persist(randomUser);
        randomUser.setName(USER_NAME_2);
        randomUser.setSurname(USER_SURNAME_2);
        randomUser.setEmail(USER_EMAIL_2);
        randomUser.setLogin(USER_LOGIN_2);
        randomUser.setPassword(USER_PASSWORD_2);
        randomUser.setRegistered(new Date());
        randomUser.setRole(USER_ROLE_2);
        assertTrue(dao.update(randomUser));
        User user = dao.read(randomUser.getId());
        assertEquals(randomUser.getName(), user.getName());
        assertEquals(randomUser.getSurname(), user.getSurname());
        assertEquals(randomUser.getEmail(), user.getEmail());
        assertEquals(randomUser.getLogin(), user.getLogin());
        assertEquals(randomUser.getPassword(), user.getPassword());
        assertTrue(equalsDates(savedOriginalDate, user.getRegistered()));
    }

    @Test
    public void updateInvalidId() throws Exception {
        randomUser.setId(NON_EXISTENT_ID);
        assertFalse(dao.update(randomUser));
    }

    @Test
    public void deleteValidId() throws Exception {
        assertTrue(dao.persist(randomUser));
        assertTrue(dao.delete(randomUser));
        assertNull(dao.read(randomUser.getId()));
    }

    @Test
    public void deleteInvalidId() throws Exception {
        randomUser.setId(NON_EXISTENT_ID);
        assertFalse(dao.delete(randomUser));
    }

    @Test
    public void findAll() throws Exception {
        randomUsers.forEach(dao::persist);
        List<User> foundUsers = dao.findAll();
        assertEquals(TOTAL_COUNT_ADDED, foundUsers.size());
    }

    @Test
    public void findAllValidRange() throws Exception {
        for (int i = FIND_RANGE_START; i < FIND_RANGE_SIZE; i++) {
            dao.persist(randomUsers.get(i));
        }
        List<User> foundUsers = dao.findAll(FIND_RANGE_START, FIND_RANGE_SIZE);
        assertEquals(FIND_RANGE_SIZE, foundUsers.size());
    }

    @Test
    public void findAllNegativeStartRange() throws Exception {
        randomUsers.forEach(dao::persist);
        List<User> users = dao.findAll(NEGATIVE_RANGE, POSITIVE_RANGE);
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    public void findAllNegativeSizeRange() throws Exception {
        randomUsers.forEach(dao::persist);
        List<User> users = dao.findAll(ZERO_RANGE, NEGATIVE_RANGE);
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    public void findAllBothNegativeRange() throws Exception {
        randomUsers.forEach(dao::persist);
        List<User> users = dao.findAll(NEGATIVE_RANGE, NEGATIVE_RANGE);
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    public void findAllZeroSizeRange() throws Exception {
        randomUsers.forEach(dao::persist);
        List<User> users = dao.findAll(POSITIVE_RANGE, ZERO_RANGE);
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    public void findAllGreaterThenExistsRange() throws Exception {
        randomUsers.forEach(dao::persist);
        List<User> users = dao.findAll(TOO_BIG_RANGE, TOO_BIG_RANGE);
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    public void getCount() throws Exception {
        for (int i = 0; i < TOTAL_COUNT_ADDED; i++) {
            assertTrue(dao.persist(randomUsers.get(i)));
        }
        assertEquals(TOTAL_COUNT_ADDED, dao.getCount());
    }

    @Test
    public void getByLoginValid() throws Exception {
        assertTrue(dao.persist(randomUser));
        User user = dao.getByLogin(randomUser.getLogin());
        assertEquals(randomUser.getName(), user.getName());
        assertEquals(randomUser.getSurname(), user.getSurname());
        assertEquals(randomUser.getEmail(), user.getEmail());
        assertEquals(randomUser.getLogin(), user.getLogin());
        assertEquals(randomUser.getPassword(), user.getPassword());
        assertEquals(randomUser.getRole(), user.getRole());
        assertTrue(equalsDates(randomUser.getRegistered(), user.getRegistered()));
    }

    @Test
    public void getByLoginInvalid() throws Exception {
        assertTrue(dao.persist(randomUser));
        assertNull(dao.getByLogin(USER_LOGIN_2));
    }


}