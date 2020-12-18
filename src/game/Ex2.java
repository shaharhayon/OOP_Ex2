package game;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.Gson;

import javax.swing.*;

public class Ex2 {
    public static void main(String[] args) {
        int level;
        long id;
        if (args.length == 0) {
            levelSelect levelSelector = new levelSelect("Level Selection");
            levelSelector.setVisible(true);
            synchronized (levelSelector) {
                try {
                    levelSelector.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            level = levelSelector.getLevel();
            id = levelSelector.getID();
        } else {
            id = Integer.parseInt(args[0]);
            level = Integer.parseInt(args[1]);
        }

        game_service game = Game_Server_Ex2.getServer(level);
        if (levelSelect.signIn || args.length != 0)
            game.login(id);

        System.out.println(game.getGraph());
        System.out.println(game.getPokemons());
        System.out.println(game);
        System.out.println(game.getAgents());
        System.out.println(game.getGraph());
        //game.login(id);

        startScreen start = new startScreen(game);
        synchronized (start){
            try {
                start.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        gameArena.initArena(game);
        gameArena arena = gameArena.getArena();

        gameFrame frame = new gameFrame();

        while (game.isRunning()) {
            for (Agent a : arena.agents) {
                a.run();
                //if(a.get_dest()==null) continue;
                game.chooseNextEdge(a.getID(), a.get_dest().getKey());
            }
            game.move();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void placeAgents(){

    }
}
