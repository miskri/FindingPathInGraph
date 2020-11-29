/**
 * Container class to different classes, that makes the whole
 * set of classes one class formally.
 */
public class GraphsSum {

    private Graph first;
    private Graph second;

    GraphsSum(Graph first, Graph second) {
        this.first = first;
        this.second = second;
    }

    public Graph sum() {
        return first.addGraph(second);
    }
}
