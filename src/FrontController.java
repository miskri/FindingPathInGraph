public class FrontController {

    public static void main(String[] args) {
        String tabulation = "\n";
        tabulation += new String((new char[150])).replace("\0", "=");

        Graph graph1 = new Graph("A");
        graph1.createRandomSimpleGraph(25, 25); // max (2500, 3123475)
        Graph graph2 = new Graph("B");
        graph2.createRandomSimpleGraph(25, 25); // max (2500, 3123475)

        ShortestPath shortestPath = new ShortestPath();
        Vertex vertexFrom = graph1.getRandomVertex();
        Vertex vertexTo = graph1.getRandomVertex();
        var path = shortestPath.getPath(vertexFrom, vertexTo, false, graph1);
        System.out.println(tabulation + "\nBORISS DROZDOV EXERCISE\n");
        System.out.println(shortestPath.pathToString(path, vertexFrom, vertexTo) + tabulation);

        System.out.println("MIHHAIL SKRIPNIK EXERCISE\n");
        HeightPath heightPath = new HeightPath(graph2);
        heightPath.run();
        System.out.println(tabulation);

        System.out.println("ALEKSEI ZAVORONKOV EXERCISE");
        GraphsSum graphsSum = new GraphsSum(graph1, graph2);
        Graph mainGraph = graphsSum.sum();
        System.out.println(mainGraph + tabulation);
    }
}