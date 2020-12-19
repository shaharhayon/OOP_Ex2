package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
This class is a start game window, that lets you choose level and login to the game
Only shows if the user did not enter those values from the terminal
 */
public class levelSelect extends JFrame implements ActionListener {

    private JButton startButton = new JButton("Start Game");
    private JFormattedTextField IDbox = new JFormattedTextField();
    private JComboBox<Integer> levelList = new JComboBox();

    public static boolean signIn = false;
    private int level;
    private long id = 0;

    public levelSelect(String title) throws HeadlessException {
        super(title);
        this.setSize(400, 400);
        this.setResizable(false);
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        for (int i = 0; i <= 23; i++) {
            levelList.addItem(i);
        }
        this.add(levelList);
        this.add(IDbox);
        this.add(startButton);
        startButton.addActionListener(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintButton();
        paintText();

        g.drawString("Level:", levelList.getX() - 30, levelList.getY() + 45);
        g.drawString("ID:", IDbox.getX() - 20, IDbox.getY() + 45);
        paintLevelList();
    }

    private void paintLevelList() {
        levelList.setVisible(true);
        levelList.setEnabled(true);
        levelList.setBounds(100, 50, this.getWidth() - 200, 20);
        levelList.setToolTipText("Choose level");
    }

    private void paintText() {
        IDbox.setVisible(true);
        IDbox.setEnabled(true);
        IDbox.setBounds(100, 100, this.getWidth() - 200, 20);
        //IDbox.setText("Enter ID");
        IDbox.setToolTipText("Enter ID");
    }

    private void paintButton() {
        startButton.setEnabled(true);
        startButton.setVisible(true);
        startButton.setBounds(100, this.getHeight() - 200, this.getWidth() - 200, 100);
        startButton.setFont(new Font("Arial", Font.PLAIN, startButton.getHeight() / 3));
    }

    public long getID() {
        return this.id;
    }

    public int getLevel() {
        return this.level;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        level = (int) levelList.getSelectedItem();
        if (IDbox.getValue() != null) {
            id = (long) IDbox.getValue();
            signIn = true;
        }
        synchronized (this) {
            this.notifyAll();
        }
        this.setVisible(false);
    }
}
