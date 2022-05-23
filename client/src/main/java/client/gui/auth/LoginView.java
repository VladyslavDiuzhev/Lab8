package client.gui.auth;

import client.argcreation.Authorizer;
import client.gui.main.MainView;
import com.intellij.uiDesigner.core.GridConstraints;
import core.essentials.UserInfo;
import core.interact.Message;
import core.interact.NetInteractor;
import core.precommands.ObjectPrecommand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class LoginView extends JFrame {
    private final JPanel logPane = new JPanel();
    private final JTextField loginField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton actionButton = new JButton("Войти");
    private final JButton orButton = new JButton("Регистрация");
    private final JLabel mainTitle = new JLabel("Авторизация");
    private final JLabel askLabel = new JLabel("Нет аккаунта?");

    private boolean isLoginType = true;
    private final AuthListener authListener;

    public LoginView(NetInteractor netInteractor) {
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
        this.logPane.add(new JLabel("Логин"), constraints);
        constraints.gridx = 1;
        this.logPane.add(new JLabel("Пароль"), constraints);
        constraints.gridy = 2;
        constraints.gridx = 0;
        this.logPane.add(loginField, constraints);
        constraints.gridx = 1;
        this.logPane.add(passwordField, constraints);
        constraints.gridy = 3;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        this.logPane.add(actionButton, constraints);
        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        this.logPane.add(askLabel, constraints);
        constraints.gridx = 1;
        this.logPane.add(orButton, constraints);
        this.authListener = new AuthListener(netInteractor, isLoginType);
        actionButton.addActionListener(authListener);
        orButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionButton.removeAll();
                if (isLoginType) {
                    mainTitle.setText("Регистрация");
                    askLabel.setText("Уже есть аккаунт?");
                    actionButton.setText("Зарегистрироваться");
                    orButton.setText("Вход");
                } else {
                    mainTitle.setText("Авторизация");
                    askLabel.setText("Нет аккаунта?");
                    actionButton.setText("Войти");
                    orButton.setText("Регистрация");
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
        private final NetInteractor netInteractor;
        private boolean isLogin;

        protected AuthListener(NetInteractor netInteractor, boolean isLogin) {
            this.netInteractor = netInteractor;
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
                JOptionPane.showMessageDialog(logPane, "Неверный формат ввода.");
                return;
            }
            loginPrecommand.preprocess(userInfo, false);
            loginField.setText("");
            passwordField.setText("");
            try {
                netInteractor.sendObject(loginPrecommand);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(logPane, "Ошибка передачи данных.");
                return;
            }
            try {
                Message message = (Message) netInteractor.readObject();
                if (message.isSuccessful() && isLogin) {
                    setVisible(false);
                    new MainView(netInteractor);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(logPane, message.getText());
                }
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(logPane, "Ответ не получен.");
            }
        }
    }
}
