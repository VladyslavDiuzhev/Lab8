package client.gui.parts;

import client.gui.main.MainView;
import core.essentials.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Stack;

public class DrawPanel extends JPanel {
    private Stack<int[]> objects = new Stack<>();
    private HashMap<Integer, Color> hashMap = new HashMap<>();
    private int selectedId = -1;
    private ResourceBundle resourceBundle;

    public DrawPanel(Stack<int[]> objs, MainView mainView, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        objects = objs;
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem(resourceBundle.getString("delete"));
        JMenuItem updateItem = new JMenuItem(resourceBundle.getString("update"));
        popupMenu.add(deleteItem);
        popupMenu.add(updateItem);

        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainView.deleteObject(Integer.toString(selectedId));
            }
        });

        updateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainView.updateObject(Integer.toString(selectedId));
            }
        });

        DrawPanel drawPanel = this;

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Rectangle r = getBounds();
                    int x = e.getX() - r.width / 2;
                    int y = -e.getY() + r.height / 2;
                    for (int[] obj : objects) {
                        if (Math.pow(x - obj[2], 2) + Math.pow(y - obj[3], 2) <= 100) {
                            popupMenu.show(drawPanel, e.getX(), e.getY());
                            selectedId = obj[0];
                            return;
                        }
                    }

                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Rectangle r = getBounds();
        g2.setBackground(Color.white);
        g2.clearRect(0, 0, r.width, r.height);
        drawAxis(g2);
        Random random = new Random();
        for (int[] obj : objects) {
            Color color;
            if (hashMap.containsKey(obj[1])) {
                color = hashMap.get(obj[1]);
            } else {
                color = Color.getHSBColor(random.nextFloat(), random.nextFloat(), random.nextFloat());
                hashMap.put(obj[1], color);
            }
            g2.setColor(color);

            g2.fillOval(r.width / 2 + obj[2] - 10, r.height / 2 - obj[3] - 10, 20, 20);
            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(obj[0]), r.width / 2 + obj[2] - 10, r.height / 2 - obj[3] - 10);

        }
    }

    public void addObjectOut(int id, int owner, int x, int y) {
        final int[] x_now = {0};
        final int[] y_now = {0};
        int x_step = x / 50;
        int y_step = y / 50;
        if (x < 0 && x_step == 0) {
            x_step = -1;
        } else if (x > 0 && x_step < 1) {
            x_step = 1;
        }
        if (y < 0 && y_step == 0) {
            y_step = -1;
        } else if (y > 0 && y_step < 1) {
            y_step = 1;
        }
        Timer timer = new Timer(5, null);
        int finalX_step = x_step;
        int finalY_step = y_step;
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!((finalX_step < 0 && x < x_now[0]) || (finalX_step > 0 && x > x_now[0])) &&
                        !((finalY_step < 0 && y < y_now[0]) || (finalY_step > 0 && y > y_now[0]))) {
                    timer.stop();
                    return;
                }
                if (x_now[0] != x) {
                    if ((finalX_step < 0 && x < x_now[0]) || (finalX_step > 0 && x > x_now[0])) {
                        x_now[0] = x_now[0] + finalX_step;
                    }
                }
                if (y_now[0] != y) {
                    if ((finalY_step < 0 && y < y_now[0]) || (finalY_step > 0 && y > y_now[0])) {
                        y_now[0] += finalY_step;
                    }
                }
//                System.out.println("Work");
                addObject(id, owner, x_now[0], y_now[0]);
            }
        };
        timer.addActionListener(actionListener);
        timer.start();
    }

    private void addObject(int id, int owner, int x, int y) {
        int index = -1;
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i)[0] == id) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            objects.remove(index);
            objects.add(index, new int[]{id, owner, x, y});
        } else {
            objects.add(new int[]{id, owner, x, y});
        }
        repaint();
    }

    public void deleteObject(int id) {
        int index = -1;
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i)[0] == id) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            objects.remove(index);
            repaint();
        }
    }

    private void drawAxis(Graphics2D graphics2D) {
        Rectangle r = getBounds();
        graphics2D.drawLine(0, r.height / 2, r.width, r.height / 2);
        graphics2D.drawLine(r.width / 2, 0, r.width / 2, r.height);

    }
}

