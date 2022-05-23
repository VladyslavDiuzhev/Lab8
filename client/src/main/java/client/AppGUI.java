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
        try {
            socket = new Socket("localhost", 8001);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            NetInteractor serverInteractor = new NetInteractor(objectInputStream, objectOutputStream);


            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    LoginView loginView = new LoginView(serverInteractor);

//                    final JFrame frame = new JFrame("Управление коллекцией");
//                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                    cards.setLayout(new CardLayout());
//                    cards.add(loginView.getLogPane(),"Login");
//                    cards.add(new MainView().getMainPanel(),"Main");
////                    loginView.getLogPane().addPropertyChangeListener(new PropertyChangeListener() {
////                        @Override
////                        public void propertyChange(PropertyChangeEvent evt) {
////
////                        }
////                    });
//
//                    frame.setContentPane(cards);
//                    frame.pack();
//                    frame.setVisible(true);
//                    frame.setLocation(400, 400);
//                    frame.setMaximumSize(new Dimension(500, 250));
//                    frame.setMinimumSize(new Dimension(500, 200));
//
////                    try {
////                        Thread.sleep(5000);
////                    } catch (InterruptedException e) {
////                        throw new RuntimeException(e);
////                    }
//                    ((CardLayout)cards.getLayout()).show(cards,"Main");
                }
            });


        } catch (IOException e) {
            JFrame jFrame = new JFrame();
            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            JOptionPane.showMessageDialog(jFrame, "Server unavailable!");
            jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
        }
//        System.out.println("Logged in!");
    }
}
