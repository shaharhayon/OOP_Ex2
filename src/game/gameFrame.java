package game;

import api.game_service;

import javax.swing.*;

public class gameFrame extends JFrame {
    gamePanel panel;

    public gameFrame(gameArena arena) {
        super();
        this.setSize(800, 800);
        this.setVisible(true);
        panel = new gamePanel(arena);
        this.add(panel);
        arena.game.startGame();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
