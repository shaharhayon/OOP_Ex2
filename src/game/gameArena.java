package game;

import api.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class gameArena {
    public static final double EPS = 0.001 * 0.0001;
    public DWGraph_DS G;
    public DWGraph_Algo G_algo;
    public game_service game;
    public List<Pokemon> pokemons = Collections.synchronizedList(new ArrayList<>()); //ArrayList<>();
    public List<Agent> agents = new ArrayList<>();
    public HashMap<String, String> stats;
    public Gson gson;
    public HashMap<Agent, Pokemon> agentsToPokemons = new HashMap<>();
    private boolean firstAgent = true;

    private static final gameArena arena=new gameArena();


    private gameArena(){

    }

    public static void initArena(game_service game){
        arena.game = game;
        arena.gson = gameJsonAdapter.getGson();
        arena.initGraph();
        arena.getPokemons();
        arena.getStats();
        arena.getAgents();
    }

    public static gameArena getArena(){
        if(arena.game!=null)
            return arena;
        else
            return null;
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
        JsonObject pokemonsJson = arena.gson.fromJson(game.getPokemons(), JsonObject.class);
        JsonArray pokemonsJsonArray = pokemonsJson.get("Pokemons").getAsJsonArray();
        List<Pokemon> newPokemons = new ArrayList<>();
        boolean alreadyExists = false;
        Pokemon newPokemon;
        for (JsonElement e : pokemonsJsonArray) {
            newPokemon = arena.gson.fromJson(e.getAsJsonObject().get("Pokemon").toString(), Pokemon.class);
            newPokemons.add(newPokemon);
        }
        /*
        Remove pokemons that got caught
         */

        ListIterator<Pokemon> iterator = pokemons.listIterator();
        while (iterator.hasNext()) {
            Pokemon org_p = iterator.next();

            boolean stillExist = false;
            for (Pokemon new_p : newPokemons) {
                if (org_p.get_pos().equals(new_p.get_pos())) stillExist = true;
            }
            if (!stillExist) iterator.remove();
        }
        ListIterator<Pokemon> newIterator = newPokemons.listIterator();
        while (newIterator.hasNext()) {
            Pokemon new_p = newIterator.next();
            boolean isNew = true;
            for (Pokemon org_p : pokemons) {
                if (new_p.get_pos().equals(org_p.get_pos())) isNew = false;
            }
            if (isNew) {
                iterator.add(new_p);
            }
        }

        for (Pokemon p : pokemons) {
            if (p.get_edge() == null) {
                p.set_edge(findEdge(p.get_pos()));
            }
        }
    }


    /*
    This method finds if a specific position is on an existing edge
    returns that edge if it exists, else returns null
     */
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

    /*
    This method checks if a position is on an edge that goes from src -> dest
     */
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
        for (JsonElement e : agentsJsonArray) {
            int id = e.getAsJsonObject().get("Agent").getAsJsonObject().get("id").getAsInt();

            boolean newAgent = true;
            for (Agent a : this.agents) {
                if (a.getID() == id) {
                    newAgent = false;
                    break;
                }
            }
            Agent a = this.gson.fromJson(e.getAsJsonObject().get("Agent").toString(), Agent.class);

            if (newAgent) {
                this.agents.add(a);
                System.out.println("agent added");
            }
        }
    }

    protected void getStats() {
        this.stats = gson.fromJson(this.game.toString(), HashMap.class);

    }

    /*
    This method finds the closest pokemon from the pool to a selected agent
     */
    protected Pokemon findClosestPokemon(Agent a) {

        DWGraph_DS.Node node = (DWGraph_DS.Node) a.get_src();
        HashMap<Pokemon, DWGraph_DS.Node[]> map = new HashMap<>();
        for (Pokemon p : pokemons) {
            DWGraph_DS.Node arr[] = new DWGraph_DS.Node[2];
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
    }
}
