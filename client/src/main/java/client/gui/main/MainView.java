package client.gui.main;

import client.argcreation.VehicleGenerator;
import core.essentials.FuelType;
import core.essentials.Vehicle;
import core.essentials.VehicleType;
import core.interact.Message;
import core.interact.NetInteractor;
import core.precommands.BasicPrecommand;
import core.precommands.ObjectPrecommand;
import core.precommands.Precommand;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

public class MainView extends JFrame {
    private JTable vehicleTable;
    private JPanel mainPanel;

    private Stack<Vehicle> collection = new Stack<>();
    private Stack<Object[]> current_view = new Stack<>();

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public MainView(NetInteractor netInteractor) {
        this.objectInputStream = netInteractor.getDataInputStream();
        this.objectOutputStream = netInteractor.getDataOutputStream();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2, 0, 0));
        setupCollectionTable();
        mainPanel.add(new JScrollPane(vehicleTable));
        JPanel creationPanel = setupCreationPanel();
        mainPanel.add(creationPanel);
        startWindow();
        startBackground();
    }

    private void updateTableAdd(Vehicle vehicle){
        collection.add(vehicle);
        DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();
        model.addRow(new String[]{vehicle.getId().toString(), vehicle.getCreationDate().toString(),
                vehicle.getName(), vehicle.getType().toString(), vehicle.getFuelType().toString(), Float.toString(vehicle.getEnginePower()),
                vehicle.getCoordinates().getX().toString(), Double.toString(vehicle.getCoordinates().getY()),
                Integer.toString(vehicle.getOwnerId())});
        current_view.add(new String[]{vehicle.getId().toString(), vehicle.getCreationDate().toString(),
                vehicle.getName(), vehicle.getType().toString(), vehicle.getFuelType().toString(), Float.toString(vehicle.getEnginePower()),
                vehicle.getCoordinates().getX().toString(), Double.toString(vehicle.getCoordinates().getY()),
                Integer.toString(vehicle.getOwnerId())});
    }

    private void startBackground() {
        SwingWorker<Message, Message> swingWorker = new SwingWorker<Message, Message>() {
            @Override
            protected Message doInBackground() throws Exception {
                Message msg = null;
                if (!isCancelled()) {
                    msg = (Message) objectInputStream.readObject();
                    publish(msg);
                    if (getProgress() != 5) {
                        setProgress(5);
                    } else {
                        setProgress(4);
                    }
                }
                return msg;
            }

            @Override
            protected void done() {
                try {
                    Message msg = get();
                    if (msg != null && msg.isSuccessful()) {
                        if (msg.getType().equals("ADD")) {
                            updateTableAdd((Vehicle) msg.getObject());
                        }
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "Ошибка при обработке запроса!");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(mainPanel, "Ошибка соединения с сервером!");
                }
                startBackground();
            }
        };
        swingWorker.execute();

    }

    private void startWindow() {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setLocation(400, 400);
        setMaximumSize(new Dimension(1000, 400));
        setMinimumSize(new Dimension(1000, 400));
    }

    private JPanel setupCreationPanel() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new GridLayout(16, 1, 0, 0));

        addPanel.add(new JLabel("Создание нового объекта"));

        addPanel.add(new JLabel("Тип транспорта:"));
        JComboBox<String> jComboBoxType = new JComboBox<>();
        for (VehicleType vehicleType : VehicleType.values()) {
            jComboBoxType.addItem(vehicleType.toString());
        }
        addPanel.add(jComboBoxType);

        addPanel.add(new JLabel("Тип топлива:"));
        JComboBox<String> jComboBoxFuelType = new JComboBox<>();
        for (FuelType fuelType : FuelType.values()) {
            jComboBoxFuelType.addItem(fuelType.toString());
        }
        addPanel.add(jComboBoxFuelType);

        addPanel.add(new JLabel("Название:"));
        JTextField nameTextField = new JTextField();
        addPanel.add(nameTextField);

        addPanel.add(new JLabel("Мощность:"));
        JTextField powerTextField = new JTextField();
        addPanel.add(powerTextField);

        addPanel.add(new JLabel("X-координата:"));
        JTextField xCordTextField = new JTextField();
        addPanel.add(xCordTextField);

        addPanel.add(new JLabel("Y-координата:"));
        JTextField yCordTextField = new JTextField();
        addPanel.add(yCordTextField);

        addPanel.add(new JLabel("Условие минимальности:"));
        JCheckBox checkBox = new JCheckBox();
        addPanel.add(checkBox);

        JButton createButton = new JButton("Создать");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ObjectPrecommand addPrecommand;
                if (checkBox.isSelected()) {
                    addPrecommand = new ObjectPrecommand("add_if_min");
                } else {
                    addPrecommand = new ObjectPrecommand("add");
                }

                try {
                    addPrecommand.preprocess(VehicleGenerator.createVehicle((String) jComboBoxType.getSelectedItem(), (String) jComboBoxFuelType.getSelectedItem(),
                            nameTextField.getText(), powerTextField.getText(),
                            xCordTextField.getText(), yCordTextField.getText()), false);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(mainPanel, "Данные не валидны! " + exception.getMessage());
                    return;
                }

                try {
                    objectOutputStream.writeObject(addPrecommand);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Не удалось оправить команду.");
                }
            }
        });
        addPanel.add(createButton);
        return addPanel;
    }

    private void setupCollectionTable() {
        String[] columnsHeader = new String[]{"id", "Дата создания",
                "Название", "Вид", "Топливо", "Мощность", "X-координата", "Y-координата", "Владелец"};
        DefaultTableModel model = new DefaultTableModel();
        vehicleTable = new JTable(model);
        for (String field : columnsHeader) {
            model.addColumn(field);
        }
        try {
            Precommand precommand = new BasicPrecommand("show");
            this.objectOutputStream.writeObject(precommand);
            Message msg = (Message) this.objectInputStream.readObject();
            if (Objects.equals(msg.getType(), "ALL")) {
                this.collection = (Stack<Vehicle>) msg.getObject();
            }
            for (Vehicle vehicle : this.collection) {
                this.current_view.add(new String[]{vehicle.getId().toString(), vehicle.getCreationDate().toString(),
                        vehicle.getName(), vehicle.getType().toString(), vehicle.getFuelType().toString(), Float.toString(vehicle.getEnginePower()),
                        vehicle.getCoordinates().getX().toString(), Double.toString(vehicle.getCoordinates().getY()),
                        Integer.toString(vehicle.getOwnerId())});
                model.addRow(new String[]{vehicle.getId().toString(), vehicle.getCreationDate().toString(),
                        vehicle.getName(), vehicle.getType().toString(), vehicle.getFuelType().toString(), Float.toString(vehicle.getEnginePower()),
                        vehicle.getCoordinates().getX().toString(), Double.toString(vehicle.getCoordinates().getY()),
                        Integer.toString(vehicle.getOwnerId())});
            }
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            JOptionPane.showMessageDialog(mainPanel, "Ошибка соединения с сервером!");
        }
    }
}
