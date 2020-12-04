import java.util.*;

/** Container class to different classes, that makes the whole
 * set of classes one class formally. */
public class HeightPath {

   Graph graph;

   HeightPath(Graph g) {
      this.graph = g;
   }

   /** Actual main method to run examples and everything. */
   public void run(String vStart, String vDestination) {
      Path optimizedPath = getOptimizedPath(graph, vStart, vDestination); // find path from first vertex with id v1 to vertex with id v2
      System.out.println("\nArc representation of best path:\n" + optimizedPath.printArcPath());
      System.out.println("\nVertex representation of best path:\n" + optimizedPath);
   }

   /** Method that returns the best possible path, where the highest point is the lowest.
    * @param source an object containing vertices and arcs.
    * @param startId a vertex id value that is the starting point of the path search.
    * @param destinationId the vertex id value that is the end point of the path.
    * @return Path class object that contains vertices and arcs from start point to destination point. */
   public Path getOptimizedPath(Graph source, String startId, String destinationId) {
      GraphPathManager graphPathManager = new GraphPathManager();
      Vertex start = findVertex(source, startId);
      Vertex destination = findVertex(source, destinationId);
      List<Path> paths = new ArrayList<>();
      int index = 0;
      long startTime = System.currentTimeMillis();

      while (true) {
         if (findPath(graphPathManager, start, destination)) {
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

   /** Method searches for the required vertex in the given graph, if there is no such vertex
    * it returns GraphException.
    * @param source search graph.
    * @param targetId id of the requested vertex. */
   public Vertex findVertex(Graph source, String targetId) {
      Vertex vertex = source.first;
      while (vertex != null) {
         if (vertex.id.equals(targetId)) return vertex;
         vertex = vertex.next;
      }
      throw new GraphException("Current graph does not contain this vertex - " + targetId);
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
   public boolean findPath(HeightPath.GraphPathManager path, Vertex start, Vertex destination) {
      Queue<Arc> queue = new LinkedList<>();
      List<String> visitedPoints = new ArrayList<>();
      queue.add(start.first);
      path.addVertex(start);
      visitedPoints.add(start.id);
      while (queue.size() > 0) {
         Arc current = queue.remove();
         while (current != null) {
            if (current.id.equals(path.arcToIgnore)) { current = current.next; continue; }
            Vertex neighbor = current.target;
            if (!visitedPoints.contains(neighbor.id) && (neighbor.height < path.heightBorder || neighbor.id.equals(destination.id))) {
               queue.add(neighbor.first);
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

   /** Class that contains two lists for conveniently representing the resulting path.
    * Has a height parameter for sorting and a unique id. */
   class Path {

      public final List<Vertex> pathVertexPoints; // list of path vertices
      public final List<Arc> pathArcPoints; // list of path arcs
      public final int highestPoint; // highest point of path
      public final int id; // unique id

      Path (List<Vertex> pathVertexPoints, List<Arc> pathArcPoints, int highestPoint, int id) {
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
            result.append("(").append(pathVertexPoints.get(i).id).append(": ").append(pathVertexPoints.get(i).height).append("m)");
            if (i + 1 < pathVertexPoints.size()) result.append(" ==> ");
         }
         result.append("\nHighest point of path: ").append(highestPoint).append("m").append(" [Not included start and destination points if vertices more than 2]");
         result.append("\nPoints in the route: ").append(pathVertexPoints.size());
         return result.toString();
      }
   }

   /** Manager class that optimizes the found path and returns it as an object of the class Path.
    * If one of the possible paths in the graph has a length of 2 vertices
    * (the beginning and immediately the end), then after finding this path, it must be ignored
    * (arcToIgnore value) so that the method for finding the path does not fall into an infinite loop. */
   class GraphPathManager {

      public int id = 1; // current path id
      public int highestPoint;
      public int heightBorder = 10000; // param that will be used in path finding (Graph findPath method)
      public String arcToIgnore = "";
      public List<Vertex> pathVertex = new ArrayList<>();
      public List<Arc> pathArc = new ArrayList<>();

      public void addVertex(Vertex vertex) {
         pathVertex.add(vertex);
      }

      /** Method returns an object of the class Path and clears variables for working with coming data.
       * Returns an GraphPathException if the method is called with empty GraphPathManager variables.
       * @return Path object. */
      public Path getPath() {
         if (pathVertex.size() == 0) throw new GraphPathException("No available paths found!", "Null", "Null");
         List<Vertex> pathVertexPoints = new ArrayList<>(pathVertex);
         List<Arc> pathArcPoints = new ArrayList<>(pathArc);
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
               pathArc.add(arc);
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
}