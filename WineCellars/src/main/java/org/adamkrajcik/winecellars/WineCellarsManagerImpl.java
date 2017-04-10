/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.winecellars;

import common.DBUtils;
import common.IllegalEntityException;
import common.ServiceFailureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author dmwm
 */
public class WineCellarsManagerImpl implements WineCellarsManager {

    private static final Logger log = Logger.getLogger(WineCellarsManagerImpl.class.getName());
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void putWineInCellar(Wine wine, Cellar cellar) {
        checkDataSource();
        if (wine == null) {
            throw new IllegalArgumentException("wine is null");
        }
        if (wine.getId() == null) {
            throw new IllegalArgumentException("wine id is null");
        }
        if (cellar == null) {
            throw new IllegalArgumentException("cellar is null");
        }
        if (cellar.getId() == null) {
            throw new IllegalArgumentException("cellar id is null");
        }
        Connection conn = null;
        PreparedStatement updateSt = null;
        try {
            conn = dataSource.getConnection();
            //checkIfCellarHasSpace(conn, cellar);
            int cellarFreeSpace = getCellarFreeSpace(conn, cellar);
            if(wine.getQuantity() > cellarFreeSpace){
                throw new IllegalArgumentException("Cellar " + cellar + " does not have enough free space."
                        + "\nWine quantity: " + wine.getQuantity()
                        + "\nCellar free space: " + cellarFreeSpace);
            }

            updateSt = conn.prepareStatement("UPDATE Wine SET cellarId = ? WHERE id = ? AND cellarId IS NULL");
            updateSt.setLong(1, cellar.getId());
            updateSt.setLong(2, wine.getId());
            int count = updateSt.executeUpdate();
            if (count == 0) {
                throw new IllegalEntityException("Wine " + wine + " not found or it is already placed in some cellar");
            }

        } catch (SQLException ex) {
            String msg = "Error when putting wine into cellar";
            log.log(Level.SEVERE, msg, ex);
            throw new IllegalArgumentException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, updateSt);
        }
    }

    @Override
    public void removeWineFromCellar(Wine wine, Cellar cellar) {
        checkDataSource();
        if (wine == null) {
            throw new IllegalArgumentException("wine is null");
        }
        if (wine.getId() == null) {
            throw new IllegalArgumentException("wine id is null");
        }
        if (cellar == null) {
            throw new IllegalArgumentException("Wine is not in cellar.");
        }
        if (cellar.getId() == null) {
            throw new IllegalArgumentException("cellar id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE Wine SET cellarId = NULL WHERE id = ? AND cellarId = ?");
            st.setLong(1, wine.getId());
            st.setLong(2, cellar.getId());
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, wine, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when putting wine into cellar";
            log.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Cellar findCellarWithWine(Wine wine) {
        checkDataSource();
        if (wine == null) {
            throw new IllegalArgumentException("wine is null");
        }
        if (wine.getId() == null) {
            throw new IllegalArgumentException("wine id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Cellar.id, Cellar.name, address, wineCapacity "
                    + "FROM Cellar JOIN WINE ON Cellar.id = Wine.cellarId "
                    + "WHERE Wine.id = ?");
            st.setLong(1, wine.getId());

            return CellarManagerImpl.executeQueryForSingleCellar(st);

        } catch (SQLException ex) {
            String msg = "Error when trying to find cellar with wine " + wine;
            log.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Wine> findWinesInCellar(Cellar cellar) {
        checkDataSource();
        if (cellar == null) {
            throw new IllegalArgumentException("cellar is null");
        }
        if (cellar.getId() == null) {
            throw new IllegalArgumentException("cellar id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Wine.id, Wine.cellarId, Wine.name, country, productionYear, quantity, type "
                    + "FROM Wine JOIN Cellar ON Cellar.id = Wine.cellarId "
                    + "WHERE Cellar.id = ?");
            st.setLong(1, cellar.getId());
            return WineManagerImpl.executeQueryForMultipleWines(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find wines in cellar " + cellar;
            log.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    private static void checkIfCellarHasSpace(Connection conn, Cellar cellar) throws SQLException {
        PreparedStatement checkSt = null;
        try {
            checkSt = conn.prepareStatement(
                    "SELECT wineCapacity, SUM(Wine.quantity) as wineQuantityCount "
                    + "FROM Cellar LEFT JOIN Wine ON Cellar.id = Wine.cellarId "
                    + "WHERE Cellar.id = ? "
                    + "GROUP BY Cellar.id, wineCapacity");
            checkSt.setLong(1, cellar.getId());
            ResultSet rs = checkSt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("wineCapacity") <= rs.getInt("wineQuantityCount")) {
                    throw new IllegalArgumentException("Cellar " + cellar + " is already full");
                }
            } else {
                throw new IllegalArgumentException("Cellar " + cellar + " does not exist in the database");
            }
        } finally {
            DBUtils.closeQuietly(null, checkSt);
        }
    }
    
        private static int getCellarFreeSpace(Connection conn, Cellar cellar) throws SQLException {
        PreparedStatement checkSt = null;
        try {
            checkSt = conn.prepareStatement(
                    "SELECT wineCapacity, SUM(Wine.quantity) as wineQuantityCount "
                    + "FROM Cellar LEFT JOIN Wine ON Cellar.id = Wine.cellarId "
                    + "WHERE Cellar.id = ? "
                    + "GROUP BY Cellar.id, wineCapacity");
            checkSt.setLong(1, cellar.getId());
            ResultSet rs = checkSt.executeQuery();
            if (rs.next()) {
                return rs.getInt("wineCapacity") - rs.getInt("wineQuantityCount");
                /*
                if (rs.getInt("wineCapacity") <= rs.getInt("wineQuantityCount")) {
                    throw new IllegalArgumentException("Cellar " + cellar + " is already full");
                }*/
            } else {
                throw new IllegalArgumentException("Cellar " + cellar + " does not exist in the database");
            }
        } finally {
            DBUtils.closeQuietly(null, checkSt);
        }
    }
}
