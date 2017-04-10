/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.gui;

import common.ServiceFailureException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import org.adamkrajcik.winecellars.Cellar;
import org.adamkrajcik.winecellars.CellarManager;
import org.adamkrajcik.winecellars.Wine;
import org.adamkrajcik.winecellars.WineCellarsManager;
import org.apache.log4j.Logger;

/**
 *
 * @author dmwm
 */
public class CellarTableModel extends AbstractTableModel {

    private static final Logger log = Logger.getLogger(CellarTableModel.class);
    private CellarManager cellarManager;
    private WineCellarsManager wineCellarsManager;
    private List<Cellar> cellars = new ArrayList<>();
    private ResourceBundle bundle;
    private boolean isEditable;

    private static enum COLUMNS {

        NAME, ADDRESS, WINECAPACITY
    }

    public CellarTableModel(CellarManager cellarManager, WineCellarsManager wineCellarsManager, ResourceBundle bundle) {
        this.cellarManager = cellarManager;
        this.bundle = bundle;
        isEditable = true;
        this.wineCellarsManager = wineCellarsManager;
    }

    @Override
    public int getRowCount() {
        return cellars.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Cellar cellar = cellars.get(rowIndex);
        switch (COLUMNS.values()[columnIndex]) {
            case NAME:
                return cellar.getName();
            case ADDRESS:
                return cellar.getAddress();
            case WINECAPACITY:
                return cellar.getWineCapacity();
            default:
                throw new IllegalArgumentException("ColumnIndex out of range");
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (COLUMNS.values()[column]) {
            case NAME:
                return bundle.getString("name");
            case ADDRESS:
                return bundle.getString("address");
            case WINECAPACITY:
                return bundle.getString("wineCapacity");
            default:
                throw new IllegalArgumentException("Column out of range.");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (COLUMNS.values()[columnIndex]) {
            case NAME:
            case ADDRESS:
                return String.class;
            case WINECAPACITY:
                return Integer.class;
            default:
                throw new IllegalArgumentException("ColumnIndex out of range.");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return isEditable;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Cellar cellar = cellars.get(rowIndex);
        UpdateCellarSwingWorker updateCellarSW;

        isEditable = false;
        updateCellarSW = new UpdateCellarSwingWorker(cellar, cellarManager, columnIndex, aValue, wineCellarsManager);
        updateCellarSW.execute();
    }

    public void addCellar(Cellar cellar) {
        cellars.add(cellar);
        fireTableDataChanged();
    }

    public void removeCellar(Cellar cellar) {
        cellars.remove(cellar);
        fireTableDataChanged();
    }

    public void clear() {
        cellars.clear();
        fireTableDataChanged();
    }

    public Cellar getCellarAtRow(int row) {
        return cellars.get(row);
    }

    public List<Cellar> getAllCellars() {
        return cellars;
    }

    private class UpdateCellarSwingWorker extends SwingWorker<Void, Void> {

        private Cellar cellar;
        private CellarManager cellarManager;
        private int columnIndex;
        private Object aValue;
        private WineCellarsManager wineCellarsManager;

        public UpdateCellarSwingWorker(Cellar cellar, CellarManager cellarManager, int columnIndex, Object aValue, WineCellarsManager wineCellarsManager) {
            this.cellar = cellar;
            this.cellarManager = cellarManager;
            this.columnIndex = columnIndex;
            this.aValue = aValue;
            this.wineCellarsManager = wineCellarsManager;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Cellar clone = cloneCellar(cellar);
            switch (COLUMNS.values()[columnIndex]) {
                case NAME:
                    clone.setName((String) aValue);
                    break;
                case ADDRESS:
                    clone.setAddress((String) aValue);
                    break;
                case WINECAPACITY:
                    if (countWineQuntity(wineCellarsManager.findWinesInCellar(cellar)) > (Integer) aValue) {
                        JOptionPane.showMessageDialog(null, bundle.getString("loadWinesError"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                        throw new IllegalArgumentException("Cellar contains more wines than new capacity");
                    }
                    clone.setWineCapacity((Integer) aValue);
                    break;
                default:
                    throw new IllegalArgumentException("ColumnIndex out of range.");
            }
            cellarManager.updateCellar(clone);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                switch (COLUMNS.values()[columnIndex]) {
                    case NAME:
                        cellar.setName((String) aValue);
                        break;
                    case ADDRESS:
                        cellar.setAddress((String) aValue);
                        break;
                    case WINECAPACITY:
                        cellar.setWineCapacity((Integer) aValue);
                        break;
                    default:
                        throw new IllegalArgumentException("ColumnIndex out of range.");
                }
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error when updating cellar" + cellar, e);
            }
            isEditable = true;
        }
    }

    private Cellar cloneCellar(Cellar cellar) {
        Cellar c = new Cellar();
        c.setId(cellar.getId());
        c.setName(cellar.getName());
        c.setAddress(cellar.getAddress());
        c.setWineCapacity(cellar.getWineCapacity());
        return c;
    }

    private int countWineQuntity(List<Wine> wines) {
        int q = 0;
        for (Wine wine : wines) {
            q += wine.getQuantity();
        }
        return q;
    }
}
