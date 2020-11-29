/**
 * Arc represents one arrow in the graph. Two-directional edges are
 * represented by two Arc objects (for both directions).
 */
public class Arc {

    public final String id;
    public Vertex target;
    public Arc next;
    public int info = 0;
    public int weight = 0;

    Arc(String s, Vertex v, Arc a) {
        id = s;
        target = v;
        next = a;
    }

    Arc(String s) {
        this(s, null, null);
    }

    @Override
    public String toString() {
        return id;
    }

    public Arc getNext() {
        return next;
    }
}