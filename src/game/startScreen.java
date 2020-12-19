package game;

import api.*;
import api.edge_data;
import api.node_data;
import com.google.gson.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
This class is a window that lets you choose where you want to place the agents
User can also choose to place the agents automatically
 */
public class startScreen extends JFrame implements MouseListener {

    Gson gson;
    DWGraph_DS G;
    DWGraph_Algo G_algo;
    private HashMap<Integer, Point> nodesMap = new HashMap<>();
    private int textSize;
    private gamePanel.Range graphRange;
    private List<Pokemon> pokemons;
    private game_service game;
    private HashMap<String, String> stats;
    private int clicks = 0;
    private int numOfAgents;
    private JButton button = new JButton("Place agents automatically");


    public startScreen(game_service game) {
        super();
        this.setLayout(null);
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
        gson = JsonDeserializer();
        this.game = game;
        initGraph();
        getPokemons();
        getStats();
        graphRange = graphRange(G);
        addMouseListener(this);
        numOfAgents = Integer.parseInt(this.stats.get("Agents"));
        button.setBounds(10, 10, 200, 50);
        button.setVisible(true);
        button.setEnabled(true);
        button.addActionListener(e -> {
            placeAgents();
        });
        this.add(button);

        this.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
/*        this.revalidate();
        this.repaint();*/
        printNodes(g);
        getPokemons();
        printPokemons(g);
        printStats(g);
    }

    /*
    This method places the agents automatically, as close as possible to the pokemons present.
     */
    private void placeAgents() {
        int numOfAgents = Integer.parseInt(stats.get("Agents"));
        HashMap<Integer, Integer> nodes = new HashMap<>();
        for (Pokemon p : pokemons) {
            int src;
            if (p.get_type() == 1) {
                src = Math.min(p.get_edge().getSrc(), p.get_edge().getDest());
            } else {
                src = Math.max(p.get_edge().getSrc(), p.get_edge().getDest());
            }
            nodes.put(src, ((int) p.get_value()));
        }
        for (int i = 0; i < numOfAgents; i++) {
            int maxValue = 0, maxNode = 0;
            for (Map.Entry<Integer, Integer> e : nodes.entrySet()) {
                if (e.getValue() > maxValue) {
                    maxValue = e.getValue();
                    maxNode = e.getKey();
                }
            }
            nodes.remove(maxNode);
            game.addAgent(maxNode);
        }
        this.setVisible(false);
        synchronized (this) {
            notifyAll();
        }
        return;
    }

    /*
    Scale coordinates
     */
    private double scale(double n, double MIN_INPUT, double MAX_INPUT, double MIN_OUTPUT, double MAX_OUTPUT) {
        double tmp = (((n - MIN_INPUT) / (MAX_INPUT - MIN_INPUT)) * (MAX_OUTPUT - MIN_OUTPUT) + MIN_OUTPUT);
        return tmp;
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

    /*
    Find coordinate range according to the selected graph
    */
    private gamePanel.Range graphRange(directed_weighted_graph g) {
        double xMin = Double.MAX_VALUE, yMin = Double.MAX_VALUE, xMax = 0, yMax = 0;
        for (node_data p : g.getV()) {
            DWGraph_DS.Position point = (DWGraph_DS.Position) p.getLocation();
            if (point.x() < xMin) xMin = point.x();
            if (point.y() < yMin) yMin = point.y();
            if (point.x() > xMax) xMax = point.x();
            if (point.y() > yMax) yMax = point.y();
        }
        gamePanel.Range range = new gamePanel.Range(xMin, yMin, xMax, yMax);
        return range;
    }

    private void printNodes(Graphics g) {
        textSize = ((int) Math.sqrt(this.getHeight() * this.getWidth()) / 60);
        if (this.G != null) {
            for (node_data n : G.getV()) {
                g.setColor(Color.BLACK);
                int x = (int) scale(n.getLocation().x(), graphRange.xMin, graphRange.xMax, gamePanel.OFFSET, this.getWidth() - gamePanel.OFFSET);
                int y = (int) scale(n.getLocation().y(), graphRange.yMin, graphRange.yMax, gamePanel.OFFSET, this.getHeight() - gamePanel.OFFSET);
                nodesMap.put(n.getKey(), new Point(x, y));
                g.fillOval(x - (gamePanel.nodeSize / 2), y - (gamePanel.nodeSize / 2), gamePanel.nodeSize, gamePanel.nodeSize);
                g.setFont(new Font("Arial", Font.BOLD, textSize));
                g.drawString("Node " + n.getKey(), x - (int) (textSize * 1.5), y - (int) (textSize / 2));
                //g.drawString("(" + gamePanel.df.format(n.getLocation().x()) + "," + gamePanel.df.format(n.getLocation().y()) + ")", x - textSize * 3, y - gamePanel.nodeSize / 2);
                for (edge_data e : G.getE(n.getKey())) {
                    g.setColor(Color.BLUE);
                    int x1, y1, x2, y2;
                    x1 = (int) scale(G.getNode(e.getSrc()).getLocation().x(), graphRange.xMin, graphRange.xMax, gamePanel.OFFSET, this.getWidth() - gamePanel.OFFSET);
                    x2 = (int) scale(G.getNode(e.getDest()).getLocation().x(), graphRange.xMin, graphRange.xMax, gamePanel.OFFSET, this.getWidth() - gamePanel.OFFSET);
                    y1 = (int) scale(G.getNode(e.getSrc()).getLocation().y(), graphRange.yMin, graphRange.yMax, gamePanel.OFFSET, this.getHeight() - gamePanel.OFFSET);
                    y2 = (int) scale(G.getNode(e.getDest()).getLocation().y(), graphRange.yMin, graphRange.yMax, gamePanel.OFFSET, this.getHeight() - gamePanel.OFFSET);
                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }

    private void printPokemons(Graphics g) {
        //g.setColor(Color.YELLOW);
        for (Pokemon p : pokemons) {
            DWGraph_DS.Position pos = p.get_pos();
            int x = (int) scale(pos.x(), graphRange.xMin, graphRange.xMax, gamePanel.OFFSET, this.getWidth() - gamePanel.OFFSET);
            int y = (int) scale(pos.y(), graphRange.yMin, graphRange.yMax, gamePanel.OFFSET, this.getHeight() - gamePanel.OFFSET);
            File file = new File("./icons/pokemon.png");
            Image img = null;
            try {
                img = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int imgSize = (int) Math.sqrt(this.getHeight() * this.getWidth()) / 20;
            //int textSize = imgSize / 5;
            img = img.getScaledInstance(imgSize, imgSize, Image.SCALE_SMOOTH);
            g.drawImage(img, x - imgSize / 2, y - imgSize / 2, null);
            g.setFont(new Font("Arial", Font.PLAIN, textSize));
            g.drawString("Value: " + p.get_value(), x - imgSize / 2, y - imgSize / 2);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, textSize));
            //g.drawString("Edge: " + p.get_edge().getSrc() + " -> " + p.get_edge().getDest(), x - imgSize / 2, y + imgSize);
            //g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
        }
    }

    /*
    Update pokemons from the server
     */
    protected void getPokemons() {
        JsonObject pokemonsJson = gson.fromJson(game.getPokemons(), JsonObject.class);
        JsonArray pokemonsJsonArray = pokemonsJson.get("Pokemons").getAsJsonArray();
        this.pokemons = new ArrayList<>();
        for (JsonElement e : pokemonsJsonArray) {
            pokemons.add(gson.fromJson(e.getAsJsonObject().get("Pokemon").toString(), Pokemon.class));
        }
        for (Pokemon p : pokemons) {
            if (p.get_edge() == null) {
                p.set_edge(findEdge(p.get_pos()));
            }
        }
    }

    /*
    This method checks if a position is on an edge that goes from src -> dest
     */
    private boolean isOnEdge(DWGraph_DS.Position src, DWGraph_DS.Position dest, DWGraph_DS.Position p) {
        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if (dist > d1 - gameArena.EPS) {
            ans = true;
        }
        return ans;
    }

    /*
    This method finds if a specific position is on an existing edge
    returns that edge if it exists, else returns null
     */
    private edge_data findEdge(DWGraph_DS.Position p) {
        for (node_data node : this.G.getV()) {
            for (edge_data edge : this.G.getE(node.getKey())) {
                if (isOnEdge((DWGraph_DS.Position) node.getLocation(), (DWGraph_DS.Position) this.G.getNode(edge.getDest()).getLocation(), p)) {
                    return edge;
                }
            }
        }
        return null;
    }

    private Gson JsonDeserializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
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

    private void getStats() {
        this.stats = gson.fromJson(this.game.toString(), HashMap.class);

    }

    private void printStats(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, textSize));
        g.setColor(Color.BLACK);
        int startPixel = 10;
        g.drawString("Moves: " + this.stats.get("Moves"), startPixel, this.getHeight() - 3 * textSize);

        g.drawString("Game level: " + this.stats.get("Game level"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 7;
        g.drawString("Pokemons: " + this.stats.get("Pokemons"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 7;
        g.drawString("Agents: " + this.stats.get("Agents"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 7;
        g.drawString("Graph: " + this.stats.get("Graph"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 8;
        g.drawString("Max user level: " + this.stats.get("Max user level"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 9;
        g.drawString("Is logged in: " + this.stats.get("Is logged in"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 9;
        if (this.stats.get("Is logged in") == "true")
            g.drawString("ID: " + this.stats.get("ID"), startPixel, this.getHeight() - 10);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (clicks < numOfAgents) {
            Point mousePoint = e.getPoint();
            for (Integer key : nodesMap.keySet()) {
                if ((Math.abs(nodesMap.get(key).x - mousePoint.x) < gamePanel.nodeSize * 2) &&
                        Math.abs(nodesMap.get(key).y - mousePoint.y) < gamePanel.nodeSize * 2) {
                    game.addAgent(key);
                    System.out.println(game.getAgents());
                    clicks++;

                    if (clicks >= numOfAgents) {
                        this.setVisible(false);
                        synchronized (this) {
                            notifyAll();
                        }
                        return;
                    }
                }
            }
        }
        //this.setVisible(false);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
