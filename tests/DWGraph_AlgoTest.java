import Server.Game_Server_Ex2;
import api.DWGraph_Algo;
import api.DWGraph_DS;
import api.game_service;
import api.node_data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {

    @Test
    void copy() {
        game_service game = Game_Server_Ex2.getServer(23);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        DWGraph_DS g2= (DWGraph_DS) algo.copy();
        assertEquals(algo.getGraph(),g2);
    }

    @Test
    void isConnected() {
        game_service game = Game_Server_Ex2.getServer(23);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        assertTrue(algo.isConnected());
    }

    @Test
    void shortestPathDist() {
        game_service game = Game_Server_Ex2.getServer(0);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        DWGraph_DS g = (DWGraph_DS) algo.getGraph();
        assertEquals(algo.shortestPathDist(3,9),5);
    }

    @Test
    void shortestPath() {
        game_service game = Game_Server_Ex2.getServer(0);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        DWGraph_DS g = (DWGraph_DS) algo.getGraph();
        List<node_data> list = new ArrayList<>();
        list.add(g.getNode(2));
        list.add(g.getNode(1));
        list.add(g.getNode(0));
        list.add(g.getNode(10));
        list.add(g.getNode(9));
        assertEquals(list,algo.shortestPath(3,9));
    }

    @Test
    void save_load_loadfromString() {
        String path = "TEST.json";
        game_service game = Game_Server_Ex2.getServer(15);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        DWGraph_DS g1 = (DWGraph_DS) algo.getGraph();
        algo.save(path);
        algo.load(path);
        DWGraph_DS g2 = (DWGraph_DS) algo.getGraph();
        boolean condition = g1.equals(g2);
        assertTrue(condition);

    }
}