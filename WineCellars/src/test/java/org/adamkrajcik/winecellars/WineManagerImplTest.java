/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.winecellars;

import common.DBUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author dmwm
 */
public class WineManagerImplTest {

    private DataSource dataSource;
    private WineManagerImpl manager;

    @Before
    public void setUp() throws SQLException {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby:memory:winemanager-test;create=true");
        dataSource = bds;

        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("CREATE TABLE CELLAR (id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
                    + "name VARCHAR(256) NOT NULL, address VARCHAR(256) NOT NULL, wineCapacity INTEGER NOT NULL, "
                    + "CONSTRAINT WINECAPACITY_LESS_THAN_OR_EQUAL_ZERO CHECK (wineCapacity > 0))").executeUpdate();
            conn.prepareStatement("CREATE TABLE WINE (id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
                    + "cellarId BIGINT REFERENCES CELLAR (id), name VARCHAR(256) NOT NULL, "
                    + "country VARCHAR(128) NOT NULL, productionYear INTEGER NOT NULL, "
                    + "quantity INTEGER NOT NULL, type VARCHAR(5) NOT NULL, "
                    + "CONSTRAINT YEAR_LESS_THAN_OR_EQUAL_ZERO CHECK (productionYear > 0), "
                    + "CONSTRAINT QUANTITY_LESS_THEN_OR_EQUAL_ZERO CHECK (quantity > 0), "
                    + "CONSTRAINT INVALID_TYPE CHECK (type IN ('RED', 'WHITE', 'ROSE')))").executeUpdate();
        }

        manager = new WineManagerImpl();
        manager.setDataSource(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("DROP TABLE WINE").executeUpdate();
            conn.prepareStatement("DROP TABLE CELLAR").executeUpdate();
        }
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCreateWine() {
        Wine wine = newWine("Tokaji Azsu", "Hungary", (short) 2005, 20, WineType.WHITE);
        manager.createWine(wine);

        Long wineId = wine.getId();
        assertNotNull(wineId);

        Wine result = manager.findWineById(wineId);

        assertEquals(wine, result);
        assertNotSame(wine, result);
        assertDeepEquals(wine, result);
    }

    @Test()
    public void testCreateWineWitNull() {
        expectedException.expect(IllegalArgumentException.class);
        manager.createWine(null);
    }

    @Test()
    public void testCreateWineWithNullName() {
        Wine wine = newWine(null, "France", (short) 1994, 100, WineType.RED);

        expectedException.expect(IllegalArgumentException.class);
        manager.createWine(wine);
    }

    @Test()
    public void testCreateWineWithNullCountry() {
        Wine wine = newWine("Château Petrus", null, (short) 1994, 100, WineType.RED);

        expectedException.expect(IllegalArgumentException.class);
        manager.createWine(wine);
    }

    @Test()
    public void testCreateWineWithNegativeYear() {
        Wine wine = newWine("Château Petrus", "France", (short) -1, 100, WineType.RED);

        expectedException.expect(IllegalArgumentException.class);
        manager.createWine(wine);
    }

    @Test()
    public void testCreateWineWithNegativeQuantity() {
        Wine wine = newWine("Château Petrus", "France", (short) 1994, -1, WineType.RED);

        expectedException.expect(IllegalArgumentException.class);
        manager.createWine(wine);
    }

    @Test
    public void testCreateWineWithNullType() {
        Wine wine = newWine("Château Petrus", "France", (short) 1994, -1, null);

        expectedException.expect(IllegalArgumentException.class);
        manager.createWine(wine);
    }

    @Test()
    public void testCreateWineWithSetId() {
        Wine wine = newWine("Château Petrus", "France", (short) 1994, 100, WineType.RED);
        wine.setId(1l);

        expectedException.expect(IllegalArgumentException.class);
        manager.createWine(wine);
    }

    @Test
    public void testUpdateWineName() {
        Wine wine = newWine("Château Petrus", "France", (short) 1866, 100, WineType.RED);

        manager.createWine(wine);
        Long wineId = wine.getId();

        wine.setName("Vega Sicilia Unico");
        manager.updateWine(wine);
        wine = manager.findWineById(wineId);

        assertEquals("Vega Sicilia Unico", wine.getName());
        assertEquals("France", wine.getCountry());
        assertEquals((short) 1866, wine.getYear());
        assertEquals(100, wine.getQuantity());
        assertEquals(WineType.RED, wine.getType());
    }

    @Test
    public void testUpdateWineCountry() {
        Wine wine = newWine("Château Petrus", "France", (short) 1866, 100, WineType.RED);

        manager.createWine(wine);
        Long wineId = wine.getId();

        wine.setCountry("Spain");
        manager.updateWine(wine);
        wine = manager.findWineById(wineId);

        assertEquals("Château Petrus", wine.getName());
        assertEquals("Spain", wine.getCountry());
        assertEquals((short) 1866, wine.getYear());
        assertEquals(100, wine.getQuantity());
        assertEquals(WineType.RED, wine.getType());
    }

    @Test
    public void testUpdateWineYear() {
        Wine wine = newWine("Château Petrus", "France", (short) 1866, 100, WineType.RED);

        manager.createWine(wine);
        Long wineId = wine.getId();

        wine.setYear((short) 1922);
        manager.updateWine(wine);
        wine = manager.findWineById(wineId);

        assertEquals("Château Petrus", wine.getName());
        assertEquals("France", wine.getCountry());
        assertEquals((short) 1922, wine.getYear());
        assertEquals(100, wine.getQuantity());
        assertEquals(WineType.RED, wine.getType());

    }

    @Test
    public void testUpdateWineQuantity() {
        Wine wine = newWine("Château Petrus", "France", (short) 1866, 100, WineType.RED);

        manager.createWine(wine);
        Long wineId = wine.getId();

        wine.setQuantity(20);
        manager.updateWine(wine);
        wine = manager.findWineById(wineId);

        assertEquals("Château Petrus", wine.getName());
        assertEquals("France", wine.getCountry());
        assertEquals((short) 1866, wine.getYear());
        assertEquals(20, wine.getQuantity());
        assertEquals(WineType.RED, wine.getType());
    }

    @Test
    public void testUpdateWineType() {
        Wine wine = newWine("Château Petrus", "France", (short) 1866, 100, WineType.RED);

        manager.createWine(wine);
        Long wineId = wine.getId();

        wine.setType(WineType.WHITE);
        manager.updateWine(wine);
        wine = manager.findWineById(wineId);

        assertEquals("Château Petrus", wine.getName());
        assertEquals("France", wine.getCountry());
        assertEquals((short) 1866, wine.getYear());
        assertEquals(100, wine.getQuantity());
        assertEquals(WineType.WHITE, wine.getType());
    }

    @Test()
    public void testUpdateWineWithNullWine() {
        expectedException.expect(IllegalArgumentException.class);
        manager.updateWine(null);
    }

    @Test()
    public void testUpdateWineWithNullName() {
        Wine wine = newWine("Quinta do Vale Dona Maria", "Portugal", (short) 2008, 5, WineType.RED);
        manager.createWine(wine);
        Long wineId = wine.getId();

        wine = manager.findWineById(wineId);
        wine.setName(null);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateWine(wine);
    }

    @Test()
    public void testUpdateWineWithNullCountry() {
        Wine wine = newWine("Casa de Casal De Loivos", "Portugal", (short) 2008, 10, WineType.RED);
        manager.createWine(wine);
        Long wineId = wine.getId();

        wine = manager.findWineById(wineId);
        wine.setCountry(null);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateWine(wine);
    }

    @Test()
    public void testUpdateWineWithNegativeQuantity() {
        Wine wine = newWine("Il Barroccio", "Portugal", (short) 2013, 50, WineType.WHITE);
        manager.createWine(wine);
        Long wineId = wine.getId();

        wine = manager.findWineById(wineId);
        wine.setQuantity(-1);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateWine(wine);
    }

    @Test()
    public void testUpdateWineWithNegatveYear() {
        Wine wine = newWine("Pinot Grigio Rose Montevento", "Italy", (short) 2013, 40, WineType.WHITE);
        manager.createWine(wine);
        Long wineId = wine.getId();

        wine = manager.findWineById(wineId);
        wine.setYear((short) -1);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateWine(wine);
    }

    @Test()
    public void testUpdateWineWithNullType() {
        Wine wine = newWine("Rosé Růžený", "Czech Republic", (short) 2013, 10, WineType.ROSE);
        manager.createWine(wine);
        Long wineId = wine.getId();

        wine = manager.findWineById(wineId);
        wine.setType(null);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateWine(wine);
    }

    @Test
    public void testDeleteWine() {
        Wine wine1 = newWine("DPinot Nero La Tunella", "Italy", (short) 2012, 20, WineType.RED);
        Wine wine2 = newWine("Torres Mas La Plana", "Spain", (short) 1968, 20, WineType.WHITE);

        manager.createWine(wine1);
        manager.createWine(wine2);

        assertNotNull(manager.findWineById(wine1.getId()));
        assertNotNull(manager.findWineById(wine2.getId()));

        manager.deleteWine(wine1);

        assertNull(manager.findWineById(wine1.getId()));
        assertNotNull(manager.findWineById(wine2.getId()));

        assertDeepEquals(wine2, manager.findWineById(wine2.getId()));
    }

    @Test
    public void testDeleteWithNullWine() {
        expectedException.expect(IllegalArgumentException.class);
        manager.deleteWine(null);
    }

    @Test
    public void testDeleteWithNullId() {
        Wine wine = newWine("Soave Terre di Brognoligo Cecilia Beretta", "Italy", (short) 2013, 20, WineType.WHITE);
        expectedException.expect(IllegalArgumentException.class);
        manager.deleteWine(wine);
    }

    @Test
    public void testDeleteWithBadId() {
        Wine wine = newWine("Soave Terre di Brognoligo Cecilia Beretta", "Italy", (short) 2013, 20, WineType.WHITE);
        wine.setId(0l);

        expectedException.expect(IllegalArgumentException.class);
        manager.deleteWine(wine);
    }

    @Test
    public void testFindWineById() {
        Wine wine = newWine("Dominio de Pingus", "Spain", (short) 1998, 20, WineType.RED);

        manager.createWine(wine);
        Long wineId = wine.getId();

        Wine result = manager.findWineById(wineId);
        assertEquals(wine, result);
        assertDeepEquals(wine, result);
    }

    @Test()
    public void testFindWineByIdWithNull() {
        expectedException.expect(IllegalArgumentException.class);
        manager.findWineById(null);
    }

    @Test
    public void testFindAllWines() {
        assertTrue(manager.findAllWines().isEmpty());

        Wine wine1 = newWine("Soave Terre di Brognoligo Cecilia Beretta", "Italy", (short) 2013, 20, WineType.WHITE);
        Wine wine2 = newWine("Dominio de Pingus", "Spain", (short) 1998, 20, WineType.RED);
        manager.createWine(wine1);
        manager.createWine(wine2);

        List<Wine> expected = Arrays.asList(wine1, wine2);
        List<Wine> actual = manager.findAllWines();
        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    static Wine newWine(String name, String country, short year, int quantity, WineType type) {
        Wine wine = new Wine();
        wine.setName(name);
        wine.setCountry(country);
        wine.setQuantity(quantity);
        wine.setYear(year);
        wine.setType(type);

        return wine;
    }

    private void assertDeepEquals(Wine expected, Wine actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getYear(), actual.getYear());
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getType(), actual.getType());

    }

    private void assertDeepEquals(List<Wine> expectedList, List<Wine> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Wine expected = expectedList.get(i);
            Wine actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private static Comparator<Wine> idComparator = new Comparator<Wine>() {

        @Override
        public int compare(Wine o1, Wine o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
}
