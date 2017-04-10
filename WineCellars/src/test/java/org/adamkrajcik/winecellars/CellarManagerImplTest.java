/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.winecellars;

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
public class CellarManagerImplTest {

    private CellarManagerImpl manager;
    private DataSource dataSource;

    private static DataSource prepareDataSource() {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby:memory:cellarmanager-test;create=true");
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

        manager = new CellarManagerImpl();
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
    public void testCreateCellar() {
        Cellar cellar = newCellar("La Flea de Acid", "cellar adress", 50);
        manager.createCellar(cellar);

        Long cellarID = cellar.getId();
        assertNotNull(cellar);

        Cellar result = manager.findCellarById(cellarID);
        assertEquals(cellar, result);
        assertNotSame(cellar, result);
        assertDeepEquals(cellar, result);
    }

    @Test
    public void testCreateCellarWithNull() {
        expectedException.expect(IllegalArgumentException.class);
        manager.createCellar(null);
    }

    @Test
    public void testCreateCellarWithNullName() {
        Cellar cellar = newCellar(null, "", 5);
        expectedException.expect(IllegalArgumentException.class);
        manager.createCellar(cellar);
    }

    @Test
    public void testCreateCellarWithNullAddress() {
        Cellar cellar = newCellar("La Flea de Acid", null, 5);
        expectedException.expect(IllegalArgumentException.class);
        manager.createCellar(cellar);
    }

    @Test
    public void testCreateCellarWithNegativeCapacity() {
        Cellar cellar = newCellar("La Flea de Acid", "fff", -1);
        expectedException.expect(IllegalArgumentException.class);
        manager.createCellar(cellar);
    }

    @Test
    public void testUpdateCellarName() {
        Cellar cellar = newCellar("La Flea de Acid", "address", 50);

        manager.createCellar(cellar);
        Long cellarId = cellar.getId();

        cellar.setName("Mi Corrason");
        manager.updateCellar(cellar);
        cellar = manager.findCellarById(cellarId);

        assertEquals("Mi Corrason", cellar.getName());
        assertEquals("address", cellar.getAddress());
        assertEquals(50, cellar.getWineCapacity());
    }

    @Test
    public void testUpdateCellarAddress() {
        Cellar cellar = newCellar("La Flea de Acid", "address", 50);

        manager.createCellar(cellar);
        Long cellarId = cellar.getId();

        cellar.setAddress("NewBuilding");
        manager.updateCellar(cellar);
        cellar = manager.findCellarById(cellarId);

        assertEquals("La Flea de Acid", cellar.getName());
        assertEquals("NewBuilding", cellar.getAddress());
        assertEquals(50, cellar.getWineCapacity());
    }

    @Test
    public void testUpdateCellarCapacity() {
        Cellar cellar = newCellar("La Flea de Acid", "address", 50);

        manager.createCellar(cellar);
        Long cellarId = cellar.getId();

        cellar.setWineCapacity(100);
        manager.updateCellar(cellar);
        cellar = manager.findCellarById(cellarId);

        assertEquals("La Flea de Acid", cellar.getName());
        assertEquals("address", cellar.getAddress());
        assertEquals(100, cellar.getWineCapacity());
    }

    @Test
    public void testUpdateCellarWithNull() {
        expectedException.expect(IllegalArgumentException.class);
        manager.updateCellar(null);
    }

    @Test
    public void testUpdateCellarWithNullId() {
        Cellar cellar = newCellar("name", "address", 50);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateCellar(cellar);
    }

    @Test
    public void testUpdateCellarWithNullName() {
        Cellar cellar = newCellar("name", "address", 50);

        manager.createCellar(cellar);
        Long cellarId = cellar.getId();

        cellar = manager.findCellarById(cellarId);
        cellar.setName(null);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateCellar(cellar);
    }

    @Test
    public void testUpdateCellarWithNullAddress() {
        Cellar cellar = newCellar("name", "address", 50);

        manager.createCellar(cellar);
        Long cellarId = cellar.getId();

        cellar = manager.findCellarById(cellarId);
        cellar.setAddress(null);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateCellar(cellar);
    }

    @Test
    public void testUpdateCellarWithNegtiveCapacity() {
        Cellar cellar = newCellar("name", "address", 50);
        manager.createCellar(cellar);
        Long cellarId = cellar.getId();

        cellar = manager.findCellarById(cellarId);
        cellar.setWineCapacity(-1);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateCellar(cellar);
    }

    @Test
    public void testDeleteCellar() {
        Cellar cellar1 = newCellar("name", "address", 50);
        Cellar cellar2 = newCellar("name", "address", 100);

        manager.createCellar(cellar1);
        manager.createCellar(cellar2);

        Long cellarId = cellar1.getId();
        manager.deleteCellar(cellar1);

        assertNull(manager.findCellarById(cellarId));
        assertDeepEquals(cellar2, manager.findCellarById(cellar2.getId()));
    }

    @Test
    public void testDeleteCellarWithWrongAttributes() {
        Cellar cellar1 = newCellar("not existing name", "not existing address", 50);

        expectedException.expect(IllegalArgumentException.class);
        manager.deleteCellar(cellar1);
    }

    @Test
    public void testDeleteCellarWithNull() {
        expectedException.expect(IllegalArgumentException.class);
        manager.deleteCellar(null);
    }

    @Test
    public void testDeleteCellarWithNullId() {
        Cellar cellar = newCellar("not existing name", "not existing address", 50);

        expectedException.expect(IllegalArgumentException.class);
        manager.deleteCellar(cellar);
    }

    @Test
    public void testDeleteCellarWithBadId() {
        Cellar cellar = newCellar("not existing name", "not existing address", 50);
        cellar.setId(0l);

        expectedException.expect(IllegalArgumentException.class);
        manager.deleteCellar(cellar);
    }

    @Test
    public void testFindCellarById() {
        Cellar cellar = newCellar("cellar name", "cellar adress", 50);
        manager.createCellar(cellar);
        Long cellarId = cellar.getId();

        Cellar result = manager.findCellarById(cellarId);

        assertEquals(cellar, result);
        assertNotSame(cellar, result);
        assertDeepEquals(cellar, result);
    }

    @Test
    public void testFindCellarByIdWithNullId() {
        expectedException.expect(IllegalArgumentException.class);
        manager.findCellarById(null);
    }

    @Test
    public void testFindAllCellars() {
        assertTrue(manager.findAllCellars().isEmpty());

        Cellar cellar1 = newCellar("name", "address", 50);
        Cellar cellar2 = newCellar("name", "address", 100);
        manager.createCellar(cellar1);
        manager.createCellar(cellar2);

        List<Cellar> expected = Arrays.asList(cellar1, cellar2);
        List<Cellar> actual = manager.findAllCellars();
        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    static Cellar newCellar(String name, String address, int capacity) {
        Cellar cellar = new Cellar();
        cellar.setName(name);
        cellar.setAddress(address);
        cellar.setWineCapacity(capacity);

        return cellar;
    }

    private void assertDeepEquals(Cellar expected, Cellar actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getWineCapacity(), actual.getWineCapacity());
    }

    private void assertDeepEquals(List<Cellar> expectedList, List<Cellar> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Cellar expected = expectedList.get(i);
            Cellar actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private static Comparator<Cellar> idComparator = new Comparator<Cellar>() {
        @Override
        public int compare(Cellar o1, Cellar o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

}
