package game;

import api.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class gameArena {
    public static final double EPS = 0.001 * 0.001;
    public DWGraph_DS G;
    public DWGraph_Algo G_algo;
    public game_service game;
    public List<Pokemon> pokemons = Collections.synchronizedList(new ArrayList<>()); //ArrayList<>();
    public List<Agent> agents = new ArrayList<>();
    public HashMap<String, String> stats;
    public Gson gson;
    public gameFrame gf;
    public HashMap<Agent, List<node_data>> agentsPath = new HashMap<>();
    public HashMap<Agent, Pokemon> agentsToPokemons = new HashMap<>();
    private boolean firstAgent = true;

    public gameArena(game_service game) {
        this.game = game;
        this.gson = new gameJsonAdapter(this).getGson();
        initGraph();
        getPokemons();
        getStats();
        getAgents();
/*        Thread pokemonsUpdater = new Thread() {
            public void run() {
                getPokemons();
            }
        };
        pokemonsUpdater.setName("PokemonsThread");
        pokemonsUpdater.start();*/

        gf = new gameFrame(this);
    }

    private void initGraph() {
        File file = new File("./tmp_graph.json");
        try {
            FileWriter fr = new FileWriter(file);
            fr.write(game.getGraph());
            fr.flush();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.G_algo = new DWGraph_Algo();
        this.G_algo.load("./tmp_graph.json");
        file.delete();
        this.G = (DWGraph_DS) G_algo.getGraph();
    }

    protected synchronized void getPokemons() {
        JsonObject pokemonsJson = gson.fromJson(game.getPokemons(), JsonObject.class);
        JsonArray pokemonsJsonArray = pokemonsJson.get("Pokemons").getAsJsonArray();
        List<Pokemon> newPokemons = new ArrayList<>();
        boolean alreadyExists = false;
        Pokemon newPokemon;
        for (JsonElement e : pokemonsJsonArray) {
            newPokemon = gson.fromJson(e.getAsJsonObject().get("Pokemon").toString(), Pokemon.class);
            newPokemons.add(newPokemon);
        }
        /*
        Remove pokemons that got caught
         */

        ListIterator<Pokemon> iterator = pokemons.listIterator();
        while (iterator.hasNext()) {
            Pokemon org_p = iterator.next();
            //for (Pokemon org_p : pokemons) {

            boolean stillExist = false;
            for (Pokemon new_p : newPokemons) {
                if (org_p.get_pos().equals(new_p.get_pos())) stillExist = true;
            }
            //if (!stillExist) pokemons.remove(org_p);
            if (!stillExist) iterator.remove();
        }
        ListIterator<Pokemon> newIterator = newPokemons.listIterator();
        while (newIterator.hasNext()) {
            Pokemon new_p = newIterator.next();
            //for (Pokemon new_p : newPokemons) {
            boolean isNew = true;
            for (Pokemon org_p : pokemons) {
                if (new_p.get_pos().equals(org_p.get_pos())) isNew = false;
            }
            if (isNew) {
                //pokemons.add(new_p);
                iterator.add(new_p);
            }
            /*if(!pokemons.contains(p)){
                pokemons.add(p);
            }*/
        }

        for (Pokemon p : pokemons) {
            if (p.get_edge() == null) {
                p.set_edge(findEdge(p.get_pos()));
            }
        }
    }


    protected edge_data findEdge(DWGraph_DS.Position p) {
        for (node_data node : this.G.getV()) {
            for (edge_data edge : this.G.getE(node.getKey())) {
                if (isOnEdge((DWGraph_DS.Position) node.getLocation(), (DWGraph_DS.Position) this.G.getNode(edge.getDest()).getLocation(), p)) {
                    return edge;
                }
            }
        }
        return null;
    }


    private boolean isOnEdge(DWGraph_DS.Position src, DWGraph_DS.Position dest, DWGraph_DS.Position p) {
        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if (dist > d1 - EPS) {
            ans = true;
        }
        return ans;
    }

    protected void getAgents() {
        JsonObject agentsJson = gson.fromJson(game.getAgents(), JsonObject.class);
        JsonArray agentsJsonArray = agentsJson.get("Agents").getAsJsonArray();
        //agents = new ArrayList<>();
        for (JsonElement e : agentsJsonArray) {
            int id = e.getAsJsonObject().get("Agent").getAsJsonObject().get("id").getAsInt();

            boolean newAgent = true;
            for (Agent a : this.agents) {
                if (a.getID() == id) {
                    newAgent = false;
                    break;
                }
            }
            //newPokemon = gson.fromJson(e.getAsJsonObject().get("Pokemon").toString(), Pokemon.class);
//            Agent a = this.gson.fromJson(e.getAsJsonObject().get("Agent"), Agent.class);
            Agent a = this.gson.fromJson(e.getAsJsonObject().get("Agent").toString(), Agent.class);

            if (newAgent) {

                this.agents.add(a);
                System.out.println("agent added");
                /*if (firstAgent) {
                    //new Thread(a).start();
                    Thread t = new Thread(a);
                    t.setName("AgentsThread");
                    t.start();
                }*/
            }
        }
    }

    protected void getStats() {
        this.stats = gson.fromJson(this.game.toString(), HashMap.class);

    }

    /*protected node_data findAgentDest(Agent a) {
        DWGraph_DS.Node nextNode = null;
        if ((agentsPath.get(a) == null) || (agentsPath.get(a).isEmpty())) {
            agentsPath.remove(a);
            agentsToPokemons.remove(a);
            agentsPath.put(a, findClosestPokemon(a));
        }
        nextNode = (DWGraph_DS.Node) agentsPath.get(a).get(0);
        agentsPath.get(a).remove(0);
        return nextNode;
    }*/

    protected Pokemon findClosestPokemon(Agent a) { //List<node_data> findClosestPokemon(Agent a) {

        DWGraph_DS.Node node = (DWGraph_DS.Node) a.get_src();
        HashMap<Pokemon, DWGraph_DS.Node[]> map = new HashMap<>();
        DWGraph_DS.Node arr[] = new DWGraph_DS.Node[2];
        for (Pokemon p : pokemons) {
            arr[0] = (DWGraph_DS.Node) G.getNode(p.get_edge().getSrc()); // BeforeLast
            arr[1] = (DWGraph_DS.Node) G.getNode(p.get_edge().getDest()); // Last
            map.put(p, arr);
        }
        double minDistance = Double.MAX_VALUE;
        Pokemon closestPokemon = null;
        for (Pokemon p : map.keySet()) {
            if (this.agentsToPokemons.containsValue(p)) continue;
            double d = G_algo.shortestPathDist(node.getKey(), map.get(p)[0].getKey());
            if (d < minDistance) {
                minDistance = d;
                closestPokemon = p;
            }
        }
        return closestPokemon;
        /*this.agentsToPokemons.put(a, closestPokemon);
        List<node_data> result = G_algo.shortestPath(node.getKey(), map.get(closestPokemon)[0].getKey());
        result.add(arr[1]);
        return result;*/
    }
}
