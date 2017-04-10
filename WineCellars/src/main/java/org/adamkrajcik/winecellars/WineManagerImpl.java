/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.winecellars;

import common.ServiceFailureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author dmwm
 */
public class WineManagerImpl implements WineManager {

    static final Logger log = Logger.getLogger(WineManagerImpl.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {

        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set.");
        }
    }

    @Override
    public void createWine(Wine wine) {
        checkDataSource();
        validateWine(wine);

        if (wine.getId() != null) {
            throw new IllegalArgumentException("Wine id is not null.");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO WINE (name, country, productionYear, quantity, type) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, wine.getName());
                st.setString(2, wine.getCountry());
                st.setInt(3, wine.getYear());
                st.setInt(4, wine.getQuantity());
                st.setString(5, wine.getType().name());

                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert cellar " + wine);
                }
                ResultSet keyRS = st.getGeneratedKeys();
                wine.setId(getKey(keyRS, wine));
            }
        } catch (SQLException ex) {
            String msg = "Error when inserting to DB.";
            log.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void updateWine(Wine wine) {
        checkDataSource();
        validateWine(wine);

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("UPDATE WINE SET name = ?, country = ?, productionYear = ?, quantity = ?, type = ? WHERE id = ?")) {
                st.setString(1, wine.getName());
                st.setString(2, wine.getCountry());
                st.setInt(3, wine.getYear());
                st.setLong(4, wine.getQuantity());
                st.setString(5, wine.getType().name());
                st.setLong(6, wine.getId());
                if (st.executeUpdate() != 1) {
                    throw new IllegalArgumentException("Cannot update wine" + wine);
                }
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "DB connection problem.", ex);
            throw new ServiceFailureException("DB .connection problem", ex);
        }

    }

    @Override
    public void deleteWine(Wine wine) {
        checkDataSource();

        if (wine == null) {
            throw new IllegalArgumentException("Wine is null.");
        }

        if (wine.getId() == null) {
            throw new IllegalArgumentException("Wine is is null.");
        }

        if (wine.getId() <= 0) {
            throw new IllegalArgumentException("Wine id is bad.");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM WINE WHERE id = ?")) {
                st.setLong(1, wine.getId());

                if (st.executeUpdate() != 1) {
                    throw new ServiceFailureException("Deleting problem");
                }

            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "DB connection problem.", ex);
            throw new ServiceFailureException("DB .connection problem", ex);
        }
    }

    @Override
    public Wine findWineById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id is null.");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, name, country, productionYear, quantity, type FROM WINE WHERE id = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Wine result = resultSetToWine(rs);

                    if (rs.next()) {
                        throw new ServiceFailureException("Internal error: More entities with the same id found (source id: " + id + ", found " + result + " and " + resultSetToWine(rs));
                    }

                    return result;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "DB connection problem.", ex);
            throw new ServiceFailureException("DB .connection problem", ex);
        }
    }

    @Override
    public List<Wine> findAllWines() {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, name, country, productionYear, quantity, type FROM WINE")) {
                ResultSet rs = st.executeQuery();
                List<Wine> result = new ArrayList<>();

                while (rs.next()) {
                    result.add(resultSetToWine(rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            String msg = "Error when getting all the cellars from DB";
            log.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private void validateWine(Wine wine) {

        if (wine == null) {
            throw new IllegalArgumentException("Wine is null.");
        }

        /*  if (wine.getId() != null) {
         throw new IllegalArgumentException("Wine id is already set.");
         }*/
        if (wine.getName() == null) {
            throw new IllegalArgumentException("Wine name is null.");
        }

        if (wine.getCountry() == null) {
            throw new IllegalArgumentException("Wine country is null.");
        }

        if (wine.getYear() <= 0) {
            throw new IllegalArgumentException("Wine year is less than or equal zero.");
        }

        if (wine.getQuantity() <= 0) {
            throw new IllegalArgumentException("Wine quantity is less than or equal zero.");
        }

        if (wine.getType() == null) {
            throw new IllegalArgumentException("Wine type is null.");
        }
    }

    private Long getKey(ResultSet keyRS, Wine wine) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key retriving failed when trying to insert wine " + wine + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }

            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key retriving failed when trying to insert wine " + wine + " - more keys found");
            }

            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key retriving failed when trying to insert wine " + wine + " - no key found");
        }
    }

    private static Wine resultSetToWine(ResultSet rs) throws SQLException {
        Wine wine = new Wine();
        wine.setId(rs.getLong("id"));
        wine.setName(rs.getString("name"));
        wine.setCountry(rs.getString("country"));
        wine.setYear((short) rs.getInt("productionYear"));
        wine.setQuantity(rs.getInt("quantity"));
        wine.setType(WineType.valueOf(rs.getString("type")));
        return wine;
    }

    static Wine executeQueryForSingleWine(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Wine result = resultSetToWine(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more wines with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    static List<Wine> executeQueryForMultipleWines(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Wine> result = new ArrayList<Wine>();
        while (rs.next()) {
            result.add(resultSetToWine(rs));
        }
        return result;
    }

}
