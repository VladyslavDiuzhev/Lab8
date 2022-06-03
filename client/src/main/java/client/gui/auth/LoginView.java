package client.gui.auth;

import client.argcreation.Authorizer;
import client.gui.main.MainView;
import core.essentials.UserInfo;
import core.interact.Message;
import core.interact.NetInteractor;
import core.precommands.ObjectPrecommand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginView extends JFrame {
    private final JPanel logPane = new JPanel();
    private final JTextField loginField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private JButton actionButton = null;
    private JButton orButton = null;
    private JLabel mainTitle = null;
    private JLabel askLabel = null;
    private JLabel langLabel = null;
    private JLabel loginLabel = null;
    private JLabel passwordLabel = null;

    private NetInteractor netInteractor;

    private boolean isLoginType = true;
    private AuthListener authListener = null;
    private Locale locale = new Locale("ru", "RU");
    private ResourceBundle resourceBundle;

    private void connect() {
        try {
            Socket socket = new Socket("localhost", 8001);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.netInteractor = new NetInteractor(objectInputStream, objectOutputStream);
        } catch (IOException e) {
            JFrame jFrame = new JFrame();
            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            JOptionPane.showMessageDialog(jFrame, "Server unavailable!");
            jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
        }
    }

    private boolean relang() {
        try {
            actionButton.setText(resourceBundle.getString("enter"));
            orButton.setText(resourceBundle.getString("reg"));
            mainTitle.setText(resourceBundle.getString("auth"));
            askLabel.setText(resourceBundle.getString("question_enter"));
            langLabel.setText(resourceBundle.getString("lang") + ": ");
            loginLabel.setText(resourceBundle.getString("login"));
            passwordLabel.setText(resourceBundle.getString("password"));
        } catch (Exception e) {
            dispose();
            return false;
        }
        return true;
    }

    public LoginView() {
        resourceBundle = ResourceBundle.getBundle("client.lang.Bundle", locale);
        try {
            actionButton = new JButton(resourceBundle.getString("enter"));
            orButton = new JButton(resourceBundle.getString("reg"));
            mainTitle = new JLabel(resourceBundle.getString("auth"));
            askLabel = new JLabel(resourceBundle.getString("question_enter"));
            langLabel = new JLabel(resourceBundle.getString("lang") + ": ");
            loginLabel = new JLabel(resourceBundle.getString("login"));
            passwordLabel = new JLabel(resourceBundle.getString("password"));
        } catch (Exception e) {
            dispose();
            return;
        }

        mainTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JComboBox<String> languageBox = new JComboBox<>();
        languageBox.setMaximumSize(new Dimension(50, 40));
        languageBox.setEditable(false);
        languageBox.addItem("Русский");
        languageBox.addItem("English (Australia)");
        languageBox.addItem("Dansk");
        languageBox.addItem("Deutsch");

        languageBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (Objects.equals(e.getItem().toString(), "Русский")) {
                    locale = new Locale("ru", "RU");
                } else if (Objects.equals(e.getItem().toString(), "English (Australia)")) {
                    locale = new Locale("en", "AU");
                } else if (Objects.equals(e.getItem().toString(), "Dansk")) {
                    locale = new Locale("dan", "DAN");
                } else if (Objects.equals(e.getItem().toString(), "Deutsch")) {
                    locale = new Locale("de", "DE");
                }
                resourceBundle = ResourceBundle.getBundle("client.lang.Bundle", locale);
                relang();
            }
        });

        connect();
        this.logPane.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        this.logPane.add(mainTitle, constraints);
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        this.logPane.add(langLabel, constraints);
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        this.logPane.add(languageBox, constraints);
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        this.logPane.add(loginLabel, constraints);
        constraints.gridx = 1;
        this.logPane.add(passwordLabel, constraints);
        constraints.gridy = 3;
        constraints.gridx = 0;
        this.logPane.add(loginField, constraints);
        constraints.gridx = 1;
        this.logPane.add(passwordField, constraints);
        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        this.logPane.add(actionButton, constraints);
        constraints.gridy = 5;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        this.logPane.add(askLabel, constraints);
        constraints.gridx = 1;
        this.logPane.add(orButton, constraints);
        this.authListener = new AuthListener(isLoginType);
        actionButton.addActionListener(authListener);
        orButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionButton.removeAll();
                if (isLoginType) {
                    mainTitle.setText(resourceBundle.getString("reg"));
                    askLabel.setText(resourceBundle.getString("question_reg"));
                    actionButton.setText(resourceBundle.getString("register"));
                    orButton.setText(resourceBundle.getString("enter"));
                } else {
                    mainTitle.setText(resourceBundle.getString("auth"));
                    askLabel.setText(resourceBundle.getString("question_enter"));
                    actionButton.setText(resourceBundle.getString("enter"));
                    orButton.setText(resourceBundle.getString("reg"));
                }
                isLoginType = !isLoginType;
                authListener.toggleLogin();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(logPane);
        pack();
        setVisible(true);
        setLocation(400, 400);
        setMaximumSize(new Dimension(500, 250));
        setMinimumSize(new Dimension(500, 200));
    }

    public JPanel getLogPane() {
        return logPane;
    }

    class AuthListener implements ActionListener {
        //        private final NetInteractor netInteractor;
        private boolean isLogin;

        protected AuthListener(boolean isLogin) {
//            this.netInteractor = netInteractor;
            this.isLogin = isLogin;
        }

        protected void toggleLogin() {
            this.isLogin = !this.isLogin;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String commandType;
            if (this.isLogin) {
                commandType = "authorize";
            } else {
                commandType = "register";
            }

            ObjectPrecommand loginPrecommand = new ObjectPrecommand(commandType);
            UserInfo userInfo = new UserInfo(loginField.getText(), String.valueOf(passwordField.getPassword()));
            if (!Authorizer.validate(userInfo)) {
                JOptionPane.showMessageDialog(logPane, resourceBundle.getString("inv_format"));
                return;
            }
            loginPrecommand.preprocess(userInfo, false);
            loginField.setText("");
            passwordField.setText("");
            try {
                netInteractor.sendObject(loginPrecommand);
            } catch (IOException | NullPointerException ex) {
                JOptionPane.showMessageDialog(logPane, resourceBundle.getString("gen_err"));
                connect();
                return;
            }
            try {
                Message message = (Message) netInteractor.readObject();
                if (message.isSuccessful() && isLogin) {
                    setVisible(false);
                    new MainView(netInteractor, userInfo.getLogin() + "(" + userInfo.getId() + ")", resourceBundle);
                    dispose();
                } else if (message.isSuccessful() && !isLogin) {
                    JOptionPane.showMessageDialog(logPane, resourceBundle.getString(message.getText()));
                } else {
//                    JOptionPane.showMessageDialog(logPane, message.getText());
                    JOptionPane.showMessageDialog(logPane, resourceBundle.getString("gen_err"));
                }
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(logPane, resourceBundle.getString("gen_err"));
            }
        }
    }
}
