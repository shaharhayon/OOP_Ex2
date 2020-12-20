package api;

import java.io.Serializable;
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

public class DWGraph_DS implements directed_weighted_graph, Serializable {

    public class Edge implements edge_data, Serializable {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;

            Edge edge = (Edge) o;

            if (src != edge.src) return false;
            if (dest != edge.dest) return false;
            if (tag != edge.tag) return false;
            if (Double.compare(edge.weight, weight) != 0) return false;
            return info != null ? info.equals(edge.info) : edge.info == null;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = src;
            result = 31 * result + dest;
            result = 31 * result + tag;
            temp = Double.doubleToLongBits(weight);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (info != null ? info.hashCode() : 0);
            return result;
        }
    }

    public static class Node implements node_data, Serializable {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;

            Node node = (Node) o;

            if (key != node.key) return false;
            if (tag != node.tag) return false;
            if (Double.compare(node.weight, weight) != 0) return false;
            if (info != null ? !info.equals(node.info) : node.info != null) return false;
            return location != null ? location.equals(node.location) : node.location == null;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = key;
            result = 31 * result + tag;
            temp = Double.doubleToLongBits(weight);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (info != null ? info.hashCode() : 0);
            result = 31 * result + (location != null ? location.hashCode() : 0);
            return result;
        }
    }

    public static class Position implements geo_location, Serializable {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Position)) return false;

            Position position = (Position) o;

            if (Double.compare(position.x, x) != 0) return false;
            if (Double.compare(position.y, y) != 0) return false;
            return Double.compare(position.z, z) == 0;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(x);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(y);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(z);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    private int nodeSize = 0, edgeSize = 0, MC = 0;

    private HashMap<Integer, node_data> nodes_list = new HashMap<>();

    private HashMap<Integer, List<edge_data>> edges_list = new HashMap<>();

    public DWGraph_DS() {

    }

    @Override
    public node_data getNode(int key) {
        return nodes_list.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        //return edges_list.get(src).g;
        List<edge_data> l = edges_list.get(src);
        for (edge_data e : l) {
            if (e.getDest() == dest) {
                return e;
            }
        }
        return null;
    }

    @Override
    public void addNode(node_data n) {
        nodes_list.put(n.getKey(), n);
        nodeSize++;
        MC++;
    }

    @Override
    public void connect(int src, int dest, double w) {
        if (edges_list.get(src) == null)
            edges_list.put(src, new ArrayList<>());
        if (getNode(src) != null && getNode(dest) != null && getEdge(src, dest) == null) {
            edges_list.get(src).add(new Edge(src, dest, w));
            edgeSize++;
            MC++;
        }
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
        if (this.getNode(key) == null)
            return null;
        List<int[]> toRemove = new ArrayList();
        node_data removedNode = getNode(key);
        for (int id : edges_list.keySet()) {
            for (edge_data edge : edges_list.get(id)) {
                if ((edge.getDest() == key) || (edge.getSrc() == key)) {
                    toRemove.add(new int[]{edge.getSrc(), edge.getDest()});
                }
            }
        }
        for (int[] arr : toRemove) {
            removeEdge(arr[0], arr[1]);
        }
        nodeSize--;
        MC++;
        nodes_list.remove(key);
        return removedNode;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        edge_data removedEdge = getEdge(src, dest);
        if (removedEdge != null) {
            edges_list.get(src).remove(removedEdge);
            edgeSize--;
            MC++;
        }
        return removedEdge;
    }

    @Override
    public int nodeSize() {
        return this.nodeSize;
        //return this.nodes_list.size();
    }

    @Override
    public int edgeSize() {
        return this.edgeSize;
    }

    @Override
    public int getMC() {
        return this.MC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DWGraph_DS)) return false;

        DWGraph_DS that = (DWGraph_DS) o;

        if (!nodes_list.equals(that.nodes_list)) return false;
        for (Integer key : edges_list.keySet()) {
            List<edge_data> l1 = edges_list.get(key);
            List<edge_data> l2 = that.edges_list.get(key);
            if (!(new HashSet<>(l1).equals(new HashSet<>(l2)))) return false;
        }
        return true;
        //return edges_list.equals(that.edges_list);
    }

    @Override
    public int hashCode() {
        int result = nodes_list.hashCode();
        result = 31 * result + edges_list.hashCode();
        return result;
    }
}
