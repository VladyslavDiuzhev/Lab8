package client;

import client.gui.auth.LoginView;
import client.gui.main.MainView;
import core.interact.NetInteractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AppGUI {
    private static JPanel cards = new JPanel();

    public static void main(String[] args) {

        Socket socket;
        ObjectInputStream objectInputStream;
        ObjectOutputStream objectOutputStream;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginView();
            }
        });

//        System.out.println("Logged in!");
    }
}
