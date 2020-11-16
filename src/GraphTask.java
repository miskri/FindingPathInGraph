import java.util.*;

/** Container class to different classes, that makes the whole
 * set of classes one class formally.
 */
public class GraphTask {

   /** Main method. */
   public static void main (String[] args) {
      GraphTask a = new GraphTask();
      a.run();
   }

   /** Actual main method to run examples and everything. */
   public void run() {
      Graph g = new Graph ("A");
      g.createRandomSimpleGraph (10, 15); // 2500, 3123475
      //System.out.println(g.toString());
      getOptimizedPath(g, g.first, "v10");
   }

   private void getOptimizedPath(Graph source, Vertex startPoint, String destinationId) {
      GraphPathManager graphPathManager = new GraphPathManager();
      List<Path> paths = new ArrayList<>();
      int index = 0;
      long time = System.currentTimeMillis();
      while (true) {
         try {
            source.findPath(graphPathManager, startPoint, destinationId);
         }
         catch (RuntimeException e) {
            break;
         }
         graphPathManager.pathPointsCorrection(startPoint.id, destinationId);
         paths.add(index, graphPathManager.getPath());
         index++;
      }
      int highestPoint = 10000;
      Path bestPath = null;
      for (Path path : paths) {
         System.out.println(path.toString());
         if (path.highestPoint <= highestPoint) {
            highestPoint = path.highestPoint;
            bestPath = path;
         }
      }
      System.out.println("Time spent in seconds: " + (System.currentTimeMillis() - time) / 1000 + "\nTotal path count: "
              + paths.size() + "\n\nBest path");

      assert bestPath != null;
      System.out.println(bestPath.toString());
   }

   /** Class that generates realistic elevation values ranging from -28 to 5642 meters
    * (the lowest and highest point in Europe above sea level). */
   static class HeightGenerator {
      static int previousHeight = 143; // 143m height of the center of Tallinn above sea level
      static int maxValueElevation = 10; // 10m maximal difference between 2 vertices
      static int heightMin = -28; // -28m
      static int heightMax = 5642; // 5642m

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

   // TODO!!! add javadoc relevant to your problem
   class Vertex {

      private String id;
      private Vertex next;
      private Arc first;
      private int info;
      // You can add more fields, if needed
      private int height = HeightGenerator.getHeight();
      private boolean inIgnore = false;

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
    * represented by two Arc objects (for both directions).
    */
   class Arc {

      private String id;
      private Vertex target;
      private Arc next;
      private int info = 0;
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

   class Path {

      private final LinkedList<Vertex> pathPoints;
      private final String stringRepresentation;
      private final int highestPoint;

      Path (LinkedList<Vertex> pathPoints, String text, int highestPoint) {
         this.pathPoints = pathPoints;
         this.stringRepresentation = text;
         this.highestPoint = highestPoint;
      }

      @Override
      public String toString() {
         return stringRepresentation;
      }
   }

   class GraphPathManager {

      private int id = 1;
      private int highestPoint;
      private String arcToIgnore = "";
      private LinkedList<Vertex> path = new LinkedList<>();

      public void addVertex(Vertex vertex) {
         path.add(vertex);
      }

      public Path getPath() {
         StringBuilder result = new StringBuilder();
         result.append(id);
         result.append(": ");
         for (int i = 0; i < path.size(); i++) {
            result.append("(v").append(path.get(i).info + 1).append(": ").append(path.get(i).height).append("m)");
            if (i + 1 < path.size()) result.append(" ==> ");
         }
         result.append("\nHighest point of path: ").append(highestPoint).append("m").append(" [Not included start and destination points if point count > 2]");
         result.append("\nPoints in the route: ").append(path.size()).append("\n");

         id += 1;
         LinkedList<Vertex> pathPoints = new LinkedList<>(path);
         if (path.size() == 2) arcToIgnore = "a" + path.get(0).id + "_" + path.get(1).id;
         path.clear();
         return new Path(pathPoints, result.toString(), highestPoint);
      }
      
      public void pathPointsCorrection(String startId, String endId) {
         List<Vertex> verticesToRemove = new ArrayList<>();
         int current = path.size() - 1;
         while (current != 0)
            for (int i = 0; i < current; i++) {
               if (current - 1 - i >= 0 && !areVerticesConnected(path.get(current - 1 - i), path.get(current))) {
                  verticesToRemove.add(path.get(current - 1 - i));
               }
               else {
                  current = current - 1 - i;
                  break;
               }
         }

         for (Vertex vertex : verticesToRemove) {
            path.remove(vertex);
         }
         setHighestPoint(startId, endId);
      }

      private boolean areVerticesConnected(Vertex vertexFrom, Vertex vertexTo) {
         Arc arc = vertexFrom.first;
         while (arc != null) {
            if (arc.target.id.equals(vertexTo.id)) return true;
            arc = arc.next;
         }
         return false;
      }

      private void setHighestPoint(String startId, String endId) {
         int value = -1000, startValue = 0, endValue = 0;
         Vertex highest = null;
         for (Vertex vertex : path) {
            if (vertex.height > value && !vertex.id.equals(startId) && !vertex.id.equals(endId)) {
               value = vertex.height;
               highest = vertex;
            } else if (vertex.id.equals(startId)) {
               startValue = vertex.height;
            } else if (vertex.id.equals(endId)) {
               endValue = vertex.height;
            }
         }
         if (highest != null) highest.inIgnore = true;
         if (value == -1000 ) value = Math.max(startValue, endValue);
         highestPoint = value;
      }
   }

   class Graph {

      private String id;
      private Vertex first;
      private int info = 0;
      // You can add more fields, if needed

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
       * @param n number of vertices added to this graph
       */
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
       * @return adjacency matrix
       */
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
       * @param m number of edges
       */
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

      public void findPath(GraphPathManager path, Vertex start, String destinationId) {
         LinkedList<Arc> queue = new LinkedList<>();
         List<String> visitedPoints = new ArrayList<>();
         queue.addLast(start.first);
         path.addVertex(start);
         visitedPoints.add(start.id);
         while (queue.size() > 0) {
            Arc current = queue.removeFirst();
            while (current != null) {
               if (current.id.equals(path.arcToIgnore)) current = current.next;
               Vertex neighbor = current.target;
               if (!visitedPoints.contains(neighbor.id) && !neighbor.inIgnore) {
                  queue.addLast(neighbor.first);
                  path.addVertex(neighbor);
                  visitedPoints.add(neighbor.id);
                  if (neighbor.id.equals(destinationId)) return;
               }
               current = current.next;
            }
         }
         throw new RuntimeException("Start point and destination are not connected!");
      }

      // TODO!!! Your Graph methods here! Probably your solution belongs here.
   }

} 

