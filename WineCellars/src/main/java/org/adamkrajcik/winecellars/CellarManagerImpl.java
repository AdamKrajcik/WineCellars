/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.winecellars;

import common.IllegalEntityException;
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
public class CellarManagerImpl implements CellarManager {
    
    private static final Logger log = Logger.getLogger(CellarManagerImpl.class.getName());
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
    public void createCellar(Cellar cellar) throws ServiceFailureException {
        checkDataSource();
        validate(cellar);
        
        if (cellar.getId() != null) {
            throw new IllegalEntityException("Cellar id is already set.");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO CELLAR (name, address, wineCapacity) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, cellar.getName());
                st.setString(2, cellar.getAddress());
                st.setInt(3, cellar.getWineCapacity());
                
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert cellar " + cellar);
                }
                
                ResultSet keyRS = st.getGeneratedKeys();
                cellar.setId(getKey(keyRS, cellar));
            }
        } catch (SQLException ex) {
            String msg = "Error when inserting to DB.";
            log.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void updateCellar(Cellar cellar) throws ServiceFailureException {
        checkDataSource();
        validate(cellar);
        
        if (cellar.getId() == null) {
            throw new IllegalArgumentException("Cellar id is null.");
        }
        
        if (cellar.getId() < 1) {
            throw new IllegalArgumentException("Cellar id less than 1.");
        }
        
        try(Connection conn = dataSource.getConnection())
        {
            try(PreparedStatement st = conn.prepareStatement("UPDATE CELLAR SET name = ?, address = ?, wineCapacity = ? WHERE id = ?")) {
                st.setString(1, cellar.getName());
                st.setString(2, cellar.getAddress());
                st.setInt(3, cellar.getWineCapacity());
                st.setLong(4, cellar.getId());
                if (st.executeUpdate() != 1) {
                    throw new IllegalArgumentException("Cannot update cellar" + cellar);
                }
            }
        }
        catch (SQLException ex) {
            log.log(Level.SEVERE,"DB connection problem.", ex);
            throw new ServiceFailureException("DB .connection problem", ex);
        }
    }

    @Override
    public void deleteCellar(Cellar cellar) throws ServiceFailureException {
        if (cellar == null) {
            throw new IllegalArgumentException("Cellar is null.");
        }
        
        if (cellar.getId() == null) {
            throw new IllegalArgumentException("Cellar is is null.");
        }
        
        if (cellar.getId() <= 0) {
            throw new IllegalArgumentException("Cellar id is bad.");
        }
        
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st = conn.prepareStatement("DELETE FROM CELLAR WHERE id = ?")) {
                st.setLong(1, cellar.getId());
                
                if (st.executeUpdate() != 1) {
                    throw new ServiceFailureException("Deleting problem");
                }
                
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE,"DB connection problem.", ex);
            throw new ServiceFailureException("DB .connection problem", ex);
        }
    }

    @Override
    public Cellar findCellarById(Long id) throws ServiceFailureException {
        if (id == null) {
            throw  new IllegalArgumentException("Id is null.");
        }
        
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, name, address, winecapacity FROM CELLAR WHERE id = ?")) {
                st.setLong(1, id);
                
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Cellar result = resultSetToCellar(rs);
                    
                    if (rs.next()) {
                        throw new ServiceFailureException("Internal error: More entities with the same id found (source id: " + id + ", found " + result + " and " + resultSetToCellar(rs));
                    }
                    
                    return result;
                }
                else
                {
                    return null;
                }
            }
        }
        catch (SQLException ex) {
            log.log(Level.SEVERE,"DB connection problem.", ex);
            throw new ServiceFailureException("DB .connection problem", ex);
        }
    }

    @Override
    public List<Cellar> findAllCellars() throws ServiceFailureException {
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st = conn.prepareStatement("SELECT id, name, address, wineCapacity FROM CELLAR")) {
                ResultSet rs = st.executeQuery();
                List<Cellar> result = new ArrayList<>();
                
                while(rs.next()) {
                    result.add(resultSetToCellar(rs));
                }
                
                return result;
            }
        }
        catch (SQLException ex) {
            String msg = "Error when getting all the cellar from DB";
            log.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private static void validate(Cellar cellar) {
        if (cellar == null) {
            throw new IllegalArgumentException("Cellar is null.");
        }

        if (cellar.getName() == null) {
            throw new IllegalArgumentException("Cellar name is null.");
        }

        if (cellar.getAddress() == null) {
            throw new IllegalArgumentException("Cellar address in null.");
        }

        if (cellar.getWineCapacity() <= 0) {
            throw new IllegalArgumentException("Cellar capacity is less than or equal zero.");
        }
    }
    
    private Long getKey(ResultSet keyRS, Cellar cellar) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key retriving failed when trying to insert cellar " + cellar + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key retriving failed when trying to insert cellar " + cellar + " - more keys found");
            }
            
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key retriving failed when trying to insert cellar " + cellar +" - no key found");
        }
    }
    
    private static Cellar resultSetToCellar(ResultSet rs) throws SQLException {
        Cellar cellar = new Cellar();
        cellar.setId(rs.getLong("id"));
        cellar.setName(rs.getString("name"));
        cellar.setAddress(rs.getString("address"));
        cellar.setWineCapacity(rs.getInt("wineCapacity"));
        return cellar;
    }
    
    static Cellar executeQueryForSingleCellar(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Cellar result = resultSetToCellar(rs);                
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more wines with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    static List<Cellar> executeQueryForMultipleCellars(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Cellar> result = new ArrayList<Cellar>();
        while (rs.next()) {
            result.add(resultSetToCellar(rs));
        }
        return result;
    }
}
