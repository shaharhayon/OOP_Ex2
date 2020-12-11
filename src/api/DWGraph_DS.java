package api;

import java.util.*;

/*public class Edge_Position implements edge_location {
    @Override
    public edge_data getEdge() {
        return null;
    }

    @Override
    public double getRatio() {
        return 0;
    }
}*/

public class DWGraph_DS implements directed_weighted_graph {

    public class Edge implements edge_data {
        private int src, dest, tag;
        private double weight;
        private String info;

        /*
        Constructors
         */
        public Edge(int src, int dest, double weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
            this.tag = 0;
            this.info = new String();
        }

        public Edge(int src, int dest, double weight, int tag) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
            this.tag = tag;
            this.info = new String();
        }

        public Edge(int src, int dest, double weight, String info) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
            this.tag = 0;
            this.info = info;
        }

        public Edge(int src, int dest, double weight, int tag, String info) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
            this.tag = tag;
            this.info = info;
        }
        /*
        END Constructors
         */

        @Override
        public int getSrc() {
            return this.src;
        }

        @Override
        public int getDest() {
            return this.dest;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public String getInfo() {
            return this.info;
        }

        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        @Override
        public int getTag() {
            return this.tag;
        }

        @Override
        public void setTag(int t) {
            this.tag = t;
        }
    }

    public static class Node implements node_data {
        private static int id = 0;

        private int key, tag;
        private double weight;
        private String info;
        private geo_location location;

        public Node() {
            this.key = id;
            id++;
        }

        public Node(int key) {
            this.key = key;
        }

        @Override
        public int getKey() {
            return this.key;
        }

        @Override
        public geo_location getLocation() {
            return this.location;
        }

        @Override
        public void setLocation(geo_location p) {
            this.location = p;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public void setWeight(double w) {
            this.weight = w;
        }

        @Override
        public String getInfo() {
            return this.info;
        }

        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        @Override
        public int getTag() {
            return this.tag;
        }

        @Override
        public void setTag(int t) {
            this.tag = t;
        }
    }

    public static class Position implements geo_location {
        double x, y, z;

        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        @Override
        public double x() {
            return this.x;
        }

        @Override
        public double y() {
            return this.y;
        }

        @Override
        public double z() {
            return this.z;
        }

        @Override
        public double distance(geo_location g) {
            return Math.sqrt(Math.pow(g.x() - this.x, 2) + Math.pow(g.y() - this.y, 2) + Math.pow(g.z() - this.z, 2));
        }

    }

    static int nodeSize = 0, edgeSize = 0, MC = 0;

    private HashMap<Integer, node_data> nodes_list = new HashMap<>();

    private HashMap<Integer, List<edge_data>> edges_list = new HashMap<>();

    /*public DWGraph_DS(HashMap<Integer, node_data> nodes_list,HashMap<Integer, List<edge_data>> edges_list) {
        this.nodes_list=nodes_list;
        this.edges_list=edges_list;
    }*/

    @Override
    public node_data getNode(int key) {
        return nodes_list.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        //return edges_list.get(src).g;
        List<edge_data> l = edges_list.get(src);
        for(edge_data e : l){
            if(e.getDest()==dest){
                return e;
            }
        }
        return null;
    }

    @Override
    public void addNode(node_data n) {
        nodes_list.put(n.getKey(), n);
    }

    @Override
    public void connect(int src, int dest, double w) {
        //HashMap<Integer, edge_data> edge = new HashMap<>();
        if (edges_list.get(src) == null)
            edges_list.put(src, new ArrayList<>());

        edges_list.get(src).add(new Edge(src, dest, w));
    }

    @Override
    public Collection<node_data> getV() {
        return nodes_list.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        /*ArrayList<edge_data> collection = new ArrayList<>();
        *//*for (List edgeList : edges_list.values())
            collection.addAll(edgeList);*/
        return edges_list.get(node_id);
    }

    @Override
    public node_data removeNode(int key) {
        node_data removedNode = getNode(key);
        for (int id : edges_list.keySet()) {
            for (edge_data edge : edges_list.get(id)) {
                if ((edge.getDest() == key) || (edge.getSrc() == key))
                    removeEdge(edge.getSrc(), edge.getDest());
            }
        }
        return removedNode;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        edge_data removedEdge = getEdge(src, dest);
        edges_list.get(src).remove(removedEdge);
        return removedEdge;
    }

    @Override
    public int nodeSize() {
        //return this.nodeSize;
        return this.nodes_list.size();
    }

    @Override
    public int edgeSize() {
        return this.edgeSize;
    }

    @Override
    public int getMC() {
        return this.MC;
    }
}
