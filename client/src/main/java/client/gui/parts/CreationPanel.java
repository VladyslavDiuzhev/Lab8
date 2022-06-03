package client.gui.parts;

import client.argcreation.VehicleGenerator;
import core.essentials.FuelType;
import core.essentials.Vehicle;
import core.essentials.VehicleType;
import core.precommands.ObjectIdPrecommand;
import core.precommands.ObjectPrecommand;
import core.precommands.Precommand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ResourceBundle;

public class CreationPanel extends JPanel {

    private JTextField nameTextField;
    private JTextField powerTextField;
    private JTextField xCordTextField;
    private JTextField yCordTextField;
    private JLabel formLabel;
    private JButton createButton;
    private JCheckBox checkBox;
    private JComboBox<String> jComboBoxType;
    private JComboBox<String> jComboBoxFuelType;
    private boolean updateRequest = false;
    private boolean updateForm = false;
    private int updateId;
    private ResourceBundle resourceBundle;

    public CreationPanel(JPanel mainPanel, ObjectOutputStream objectOutputStream, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        setLayout(new GridLayout(16, 1, 0, 0));
        setMinimumSize(new Dimension(200, 450));
        setPreferredSize(new Dimension(300, 450));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
//        JButton close = new JButton("-");
//        close.setMargin(new Insets(0, 0, 0, 0));
//        close.setHorizontalAlignment(SwingConstants.CENTER);
//        close.setVerticalAlignment(SwingConstants.CENTER);
//        close.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                setVisible(false);
//            }
//        });
//        add(close);

        formLabel = new JLabel(resourceBundle.getString("creat_title"));
        add(formLabel);

        add(new JLabel(resourceBundle.getString("type")+":"));
        jComboBoxType = new JComboBox<>();
        for (VehicleType vehicleType : VehicleType.values()) {
            jComboBoxType.addItem(vehicleType.toString());
        }
        add(jComboBoxType);

        add(new JLabel(resourceBundle.getString("fuel")+":"));
        jComboBoxFuelType = new JComboBox<>();
        for (FuelType fuelType : FuelType.values()) {
            jComboBoxFuelType.addItem(fuelType.toString());
        }
        add(jComboBoxFuelType);

        add(new JLabel(resourceBundle.getString("naming")+":"));
        nameTextField = new JTextField();
        add(nameTextField);

        add(new JLabel(resourceBundle.getString("power")+":"));
        powerTextField = new JTextField();
        add(powerTextField);

        add(new JLabel(resourceBundle.getString("x_cord")+":"));
        xCordTextField = new JTextField();
        add(xCordTextField);

        add(new JLabel(resourceBundle.getString("y_cord")+":"));
        yCordTextField = new JTextField();
        add(yCordTextField);

        add(new JLabel(resourceBundle.getString("min_cond")+":"));
        checkBox = new JCheckBox();
        add(checkBox);

        createButton = new JButton(resourceBundle.getString("create"));
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Precommand addPrecommand;
                if (updateForm) {
                    addPrecommand = new ObjectIdPrecommand("update", Integer.toString(updateId));
                } else {
                    if (checkBox.isSelected()) {
                        addPrecommand = new ObjectPrecommand("add_if_min");
                    } else {
                        addPrecommand = new ObjectPrecommand("add");
                    }
                }

                try {
                    addPrecommand.preprocess(VehicleGenerator.createVehicle((String) jComboBoxType.getSelectedItem(), (String) jComboBoxFuelType.getSelectedItem(),
                            nameTextField.getText(), powerTextField.getText(),
                            xCordTextField.getText(), yCordTextField.getText()), false);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(mainPanel, resourceBundle.getString("inv_format")+" " + exception.getMessage());
                    return;
                }

                try {
                    objectOutputStream.writeObject(addPrecommand);
                    updateRequest = true;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(mainPanel, resourceBundle.getString("gen_err"));
                }
            }
        });
        add(createButton);
    }

    public void setUpdateRequest(boolean updateRequest) {
        this.updateRequest = updateRequest;
    }

    public void dataGotten() {
        if (updateRequest) {
            if (updateForm) {
                setCreateForm();
            } else {
                nameTextField.setText("");
                powerTextField.setText("");
                xCordTextField.setText("");
                yCordTextField.setText("");
            }
        }
        updateRequest = false;
    }

    public void setUpdateForm(Vehicle vehicle) {
        this.updateForm = true;
        this.updateId = vehicle.getId();
        nameTextField.setText(vehicle.getName());
        powerTextField.setText(Float.toString(vehicle.getEnginePower()));
        xCordTextField.setText(Long.toString(vehicle.getCoordinates().getX()));
        yCordTextField.setText(Double.toString(vehicle.getCoordinates().getY()));
        jComboBoxType.setSelectedItem(vehicle.getType().toString());
        jComboBoxFuelType.setSelectedItem(vehicle.getFuelType().toString());
        checkBox.setEnabled(false);
        formLabel.setText(String.format(resourceBundle.getString("update_title")+" (%d)", vehicle.getId()));
        createButton.setText(resourceBundle.getString("update"));
        setVisible(true);
    }

    public void setCreateForm() {
        this.updateForm = false;
        nameTextField.setText("");
        powerTextField.setText("");
        xCordTextField.setText("");
        yCordTextField.setText("");
        checkBox.setEnabled(true);
        formLabel.setText(resourceBundle.getString("creat_title"));
        createButton.setText(resourceBundle.getString("create"));
        setVisible(true);
    }
}
