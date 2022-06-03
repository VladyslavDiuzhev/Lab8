package client.gui.main;

import client.gui.auth.LoginView;
import client.gui.parts.CreationPanel;
import client.gui.parts.DrawPanel;
import core.essentials.FuelType;
import core.essentials.Vehicle;
import core.essentials.VehicleType;
import core.interact.Message;
import core.interact.NetInteractor;
import core.precommands.BasicPrecommand;
import core.precommands.IdPrecommand;
import core.precommands.Precommand;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainView extends JFrame {
    private JTable vehicleTable;
    private JPanel contentPanel;
    private JPanel mainPanel;
    private JPanel cardPanel;
    private DrawPanel drawPanel;
    private JPanel instrumentPanel;

    private JPanel topPanel;
    private CreationPanel creationPanel;
    private JPanel choosingPanel;

    private TableRowSorter<DefaultTableModel> tableRowSorter;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private boolean updateRequest = false;

    private boolean mapSelected = false;
    private String username;
    private ResourceBundle resourceBundle;


    public MainView(NetInteractor netInteractor, String username, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.username = username;
        this.objectInputStream = netInteractor.getDataInputStream();
        this.objectOutputStream = netInteractor.getDataOutputStream();

        startWindow();
        startBackground();
    }

    private void updateTableAdd(Vehicle vehicle) {
        DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();
        model.addRow(new Object[]{vehicle.getId(), vehicle.getCreationDate(),
                vehicle.getName(), vehicle.getType(), vehicle.getFuelType(), vehicle.getEnginePower(),
                vehicle.getCoordinates().getX(), vehicle.getCoordinates().getY(),
                vehicle.getOwnerId()});
    }

    private void updateTableDel(int id) {
        DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equals(Integer.toString(id))) {
                model.removeRow(i);
                break;
            }
        }
    }

    private void updateTableUpdate(Vehicle vehicle) {
        DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (vehicle.getId().equals(model.getValueAt(i, 0))) {
                model.insertRow(i, new Object[]{vehicle.getId(), vehicle.getCreationDate(),
                        vehicle.getName(), vehicle.getType(), vehicle.getFuelType(), vehicle.getEnginePower(),
                        vehicle.getCoordinates().getX(), vehicle.getCoordinates().getY(),
                        vehicle.getOwnerId()});
                model.removeRow(i + 1);
                return;
            }
        }
        JOptionPane.showMessageDialog(mainPanel, resourceBundle.getString("unable_update"));
    }

    private void startBackground() {
        SwingWorker<Message, Message> swingWorker = new SwingWorker<Message, Message>() {
            @Override
            protected Message doInBackground() throws Exception {
                Message msg = null;
                if (!isCancelled()) {
                    msg = (Message) objectInputStream.readObject();
                }
                return msg;
            }

            @Override
            protected void done() {
                try {
                    Message msg = get();
                    if (msg != null && msg.isSuccessful()) {
                        Vehicle vehicle;
                        switch (msg.getType()) {
                            case "ADD":
                                vehicle = (Vehicle) msg.getObject();
                                updateTableAdd(vehicle);
                                creationPanel.dataGotten();
                                drawPanel.addObjectOut(vehicle.getId(), vehicle.getOwnerId(),
                                        Math.toIntExact(vehicle.getCoordinates().getX()),
                                        ((Double) vehicle.getCoordinates().getY()).intValue());
                                break;
                            case "DEL":
                                updateTableDel((int) msg.getObject());
                                drawPanel.deleteObject((int) msg.getObject());
                                break;
                            case "UPDATE":
                                vehicle = (Vehicle) msg.getObject();
                                updateTableUpdate(vehicle);
                                creationPanel.dataGotten();
                                drawPanel.addObjectOut(vehicle.getId(), vehicle.getOwnerId(),
                                        Math.toIntExact(vehicle.getCoordinates().getX()),
                                        ((Double) vehicle.getCoordinates().getY()).intValue());
                                break;
                        }
                    } else {
                        if (msg != null) {
                            if (msg.getType() != null && msg.getType().equals("UPDATE")) {
                                creationPanel.setCreateForm();
                            }
                            JOptionPane.showMessageDialog(mainPanel, resourceBundle.getString(msg.getText()));
                        } else {
                            JOptionPane.showMessageDialog(mainPanel, resourceBundle.getString("req_err"));
                        }
                    }
                } catch (InterruptedException | ExecutionException | ClassCastException e) {
                    JOptionPane.showMessageDialog(mainPanel, resourceBundle.getString("serv_conn_err"));
                    setVisible(false);
                    new LoginView();
                    dispose();
                    return;
                }
                startBackground();
            }
        };
        swingWorker.execute();

    }

    private void startWindow() {
        setupChoosingPanel();
        setContentPane(choosingPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setLocation(400, 400);
        setMinimumSize(new Dimension(1000, 500));
        setResizable(false);
    }

    private void setupChoosingPanel() {
        setupContentPanel();
        setupTopPanel();
        choosingPanel = new JPanel();
        choosingPanel.setLayout(new BoxLayout(choosingPanel, BoxLayout.Y_AXIS));
        choosingPanel.add(topPanel);
        choosingPanel.add(contentPanel);
    }

    private void setupContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        setupCardPanel();
        setupCreationPanel();
        contentPanel.add(cardPanel);
        contentPanel.add(creationPanel);
    }

    private void setupCardPanel() {
        setupMainPanel();
        setupDrawPanel();
        cardPanel = new JPanel(new CardLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        cardPanel.add(mainPanel, "MAIN");
        cardPanel.add(drawPanel, "DRAW");
    }

    private void setupDrawPanel() {
        Stack<int[]> stack = new Stack<>();
        for (int i = 0; i < vehicleTable.getColumnCount(); i++) {
            try {
                stack.add(new int[]{(int) vehicleTable.getModel().getValueAt(i, 0),
                        (int) vehicleTable.getModel().getValueAt(i, 8),
                        Math.toIntExact((Long) vehicleTable.getModel().getValueAt(i, 6)),
                        ((Double) vehicleTable.getModel().getValueAt(i, 7)).intValue()});
            } catch (Exception ignored) {
            }

        }
        drawPanel = new DrawPanel(stack, this, resourceBundle);
    }

    private void setupMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setupCollectionTable();
        setupInstrumentPanel();
        mainPanel.add(instrumentPanel);
        mainPanel.add(new JScrollPane(vehicleTable));
    }

    private void setupInstrumentPanel() {
        instrumentPanel = new JPanel(new GridLayout(1, 3));
        instrumentPanel.setMaximumSize(new Dimension(500, 30));
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setEditable(false);
        for (int i = 0; i < vehicleTable.getColumnCount(); i++) {
            comboBox.addItem(vehicleTable.getColumnName(i));
        }

        JTextField filterString = new JTextField();
        filterString.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            private void warn() {
                RowFilter<DefaultTableModel, Object> rf = null;
                //If current expression doesn't parse, don't update.
                try {
                    rf = RowFilter.regexFilter("^" + filterString.getText(), comboBox.getSelectedIndex());
                } catch (java.util.regex.PatternSyntaxException ex) {
                    // JOptionPane.showMessageDialog(mainPanel, "Unable");
                }
                tableRowSorter.setRowFilter(rf);
            }
        });

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RowFilter<DefaultTableModel, Object> rf = null;
                //If current expression doesn't parse, don't update.
                try {
                    rf = RowFilter.regexFilter("^" + filterString.getText(), comboBox.getSelectedIndex());
                } catch (java.util.regex.PatternSyntaxException ex) {
                    // JOptionPane.showMessageDialog(mainPanel, "Unable");
                }
                tableRowSorter.setRowFilter(rf);
            }
        });

        instrumentPanel.add(new JLabel(resourceBundle.getString("filter") + ":"));
        instrumentPanel.add(comboBox);
        instrumentPanel.add(filterString);
    }

    private void setupTopPanel() {
        topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(400, 40));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        JButton drawButton = new JButton(resourceBundle.getString("map"));
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!mapSelected) {
                    CardLayout cl = (CardLayout) (cardPanel.getLayout());
                    cl.next(cardPanel);
                    mapSelected = true;
                }
            }
        });
        JButton tableButton = new JButton(resourceBundle.getString("table"));
        tableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mapSelected) {
                    CardLayout cl = (CardLayout) (cardPanel.getLayout());
                    cl.next(cardPanel);
                    mapSelected = false;
                }
            }
        });
        JButton showCreate = new JButton(resourceBundle.getString("add_upd"));
        showCreate.setHorizontalAlignment(SwingConstants.LEFT);
        drawButton.setHorizontalAlignment(SwingConstants.LEFT);
        tableButton.setHorizontalAlignment(SwingConstants.LEFT);
        showCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                creationPanel.setVisible(!creationPanel.isVisible());
            }
        });
        drawButton.setMargin(new Insets(0, 0, 0, 0));
        showCreate.setMargin(new Insets(0, 0, 0, 0));
        tableButton.setMargin(new Insets(0, 0, 0, 0));


        topPanel.add(drawButton);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(tableButton);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(showCreate);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel(resourceBundle.getString("user") + ": " + username));
        topPanel.add(Box.createHorizontalStrut(20));
//        topPanel.add(languageBox);
    }

    private void setupCreationPanel() {
        creationPanel = new CreationPanel(mainPanel, objectOutputStream, resourceBundle);
    }

    public void deleteObject(String id) {
        try {
            IdPrecommand removePrecommand = new IdPrecommand("remove_by_id", id);
            objectOutputStream.writeObject(removePrecommand);
        } catch (IOException | IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(mainPanel, resourceBundle.getString("gen_err"));
        }
    }

    public void updateObject(String id) {
        DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();
        int ind = -1;
        for (int i = 0; i < vehicleTable.getRowCount(); i++) {
            if ((Integer) model.getValueAt(i, 0) == Integer.parseInt(id)) {
                ind = i;
                break;
            }
        }
        Vehicle vehicle = new Vehicle();
        vehicle.generateId((Integer) model.getValueAt(ind, 0));
        vehicle.setCreationDate((ZonedDateTime) model.getValueAt(ind, 1));
        try {
            vehicle.setName((String) model.getValueAt(ind, 2));
            vehicle.setType((VehicleType) model.getValueAt(ind, 3));
            vehicle.setFuelType((FuelType) model.getValueAt(ind, 4));
            vehicle.setEnginePower((Float) model.getValueAt(ind, 5));
            vehicle.setCoordinates((Long) model.getValueAt(ind, 6), (Double) model.getValueAt(ind, 7));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainPanel, resourceBundle.getString("gen_err"));
            return;
        }
        creationPanel.setUpdateForm(vehicle);
    }

    private void setupCollectionTable() {
        String[] columnsHeader = new String[]{"id", resourceBundle.getString("creat_date"),
                resourceBundle.getString("naming"), resourceBundle.getString("type"),
                resourceBundle.getString("fuel"), resourceBundle.getString("power"),
                resourceBundle.getString("x_cord"), resourceBundle.getString("y_cord"),
                resourceBundle.getString("owner")};
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() == 0) {
                    return super.getColumnClass(columnIndex);
                }
                return getValueAt(0, columnIndex).getClass();
            }
        };

        vehicleTable = new JTable(model);
        vehicleTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        for (String field : columnsHeader) {
            model.addColumn(field);
        }
        vehicleTable.getTableHeader().setResizingAllowed(false);

        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem(resourceBundle.getString("delete"));
        JMenuItem updateItem = new JMenuItem(resourceBundle.getString("update"));

        updateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();
                int ind = vehicleTable.getRowSorter().convertRowIndexToModel(vehicleTable.getSelectedRow());
                updateObject(model.getValueAt(ind, 0).toString());
            }
        });
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();
                deleteObject(model.getValueAt(vehicleTable.getRowSorter().convertRowIndexToModel(vehicleTable.getSelectedRow()), 0).toString());
            }
        });
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = vehicleTable.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), vehicleTable));
                        if (rowAtPoint > -1) {
                            vehicleTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                        }
                    }
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // TODO Auto-generated method stub

            }
        });
        popupMenu.add(deleteItem);
        popupMenu.add(updateItem);
        vehicleTable.setComponentPopupMenu(popupMenu);
        vehicleTable.getTableHeader().setReorderingAllowed(false);
        tableRowSorter = new TableRowSorter<>((DefaultTableModel) vehicleTable.getModel());

        vehicleTable.setRowSorter(tableRowSorter);


        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        for (int i = 0; i < vehicleTable.getColumnCount(); i++) {
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        }
        tableRowSorter.setSortKeys(sortKeys);

        try {
            Precommand precommand = new BasicPrecommand("show");
            this.objectOutputStream.writeObject(precommand);
            Message msg = (Message) this.objectInputStream.readObject();
            Stack<Vehicle> stack = new Stack<>();
            if (Objects.equals(msg.getType(), "ALL")) {
                stack = (Stack<Vehicle>) msg.getObject();
            }
            for (Vehicle vehicle : stack) {
                model.addRow(new Object[]{vehicle.getId(), vehicle.getCreationDate(),
                        vehicle.getName(), vehicle.getType(), vehicle.getFuelType(), vehicle.getEnginePower(),
                        vehicle.getCoordinates().getX(), vehicle.getCoordinates().getY(),
                        vehicle.getOwnerId()});
            }
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            JOptionPane.showMessageDialog(mainPanel, resourceBundle.getString("serv_conn_err"));
        }
    }
}
