import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Container class to different classes, that makes the whole
 * set of classes one class formally.
 */
public class ShortestPath {

    /**
     * Create a list of arcs which creates path from
     * start vertex to the end vertex
     *
     * @param from     start vertex
     * @param to       end vertex
     * @param smallest true if need path with smallest amount of arcs
     * @return first suitable path or path with smallest amount of arcs
     */
    public LinkedList<Arc> getPath(Vertex from, Vertex to, boolean smallest, Graph c) {
        setVerticesPathLength(from, c);
        LinkedList<Vertex> rightPath = new LinkedList<>();
        LinkedList<Vertex> path = new LinkedList<>();
        ArrayList<Vertex> deadlock = new ArrayList<>();
        Vertex vertex = to;
        int min = Integer.MAX_VALUE;
        path.add(vertex);
        Vertex checkVertex;
        do {
            checkVertex = vertex;
            Arc arc = vertex.first;
            int currentWeight = vertex.weight;
            while (arc != null) {
                if (arc.target.weight + arc.weight == currentWeight &&
                        !path.contains(arc.target) && !deadlock.contains(arc.target)) {
                    vertex = arc.target;
                    path.add(vertex);
                    break;
                }
                arc = arc.next;
            }
            if (vertex == from) {
                rightPath = path;
                if (!smallest) break;
                min = rightPath.size();
                path = new LinkedList<>();
                vertex = to;
                path.add(vertex);
            } else if (arc == null || path.size() >= min - 1) {
                deadlock.add(vertex);
                path = new LinkedList<>();
                vertex = to;
                path.add(vertex);
            }
        } while (vertex != checkVertex && min > 2);
        Collections.reverse(rightPath);
        return getArcPath(rightPath);
    }

    /**
     * Create arc path from right ordered vertices in path
     *
     * @param vertices right ordered vertices
     * @return path of arcs
     */
    public LinkedList<Arc> getArcPath(LinkedList<Vertex> vertices) {
        LinkedList<Arc> res = new LinkedList<>();
        for (int i = 0; i < vertices.size() - 1; i++) {
            Vertex vertex = vertices.get(i);
            Arc arc = vertex.first;
            Vertex next = vertices.get(i + 1);
            while (true) {
                if (arc.target == next) {
                    res.add(arc);
                    break;
                }
                arc = arc.next;
                if (arc == null) {
                    throw new RuntimeException("List has to contain only arcs in same path and in right order");
                }
            }
        }
        return res;
    }

    /**
     * Find length of the path to each vertex at the graph from start vertex
     *
     * @param from start vertex
     */
    public void setVerticesPathLength(Vertex from, Graph graph) {
        setVertexWeightAsMax(graph);
        LinkedList<Vertex> queue = new LinkedList<>();
        ArrayList<Vertex> seen = new ArrayList<>();
        queue.add(from);
        from.weight = 0;
        Vertex vertex;
        while (queue.size() != 0) {
            vertex = queue.removeFirst();
            seen.add(vertex);
            Arc arc = vertex.first;
            while (arc != null) {
                if (!seen.contains(arc.target) && !queue.contains(arc.target)) {
                    queue.add(arc.target);
                }
                if (arc.target.weight > arc.weight + vertex.weight) {
                    arc.target.weight = arc.weight + vertex.weight;
                    if (!queue.contains(arc.target)) {
                        seen.remove(arc.target);
                        queue.add(arc.target);
                    }
                }
                arc = arc.next;
            }
        }
        vertex = graph.first;
        while (vertex != null) {
            if (!seen.contains(vertex)) {
                throw new RuntimeException(String.format("Vertex %s has no connection to the root in graph", vertex.id));
            }
            vertex = vertex.next;
        }
    }

    /**
     * Transform list of path to string format
     * List is right ordered
     *
     * @param to   end vertex
     * @param from start vertex
     * @param path right ordered arcs
     * @return path in string format
     */
    public String pathToString(LinkedList<Arc> path, Vertex from, Vertex to) {
        if (from.equals(to)) {
            return "Path length to same vertex is zero";
        }
        LinkedList<String> res = new LinkedList<>();
        int length = 0;
        for (Arc arc : path) {
            res.add(String.format("(%s) ", length) + arc.id + String.format(" (%s)", length += arc.weight));
        }
        return String.format("Path length from %s to %s is %s \n", from, to, to.weight) +
                "Path of arcs: " + String.join(" --> ", res);
    }

    /**
     * Set all graph's vertices path length as max value
     */
    public void setVertexWeightAsMax(Graph c) {
        if (c.first == null) {
            throw new RuntimeException("No vertices");
        }
        Vertex vertex = c.first;
        while (vertex != null) {
            vertex.weight = Integer.MAX_VALUE;
            vertex = vertex.next;
        }
    }
}