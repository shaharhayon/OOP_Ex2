package game;

import api.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
//import gameClient.util.Point3D;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

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
    private List<node_data> path = new LinkedList<>();

    public Agent(int id, double value, double speed, int src, int dest, DWGraph_DS.Position pos) {
        this._arena = gameArena.getArena();
        this._game = _arena.game;
        this.G = _arena.G;
        this._id = id;
        this._value = value;
        this._speed = speed;
        this._src = G.getNode(src);
        this._dest = G.getNode(dest);
        this._pos = pos;


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

    private boolean notChasingPokemon(){
        Pokemon p = _arena.agentsToPokemons.get(this);
        if (this.get_dest() != null
                && p != null
                && this.get_dest().getKey() == p.get_edge().getDest())
            return true;
        else
            return false;
    }

    private boolean pokemonCaught(){
        Pokemon p = _arena.agentsToPokemons.get(this);
        if (p == null
                || this.get_dest() == null
                || !_arena.pokemons.contains(p))
            return true;
        else
            return false;
    }

    private boolean agentOnLastEdge(){
        Pokemon p = _arena.agentsToPokemons.get(this);
        DWGraph_DS.Position pos =p.get_pos();
        DWGraph_DS.Edge e = (DWGraph_DS.Edge) _arena.findEdge(pos);
        if(this.get_src().getKey() == e.getSrc()) return true;
        else return false;
    }

    private boolean arrivedAtNode(){
        if (this.get_src() == this.get_dest() || this.get_dest() == null) return true;
        else return  false;
    }

    @Override
    public void run() {
        _arena.getPokemons();
        this.update(_game.getAgents());

        /*DWGraph_DS.Position pos;
        DWGraph_DS.Edge e;
        Pokemon p = _arena.agentsToPokemons.get(this);
        if (notChasingPokemon()) {
            _arena.agentsToPokemons.remove(this);
            _arena.getPokemons();
        }*/
        /*if (pokemonCaught()) {
            *//*
            find new pokemon and set as destination for this agent
             *//*
            _arena.getPokemons();
            p = _arena.findClosestPokemon(this);
            _arena.agentsToPokemons.put(this, p);

            findNewDest();
        }*/
        if (arrivedAtNode()) {
            /*
            find next destination node for this pokemon
             */
            if(this.path.size()==0){
                findNewDest();
            }
            else if(this.path.get(0)!=null) {
                this.set_dest(this.path.get(0));
                this.path.remove(0);
            }
            else {
                _arena.agentsToPokemons.remove(this);
                findNewDest();
            }



            //findNewDest();
        }
    }

    private void findNewDest() {
        _arena.getPokemons();

        Pokemon p=_arena.findClosestPokemon(this);
        DWGraph_DS.Position pos= p.get_pos();
        DWGraph_DS.Edge e=(DWGraph_DS.Edge) _arena.findEdge(pos);;

        int max = e.getSrc(), min = e.getDest();
        if(max<min){
            int tmp=max;
            max=min;
            min=tmp;
        }
        if (p.get_type() == 1)
            p.set_edge(_arena.G.getEdge(min,max));
        else
            p.set_edge(_arena.G.getEdge(max,min));

        e= (DWGraph_DS.Edge) p.get_edge();

        this.path=_arena.G_algo.shortestPath(this.get_src().getKey(), e.getSrc());




        path.add(_arena.G.getNode(e.getDest()));
        _arena.agentsToPokemons.put(this,p);
        /*if (agentOnLastEdge()) {
            this.set_dest(_arena.G.getNode(e.getDest()));
        } else {
            DWGraph_DS.Node dest = (DWGraph_DS.Node) _arena.G_algo.shortestPath(this.get_src().getKey(), e.getSrc()).get(0);
            if (_arena.G.getEdge(this.get_src().getKey(), dest.getKey()) != null)
                this.set_dest(dest);
            else{
                this.set_dest(_arena.G_algo.shortestPath(this.get_src().getKey(),dest.getKey()).get(0));
            }
        }*/
    }


    public void update(String json) {
        Gson gson = new gameJsonAdapter().getGson();
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
