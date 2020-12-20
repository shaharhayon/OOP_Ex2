import Server.Game_Server_Ex2;
import api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {



    @Test
    void connect() {
        game_service game = Game_Server_Ex2.getServer(0);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        directed_weighted_graph g = algo.getGraph();

        g.connect(0, 5, 10);
        assertEquals(g.getEdge(0, 5).getWeight(), 10);
    }

    @Test
    void getV() {
        game_service game = Game_Server_Ex2.getServer(0);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        directed_weighted_graph g = algo.getGraph();

        int size = g.nodeSize(), counter = 0;
        Collection<node_data> v = g.getV();
        Iterator<node_data> iter = v.iterator();
        while (iter.hasNext()) {
            node_data n = iter.next();
            Assertions.assertNotNull(n);
            counter++;
        }
        assertEquals(size, counter);
    }

    @Test
    void removeNode() {
        game_service game = Game_Server_Ex2.getServer(0);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        directed_weighted_graph g = algo.getGraph();

        node_data n = g.getNode(0);
        assertNotNull(n);
        g.removeNode(0);
        n = g.getNode(0);
        assertNull(n);

    }

    @Test
    void removeEdge() {
        game_service game = Game_Server_Ex2.getServer(0);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        directed_weighted_graph g = algo.getGraph();

        edge_data e = g.getEdge(0,1);
        assertNotNull(e);
        g.removeEdge(0,1);
        e = g.getEdge(0,1);
        assertNull(e);

    }

    @Test
    void nodeSize() {
        game_service game = Game_Server_Ex2.getServer(0);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        directed_weighted_graph g = algo.getGraph();

        g.removeNode(2);
        g.removeNode(1);
        g.removeNode(1);
        int s = g.nodeSize();
        Assertions.assertEquals(9, s);
    }

    @Test
    void edgeSize() {
        game_service game = Game_Server_Ex2.getServer(0);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        directed_weighted_graph g = algo.getGraph();

        int size = g.edgeSize();
        assertEquals(size, 22);

    }

    @Test
    void getMC() {
        game_service game = Game_Server_Ex2.getServer(0);
        String graphAsString = game.getGraph();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.loadfromString(graphAsString);
        directed_weighted_graph g = algo.getGraph();

        assertEquals(g.getMC(), 33);

        g.removeNode(0);
        assertEquals(g.getMC(), 38);
    }
}