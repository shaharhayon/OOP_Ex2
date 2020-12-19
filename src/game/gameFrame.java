package game;

import api.game_service;

import javax.swing.*;

public class gameFrame extends JFrame {

    public gameFrame() {
        super();
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        gamePanel panel = new gamePanel();
        this.add(panel);
        gameArena.getArena().game.startGame();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
