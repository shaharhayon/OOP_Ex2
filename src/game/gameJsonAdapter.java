package game;

import api.*;
import com.google.gson.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class gameJsonAdapter {

    private final static Gson gson = createGson();
    private static boolean isInitialized = false;

    public gameJsonAdapter() {

    }

    private static Gson createGson() {
        //isInitialized = true;
        DWGraph_DS G = readGraph();
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<Agent> agentDeserializer = (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject json = jsonElement.getAsJsonObject();
            int id = json.getAsJsonObject().get("id").getAsInt();
            double value = json.getAsJsonObject().get("value").getAsDouble();
            double speed = json.getAsJsonObject().get("speed").getAsDouble();
            int src = json.getAsJsonObject().get("src").getAsInt();
            int dest = json.getAsJsonObject().get("dest").getAsInt();
            String pos = json.getAsJsonObject().get("pos").getAsString();
            String xy[];
            xy = pos.split(",");
            DWGraph_DS.Position point = new DWGraph_DS.Position(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]), 0);
            Agent a = new Agent(id, value, speed, src, dest, point);
            return a;
        };
        gsonBuilder.registerTypeAdapter(Agent.class, agentDeserializer);


        JsonDeserializer<Pokemon> pokemonDeserializer = (jsonElement, type, jsonDeserializationContext) -> {

            JsonObject json = jsonElement.getAsJsonObject();
            int t = json.getAsJsonObject().get("type").getAsInt();
            int v = json.getAsJsonObject().get("value").getAsInt();
            String pos = json.getAsJsonObject().get("pos").getAsString();
            String xy[];
            xy = pos.split(",");
            DWGraph_DS.Position point = new DWGraph_DS.Position(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]), 0);
            DWGraph_DS.Edge e = null;
            for (node_data n : G.getV()) {
                for (edge_data edge : G.getE(n.getKey())) {
                    DWGraph_DS.Node src = (DWGraph_DS.Node) G.getNode(edge.getSrc());
                    DWGraph_DS.Node dest = (DWGraph_DS.Node) G.getNode(edge.getDest());
                    if (src.getLocation().distance(point) + dest.getLocation().distance(point) ==
                            src.getLocation().distance(dest.getLocation())) {
                        e = (DWGraph_DS.Edge) edge;
                    }
                }
            }
            return new Pokemon(e, v, t, point);
        };
        gsonBuilder.registerTypeAdapter(Pokemon.class, pokemonDeserializer);
        //gson = gsonBuilder.create();


        JsonDeserializer<HashMap> statsDeserializer = (jsonElement, type, jsonDeserializationContext) -> {

            JsonObject json = jsonElement.getAsJsonObject().get("GameServer").getAsJsonObject();
            HashMap<String, String> stats = new HashMap<>();
            stats.put("Pokemons", json.getAsJsonObject().get("pokemons").getAsString());
            stats.put("Agents", json.getAsJsonObject().get("agents").getAsString());
            stats.put("Is logged in", json.getAsJsonObject().get("is_logged_in").getAsString());
            stats.put("Moves", json.getAsJsonObject().get("moves").getAsString());
            stats.put("Grade", json.getAsJsonObject().get("grade").getAsString());
            stats.put("Game level", json.getAsJsonObject().get("game_level").getAsString());
            stats.put("Max user level", json.getAsJsonObject().get("max_user_level").getAsString());
            stats.put("Graph", json.getAsJsonObject().get("graph").getAsString());
            stats.put("ID", json.getAsJsonObject().get("id").getAsString());
            return stats;
        };
        gsonBuilder.registerTypeAdapter(HashMap.class, statsDeserializer);
        return gsonBuilder.create();
    }


    private static DWGraph_DS readGraph() {
        DWGraph_Algo G_algo = new DWGraph_Algo();
        G_algo.loadfromString(gameArena.getArena().game.getGraph());
        return (DWGraph_DS) G_algo.getGraph();

    }

    /**
     * To be used only after gameArena is created!
     *
     * @Gson
     */
    public static Gson getGson() {
        //if (isInitialized) return gson;
        //else
        return gson;

    }
}
