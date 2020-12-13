package game;

import Server.Game_Server_Ex2;
import api.*;

import javax.swing.*;

public class Ex2 {
    public static void main(String[] args) {
        int level = 23;
        //long id=Integer.parseInt(args[0]);
        //int level=Integer.parseInt(args[1]);
        game_service game = Game_Server_Ex2.getServer(level);
        System.out.println(game.getGraph());
        System.out.println(game.getPokemons());
        System.out.println(game);
        System.out.println(game.getAgents());
        System.out.println(game.getGraph());
        //game.login(id);
        startScreen start = new startScreen(game);
        while (start.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        gameArena arena = new gameArena(game);

        while (game.isRunning()) {
            for (Agent a : arena.agents) {
                a.run();
                //if(a.get_dest()==null) continue;
                game.chooseNextEdge(a.getID(), a.get_dest().getKey());
            }
            game.move();
            //System.out.println(arena.agentsToPokemons);
            //System.out.println(arena.pokemons);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("moved");

        }
    }
}
