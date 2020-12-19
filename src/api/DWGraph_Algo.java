package api;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {
    directed_weighted_graph G;

    public DWGraph_Algo() {

    }

    public DWGraph_Algo(directed_weighted_graph g) {
        G = g;
    }

    @Override
    public void init(directed_weighted_graph g) {
        this.G = g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return G;
    }

    /*
    copy method is implemented using Serialization.
    instead of writing to a file, we write to a byte array in the memory (which is faster),
    and then build the object (graph) again, resulting in a deep copy.

    implementation was referenced in StackOverflow.
     */
    @Override
    public directed_weighted_graph copy() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(G);
            oos.flush();
            oos.close();
            bos.close();
            byte[] byteData = bos.toByteArray();

            ByteArrayInputStream bis = new ByteArrayInputStream(byteData);
            return (directed_weighted_graph) (new ObjectInputStream(bis).readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    this function is using a version of bfs algorithm, which is adapted for directed graphs.
     */
    @Override
    public boolean isConnected() {
        for (node_data n : G.getV()) {
            bfs(n.getKey());
            for (node_data node : G.getV()) {
                if (node.getTag() == 0)
                    return false;
            }
        }
        return true;
    }

    @Override
    public synchronized double shortestPathDist(int src, int dest) {
        return shortestPath(src, dest).size();
    }

    /*
    Comperator for a Priority Queue
     */
    Comparator<node_data> weightComperator = (o1, o2) -> {
        if (o1.getWeight() > o2.getWeight()) return 1;
        else if (o1.getWeight() < o2.getWeight()) return -1;
        else return 0;
    };

    /*
    shortestPath is implemented using Dijkstra's algorithm,
    using a PriorityQueue as a heap for better performance.
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        reset();
        for (node_data node : G.getV()) {
            node.setWeight(Double.POSITIVE_INFINITY);
        }

        DWGraph_DS.Node currentNode = (DWGraph_DS.Node) G.getNode(src);
        currentNode.setWeight(0);
        PriorityQueue<node_data> unvisitedNodes = new PriorityQueue(G.nodeSize(), weightComperator);
        unvisitedNodes.addAll(G.getV());
        HashMap<Integer, node_data> parent = new HashMap<>();
        parent.put(src, null);

        while (currentNode.getWeight() != Double.POSITIVE_INFINITY) {
            if (G.getNode(dest).getTag() == 1) {
                break;
            }
            for (edge_data edge : G.getE(currentNode.getKey())) {
                DWGraph_DS.Node neighbor = (DWGraph_DS.Node) G.getNode(edge.getDest());
                double tmpWeight = currentNode.getWeight() + edge.getWeight();
                if (tmpWeight < neighbor.getWeight()) {
                    neighbor.setWeight(tmpWeight);
                    unvisitedNodes.remove(neighbor);
                    unvisitedNodes.add(neighbor);
                    parent.put(neighbor.getKey(), currentNode);
                }
            }
            currentNode.setTag(1);
            if(currentNode.getKey()==dest) break;
            unvisitedNodes.remove(currentNode);
            currentNode = (DWGraph_DS.Node) unvisitedNodes.poll();
        }
        /*
        Rebuild the path list
         */
        if (!parent.keySet().contains(dest)) return null;
        List<node_data> pathList = new ArrayList<>();
        currentNode = (DWGraph_DS.Node) G.getNode(dest);
        while (parent.get(currentNode.getKey()) != null) {
            pathList.add(currentNode);
            currentNode = (DWGraph_DS.Node) parent.get(currentNode.getKey());
        }
        Collections.reverse(pathList);
        return pathList;
    }

    /*
    save and load methods are using Gson library
     */
    @Override
    public boolean save(String file) {
        /*
        Create a builder for the specific JSON format
         */
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        JsonSerializer<DWGraph_DS> serializer = new JsonSerializer<DWGraph_DS>() {
            @Override
            public JsonElement serialize(DWGraph_DS dwGraph_ds, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonGraph = new JsonObject();
                jsonGraph.add("Nodes", new JsonArray());
                jsonGraph.add("Edges", new JsonArray());

                for (node_data node : G.getV()) {
                    JsonObject jsonNodeObject = new JsonObject();
                    JsonObject jsonEdgeObject = new JsonObject();
                    StringBuilder pos = new StringBuilder();
                    pos.append(node.getLocation().x());
                    pos.append(',');
                    pos.append(node.getLocation().y());
                    pos.append(',');
                    pos.append(node.getLocation().z());
                    jsonNodeObject.addProperty("pos", pos.toString());
                    jsonNodeObject.addProperty("id", node.getKey());
                    jsonNodeObject.addProperty("info", node.getInfo());
                    jsonNodeObject.addProperty("tag", node.getTag());
                    jsonGraph.get("Nodes").getAsJsonArray().add(jsonNodeObject);

                    for (edge_data e : G.getE(node.getKey())) {
                        jsonEdgeObject.addProperty("src", e.getSrc());
                        jsonEdgeObject.addProperty("w", e.getWeight());
                        jsonEdgeObject.addProperty("dest", e.getDest());
                        jsonEdgeObject.addProperty("info", e.getInfo());
                        jsonEdgeObject.addProperty("tag", e.getTag());
                        jsonGraph.get("Edges").getAsJsonArray().add(jsonEdgeObject);
                    }
                }
                return jsonGraph;
            }
        };
        gsonBuilder.registerTypeAdapter(DWGraph_DS.class, serializer);
        Gson graphGson = gsonBuilder.create();
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.write(graphGson.toJson(G));
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean load(String file) {
        /*
        Read JSON file to a string
         */
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            line = br.readLine();
            while (line != null) {
                jsonString.append(line);
                line = br.readLine();
            }
            br.close();
            /*
            Create a builder for the specific JSON format
             */
            GsonBuilder gsonBuilder = new GsonBuilder();
            JsonDeserializer<DWGraph_DS> deserializer = new JsonDeserializer<DWGraph_DS>() {
                @Override
                public DWGraph_DS deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
                    JsonObject jsonObject = json.getAsJsonObject();
                    DWGraph_DS graph = new DWGraph_DS();
                    JsonArray Nodes = jsonObject.getAsJsonArray("Nodes");
                    JsonArray Edges = jsonObject.getAsJsonArray("Edges");
                    Iterator<JsonElement> iterNodes = Nodes.iterator();
                    while (iterNodes.hasNext()) {
                        JsonElement node = iterNodes.next();

                        graph.addNode(new DWGraph_DS.Node(node.getAsJsonObject().get("id").getAsInt()));

                        String coordinates[] = node.getAsJsonObject().get("pos").getAsString().split(",");
                        double coordinatesAsDouble[] = {0, 0, 0};
                        for (int i = 0; i < 3; i++) {
                            coordinatesAsDouble[i] = Double.parseDouble(coordinates[i]);
                        }
                        DWGraph_DS.Position pos = new DWGraph_DS.Position(coordinatesAsDouble[0], coordinatesAsDouble[1], coordinatesAsDouble[2]);
                        graph.getNode(node.getAsJsonObject().get("id").getAsInt()).setLocation(pos);
                    }
                    Iterator<JsonElement> iterEdges = Edges.iterator();
                    int src, dest;
                    double w;
                    while (iterEdges.hasNext()) {
                        JsonElement edge = iterEdges.next();
                        src = edge.getAsJsonObject().get("src").getAsInt();
                        dest = edge.getAsJsonObject().get("dest").getAsInt();
                        w = edge.getAsJsonObject().get("w").getAsDouble();
                        graph.connect(src, dest, w);
                    }
                    return graph;
                }
            };
            gsonBuilder.registerTypeAdapter(DWGraph_DS.class, deserializer);
            Gson graphGson = gsonBuilder.create();
            G = graphGson.fromJson(jsonString.toString(), DWGraph_DS.class);
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
    Same as the normal load method, but loading from a string rather than a file.
    */
    public boolean loadfromString(String str) {

            GsonBuilder gsonBuilder = new GsonBuilder();
            JsonDeserializer<DWGraph_DS> deserializer = new JsonDeserializer<DWGraph_DS>() {
                @Override
                public DWGraph_DS deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
                    JsonObject jsonObject = json.getAsJsonObject();
                    DWGraph_DS graph = new DWGraph_DS();
                    JsonArray Nodes = jsonObject.getAsJsonArray("Nodes");
                    JsonArray Edges = jsonObject.getAsJsonArray("Edges");
                    Iterator<JsonElement> iterNodes = Nodes.iterator();
                    while (iterNodes.hasNext()) {
                        JsonElement node = iterNodes.next();

                        graph.addNode(new DWGraph_DS.Node(node.getAsJsonObject().get("id").getAsInt()));

                        String coordinates[] = node.getAsJsonObject().get("pos").getAsString().split(",");
                        double coordinatesAsDouble[] = {0, 0, 0};
                        for (int i = 0; i < 3; i++) {
                            coordinatesAsDouble[i] = Double.parseDouble(coordinates[i]);
                        }
                        DWGraph_DS.Position pos = new DWGraph_DS.Position(coordinatesAsDouble[0], coordinatesAsDouble[1], coordinatesAsDouble[2]);
                        graph.getNode(node.getAsJsonObject().get("id").getAsInt()).setLocation(pos);
                    }
                    Iterator<JsonElement> iterEdges = Edges.iterator();
                    int src, dest;
                    double w;
                    while (iterEdges.hasNext()) {
                        JsonElement edge = iterEdges.next();
                        src = edge.getAsJsonObject().get("src").getAsInt();
                        dest = edge.getAsJsonObject().get("dest").getAsInt();
                        w = edge.getAsJsonObject().get("w").getAsDouble();
                        graph.connect(src, dest, w);
                    }
                    return graph;
                }
            };
            gsonBuilder.registerTypeAdapter(DWGraph_DS.class, deserializer);
            Gson graphGson = gsonBuilder.create();
            G = graphGson.fromJson(str.toString(), DWGraph_DS.class);
            return true;
        }

    /*
    Standard bfs searching algorithm
     */
    private void bfs(int nodeKey) {
        Queue<Integer> q = new LinkedList<>();
        // initialize all the nodes
        for (node_data node : G.getV())
            node.setTag(0);

        int currentNode = nodeKey;

        // iterate the graph and mark nodes that have been visited
        while (G.getNode(currentNode) != null) {
            for (edge_data edge : G.getE(currentNode)) {
                node_data dest = G.getNode(edge.getDest());
                if (dest.getTag() == 0) {
                    q.add(dest.getKey());
                }
                G.getNode(currentNode).setTag(1);
            }
            currentNode = q.poll();
        }
    }

    private void reset() {
        for (node_data node : G.getV()) {
            node.setTag(0);
            node.setWeight(0);
            node.setInfo(new String());
        }
    }
}
