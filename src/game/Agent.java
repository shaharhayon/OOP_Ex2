package game;

import api.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
//import gameClient.util.Point3D;
import org.json.JSONObject;

public class Agent implements Runnable {
    private int _id;
    private double _value;
    private double _speed;
    private node_data _src;
    private node_data _dest;
    private edge_data _edge;
    private DWGraph_DS.Position _pos;
    private directed_weighted_graph G;
    private game_service _game;
    private gameArena _arena;
    private Pokemon chasedPokemon;

    public Agent(int id, double value, double speed, int src, int dest, DWGraph_DS.Position pos, directed_weighted_graph g, gameArena arena) {
        this._id = id;
        this._value = value;
        this._speed = speed;
        this._src = g.getNode(src);
        this._dest = g.getNode(dest);
        this._pos = pos;
        this.G = g;
        this._arena = arena;
        this._game = arena.game;
        this.run();
        //set_dest(_arena.findAgentDest(this));
    }

    /*@Override
    public void run() {
        for (Agent a : _arena.agents) {
            a.update(_game.getAgents());
            if ((a.get_src() == a.get_dest()) || a.get_dest() == null) {
                set_dest(_arena.findAgentDest(a));
            }
            while (!_game.isRunning()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        while (_game.isRunning()) {
            //System.out.println("Agent " + this.getID() + " is running.");
            this.update(_game.getAgents());
            if ((this.get_src() == this.get_dest()) ||
                    this._arena.agentsToPokemons.get(this) == null) {
                this._arena.agentsToPokemons.remove(this);
                set_dest(_arena.findAgentDest(this));
            }
        }
    }*/

    @Override
    public void run() {
        _arena.getPokemons();
        this.update(_game.getAgents());
        Pokemon p=_arena.agentsToPokemons.get(this);
        DWGraph_DS.Position pos;
        DWGraph_DS.Edge e;

        if(this.get_dest() != null && p!=null &&this.get_dest().getKey()==p.get_edge().getDest()){
            _arena.agentsToPokemons.remove(this);
            _arena.getPokemons();
        }

        if(p==null|| this.get_dest() == null||
        !_arena.pokemons.contains(p)){
            /*
            find new pokemon and set as destination for this agent
             */
            _arena.getPokemons();
            p = _arena.findClosestPokemon(this);
            _arena.agentsToPokemons.put(this,p);

            pos=p.get_pos();
            e = (DWGraph_DS.Edge) _arena.findEdge(pos);
            if(this.get_src().getKey()==e.getSrc()){
                this.set_dest(_arena.G.getNode(e.getDest()));
            }
            else{
                this.set_dest(_arena.G_algo.shortestPath(this.get_src().getKey(),e.getSrc()).get(0));
            }
        }


        if (this.get_src() == this.get_dest()||this.get_dest() == null)  {
            /*
            find next destination node for this pokemon
             */
            pos=p.get_pos();
            e = (DWGraph_DS.Edge) _arena.findEdge(pos);
            if(this.get_src().getKey()==e.getSrc()){
                this.set_dest(_arena.G.getNode(e.getDest()));
            }
            else{
                this.set_dest(_arena.G_algo.shortestPath(this.get_src().getKey(),e.getSrc()).get(0));
            }

            //set_dest(_arena.findAgentDest(this));
        }

    }


    public void update(String json) {
        Gson gson = new gameJsonAdapter(_arena).getGson();
        JsonArray agentsArray = gson.fromJson(json, JsonObject.class).getAsJsonArray("Agents");
        for (JsonElement agentJson : agentsArray) {
            try {
                agentJson = agentJson.getAsJsonObject().get("Agent").getAsJsonObject();
                int id = agentJson.getAsJsonObject().get("id").getAsInt();
                if (id == this.getID() || this.getID() == -1) {
                    if (this.getID() == -1) {
                        this._id = id;
                    }
                    double speed = agentJson.getAsJsonObject().get("speed").getAsDouble();
                    String p = agentJson.getAsJsonObject().get("pos").getAsString();
                    String xy[] = p.split(",");
                    DWGraph_DS.Position pos = new DWGraph_DS.Position(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]), 0);
                    int src = agentJson.getAsJsonObject().get("src").getAsInt();
                    int dest = agentJson.getAsJsonObject().get("dest").getAsInt();
                    double value = agentJson.getAsJsonObject().get("value").getAsInt();
                    this._pos = pos;
                    this.set_src(G.getNode(src));
                    this.set_speed(speed);
                    if (dest != -1)
                        this.set_dest(G.getNode(dest));
                    this.set_value(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getID() {
        return _id;
    }

    public double get_value() {
        return _value;
    }

    public void set_value(double _value) {
        this._value = _value;
    }

    public double get_speed() {
        return _speed;
    }

    public void set_speed(double _speed) {
        this._speed = _speed;
    }

    public node_data get_src() {
        return _src;
    }

    public void set_src(node_data _src) {
        this._src = _src;
    }

    public node_data get_dest() {
        return _dest;
    }

    public void set_dest(node_data dest) {
        this._dest = dest;
    }


    public DWGraph_DS.Position get_pos() {
        return _pos;
    }

    public void set_pos(DWGraph_DS.Position _pos) {
        this._pos = _pos;
    }

    public Pokemon getChasedPokemon() {
        return chasedPokemon;
    }

    public void setChasedPokemon(Pokemon chasedPokemon) {
        this.chasedPokemon = chasedPokemon;
    }
}
