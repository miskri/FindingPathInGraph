/**
 * Container class to different classes, that makes the whole
 * set of classes one class formally.
 */
public class Matrix {

    private Graph graph;

    Matrix(Graph graph) {
        this.graph = graph;
    }

    public void run() {
        int[][] adjMatrix = graph.createAdjMatrix();
        transitiveClosureFloydWarshall(adjMatrix);
        createGraphFromAdjMatrix(adjMatrix);
        System.out.println(graph);
    }

    /**
     * Given an adjacency matrix of an unweighted directed graph,
     * find the shortest distances between every pair of vertices
     * by using the Floyd Warshall's algorithm.
     * Result of this function is a transitive closure of an input matrix.
     *
     * @param matrix an adjacency matrix of a graph
     *               (Every "1" in a matrix indicates a pair between two vertices)
     */
    public void transitiveClosureFloydWarshall(int[][] matrix) {
        for (int k = 0; k < matrix.length; k++) {
            for (int i = 0; i < matrix.length; i++) {
                if (matrix[i][k] == 1) {
                    for (int j = 0; j < matrix.length; j++) {
                        if (matrix[k][j] == 1) {
                            matrix[i][j] = 1;
                        }
                    }
                }
            }
        }
    }

    /**
     * Take an adjacency matrix and override the existing graph according
     * to the structure of the input matrix.
     *
     * @param matrix an adjacency matrix of a graph
     * (Every "1" in a matrix indicates a pair between two vertices)
     */
    public void createGraphFromAdjMatrix(int[][] matrix) {
        graph.first = null;
        graph.id += " GRAPH FROM MATRIX";
        Vertex[] vertices = new Vertex[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            vertices[i] = graph.createVertex(String.valueOf(matrix.length - 1 - i));
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] == 1) {
                    graph.createArc("", vertices[matrix.length - 1 - i], vertices[matrix.length - 1 - j], 0);
                }
            }
        }
    }
}
