import java.util.*;

/** Container class to different classes, that makes the whole
 * set of classes one class formally. */
public class GraphTask {

   /** Main method. */
   public static void main (String[] args) {
      GraphTask a = new GraphTask();
      a.run();
   }

   /** Actual main method to run examples and everything. */
   public void run() {
      Graph g = new Graph ("A");
      g.createRandomSimpleGraph (10, 45); // max (2500, 3123475)
      System.out.println(g.toString());
      Path optimizedPath = getOptimizedPath(g, "v1", "v2"); // find path from first vertex with id v1 to vertex with id v2
      System.out.println("\nArc representation of best path:\n" + optimizedPath.printArcPath());
      System.out.println("\nVertex representation of best path:\n" + optimizedPath.toString());
   }

   /** Method that returns the best possible path, where the highest point is the lowest.
    * @param source an object containing vertices and arcs.
    * @param startId a vertex id value that is the starting point of the path search.
    * @param destinationId the vertex id value that is the end point of the path.
    * @return Path class object that contains vertices and arcs from start point to destination point.
    */
   public Path getOptimizedPath(Graph source, String startId, String destinationId) {
      GraphPathManager graphPathManager = new GraphPathManager();
      Vertex start = findVertex(source, startId);
      Vertex destination = findVertex(source, destinationId);
      List<Path> paths = new ArrayList<>();
      int index = 0;
      long startTime = System.currentTimeMillis();

      while (true) {
         if (source.findPath(graphPathManager, start, destination)) {
            graphPathManager.pathPointsCorrection(startId, destinationId);
            paths.add(index, graphPathManager.getPath());
            index++;
         } else {
            break;
         }
      }

      int highestPoint = 10000;
      Path bestPath = null;
      for (Path path : paths) {
         //System.out.println(path.toString()); // use it if you want to see all possible vertex paths
         //System.out.println(path.printArcPath()); // use it if you want to see all possible arc paths
         if (path.highestPoint <= highestPoint) {
            highestPoint = path.highestPoint;
            bestPath = path;
         }
      }

      System.out.printf("Time spent - %,d ms", System.currentTimeMillis() - startTime);
      System.out.println("\nPath from " + startId + " to " + destinationId);
      System.out.println("Paths found: " + paths.size());

      assert bestPath != null;
      return bestPath;
   }

   /** Custom exception class for error displaying that occur when finding a path.  */
   class GraphPathException extends RuntimeException {

      public GraphPathException(String message, String startId, String endId) {
         super(message + "\nStart vertex: " + startId + "\nDestination vertex: " + endId);
      }
   }

   /** Custom exception class for error displaying that occur when finding a vertex or arc in graph.  */
   class GraphException extends RuntimeException {

      public GraphException(String message) {
         super(message);
      }
   }

   /** Method searches for the required vertex in the given graph, if there is no such vertex
    * it returns GraphException.
    * @param source search graph.
    * @param targetId id of the requested vertex. */
   public Vertex findVertex(Graph source, String targetId) {
      Vertex vertex = source.first;
      while (vertex != null) {
         if (vertex.id.equals(targetId)) {
            return vertex;
         }
         vertex = vertex.next;
      }
      throw new GraphException("Current graph does not contain this vertex!");
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

   /** Base vertex class with an additional height parameter to compose an algorithm that solves my problem. */
   class Vertex {

      private String id;
      private Vertex next;
      private Arc first;
      private int info;
      private int height = HeightGenerator.getHeight(); // param height from custom height generator

      Vertex (String s, Vertex v, Arc e) {
         id = s;
         next = v;
         first = e;
      }

      Vertex (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }
   }


   /** Arc represents one arrow in the graph. Two-directional edges are
    * represented by two Arc objects (for both directions). */
   class Arc {

      private String id;
      private Vertex target;
      private Arc next;
      // You can add more fields, if needed

      Arc (String s, Vertex v, Arc a) {
         id = s;
         target = v;
         next = a;
      }

      Arc (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }

      // TODO!!! Your Arc methods here!
   }

   /** Class that contains two lists for conveniently representing the resulting path.
    * Has a height parameter for sorting and a unique id. */
   class Path {

      private final LinkedList<Vertex> pathVertexPoints; // list of path vertices
      private final LinkedList<Arc> pathArcPoints; // list of path arcs
      private final int highestPoint; // highest point of path
      private final int id; // unique id

      Path (LinkedList<Vertex> pathVertexPoints, LinkedList<Arc> pathArcPoints, int highestPoint, int id) {
         this.pathVertexPoints = pathVertexPoints;
         this.pathArcPoints = pathArcPoints;
         this.highestPoint = highestPoint;
         this.id = id;
      }

      /** Method displaying path, displays arcs with template:
       * vertex1---vertex2 : vertex2---vertex3 : etc. */
      public String printArcPath() {
         StringBuilder result = new StringBuilder();
         result.append(id).append(": ");
         for (int i = pathArcPoints.size() - 1; i >= 0; i--) {
            result.append(pathArcPoints.get(i).id.replace("_", "---"));
            if (i != 0) result.append(" : ");
         }
         return result.toString();
      }

      /** Method that displays each vertex in the path and its height,
       * it also displays the highest point in the path and the total number of points.
       * Use this for more detailed information display. */
      @Override
      public String toString() {
         StringBuilder result = new StringBuilder();
         result.append(id);
         result.append(": ");
         for (int i = 0; i < pathVertexPoints.size(); i++) {
            result.append("(v").append(pathVertexPoints.get(i).info + 1).append(": ").append(pathVertexPoints.get(i).height).append("m)");
            if (i + 1 < pathVertexPoints.size()) result.append(" ==> ");
         }
         result.append("\nHighest point of path: ").append(highestPoint).append("m").append(" [Not included start and destination points if vertices more than 2]");
         result.append("\nPoints in the route: ").append(pathVertexPoints.size()).append("\n");
         return result.toString();
      }
   }

   /** Manager class that optimizes the found path and returns it as an object of the class Path.
    * If one of the possible paths in the graph has a length of 2 vertices
    * (the beginning and immediately the end), then after finding this path, it must be ignored
    * (arcToIgnore value) so that the method for finding the path does not fall into an infinite loop. */
   class GraphPathManager {

      private int id = 1; // current path id
      private int highestPoint;
      private int heightBorder = 10000; // param that will be used in path finding (Graph findPath method)
      private String arcToIgnore = "";
      private LinkedList<Vertex> pathVertex = new LinkedList<>();
      private LinkedList<Arc> pathArc = new LinkedList<>();

      public void addVertex(Vertex vertex) {
         pathVertex.add(vertex);
      }

      /** Method returns an object of the class Path and clears variables for working with coming data.
       * Returns an GraphPathException if the method is called with empty GraphPathManager variables.
       * @return Path object. */
      public Path getPath() {
         if (pathVertex.size() == 0) throw new GraphPathException("No available paths found!", "Null", "Null");
         LinkedList<Vertex> pathVertexPoints = new LinkedList<>(pathVertex);
         LinkedList<Arc> pathArcPoints = new LinkedList<>(pathArc);
         if (pathVertex.size() == 2) arcToIgnore = pathArc.get(0).id;
         pathVertex.clear();
         pathArc.clear();
         return new Path(pathVertexPoints, pathArcPoints, highestPoint, id++);
      }

      /** Method checks each vertex from the end whether the previous vertex has a connection with the next one.
       * If there is no connection, then this vertex is removed from the path, if there is, then this point
       * becomes a next checkpoint until the end of list.
       * @param startId is used for highest point detection.
       * @param endId the same as previous. */
      public void pathPointsCorrection(String startId, String endId) {
         List<Vertex> verticesToRemove = new ArrayList<>();
         int current = pathVertex.size() - 1;
         while (current != 0)
            for (int i = 0; i < current; i++) {
               if (current - 1 - i >= 0 && !areVerticesConnected(pathVertex.get(current - 1 - i), pathVertex.get(current))) {
                  verticesToRemove.add(pathVertex.get(current - 1 - i));
               }
               else {
                  current = current - 1 - i;
                  break;
               }
         }

         for (Vertex vertex : verticesToRemove) {
            pathVertex.remove(vertex);
         }

         setHighestPoint(startId, endId);
      }

      /** Method that checks whether two vertices are connected to each other, if it is true it
       * also adds a connecting arc between these vertices to the pathArc list.
       * @param vertexFrom vertex that is checked for a connection with the next vertex.
       * @param vertexTo next vertex.
       * @return boolean vertices are connected or not. */
      private boolean areVerticesConnected(Vertex vertexFrom, Vertex vertexTo) {
         Arc arc = vertexFrom.first;
         while (arc != null) {
            if (arc.target.id.equals(vertexTo.id)) {
               pathArc.addLast(arc);
               return true;
            }
            arc = arc.next;
         }
         return false;
      }

      /** Method that sets the highest point in path. If the number of points in the path is more than 2,
       * the method does not take into account the height of the start and destination points.
       * @param startId id of start path point.
       * @param endId id of end path point. */
      private void setHighestPoint(String startId, String endId) {
         int value = -1000, startValue = 0, endValue = 0;
         for (Vertex vertex : pathVertex) {
            if (vertex.height > value && !vertex.id.equals(startId) && !vertex.id.equals(endId)) {
               value = vertex.height;
            } else if (vertex.id.equals(startId)) {
               startValue = vertex.height;
            } else if (vertex.id.equals(endId)) {
               endValue = vertex.height;
            }
         }
         heightBorder = value != -1000 ? value : heightBorder;
         if (value == -1000 ) value = Math.max(startValue, endValue);
         highestPoint = value;
      }
   }

   class Graph {

      private String id;
      private Vertex first;
      private int info = 0;

      Graph (String s, Vertex v) {
         id = s;
         first = v;
      }

      Graph (String s) {
         this (s, null);
      }

      @Override
      public String toString() {
         String nl = System.getProperty ("line.separator");
         StringBuffer sb = new StringBuffer (nl);
         sb.append(id);
         sb.append (nl);
         Vertex v = first;
         while (v != null) {
            sb.append (v.toString());
            sb.append (" -->");
            Arc a = v.first;
            while (a != null) {
               sb.append (" ");
               sb.append (a.toString());
               sb.append (" (");
               sb.append (v.toString());
               sb.append ("->");
               sb.append (a.target.toString());
               sb.append (")");
               a = a.next;
            }
            sb.append (nl);
            v = v.next;
         }
         return sb.toString();
      }

      public Vertex createVertex (String vid) {
         Vertex res = new Vertex (vid);
         res.next = first;
         first = res;
         return res;
      }

      public Arc createArc (String aid, Vertex from, Vertex to) {
         Arc res = new Arc (aid);
         res.next = from.first;
         from.first = res;
         res.target = to;
         return res;
      }

      /**
       * Create a connected undirected random tree with n vertices.
       * Each new vertex is connected to some random existing vertex.
       * @param n number of vertices added to this graph. */
      public void createRandomTree (int n) {
         if (n <= 0)
            return;
         Vertex[] varray = new Vertex [n];
         for (int i = 0; i < n; i++) {
            varray [i] = createVertex ("v" + String.valueOf(n-i));
            if (i > 0) {
               int vnr = (int)(Math.random()*i);
               createArc ("a" + varray [vnr].toString() + "_"
                  + varray [i].toString(), varray [vnr], varray [i]);
               createArc ("a" + varray [i].toString() + "_"
                  + varray [vnr].toString(), varray [i], varray [vnr]);
            } else {}
         }
      }

      /**
       * Create an adjacency matrix of this graph.
       * Side effect: corrupts info fields in the graph
       * @return adjacency matrix. */
      public int[][] createAdjMatrix() {
         info = 0;
         Vertex v = first;
         while (v != null) {
            v.info = info++;
            v = v.next;
         }
         int[][] res = new int [info][info];
         v = first;
         while (v != null) {
            int i = v.info;
            Arc a = v.first;
            while (a != null) {
               int j = a.target.info;
               res [i][j]++;
               a = a.next;
            }
            v = v.next;
         }
         return res;
      }

      /**
       * Create a connected simple (undirected, no loops, no multiple
       * arcs) random graph with n vertices and m edges.
       * @param n number of vertices
       * @param m number of edges. */
      public void createRandomSimpleGraph (int n, int m) {
         if (n <= 0)
            return;
         if (n > 2500)
            throw new IllegalArgumentException ("Too many vertices: " + n);
         if (m < n-1 || m > n*(n-1)/2)
            throw new IllegalArgumentException
               ("Impossible number of edges: " + m);
         first = null;
         createRandomTree (n);       // n-1 edges created here
         Vertex[] vert = new Vertex [n];
         Vertex v = first;
         int c = 0;
         while (v != null) {
            vert[c++] = v;
            v = v.next;
         }
         int[][] connected = createAdjMatrix();
         int edgeCount = m - n + 1;  // remaining edges
         while (edgeCount > 0) {
            int i = (int)(Math.random()*n);  // random source
            int j = (int)(Math.random()*n);  // random target
            if (i==j)
               continue;  // no loops
            if (connected [i][j] != 0 || connected [j][i] != 0)
               continue;  // no multiple edges
            Vertex vi = vert [i];
            Vertex vj = vert [j];
            createArc ("a" + vi.toString() + "_" + vj.toString(), vi, vj);
            connected [i][j] = 1;
            createArc ("a" + vj.toString() + "_" + vi.toString(), vj, vi);
            connected [j][i] = 1;
            edgeCount--;  // a new edge happily created
         }
      }

      /** Main method which uses the breadth-first search algorithm and writes the traversed vertices to
       * the GraphPathManager. It takes into account the heightBorder, which is the highest point of the
       * previous path. If each new path exists, then it passes through the highest point which is lower
       * than in the previous path found.
       * Returns true on success. If there is no path from point a to b, it throws an GraphPathException.
       * @param path manager that processes the found path if path searching is successful.
       * @param start vertex from which path search starts.
       * @param destination path endpoint vertex.
       * @return boolean was the path found or not. */
      public boolean findPath(GraphPathManager path, Vertex start, Vertex destination) {
         LinkedList<Arc> queue = new LinkedList<>();
         List<String> visitedPoints = new ArrayList<>();
         queue.addLast(start.first);
         path.addVertex(start);
         visitedPoints.add(start.id);
         while (queue.size() > 0) {
            Arc current = queue.removeFirst();
            while (current != null) {
               if (current.id.equals(path.arcToIgnore)) { current = current.next; continue; }
               Vertex neighbor = current.target;
               if (!visitedPoints.contains(neighbor.id) && (neighbor.height < path.heightBorder || neighbor.id.equals(destination.id))) {
                  queue.addLast(neighbor.first);
                  path.addVertex(neighbor);
                  visitedPoints.add(neighbor.id);
                  if (neighbor.id.equals(destination.id)) return true;
               }
               current = current.next;
            }
         }
         if (path.id == 1) throw new GraphPathException("\nStart point and destination are not connected!", start.id, destination.id);
         return false;
      }
   }

}