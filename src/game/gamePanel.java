package game;

import api.DWGraph_Algo;
import api.DWGraph_DS;
import api.*;
import com.google.gson.*;
//import gameClient.CL_Pokemon;
//import gameClient.util.Point3D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class gamePanel extends JPanel implements MouseListener {
    protected final static int OFFSET = 100;
    protected final static int nodeSize = 10;
    protected final static DecimalFormat df = new DecimalFormat("#.###");

    private int textSize;
    private HashMap<Integer, Point> nodesMap = new HashMap<>();
    public gameArena arena;
    Range graphRange;
    List<Pokemon> pokemons = new ArrayList<>();
    Gson gson;

    static class Range {
        double xMin, yMin, xMax, yMax;

        public Range(double xMin, double yMin, double xMax, double yMax) {
            this.xMin = xMin;
            this.yMin = yMin;
            this.xMax = xMax;
            this.yMax = yMax;
        }


    }

    public gamePanel() {
        super();
        this.arena = gameArena.getArena();
        this.gson=gameJsonAdapter.getGson();
        this.setBackground(Color.LIGHT_GRAY);
        this.setSize(800, 800);
        this.addMouseListener(this);
        this.setVisible(true);
        graphRange = graphRange(arena.G);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.revalidate();
        this.repaint();
        textSize = ((int) Math.sqrt(this.getHeight() * this.getWidth()) / 60);
        long time = arena.game.timeToEnd();
        g.drawString("Time left: "+time/1000, 10, textSize);
        printNodes(g);
        printPokemons(g);
        printAgents(g);
        arena.getStats();
        printStats(g);

        if(time < 0){
            int score =0;
            for(Agent a : arena.agents)
                score+=a.get_value();
            g.setFont(new Font("Arial", Font.BOLD, textSize*5));
            g.drawString("Score: " + score,this.getWidth()/2 - textSize*8,this.getHeight()/2);
        }
    }

    private void printNodes(Graphics g) {
        for (node_data n : arena.G.getV()) {
            g.setColor(Color.BLACK);
            int x = (int) scale(n.getLocation().x(), graphRange.xMin, graphRange.xMax, OFFSET, this.getWidth() - OFFSET);
            int y = (int) scale(n.getLocation().y(), graphRange.yMin, graphRange.yMax, OFFSET, this.getHeight() - OFFSET);
            nodesMap.put(n.getKey(), new Point(x, y));
            g.fillOval(x - (nodeSize / 2), y - (nodeSize / 2), nodeSize, nodeSize);
            g.setFont(new Font("Arial", Font.BOLD, textSize));
            g.drawString("Node " + n.getKey(), x - (int) (textSize * 1.5), y - (int) (textSize * 1.5));
            //g.drawString("(" + df.format(n.getLocation().x()) + "," + df.format(n.getLocation().y()) + ")", x - textSize * 3, y - nodeSize / 2);
            for (edge_data e : arena.G.getE(n.getKey())) {
                g.setColor(Color.BLUE);
                int x1, y1, x2, y2;
                x1 = (int) scale(arena.G.getNode(e.getSrc()).getLocation().x(), graphRange.xMin, graphRange.xMax, OFFSET, this.getWidth() - OFFSET);
                x2 = (int) scale(arena.G.getNode(e.getDest()).getLocation().x(), graphRange.xMin, graphRange.xMax, OFFSET, this.getWidth() - OFFSET);
                y1 = (int) scale(arena.G.getNode(e.getSrc()).getLocation().y(), graphRange.yMin, graphRange.yMax, OFFSET, this.getHeight() - OFFSET);
                y2 = (int) scale(arena.G.getNode(e.getDest()).getLocation().y(), graphRange.yMin, graphRange.yMax, OFFSET, this.getHeight() - OFFSET);
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

    private void printPokemons(Graphics g) {
        g.setColor(Color.YELLOW);
        getPokemons();
        ListIterator<Pokemon> iterator = pokemons.listIterator();
        while(iterator.hasNext()){
            Pokemon p=iterator.next();
            DWGraph_DS.Position pos = p.get_pos();
            int x = (int) scale(pos.x(), graphRange.xMin, graphRange.xMax, OFFSET, this.getWidth() - OFFSET);
            int y = (int) scale(pos.y(), graphRange.yMin, graphRange.yMax, OFFSET, this.getHeight() - OFFSET);
            File file = new File("./icons/pokemon.png");
            Image img = null;
            try {
                img = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int imgSize = (int) Math.sqrt(this.getHeight() * this.getWidth()) / 20;
            img = img.getScaledInstance(imgSize, imgSize, Image.SCALE_SMOOTH);
            g.drawImage(img, x - imgSize / 2, y - imgSize / 2, null);
            g.setFont(new Font("Arial", Font.PLAIN, textSize));
            //g.drawString("Value: " + p.get_value(), x - imgSize / 2, y - imgSize / 5);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, textSize));
        }
    }


    private void printAgents(Graphics g) {
        for (Agent a : arena.agents) {
            DWGraph_DS.Position pos = a.get_pos();
            int x = (int) scale(pos.x(), graphRange.xMin, graphRange.xMax, OFFSET, this.getWidth() - OFFSET);
            int y = (int) scale(pos.y(), graphRange.yMin, graphRange.yMax, OFFSET, this.getHeight() - OFFSET);

            File file = new File("./icons/agent.png");
            Image img = null;
            try {
                img = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int imgSize = (int) Math.sqrt(this.getHeight() * this.getWidth()) / 20;
            int textSize = (int) (imgSize / 3);
            img = img.getScaledInstance(imgSize, imgSize, Image.SCALE_SMOOTH);
            g.drawImage(img, x - imgSize / 2, y - imgSize / 2, null);
            g.setFont(new Font("Arial", Font.PLAIN, textSize));
            g.setColor(Color.BLACK);
            /*g.drawString("Agent " + a.getID(), x - imgSize / 2, y + imgSize);
            g.drawString("Value: " + a.get_value(), x - imgSize / 2, y + imgSize + textSize);
            g.drawString("Source: " + a.get_src().getKey(), x - imgSize / 2, y + imgSize + 2 * textSize);
            if (a.get_dest() != null)
                g.drawString("Destination: " + a.get_dest().getKey(), x - imgSize / 2, y + imgSize + 3 * textSize);
            g.drawString("Speed: " + a.get_speed(), x - imgSize / 2, y + imgSize + 4 * textSize);
            g.setFont(new Font("Arial", Font.PLAIN, textSize));
            if(arena.agentsToPokemons.get(a)!=null)
            g.drawString("Chasing: " + arena.agentsToPokemons.get(a).get_edge().getSrc() + " -> " + arena.agentsToPokemons.get(a).get_edge().getDest(), x - imgSize / 2, y + imgSize + 5 * textSize);*/
        }
    }

    private void printStats(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, textSize));
        g.setColor(Color.BLACK);

        g.drawString("Grade: " + arena.stats.get("Grade"), this.getWidth()-100, 10);

        int startPixel = 10;
        g.drawString("Moves: " + arena.stats.get("Moves"), startPixel, this.getHeight() - 3 * textSize);

        g.drawString("Game level: " + arena.stats.get("Game level"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 7;
        g.drawString("Pokemons: " + arena.stats.get("Pokemons"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 7;
        g.drawString("Agents: " + arena.stats.get("Agents"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 7;
        g.drawString("Graph: " + arena.stats.get("Graph"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 8;
        g.drawString("Max user level: " + arena.stats.get("Max user level"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 9;
        g.drawString("Is logged in: " + arena.stats.get("Is logged in"), startPixel, this.getHeight() - 10);
        startPixel += textSize * 9;
        if (arena.stats.get("Is logged in") == "true")
            g.drawString("ID: " + arena.stats.get("ID"), startPixel, this.getHeight() - 10);
    }

    /*
    Scale coordinates
     */
    private double scale(double n, double MIN_INPUT, double MAX_INPUT, double MIN_OUTPUT, double MAX_OUTPUT) {
        double tmp = (((n - MIN_INPUT) / (MAX_INPUT - MIN_INPUT)) * (MAX_OUTPUT - MIN_OUTPUT) + MIN_OUTPUT);
        return tmp;
    }

    /*
    Find coordinate range according to the selected graph
     */
    private Range graphRange(directed_weighted_graph g) {
        double xMin = Double.MAX_VALUE, yMin = Double.MAX_VALUE, xMax = 0, yMax = 0;
        for (node_data p : g.getV()) {
            DWGraph_DS.Position point = (DWGraph_DS.Position) p.getLocation();
            if (point.x() < xMin) xMin = point.x();
            if (point.y() < yMin) yMin = point.y();
            if (point.x() > xMax) xMax = point.x();
            if (point.y() > yMax) yMax = point.y();
        }
        Range range = new Range(xMin, yMin, xMax, yMax);
        return range;
    }

    protected synchronized void getPokemons() {
        JsonObject pokemonsJson = gson.fromJson(arena.game.getPokemons(), JsonObject.class);
        JsonArray pokemonsJsonArray = pokemonsJson.get("Pokemons").getAsJsonArray();
        List<Pokemon> newPokemons = new ArrayList<>();
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
                p.set_edge(arena.findEdge(p.get_pos()));
            }
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {

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
