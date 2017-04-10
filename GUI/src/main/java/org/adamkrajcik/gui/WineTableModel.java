/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.sql.DataSource;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import org.adamkrajcik.winecellars.Cellar;
import org.adamkrajcik.winecellars.CellarManager;
import org.adamkrajcik.winecellars.CellarManagerImpl;
import org.adamkrajcik.winecellars.Wine;
import org.adamkrajcik.winecellars.WineCellarsManager;
import org.adamkrajcik.winecellars.WineManager;
import org.adamkrajcik.winecellars.WineManagerImpl;
import org.adamkrajcik.winecellars.WineType;
import org.apache.log4j.Logger;

/**
 *
 * @author Tomas
 */
public class WineTableModel extends AbstractTableModel {
    
    private static final Logger log = Logger.getLogger(CellarTableModel.class);
    private List<Wine> wines = new ArrayList<Wine>();
    private List<Cellar> cellars = new ArrayList<Cellar>();
    private WineManager wineManager;
    private WineCellarsManager wineCellarsMnager;
    private ResourceBundle bundle;
    
    
    private static enum COLUMNS {
        NAME, COUNTRY, VINTAGE, QUANTITY, TYPE, CELLAR
    }
    

    public WineTableModel(WineManager wineManager, WineCellarsManager wineCellarsMnager, ResourceBundle bundle)  {
        this.wineManager = wineManager;
        this.wineCellarsMnager = wineCellarsMnager;
        this.bundle = bundle;
    }
    
    public void addWine(Wine wine, Cellar cellar) {
    wines.add(wine); 
    cellars.add(cellar);
    //fireTableDataChanged();
    int lastRow = wines.size() - 1;
    fireTableRowsInserted(lastRow, lastRow);
    }
    

    @Override
    public int getRowCount() {
         return wines.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.values().length;
    }

    @Override 
    public Object getValueAt(int rowIndex, int columnIndex) {
        Wine wine = wines.get(rowIndex);
        switch (COLUMNS.values()[columnIndex]) {
            case NAME:
                return wine.getName();
            case COUNTRY:
                return wine.getCountry();
            case VINTAGE:
                return wine.getYear();
            case QUANTITY:
                return wine.getQuantity();
            case TYPE:
                return wine.getType();
            case CELLAR:
                return cellars.get(rowIndex);
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (COLUMNS.values()[columnIndex]) {
            case NAME:
                return bundle.getString("name");
            case COUNTRY:
                return bundle.getString("country");
            case VINTAGE:
                return bundle.getString("vintage");
            case QUANTITY:
                return bundle.getString("quantity");
            case TYPE:
                return bundle.getString("wineType");
            case CELLAR:
                return bundle.getString("cellar");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
         switch (COLUMNS.values()[columnIndex]) {
            case NAME:
                return String.class;
            case COUNTRY:
                return String.class;
            case VINTAGE:
                return Short.class;
            case QUANTITY:
                return Integer.class;
            case TYPE:
                return WineType.class;
            case CELLAR:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                return true;
            case 5:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        new UpdateWineSwingWorker(wines.get(rowIndex), wineManager, columnIndex, aValue, wineCellarsMnager).execute();
    }
    
    public Wine getWineAtRow(int rowIndex){
        return wines.get(rowIndex);       
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


    
    void clearWines() {
        wines.clear();
        cellars.clear();
        fireTableDataChanged();
    }
    
    void removeWine(Wine wine) {
        cellars.remove(wines.indexOf(wine));
        wines.remove(wine);
        fireTableDataChanged();
    }
    
    private class UpdateWineSwingWorker extends SwingWorker<Void, Void> {

        private Wine wine;
        private WineManager wineManager;
        private int columnIndex;
        private Object aValue;
        private WineCellarsManager wineCellarsManager;

        public UpdateWineSwingWorker(Wine wine, WineManager wineManager, int columnIndex, Object aValue, WineCellarsManager wineCellarsManager) {
            this.wine = wine;
            this.wineManager = wineManager;
            this.columnIndex = columnIndex;
            this.aValue = aValue;
            this.wineCellarsManager = wineCellarsManager;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Wine clone = cloneWine(wine);
            switch (WineTableModel.COLUMNS.values()[columnIndex]) {
                case NAME:
                    clone.setName((String) aValue);
                    break;
                case COUNTRY:
                    clone.setCountry((String) aValue);
                    break;
                case VINTAGE:
                    clone.setYear((short) aValue);
                    break;
                case QUANTITY:
                    Cellar c = wineCellarsManager.findCellarWithWine(wine);
                    if (countWineQuntity(wineCellarsManager.findWinesInCellar(c)) - wine.getQuantity() + (Integer) aValue > c.getWineCapacity()) {
                        JOptionPane.showMessageDialog(null, bundle.getString("loadWinesError"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                        throw new IllegalArgumentException("Cellar do not have enough free space.");
                    }
                    clone.setQuantity((Integer) aValue);
                    break;
                case TYPE:
                    break;
                case CELLAR:
                    break;
                default:
                    throw new IllegalArgumentException("ColumnIndex out of range.");
            }
            wineManager.updateWine(clone);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                switch (WineTableModel.COLUMNS.values()[columnIndex]) {
                    case NAME:
                        wine.setName((String) aValue);
                        break;
                    case COUNTRY:
                        wine.setCountry((String) aValue);
                        break;
                    case VINTAGE:
                        wine.setYear((short) aValue);
                        break;
                    case QUANTITY:
                        wine.setQuantity((int) aValue);
                        break;
                    case TYPE:
                        break;
                    case CELLAR:
                        break;
                    default:
                        throw new IllegalArgumentException("ColumnIndex out of range.");
                }
                fireTableDataChanged();
                log.debug("Updated wine " + wine);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error when updating wine" + wine, e);
            }
        }
    }

    public void updateWinesCellar(Wine wine, Cellar cellar){
        cellars.set(wines.indexOf(wine), cellar);
        fireTableDataChanged();
    }
    private Wine cloneWine(Wine wine) {
        Wine w = new Wine();
        w.setId(wine.getId());
        w.setName(wine.getName());
        w.setCountry(wine.getCountry());
        w.setQuantity(wine.getQuantity());
        w.setYear(wine.getYear());
        w.setType(wine.getType());
        return w;
    }

    private int countWineQuntity(List<Wine> wines) {
        int q = 0;
        for (Wine wine : wines) {
            q += wine.getQuantity();
        }
        return q;
    }

}
