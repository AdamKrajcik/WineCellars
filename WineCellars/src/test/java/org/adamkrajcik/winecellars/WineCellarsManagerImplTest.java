/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.winecellars;

import common.DBUtils;
import common.IllegalEntityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
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
public class WineCellarsManagerImplTest {

    private WineCellarsManagerImpl wineCellarsManager;
    private CellarManagerImpl cellarManager;
    private WineManagerImpl wineManager;
    private DataSource dataSource;

    private Wine wine1, wine2, wine3, wine4, wine5, wineNullId, wineNotInDB;
    private Cellar cellar1, cellar2, cellar3, cellarNullId, cellarNotInDB;

    private DataSource prepareDataSource() {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby:memory:winecellarmanager-test;create=true");
        return bds;
    }

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();

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

        wineCellarsManager = new WineCellarsManagerImpl();
        wineCellarsManager.setDataSource(dataSource);

        cellarManager = new CellarManagerImpl();
        cellarManager.setDataSource(dataSource);

        wineManager = new WineManagerImpl();
        wineManager.setDataSource(dataSource);

        prepareData();
    }

    @After
    public void TearDown() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("DROP TABLE WINE").executeUpdate();
            conn.prepareStatement("DROP TABLE CELLAR").executeUpdate();
        }
    }

    private void prepareData() {
        //TO DO
        cellar1 = newCellar("Toriko", "Moravske namestie 1, 912 02 Brno", 350);
        cellar2 = newCellar("Koriko", "Moja adreso 11, 958 54 Pleso", 500);
        cellar3 = newCellar("Cellar3name", "cellar3address", 200);

        wine1 = newWine("Wine 1", "Country 1", (short) 1997, 100, WineType.RED);
        wine2 = newWine("", "", (short) 1994, 20, WineType.RED);
        wine3 = newWine("", "", (short) 1855, 30, WineType.RED);
        wine4 = newWine("", "", (short) 1987, 10, WineType.RED);
        wine5 = newWine("", "", (short) 1975, 20, WineType.RED);

        wineManager.createWine(wine1);
        wineManager.createWine(wine2);
        wineManager.createWine(wine3);
        wineManager.createWine(wine4);
        wineManager.createWine(wine5);

        cellarManager.createCellar(cellar1);
        cellarManager.createCellar(cellar2);
        cellarManager.createCellar(cellar3);

        cellarNullId = newCellar("", "", 20);
        cellarNotInDB = newCellar("", "", 20);
        cellarNotInDB.setId(cellar3.getId() + 100);

        wineNullId = newWine("", "", (short) 1258, 20, WineType.RED);
        wineNotInDB = newWine("", "", (short) 1236, 45, WineType.RED);
        wineNotInDB.setId(wine5.getId() + 100); // 
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testPutWineInCellar() {
        assertNull(wineCellarsManager.findCellarWithWine(wine1));
        assertNull(wineCellarsManager.findCellarWithWine(wine2));
        assertNull(wineCellarsManager.findCellarWithWine(wine3));
        assertNull(wineCellarsManager.findCellarWithWine(wine4));
        assertNull(wineCellarsManager.findCellarWithWine(wine5));

        wineCellarsManager.putWineInCellar(wine1, cellar3);
        wineCellarsManager.putWineInCellar(wine3, cellar3);
        wineCellarsManager.putWineInCellar(wine5, cellar1);

        List<Wine> winesInCellar1 = Arrays.asList(wine5);
        List<Wine> winesInCellar2 = Collections.emptyList();
        List<Wine> winesInCellar3 = Arrays.asList(wine1, wine3);

        assertWineDeepEquals(winesInCellar1, wineCellarsManager.findWinesInCellar(cellar1));
        assertWineDeepEquals(winesInCellar2, wineCellarsManager.findWinesInCellar(cellar2));
        assertWineDeepEquals(winesInCellar3, wineCellarsManager.findWinesInCellar(cellar3));

        assertEquals(cellar3, wineCellarsManager.findCellarWithWine(wine1));

        assertCellarDeepEquals(cellar3, wineCellarsManager.findCellarWithWine(wine1));
        assertNull(wineCellarsManager.findCellarWithWine(wine2));
        assertEquals(cellar3, wineCellarsManager.findCellarWithWine(wine3));
        assertCellarDeepEquals(cellar3, wineCellarsManager.findCellarWithWine(wine3));
        assertNull(wineCellarsManager.findCellarWithWine(wine4));
        assertEquals(cellar1, wineCellarsManager.findCellarWithWine(wine5));
        assertCellarDeepEquals(cellar1, wineCellarsManager.findCellarWithWine(wine5));

    }

    @Test
    public void testPutWineInCellarWithWineAlreadyInSameCellar() {
        assertNull(wineCellarsManager.findCellarWithWine(wine1));
        wineCellarsManager.putWineInCellar(wine1, cellar3);

        expectedException.expect(IllegalEntityException.class);
        wineCellarsManager.putWineInCellar(wine1, cellar3);
    }

    @Test
    public void testPutWineInCellarWithWineAlreadyInOtherCellar() {
        assertNull(wineCellarsManager.findCellarWithWine(wine1));
        wineCellarsManager.putWineInCellar(wine1, cellar3);

        expectedException.expect(IllegalEntityException.class);
        wineCellarsManager.putWineInCellar(wine1, cellar2);
    }

    @Test
    public void testPutWineInNullCellar() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.putWineInCellar(wine1, null);
    }

    @Test
    public void testPutWineInNullIdCellar() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.putWineInCellar(wine1, cellarNullId);
    }

    @Test
    public void testPutWineInNotInDBCellar() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.putWineInCellar(wine1, cellarNotInDB);
    }

    @Test
    public void testPutNullWineInCellar() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.putWineInCellar(null, cellar1);
    }

    @Test
    public void testPutNullIdWineInNonExistingCellar() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.putWineInCellar(wineNullId, cellar1);
    }

    @Test
    public void testPutNotInDBWineInNonExistingCellar() {
        expectedException.expect(IllegalEntityException.class);
        wineCellarsManager.putWineInCellar(wineNotInDB, cellar1);
    }

    @Test
    public void testRemoveWineFromCellar() {
        // TO DO
    }

    @Test
    public void testRemoveNullWineFromCellar() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.removeWineFromCellar(null, cellar1);
    }

    @Test
    public void testRemoveNullIdWineFromCellar() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.removeWineFromCellar(wineNullId, cellar1);
    }

    @Test
    public void testRemoveNotInDBWineFromNonExistingCellar() {
        expectedException.expect(IllegalEntityException.class);
        wineCellarsManager.removeWineFromCellar(wineNotInDB, cellar1);
    }

    @Test
    public void testRemoveWineFromNullCellar() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.removeWineFromCellar(wine1, null);
    }

    @Test
    public void testRemoveWineFromNullIdCellar() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.removeWineFromCellar(wine1, cellarNullId);
    }

    @Test
    public void testRemoveWineFromNotInDBCellar() {
        expectedException.expect(IllegalEntityException.class);
        wineCellarsManager.removeWineFromCellar(wine1, cellarNotInDB);
    }

    @Test
    public void testFindCellarWithWine() {
        assertNull(wineCellarsManager.findCellarWithWine(wine1));
        assertNull(wineCellarsManager.findCellarWithWine(wine2));
        assertNull(wineCellarsManager.findCellarWithWine(wine3));
        assertNull(wineCellarsManager.findCellarWithWine(wine4));
        assertNull(wineCellarsManager.findCellarWithWine(wine5));

        wineCellarsManager.putWineInCellar(wine1, cellar1);
        assertEquals(cellar1, wineCellarsManager.findCellarWithWine(wine1));

        assertCellarDeepEquals(cellar1, wineCellarsManager.findCellarWithWine(wine1));
        assertNull(wineCellarsManager.findCellarWithWine(wine2));
        assertNull(wineCellarsManager.findCellarWithWine(wine3));
        assertNull(wineCellarsManager.findCellarWithWine(wine4));
        assertNull(wineCellarsManager.findCellarWithWine(wine5));
    }

    @Test
    public void testFindCellarWithWineWithNullWine() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.findCellarWithWine(null);
    }

    @Test
    public void testFindCellarWithWineWithNullWineId() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.findCellarWithWine(wineNullId);
    }

    @Test
    public void testFindWinesInCellar() {
        assertTrue(wineCellarsManager.findWinesInCellar(cellar1).isEmpty());
        assertTrue(wineCellarsManager.findWinesInCellar(cellar2).isEmpty());
        assertTrue(wineCellarsManager.findWinesInCellar(cellar3).isEmpty());

        wineCellarsManager.putWineInCellar(wine2, cellar3);
        wineCellarsManager.putWineInCellar(wine3, cellar2);
        wineCellarsManager.putWineInCellar(wine4, cellar3);
        wineCellarsManager.putWineInCellar(wine5, cellar2);

        List<Wine> wineInCellar3 = Arrays.asList(wine2, wine4);
        List<Wine> wineInCellar2 = Arrays.asList(wine3, wine5);

        assertTrue(wineCellarsManager.findWinesInCellar(cellar1).isEmpty());
        assertWineDeepEquals(wineInCellar2, wineCellarsManager.findWinesInCellar(cellar2));
        assertWineDeepEquals(wineInCellar3, wineCellarsManager.findWinesInCellar(cellar3));
    }

    @Test
    public void testFindWinesInCellarWithNullCellar() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.findWinesInCellar(null);
    }

    @Test
    public void testFindWinesInCellarWithNullCellarId() {
        expectedException.expect(IllegalArgumentException.class);
        wineCellarsManager.findWinesInCellar(cellarNullId);
    }

    private static Cellar newCellar(String name, String address, int capacity) {
        Cellar cellar = new Cellar();
        cellar.setName(name);
        cellar.setAddress(address);
        cellar.setWineCapacity(capacity);

        return cellar;
    }

    private static Wine newWine(String name, String country, short year, int quantity, WineType type) {
        Wine wine = new Wine();
        wine.setName(name);
        wine.setCountry(country);
        wine.setQuantity(quantity);
        wine.setYear(year);
        wine.setType(type);

        return wine;
    }

    private void assertWineDeepEquals(Wine expected, Wine actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getYear(), actual.getYear());
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getType(), actual.getType());

    }

    private void assertCellarDeepEquals(Cellar expected, Cellar actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getWineCapacity(), actual.getWineCapacity());
    }

    private void assertCellarDeepEquals(List<Cellar> expectedList, List<Cellar> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Cellar expected = expectedList.get(i);
            Cellar actual = actualList.get(i);
            assertCellarDeepEquals(expected, actual);
        }
    }

    private void assertWineDeepEquals(List<Wine> expectedList, List<Wine> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Wine expected = expectedList.get(i);
            Wine actual = actualList.get(i);
            assertWineDeepEquals(expected, actual);
        }
    }
}
