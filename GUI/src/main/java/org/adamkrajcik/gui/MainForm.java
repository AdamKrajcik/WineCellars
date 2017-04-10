/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.gui;

import common.IllegalEntityException;
import common.ServiceFailureException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.sql.DataSource;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import static org.adamkrajcik.gui.WineTableModel.newWine;
import org.adamkrajcik.winecellars.Cellar;
import org.adamkrajcik.winecellars.CellarManager;
import org.adamkrajcik.winecellars.CellarManagerImpl;
import org.adamkrajcik.winecellars.Wine;
import org.adamkrajcik.winecellars.WineCellarsManager;
import org.adamkrajcik.winecellars.WineCellarsManagerImpl;
import org.adamkrajcik.winecellars.WineManager;
import org.adamkrajcik.winecellars.WineManagerImpl;
import org.adamkrajcik.winecellars.WineType;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

/**
 *
 * @author dmwm
 */
public class MainForm extends javax.swing.JFrame {

    /**
     * Creates new form MainForm
     */
    private DataSource dataSource;
    private WineManager wineManager;
    private WineTableModel wineTableModel;
    private CellarManager cellarManager;
    private CellarTableModel cellarTableModel;
    private WineCellarsManager wineCellarManager;
    private ResourceBundle langResource;
    private ResourceBundle config;
    private final Logger log = Logger.getLogger(MainForm.class);

    public MainForm() {

        DataSource bds = new EmbeddedDatabaseBuilder()
                .setType(DERBY)
                .addScript("classpath:createTables.sql")
                .addScript("classpath:testData.sql")
                .build();
        dataSource = bds;

        /*config = ResourceBundle.getBundle("org/adamkrajcik/gui/resources/config");
        
         BasicDataSource bds = new BasicDataSource();
         bds.setUrl(config.getString("url"));
         bds.setUsername(config.getString("user"));
         bds.setPassword(config.getString("password"));
         bds.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
         dataSource = bds;
        */
        
        try {
            langResource = ResourceBundle.getBundle("org/adamkrajcik/gui/resources/lang", getLocale());
        } catch (Exception ex) {
            langResource = ResourceBundle.getBundle("org/adamkrajcik/gui/resources/lang");
            log.debug("Error local language bundle doesnt exist.", ex);
            JOptionPane.showMessageDialog(rootPane, "This application isn't yet translated to your language. Default language was set. ", "Warning", JOptionPane.WARNING_MESSAGE);
        }

        WineManagerImpl wmi = new WineManagerImpl();
        wmi.setDataSource(dataSource);
        wineManager = wmi;
        
        CellarManagerImpl cmi = new CellarManagerImpl();
        cmi.setDataSource(dataSource);
        cellarManager = cmi;
        
        WineCellarsManagerImpl wcmi = new WineCellarsManagerImpl();
        wcmi.setDataSource(dataSource);
        wineCellarManager = wcmi;
        
        wineTableModel = new WineTableModel(wineManager, wineCellarManager, langResource);
        cellarTableModel = new CellarTableModel(cellarManager, wineCellarManager, langResource);
        
        new LoadAllCellarsSwingWorker().execute();
        new LoadAllWinesSwingWorker().execute();

        initComponents();
        wineFilterPanel.setVisible(false);
        WineTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cellarDialog = new javax.swing.JDialog();
        cellar_nameLabel = new javax.swing.JLabel();
        cellar_nameInput = new javax.swing.JTextField();
        cellar_addressLabel = new javax.swing.JLabel();
        cellar_capacityLabel = new javax.swing.JLabel();
        cellar_addressInput = new javax.swing.JTextField();
        cellar_capacitySpinner = new javax.swing.JSpinner();
        cellar_createButton = new javax.swing.JButton();
        cellar_cancelButton = new javax.swing.JButton();
        cellar_errorLabel = new javax.swing.JLabel();
        wineDialog = new javax.swing.JDialog();
        wine_nameLabel = new javax.swing.JLabel();
        wine_countryLabel = new javax.swing.JLabel();
        wine_vintageLabel = new javax.swing.JLabel();
        wine_quantityLabel = new javax.swing.JLabel();
        wine_typeLabel = new javax.swing.JLabel();
        wine_nameInput = new javax.swing.JTextField();
        wine_countryInput = new javax.swing.JTextField();
        wine_vintageInput = new javax.swing.JSpinner();
        wine_quantityInput = new javax.swing.JSpinner();
        wine_typeInput = new javax.swing.JComboBox();
        wine_createButton = new javax.swing.JButton();
        wine_cancelButton = new javax.swing.JButton();
        wine_errorLabel = new javax.swing.JLabel();
        tabbedPanel = new javax.swing.JTabbedPane();
        winePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        WineTable = new javax.swing.JTable();
        wineFilterPanel = new javax.swing.JPanel();
        ResetFilterButton = new javax.swing.JButton();
        FilterLabel = new javax.swing.JLabel();
        cellarPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        CellarTable = new javax.swing.JTable();
        menuBar = new javax.swing.JMenuBar();
        wineMenu = new javax.swing.JMenu();
        createWineMenuItem = new javax.swing.JMenuItem();
        deleteWineMenuItem = new javax.swing.JMenuItem();
        putWineToCellarMenuItem = new javax.swing.JMenuItem();
        removeFromCellarMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        loadAllWinesMenuItem = new javax.swing.JMenuItem();
        cellarMenu = new javax.swing.JMenu();
        createCellarMenuItem = new javax.swing.JMenuItem();
        deleteCellarMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        findWinesMenuItem = new javax.swing.JMenuItem();
        loadAllCellarsMenuItem = new javax.swing.JMenuItem();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/adamkrajcik/gui/resources/lang"); // NOI18N
        cellarDialog.setTitle(bundle.getString("newCellar")); // NOI18N
        cellarDialog.setResizable(false);
        cellarDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                cellarDialogWindowClosed(evt);
            }
        });

        cellar_nameLabel.setText(bundle.getString("name")); // NOI18N

        cellar_addressLabel.setText(bundle.getString("address")); // NOI18N

        cellar_capacityLabel.setText(bundle.getString("wineCapacity")); // NOI18N

        cellar_capacitySpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));

        cellar_createButton.setText(bundle.getString("create")); // NOI18N
        cellar_createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cellar_createButtonActionPerformed(evt);
            }
        });

        cellar_cancelButton.setText(bundle.getString("cancel")); // NOI18N
        cellar_cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cellar_cancelButtonActionPerformed(evt);
            }
        });

        cellar_errorLabel.setFont(new java.awt.Font("Cantarell", 1, 18)); // NOI18N
        cellar_errorLabel.setForeground(new java.awt.Color(255, 32, 0));
        cellar_errorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout cellarDialogLayout = new javax.swing.GroupLayout(cellarDialog.getContentPane());
        cellarDialog.getContentPane().setLayout(cellarDialogLayout);
        cellarDialogLayout.setHorizontalGroup(
            cellarDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cellarDialogLayout.createSequentialGroup()
                .addGroup(cellarDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cellarDialogLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(cellarDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cellar_addressLabel)
                            .addComponent(cellar_nameLabel)
                            .addComponent(cellar_capacityLabel))
                        .addGap(18, 18, 18)
                        .addGroup(cellarDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cellar_addressInput)
                            .addComponent(cellar_nameInput)
                            .addComponent(cellar_capacitySpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(cellarDialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cellar_createButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cellar_cancelButton))
                    .addGroup(cellarDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cellar_errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        cellarDialogLayout.setVerticalGroup(
            cellarDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cellarDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cellar_errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cellarDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cellar_nameLabel)
                    .addComponent(cellar_nameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cellarDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cellar_addressLabel)
                    .addComponent(cellar_addressInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cellarDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cellar_capacitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cellar_capacityLabel))
                .addGap(18, 18, 18)
                .addGroup(cellarDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cellar_createButton)
                    .addComponent(cellar_cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        wineDialog.setTitle(bundle.getString("newWine")); // NOI18N
        wineDialog.setResizable(false);
        wineDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                wineDialogWindowClosed(evt);
            }
        });

        wine_nameLabel.setText(bundle.getString("name")); // NOI18N

        wine_countryLabel.setText(bundle.getString("country")); // NOI18N

        wine_vintageLabel.setText(bundle.getString("vintage")); // NOI18N

        wine_quantityLabel.setText(bundle.getString("quantity")); // NOI18N

        wine_typeLabel.setText(bundle.getString("wineType")); // NOI18N

        wine_nameInput.setToolTipText("");

        wine_vintageInput.setModel(new SpinnerNumberModel((short) Calendar.getInstance().get(Calendar.YEAR), (short) 1700, (short) Calendar.getInstance().get(Calendar.YEAR), (short) 1));

        wine_quantityInput.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(20), Integer.valueOf(1), null, Integer.valueOf(1)));

        wine_typeInput.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "RED", "WHITE", "ROSE" }));

        wine_createButton.setText(bundle.getString("create")); // NOI18N

        wine_cancelButton.setText(bundle.getString("cancel")); // NOI18N

        javax.swing.GroupLayout wineDialogLayout = new javax.swing.GroupLayout(wineDialog.getContentPane());
        wineDialog.getContentPane().setLayout(wineDialogLayout);
        wineDialogLayout.setHorizontalGroup(
            wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wineDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(wineDialogLayout.createSequentialGroup()
                        .addGroup(wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(wine_typeLabel)
                            .addComponent(wine_quantityLabel)
                            .addComponent(wine_vintageLabel)
                            .addComponent(wine_countryLabel)
                            .addComponent(wine_nameLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wineDialogLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(wine_createButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(wine_cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12))
                            .addGroup(wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(wine_nameInput)
                                .addComponent(wine_countryInput, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(wine_vintageInput)
                                .addComponent(wine_quantityInput, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(wine_typeInput, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(wine_errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        wineDialogLayout.setVerticalGroup(
            wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wineDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wine_errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wine_nameLabel)
                    .addComponent(wine_nameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wine_countryLabel)
                    .addComponent(wine_countryInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wine_vintageLabel)
                    .addComponent(wine_vintageInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wine_quantityInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wine_quantityLabel))
                .addGap(18, 18, 18)
                .addGroup(wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wine_typeInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wine_typeLabel))
                .addGap(18, 18, 18)
                .addGroup(wineDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wine_createButton)
                    .addComponent(wine_cancelButton))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WineCellarsManager");

        tabbedPanel.setName(""); // NOI18N

        WineTable.setModel(wineTableModel);
        jScrollPane2.setViewportView(WineTable);

        wineFilterPanel.setBackground(new java.awt.Color(255, 115, 115));

        ResetFilterButton.setText(bundle.getString("clearFilter")); // NOI18N
        ResetFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetFilterButtonActionPerformed(evt);
            }
        });

        FilterLabel.setText("Wines in cellar: name address capacity:");

        javax.swing.GroupLayout wineFilterPanelLayout = new javax.swing.GroupLayout(wineFilterPanel);
        wineFilterPanel.setLayout(wineFilterPanelLayout);
        wineFilterPanelLayout.setHorizontalGroup(
            wineFilterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wineFilterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(FilterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 365, Short.MAX_VALUE)
                .addComponent(ResetFilterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        wineFilterPanelLayout.setVerticalGroup(
            wineFilterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wineFilterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(ResetFilterButton)
                .addComponent(FilterLabel))
        );

        javax.swing.GroupLayout winePanelLayout = new javax.swing.GroupLayout(winePanel);
        winePanel.setLayout(winePanelLayout);
        winePanelLayout.setHorizontalGroup(
            winePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(winePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(winePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(wineFilterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        winePanelLayout.setVerticalGroup(
            winePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(winePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wineFilterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPanel.addTab(bundle.getString("wines"), winePanel); // NOI18N

        CellarTable.setModel(cellarTableModel);
        CellarTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(CellarTable);

        javax.swing.GroupLayout cellarPanelLayout = new javax.swing.GroupLayout(cellarPanel);
        cellarPanel.setLayout(cellarPanelLayout);
        cellarPanelLayout.setHorizontalGroup(
            cellarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cellarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 904, Short.MAX_VALUE)
                .addContainerGap())
        );
        cellarPanelLayout.setVerticalGroup(
            cellarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cellarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPanel.addTab(bundle.getString("cellars"), cellarPanel); // NOI18N

        wineMenu.setText(bundle.getString("wine")); // NOI18N

        createWineMenuItem.setText(bundle.getString("create")); // NOI18N
        createWineMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createWineMenuItemActionPerformed(evt);
            }
        });
        wineMenu.add(createWineMenuItem);

        deleteWineMenuItem.setText(bundle.getString("delete")); // NOI18N
        deleteWineMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteWineMenuItemActionPerformed(evt);
            }
        });
        wineMenu.add(deleteWineMenuItem);

        putWineToCellarMenuItem.setText(bundle.getString("putToCellar")); // NOI18N
        putWineToCellarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                putWineToCellarMenuItemActionPerformed(evt);
            }
        });
        wineMenu.add(putWineToCellarMenuItem);

        removeFromCellarMenuItem.setText(bundle.getString("removeFromCellar")); // NOI18N
        removeFromCellarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromCellarMenuItemActionPerformed(evt);
            }
        });
        wineMenu.add(removeFromCellarMenuItem);
        wineMenu.add(jSeparator1);

        loadAllWinesMenuItem.setText(bundle.getString("loadWines")); // NOI18N
        loadAllWinesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadAllWinesMenuItemActionPerformed(evt);
            }
        });
        wineMenu.add(loadAllWinesMenuItem);

        menuBar.add(wineMenu);

        cellarMenu.setText(bundle.getString("cellar")); // NOI18N

        createCellarMenuItem.setText(bundle.getString("create")); // NOI18N
        createCellarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createCellarMenuItemActionPerformed(evt);
            }
        });
        cellarMenu.add(createCellarMenuItem);

        deleteCellarMenuItem.setText(bundle.getString("delete")); // NOI18N
        deleteCellarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCellarMenuItemActionPerformed(evt);
            }
        });
        cellarMenu.add(deleteCellarMenuItem);
        cellarMenu.add(jSeparator2);

        findWinesMenuItem.setText(bundle.getString("findWines")); // NOI18N
        findWinesMenuItem.setToolTipText("");
        findWinesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findWinesMenuItemActionPerformed(evt);
            }
        });
        cellarMenu.add(findWinesMenuItem);

        loadAllCellarsMenuItem.setText(bundle.getString("loadCellars")); // NOI18N
        loadAllCellarsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadAllCellarsMenuItemActionPerformed(evt);
            }
        });
        cellarMenu.add(loadAllCellarsMenuItem);

        menuBar.add(cellarMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPanel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabbedPanel)
                .addContainerGap())
        );

        tabbedPanel.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //swingworker for database connection // DONE
    private void createWineMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createWineMenuItemActionPerformed
        JTextField name = new JTextField();
        SpinnerNumberModel model = new SpinnerNumberModel((short) Calendar.getInstance().get(Calendar.YEAR), (short) 1700, (short) Calendar.getInstance().get(Calendar.YEAR), (short) 1);
        JSpinner vintage = new JSpinner(model);
        SpinnerNumberModel model2 = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        JSpinner quantity = new JSpinner(model2);
        String[] wineTypes = {"RED", "WHITE", "ROSE"};
        JComboBox type = new JComboBox(wineTypes);

        List<String> myList = new ArrayList<String>();
        for (String countryCode : Locale.getISOCountries()) {

            Locale obj = new Locale("", countryCode);
            myList.add(obj.getDisplayCountry(Locale.ENGLISH));
        }
        String[] x = new String[myList.size()];
        myList.toArray(x);
        JComboBox countryList = new JComboBox(x);

        Object[] message = {
            "Name:", name,
            "Country:", countryList,
            "Vintage:", vintage,
            "Quantity", quantity,
            "Type", type,};

        int option = JOptionPane.showConfirmDialog(null, message, "New Wine", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            if (name.getText().length() == 0) {
                JOptionPane.showConfirmDialog(null, langResource.getString("errorName"), langResource.getString("errorName"), JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
                createWineMenuItemActionPerformed(null);
                return;
            }
            new CreateWineSwingWorker(newWine(name.getText(), (String) countryList.getSelectedItem(), (short) ((int) vintage.getValue()), (int) quantity.getValue(), WineType.valueOf((String) type.getSelectedItem()))).execute();
        }
    }//GEN-LAST:event_createWineMenuItemActionPerformed

    //done
    private class CreateWineSwingWorker extends SwingWorker<Void, Void> {

        private Wine wine;

        public CreateWineSwingWorker(Wine wine) {
            this.wine = wine;
        }

        @Override
        protected Void doInBackground() throws Exception {
            wineManager.createWine(wine);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                wineTableModel.addWine(wine, null);
                log.debug("Created wine " + wine);
            } catch (InterruptedException | ExecutionException ex) {
                log.error("Error when creating wine " + wine, ex);
                JOptionPane.showMessageDialog(rootPane, langResource.getString("createWineError"), langResource.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
            createWineMenuItem.setEnabled(true);
        }
    }

    //problem in wineTableModel.reloadWines() swingworker for database connection // DONE
    private void deleteWineMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteWineMenuItemActionPerformed
        if (WineTable.getSelectedRow() == -1 ) {
            JOptionPane.showConfirmDialog(null, langResource.getString("notSelectedWineMessage"), langResource.getString("error"), JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        Wine wineToDelete = wineTableModel.getWineAtRow(WineTable.getSelectedRow());
        Object[] message = { langResource.getString("deleteWineMessage"), wineToDelete.toString() };

        int option = JOptionPane.showConfirmDialog(null, message, langResource.getString("deleteWine"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            new DeleteWineSwingWorker((wineToDelete)).execute();
        }
    }//GEN-LAST:event_deleteWineMenuItemActionPerformed

    //done
    private class DeleteWineSwingWorker extends SwingWorker<Void, Void> {

        private Wine wine;

        public DeleteWineSwingWorker(Wine wine) {
            this.wine = wine;
        }

        @Override
        protected Void doInBackground() throws Exception {
            wineManager.deleteWine(wine);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                wineTableModel.removeWine(wine);
                log.debug("Wine deleted:" + wine);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error when deleting cellar " + wine, e);
                JOptionPane.showMessageDialog(null, langResource.getString("deleteWineError"), langResource.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //swingworker for database connection // DONE
    private void putWineToCellarMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_putWineToCellarMenuItemActionPerformed
        if (WineTable.getSelectedRow() == -1 || WineTable.getSelectedRow() >= WineTable.getRowCount()) {
            JOptionPane.showConfirmDialog(null, "No wine selected", "Error", JOptionPane.DEFAULT_OPTION);
            return;
        }

        if(!(WineTable.getValueAt(WineTable.getSelectedRow(), 5) == null)){
            JOptionPane.showConfirmDialog(null, "Wine is already in cellar.", "Error", JOptionPane.DEFAULT_OPTION);
            return;
        }

        new PutWineToCellarCreateCellarList().execute();

    }//GEN-LAST:event_putWineToCellarMenuItemActionPerformed

    private class PutWineToCellarCreateCellarList extends SwingWorker<Void, Void> {

        List<Cellar> cellarList = new ArrayList<Cellar>();

        @Override
        protected Void doInBackground() throws Exception {
            cellarList = cellarManager.findAllCellars();
            return null;
        }

        @Override
        protected void done() {
                JComboBox cellars = new JComboBox();
                for (Cellar x : cellarList) {
                    cellars.addItem(x);
                }
                Object[] message = {
                    "Choose cellar", cellars

                };
                int option = JOptionPane.showConfirmDialog(null, message, "Put to cellar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    new PutWineToCellarSwingWorker(wineTableModel.getWineAtRow(WineTable.getSelectedRow()), (Cellar) cellars.getSelectedItem()).execute();
                }
        }
    }
    //done with problems // DONE
    private class PutWineToCellarSwingWorker extends SwingWorker<Void, Void> {

        private Wine wine;
        private Cellar cellar;

        public PutWineToCellarSwingWorker(Wine wine, Cellar cellar) {
            this.wine = wine;
            this.cellar = cellar;
        }

        @Override
        protected Void doInBackground() throws Exception {
            wineCellarManager.putWineInCellar(wine, cellar);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
               // wineTableModel.fireTableDataChanged();
                wineTableModel.updateWinesCellar(wine, cellar);
                //new LoadAllWinesSwingWorker().execute();
                log.debug("Inserted wine " + wine + "to cellar " + cellar);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error when inserting wine" + wine + "to cellar " + cellar, e);
                JOptionPane.showMessageDialog(rootPane, langResource.getString("putWineToCellarError"), langResource.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //done with problems //DONE
    private class RemoveWineFromCellarSwingWorker extends SwingWorker<Void, Void> {

        private Wine wine;
        private Cellar cellar;
        
        public RemoveWineFromCellarSwingWorker(Wine wine, Cellar cellar) {
            this.wine = wine;
            this.cellar = cellar;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            wineCellarManager.removeWineFromCellar(wine, cellar);
            return null;
        }
        
        @Override
        protected void done() {
            try {
                get();
                //wineTableModel.fireTableDataChanged();
                //new LoadAllWinesSwingWorker().execute();
                wineTableModel.updateWinesCellar(wine, null);
                log.debug("Wine " + wine + "removed from cellar " + cellar);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error when deleting " + wine + "from cellar" + cellar, e );
                JOptionPane.showMessageDialog(rootPane, langResource.getString("removeWineFromCellarError"), langResource.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    //swingworker for database connection // DONE //CHYBA
    private void removeFromCellarMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFromCellarMenuItemActionPerformed
        if (WineTable.getSelectedRow() == -1 || WineTable.getSelectedRow() >= WineTable.getRowCount()) {
            JOptionPane.showConfirmDialog(null, "No wine selected", "Error", JOptionPane.DEFAULT_OPTION);
            return;
        }

        Wine wine = wineTableModel.getWineAtRow(WineTable.getSelectedRow());
            new RemoveWineFromCellarSwingWorker(wine,(Cellar)WineTable.getValueAt(WineTable.getSelectedRow(), 5) ).execute();
    }//GEN-LAST:event_removeFromCellarMenuItemActionPerformed

    
    //done
    private class DeleteCellarSwingWorker extends SwingWorker<Void, Void> {

        private boolean isEmpty;
        private Cellar cellar;

        public DeleteCellarSwingWorker(Cellar cellar) {
            this.cellar = cellar;
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (wineCellarManager.findWinesInCellar(cellar).isEmpty()) {
                isEmpty = true;
                cellarManager.deleteCellar(cellar);
            } else {
                isEmpty = false;
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                if (isEmpty) {
                    cellarTableModel.removeCellar(cellar);
                    log.debug("Cellar deleted:" + cellar);
                } else {
                    JOptionPane.showMessageDialog(null, langResource.getString("notEmptyCellarMessage"), langResource.getString("error"), JOptionPane.WARNING_MESSAGE);
                    log.debug("Tried to delete not empty cellar: " + cellar);
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error when deleting cellar " + cellar, e);
                JOptionPane.showMessageDialog(null, langResource.getString("deleteCellarError"), langResource.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
            deleteCellarMenuItem.setEnabled(true);
        }
    }

    //done
    private void cellar_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cellar_cancelButtonActionPerformed
        cellarDialog.setVisible(false);
        createCellarMenuItem.setEnabled(true);
    }//GEN-LAST:event_cellar_cancelButtonActionPerformed

    //done
    private void cellar_createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cellar_createButtonActionPerformed
        CreateCellarSwingWorker createCellarSW;

        if (cellar_nameInput.getText().isEmpty()) {
            cellar_errorLabel.setText(langResource.getString("errorName"));
            return;
        }

        if (cellar_addressInput.getText().isEmpty()) {
            cellar_errorLabel.setText(langResource.getString("errorAddress"));
            return;
        }

        Cellar cellar = new Cellar();
        cellar.setName(cellar_nameInput.getText());
        cellar.setAddress(cellar_addressInput.getText());
        cellar.setWineCapacity((int) cellar_capacitySpinner.getValue());

        createCellarSW = new CreateCellarSwingWorker(cellar);
        createCellarSW.execute();
        cellarDialog.setVisible(false);
    }//GEN-LAST:event_cellar_createButtonActionPerformed

    //done
    private class CreateCellarSwingWorker extends SwingWorker<Void, Void> {

        private Cellar cellar;

        public CreateCellarSwingWorker(Cellar cellar) {
            this.cellar = cellar;
        }

        @Override
        protected Void doInBackground() throws Exception {
            cellarManager.createCellar(cellar);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                cellarTableModel.addCellar(cellar);
                log.debug("Created cellar " + cellar);
            } catch (InterruptedException | ExecutionException ex) {
                log.error("Error when creating cellar " + cellar, ex);
                JOptionPane.showMessageDialog(rootPane, langResource.getString("createCellarError"), langResource.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
            createCellarMenuItem.setEnabled(true);
        }
    }

    //done
    private void cellarDialogWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_cellarDialogWindowClosed
        createCellarMenuItem.setEnabled(true);
    }//GEN-LAST:event_cellarDialogWindowClosed

    //unused
    private void wineDialogWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_wineDialogWindowClosed
        createWineMenuItem.setEnabled(true);
    }//GEN-LAST:event_wineDialogWindowClosed

    //not using swingworker when conecting to database //DONE
    private void loadAllWinesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAllWinesMenuItemActionPerformed
        new LoadAllWinesSwingWorker().execute();
    }//GEN-LAST:event_loadAllWinesMenuItemActionPerformed

    //done
    private void loadAllCellarsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAllCellarsMenuItemActionPerformed
        loadAllCellarsMenuItem.setEnabled(false);
        LoadAllCellarsSwingWorker loadAllCellarsSW = new LoadAllCellarsSwingWorker();
        loadAllCellarsSW.execute();
    }//GEN-LAST:event_loadAllCellarsMenuItemActionPerformed

    //done
    private void findWinesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findWinesMenuItemActionPerformed

        FindWinesInCellarSwingWorker findWinesSW;

        if (CellarTable.getSelectedRow() == -1) {
            JOptionPane.showConfirmDialog(null, langResource.getString("notSelectedCellarMessage"), langResource.getString("error"), JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cellar cellar = cellarTableModel.getCellarAtRow(CellarTable.getSelectedRow());

        //aby sa nezobrazovalo v pripade ze pivnicaje prazdna
        //FilterLabel.setText(langResource.getString("winesFilter") + " " + cellar);
        //wineFilterPanel.setVisible(true);

        findWinesMenuItem.setEnabled(false);
        findWinesSW = new FindWinesInCellarSwingWorker(cellar);
        findWinesSW.execute();
    }//GEN-LAST:event_findWinesMenuItemActionPerformed

    //done
    private void deleteCellarMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCellarMenuItemActionPerformed
        DeleteCellarSwingWorker deleteCellarSW;

        if (CellarTable.getSelectedRow() == -1) {
            JOptionPane.showConfirmDialog(null, langResource.getString("notSelectedCellarMessage"), langResource.getString("error"), JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cellar cellar = cellarTableModel.getCellarAtRow(CellarTable.getSelectedRow());
        Object[] message = {langResource.getString("deleteCellarMessage"), cellar.toString()};
        int option = JOptionPane.showConfirmDialog(null, message, langResource.getString("deleteCellarTitle"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            deleteCellarMenuItem.setEnabled(false);
            deleteCellarSW = new DeleteCellarSwingWorker(cellar);
            deleteCellarSW.execute();
        }
    }//GEN-LAST:event_deleteCellarMenuItemActionPerformed

    //done
    private void createCellarMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createCellarMenuItemActionPerformed
        createCellarMenuItem.setEnabled(false);
        cellar_addressInput.setText("");
        cellar_nameInput.setText("");
        cellar_capacitySpinner.setValue(1);
        cellar_errorLabel.setText("");
        cellarDialog.pack();
        cellarDialog.setVisible(true);
    }//GEN-LAST:event_createCellarMenuItemActionPerformed

    //not using swingworker when conecting to database // DONE
    private void ResetFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetFilterButtonActionPerformed
        wineFilterPanel.setVisible(false);
        new LoadAllWinesSwingWorker().execute();
    }//GEN-LAST:event_ResetFilterButtonActionPerformed

    //done
    private class LoadAllCellarsSwingWorker extends SwingWorker<List<Cellar>, Void> {

        @Override
        protected List<Cellar> doInBackground() throws Exception {
            return cellarManager.findAllCellars();
        }

        @Override
        protected void done() {
            try {
                get();
                cellarTableModel.clear();
                for (Cellar c : get()) {
                    cellarTableModel.addCellar(c);
                }
                log.debug("All cellars listed.");
            } catch (InterruptedException | ExecutionException ex) {
                log.error("Error when loading cellars ", ex);
                JOptionPane.showMessageDialog(rootPane, langResource.getString("loadCellarError"), langResource.getString("Error"), JOptionPane.ERROR_MESSAGE);
            }
            loadAllCellarsMenuItem.setEnabled(true);
            tabbedPanel.setSelectedComponent(cellarPanel);
        }
    }

    //done
    private class LoadAllWinesSwingWorker extends SwingWorker<Void, Void> {

        private List<Wine> wines = new ArrayList<Wine>();
        private List<Cellar> cellars = new ArrayList<Cellar>();
        
        @Override
        protected Void doInBackground() throws Exception {
            wines =  wineManager.findAllWines();
            for (int i = 0; i < wines.size(); i++) {
                    cellars.add(wineCellarManager.findCellarWithWine(wines.get(i)));
		}
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                wineTableModel.clearWines();
                for (int i = 0; i < wines.size(); i++) {
                wineTableModel.addWine(wines.get(i), cellars.get(i));
                }
                wineFilterPanel.setVisible(false);
                log.debug("All wines listed.");
            } catch (InterruptedException | ExecutionException ex) {
                log.error("Error when loading wines ", ex);
                JOptionPane.showMessageDialog(rootPane, langResource.getString("loadWineError"), langResource.getString("Error"), JOptionPane.ERROR_MESSAGE);
            }
            loadAllWinesMenuItem.setEnabled(true);
        tabbedPanel.setSelectedComponent(winePanel);
        }
    }
    
    //done
    private class FindWinesInCellarSwingWorker extends SwingWorker<List<Wine>, Void> {

        Cellar cellar;

        public FindWinesInCellarSwingWorker(Cellar cellar) {
            this.cellar = cellar;
        }

        @Override
        protected List<Wine> doInBackground() throws Exception {
            return wineCellarManager.findWinesInCellar(cellar);
        }

        @Override
        protected void done() {
            try {
                List<Wine> list = get();

                if (list.isEmpty()) {
                    log.debug("Cellar empty when listing wines in cellar " + cellar);
                    JOptionPane.showMessageDialog(rootPane, langResource.getString("emptyCellarMessage"), langResource.getString("cellars"), JOptionPane.PLAIN_MESSAGE);
                } else {
                    wineTableModel.clearWines();
                    for (Wine wine : list) {
                        wineTableModel.addWine(wine, cellar);
                    }
                    FilterLabel.setText(langResource.getString("winesFilter") + " " + cellar);
                    wineFilterPanel.setVisible(true);
                    log.debug("Listed wines from cellar" + cellar);
                    tabbedPanel.setSelectedComponent(winePanel);
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error when listing listing in cellar " + cellar, e);
            }
            findWinesMenuItem.setEnabled(true);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable CellarTable;
    private javax.swing.JLabel FilterLabel;
    private javax.swing.JButton ResetFilterButton;
    private javax.swing.JTable WineTable;
    private javax.swing.JDialog cellarDialog;
    private javax.swing.JMenu cellarMenu;
    private javax.swing.JPanel cellarPanel;
    private javax.swing.JTextField cellar_addressInput;
    private javax.swing.JLabel cellar_addressLabel;
    private javax.swing.JButton cellar_cancelButton;
    private javax.swing.JLabel cellar_capacityLabel;
    private javax.swing.JSpinner cellar_capacitySpinner;
    private javax.swing.JButton cellar_createButton;
    private javax.swing.JLabel cellar_errorLabel;
    private javax.swing.JTextField cellar_nameInput;
    private javax.swing.JLabel cellar_nameLabel;
    private javax.swing.JMenuItem createCellarMenuItem;
    private javax.swing.JMenuItem createWineMenuItem;
    private javax.swing.JMenuItem deleteCellarMenuItem;
    private javax.swing.JMenuItem deleteWineMenuItem;
    private javax.swing.JMenuItem findWinesMenuItem;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenuItem loadAllCellarsMenuItem;
    private javax.swing.JMenuItem loadAllWinesMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem putWineToCellarMenuItem;
    private javax.swing.JMenuItem removeFromCellarMenuItem;
    private javax.swing.JTabbedPane tabbedPanel;
    private javax.swing.JDialog wineDialog;
    private javax.swing.JPanel wineFilterPanel;
    private javax.swing.JMenu wineMenu;
    private javax.swing.JPanel winePanel;
    private javax.swing.JButton wine_cancelButton;
    private javax.swing.JTextField wine_countryInput;
    private javax.swing.JLabel wine_countryLabel;
    private javax.swing.JButton wine_createButton;
    private javax.swing.JLabel wine_errorLabel;
    private javax.swing.JTextField wine_nameInput;
    private javax.swing.JLabel wine_nameLabel;
    private javax.swing.JSpinner wine_quantityInput;
    private javax.swing.JLabel wine_quantityLabel;
    private javax.swing.JComboBox wine_typeInput;
    private javax.swing.JLabel wine_typeLabel;
    private javax.swing.JSpinner wine_vintageInput;
    private javax.swing.JLabel wine_vintageLabel;
    // End of variables declaration//GEN-END:variables
}
