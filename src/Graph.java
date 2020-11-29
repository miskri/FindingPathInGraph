import java.util.*;

/**
 * Class which represents Graph itself with vertexes and arcs.
 */
public class Graph {

    public String id;
    public Vertex first;
    public int info = 0;

    Graph(String s, Vertex v) {
        id = s;
        first = v;
    }

    Graph(String s) {
        this(s, null);
    }

    @Override
    public String toString() {
        if (!hasFirst()) return id + " is empty" + "\n";
        String nl = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer(nl);
        sb.append(id);
        sb.append(nl);
        Vertex v = first;
        while (v != null) {
            sb.append(v.toString());
            sb.append(" -->");
            Arc a = v.first;
            while (a != null) {
                sb.append(" ");
                sb.append(a.toString());
                sb.append(" (");
                sb.append(v.toString());
                sb.append("->");
                sb.append(a.target.toString());
                sb.append(")");
                a = a.next;
            }
            sb.append(nl);
            v = v.next;
        }
        return sb.toString();
    }

    public Vertex createVertex(String vid) {
        Vertex res = new Vertex(vid);
        res.next = first;
        first = res;
        return res;
    }

    public void createVertex(String vid, Vertex previousVertex) {
        previousVertex.next = new Vertex(vid);
    }

    public Arc createArc(String aid, Vertex from, Vertex to) {
        Arc res = new Arc(aid);
        res.next = from.first;
        from.first = res;
        res.target = to;
        return res;
    }

    public Arc createArc(String aid, Vertex from, Vertex to, int weight) {
        Arc res = new Arc(aid);
        res.weight = weight;
        res.next = from.first;
        from.first = res;
        res.target = to;
        return res;
    }

    /**
     * Create a connected undirected random tree with n vertices.
     * Each new vertex is connected to some random existing vertex.
     *
     * @param n number of vertices added to this graph
     */
    /**
     * Create a connected undirected random tree with n vertices.
     * Each new vertex is connected to some random existing vertex.
     *
     * @param n number of vertices added to this graph
     */
    public void createRandomTree(int n) {
        if (n <= 0)
            return;
        Vertex[] varray = new Vertex[n];
        for (int i = 0; i < n; i++) {
            varray[i] = createVertex("v" + (n - i));
            if (i > 0) {
                int vnr = (int) (Math.random() * i);
                int randomLength = (int) (Math.random() * n);
                createArc("a" + varray[vnr].toString() + "_"
                        + varray[i].toString(), varray[vnr], varray[i], randomLength);
                createArc("a" + varray[i].toString() + "_"
                        + varray[vnr].toString(), varray[i], varray[vnr], randomLength);
            }
        }
    }

    /**
     * Create an adjacency matrix of this graph.
     * Side effect: corrupts info fields in the graph
     *
     * @return adjacency matrix
     */
    public int[][] createAdjMatrix() {
        info = 0;
        Vertex v = first;
        while (v != null) {
            v.info = info++;
            v = v.next;
        }
        int[][] res = new int[info][info];
        v = first;
        while (v != null) {
            int i = v.info;
            Arc a = v.first;
            while (a != null) {
                int j = a.target.info;
                res[i][j]++;
                a = a.next;
            }
            v = v.next;
        }
        return res;
    }

    /**
     * Create a connected simple (undirected, no loops, no multiple
     * arcs) random graph with n vertices and m edges.
     *
     * @param n number of vertices
     * @param m number of edges
     */
    public void createRandomSimpleGraph(int n, int m) {
        if (n <= 0)
            return;
        if (n > 2500)
            throw new IllegalArgumentException("Too many vertices: " + n);
        if (m < n - 1 || m > n * (n - 1) / 2)
            throw new IllegalArgumentException
                    ("Impossible number of edges: " + m);
        first = null;
        createRandomTree(n);       // n-1 edges created here
        Vertex[] vert = new Vertex[n];
        Vertex v = first;
        int c = 0;
        while (v != null) {
            vert[c++] = v;
            v = v.next;
        }
        int[][] connected = createAdjMatrix();
        int edgeCount = m - n + 1;  // remaining edges
        while (edgeCount > 0) {
            int i = (int) (Math.random() * n);  // random source
            int j = (int) (Math.random() * n);  // random target
            if (i == j)
                continue;  // no loops
            if (connected[i][j] != 0 || connected[j][i] != 0)
                continue;  // no multiple edges
            Vertex vi = vert[i];
            Vertex vj = vert[j];
            int randomLength = (int) (Math.random() * n);
            createArc("a" + vi.toString() + "_" + vj.toString(), vi, vj, randomLength);
            connected[i][j] = 1;
            createArc("a" + vj.toString() + "_" + vi.toString(), vj, vi, randomLength);
            connected[j][i] = 1;
            edgeCount--;  // a new edge happily created
        }
    }

    public boolean hasFirst() {
        return first != null;
    }

    public Vertex getFirst() {
        return first;
    }

    /**
     * [MY TASK]
     * Add Graph to the existing Graph.
     *
     * @param graphToAdd Graph which will be added
     * @return result of two Graphs addition
     */
    public Graph addGraph(Graph graphToAdd) {

        if (!hasFirst()) throw new RuntimeException(String.format("Graph %s is empty.", this.id));
        if (!graphToAdd.hasFirst())
            throw new RuntimeException(String.format("Can't add empty [Graph %s] to [Graph %s]", graphToAdd.id, this.id));

        id = id + " + " + graphToAdd.id;

        addAllNewVertexes(graphToAdd);

        Vertex homeVertex = getFirst();
        Vertex guestVertex = graphToAdd.getFirst();
        while (guestVertex != null) {
            if (homeVertex.id.equals(guestVertex.id)) {
                synchroniseAllArks(guestVertex, homeVertex);
                guestVertex = guestVertex.getNext();
                homeVertex = homeVertex.getNext();
            } else if (homeVertex.hasNext()) {
                homeVertex = homeVertex.getNext();
            } else {
                break;
            }
        }

        return this;
    }

    /**
     * [addGraph help method]
     * Add to the Vertex in existing Graph all Arcs which it doesn't have.
     *
     * @param guestVertex Vertex from Graph which will be added
     * @param homeVertex  Vertex from Graph to which we add
     */
    private void synchroniseAllArks(Vertex guestVertex, Vertex homeVertex) {

        Arc guestArc = guestVertex.getFirst();

        while (guestArc != null) {

            if (!checkArcExistence(guestArc, homeVertex)) {
                createArc("a" + homeVertex.id + "_" + guestArc.target.id, homeVertex, getTargetVertex(guestArc.target));
            }

            guestArc = guestArc.getNext();
        }
    }

    /**
     * [synchroniseAllArks help method]
     * Find in existing Graph Vertex with the specific id.
     *
     * @param targetVertex Vertex from Graph which will be added
     * @return Vertex (with the same ID as @param targetVertex has) from Graph to which we add
     */
    private Vertex getTargetVertex(Vertex targetVertex) {

        Vertex currentVertex = getFirst();

        while (currentVertex != null) {

            if (currentVertex.id.equals(targetVertex.id)) return currentVertex;

            currentVertex = currentVertex.getNext();
        }

        throw new RuntimeException("Was unexpected situation in search of Vertex!");
    }

    /**
     * [synchroniseAllArks help method]
     * Check if existing Graph already has current Arc.
     *
     * @param guestArc   Arc from Graph which will be added
     * @param homeVertex Vertex from Graph to which we add
     * @return if @param homeVertex has @param guestArc
     */
    private boolean checkArcExistence(Arc guestArc, Vertex homeVertex) {

        Arc homeArc = homeVertex.getFirst();

        while (homeArc != null) {

            if (guestArc.target.id.equals(homeArc.target.id)) return true;
            homeArc = homeArc.getNext();
        }

        return false;
    }

    /**
     * [addGraph help method]
     * Add to the existing Graph all Vertexes which it doesn't have.
     *
     * @param graphToAdd Graph which will be added
     */
    private void addAllNewVertexes(Graph graphToAdd) {

        Vertex currentVertex = graphToAdd.getFirst();

        while (currentVertex != null) {

            Vertex previousVertex;
            try {
                previousVertex = checkForVertexExistence(currentVertex);
                createVertex(currentVertex.id, previousVertex);
            } catch (RuntimeException ignored) {
            }

            currentVertex = currentVertex.getNext();
        }

    }

    /**
     * [addAllNewVertexes help method]
     * Check if existing Graph already has current Vertex.
     *
     * @param vertexToCheck Vertex from Graph which will be added
     * @return Vertex (with the same ID as @param vertexToCheck has) from Graph to which we add
     */
    private Vertex checkForVertexExistence(Vertex vertexToCheck) {

        Vertex currentVertex = getFirst();
        while (currentVertex != null) {
            if (currentVertex.id.equals(vertexToCheck.id)) throw new RuntimeException("This Vertex exists!");
            if (currentVertex.hasNext()) currentVertex = currentVertex.getNext();
            else return currentVertex;
        }
        throw new RuntimeException("Was unexpected situation in Graph when added Vertexes!");
    }

    /**
     * Find all Arcs in current Graph.
     *
     * @param graph Graph which is one of the term
     * @return array of Arcs IDs which contains this Graph
     */
    private ArrayList<String> getAllArcsId(Graph graph) {

        ArrayList<String> arcsId = new ArrayList<>();
        Vertex currentVertex = graph.getFirst();

        while (currentVertex != null) {
            Arc currentArc = currentVertex.getFirst();
            while (currentArc != null) {
                arcsId.add(currentArc.id);
                currentArc = currentArc.getNext();
            }
            currentVertex = currentVertex.getNext();
        }
        return arcsId;
    }

    /**
     * Remove duplicates from list of IDs.
     *
     * @param first  array of (Vertex or Arc) IDs
     * @param second array of (Vertex or Arc) IDs
     * @return array which consists of @param first and @param @second IDs (all duplicates are removed)
     */
    private ArrayList<String> uniteAllId(ArrayList<String> first, ArrayList<String> second) {
        ArrayList<String> vertexesIdNoDuplicates;
        ArrayList<String> big;

        if (first.size() >= second.size()) {
            vertexesIdNoDuplicates = second;
            big = first;
        } else {
            vertexesIdNoDuplicates = first;
            big = second;
        }

        for (String s : big) {
            if (!vertexesIdNoDuplicates.contains(s)) vertexesIdNoDuplicates.add(s);
        }

        return vertexesIdNoDuplicates;
    }

    /**
     * Get random vertex from the graph
     *
     * @return random vertex
     */
    public Vertex getRandomVertex() {
        Vertex last = first;
        int random = (int) (Math.random() * 1000);
        while (last.next != null) {
            if (random < 300) {
                return last;
            }
            last = last.next;
            random = (int) (Math.random() * 1000);
        }
        return last;
    }

    /** Class that generates realistic elevation values ranging from -7 to 5642 meters
     * (the lowest and highest point in Europe above sea level). */
    static class HeightGenerator {
        static int previousHeight = 143; // 143m height of the center of Tallinn above sea level
        static int maxValueElevation = 10; // 10m maximal difference between 2 vertices
        static int heightMin = -7; // The lowest point -7m is in the north of Rotterdam
        static int heightMax = 5642; // The height of Mount Elbrus is 5642m above sea level

        /** Method that randomly generates the height for a point, taking into account the above parameters. */
        static int getHeight() {
            Random rand = new Random();
            int value = rand.nextInt(maxValueElevation);
            int direction = rand.nextBoolean() ? 1 : -1;
            value *= direction;
            if (previousHeight + value >= heightMin && previousHeight + value <= heightMax) {
                previousHeight += value;
            } else if (previousHeight + value < heightMin) {
                previousHeight = heightMin;
            } else {
                previousHeight = heightMax;
            }
            return previousHeight;
        }
    }
}