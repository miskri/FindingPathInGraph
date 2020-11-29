/**
 * Vertex represents one of the objects that are connected together
 * (but can be also without connections) in Graph.
 */
public class Vertex {

    public final String id;
    public Vertex next;
    public Arc first;
    public int info = 0;
    public int height = Graph.HeightGenerator.getHeight(); // param height from custom height generator
    public int weight;


    Vertex(String s, Vertex v, Arc e) {
        id = s;
        next = v;
        first = e;
    }

    Vertex(String s) {
        this(s, null, null);
    }

    public boolean hasNext() {
        return next != null;
    }

    public Vertex getNext() {
        return next;
    }

    public Arc getFirst() {
        return first;
    }

    @Override
    public String toString() {
        return id;
    }
}