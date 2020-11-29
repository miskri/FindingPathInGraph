import java.util.LinkedList;

public class FrontController {

    public static void main(String[] args) {
        String tabulation = "\n";
        tabulation += new String((new char[150])).replace("\0", "=");

        Graph graph1 = new Graph("A");
        graph1.createRandomSimpleGraph(25, 25); // max (2500, 3123475)
        Graph graph2 = new Graph("B");
        graph2.createRandomSimpleGraph(25, 25); // max (2500, 3123475)

        System.out.println(tabulation + "\nALEKSEI ZAVORONKOV EXERCISE");
        GraphsSum graphsSum = new GraphsSum(graph1, graph2);
        Graph mainGraph = graphsSum.sum();
        System.out.println(mainGraph);

        ShortestPath shortestPath = new ShortestPath();
        Vertex vertexFrom = mainGraph.getRandomVertex();
        Vertex vertexTo = mainGraph.getRandomVertex();
        LinkedList<Arc> path = shortestPath.getPath(vertexFrom, vertexTo, false, mainGraph);
        System.out.println(tabulation + "\nBORISS DROZDOV EXERCISE\n");
        System.out.println(shortestPath.pathToString(path, vertexFrom, vertexTo) + tabulation);

        System.out.println("MIHHAIL SKRIPNIK EXERCISE\n");
        HeightPath heightPath = new HeightPath(mainGraph);
        heightPath.run("v1", "v10");
        System.out.println(tabulation);
    }
}