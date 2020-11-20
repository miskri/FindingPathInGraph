import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

/** Test class.
 * @author Mihhail Skripnik. */
public class GraphTaskTest {

   @Test (timeout=1000)
   public void algorithmPathDepthTestSet1() {
      GraphTask task = new GraphTask();
      GraphTask.Graph graph = getExampleGraph(new int[]{35, 47, 48, 49, 48, 50, 44, 34, 52, 50});
      var time = System.currentTimeMillis();
      GraphTask.Path path = task.getOptimizedPath(graph, "A", "H");
      System.out.println("Elapsed time: " + (System.currentTimeMillis() - time) + " ms");
      // right vertex path is A, B, C, D, E, H
      String[] resultIndexes = new String[]{"A", "B", "C", "D", "E", "H"};
      for (int i = 0; i < path.pathVertexPoints.size(); i++) {
         assertEquals(path.pathVertexPoints.get(i).id, resultIndexes[i]);
      }
   }

   @Test (timeout=1000)
   public void algorithmPathDepthTestSet2() {
      GraphTask task = new GraphTask();
      GraphTask.Graph graph = getExampleGraph(new int[]{100, 15, 90, 91, 25, 91, 50, 90, 90, 100});
      var time = System.currentTimeMillis();
      GraphTask.Path path = task.getOptimizedPath(graph, "A", "H");
      System.out.println("Elapsed time: " + (System.currentTimeMillis() - time) + " ms");
      // right vertex path is A, I, J, H
      String[] resultIndexes = new String[]{"A", "I", "J", "H"};
      for (int i = 0; i < path.pathVertexPoints.size(); i++) {
         assertEquals(path.pathVertexPoints.get(i).id, resultIndexes[i]);
      }
   }

   /** Attention! Maximum test execution time is 3 minutes! */
   @Test (timeout = 240000)
   public void averageAlgorithmExecutionTimeWithHugeData10Times() {
      GraphTask task = new GraphTask();
      List<Long> executionTime = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
         GraphTask.Graph graph = task.new Graph("TEST");
         graph.createRandomSimpleGraph(2500, 3123475);
         var time = System.currentTimeMillis();
         task.getOptimizedPath(graph, "v1", "v2500");
         time = System.currentTimeMillis() - time;
         executionTime.add(time);
         System.out.println();
      }
      double averageTime = executionTime.stream().mapToDouble(a -> a).sum() / executionTime.size();
      System.out.printf("Average execution time after 10 iterations is - %,d ms", (int)averageTime);
      assertTrue(averageTime < 10000); // 10000 ms is 10s.
   }

   /** ATTENTION! Maximum test execution time is 30 minutes! */
   @Test (timeout = 2400000)
   public void averageAlgorithmExecutionTimeWithHugeData100Times() {
      GraphTask task = new GraphTask();
      List<Long> executionTime = new ArrayList<>();
      for (int i = 0; i < 100; i++) {
         GraphTask.Graph graph = task.new Graph("TEST");
         graph.createRandomSimpleGraph(2500, 3123475);
         var time = System.currentTimeMillis();
         task.getOptimizedPath(graph, "v1", "v2500");
         time = System.currentTimeMillis() - time;
         executionTime.add(time);
         System.out.println();
      }
      double averageTime = executionTime.stream().mapToDouble(a -> a).sum() / executionTime.size();
      System.out.printf("Average execution time after 100 iterations is - %,d ms", (int)averageTime);
      assertTrue(averageTime < 10000); // 10000 ms is 10s
   }

   private GraphTask.Graph getExampleGraph(int[] heights) {
      GraphTask task = new GraphTask();
      GraphTask.Vertex vertexA = task.new Vertex("A", heights[0]);
      GraphTask.Vertex vertexB = task.new Vertex("B", heights[1]);
      GraphTask.Vertex vertexC = task.new Vertex("C", heights[2]);
      GraphTask.Vertex vertexD = task.new Vertex("D", heights[3]);
      GraphTask.Vertex vertexE = task.new Vertex("E", heights[4]);
      GraphTask.Vertex vertexF = task.new Vertex("F", heights[5]);
      GraphTask.Vertex vertexG = task.new Vertex("G", heights[6]);
      GraphTask.Vertex vertexI = task.new Vertex("I", heights[7]);
      GraphTask.Vertex vertexJ = task.new Vertex("J", heights[8]);
      GraphTask.Vertex vertexH = task.new Vertex("H", heights[9]);
      vertexA.setNextVertex(vertexB);
      vertexB.setNextVertex(vertexC);
      vertexC.setNextVertex(vertexD);
      vertexD.setNextVertex(vertexE);
      vertexE.setNextVertex(vertexF);
      vertexF.setNextVertex(vertexG);
      vertexG.setNextVertex(vertexH);
      vertexH.setNextVertex(vertexI);
      vertexI.setNextVertex(vertexJ);
      GraphTask.Arc arcAI = task.new Arc("A_I", vertexI, null);
      GraphTask.Arc arcAG = task.new Arc("A_G", vertexG, arcAI);
      GraphTask.Arc arcAB = task.new Arc("A_B", vertexB, arcAG);
      vertexA.setArc(arcAB);
      GraphTask.Arc arcBC = task.new Arc("B_C", vertexC, null);
      vertexB.setArc(arcBC);
      GraphTask.Arc arcCD = task.new Arc("C_D", vertexD, null);
      vertexC.setArc(arcCD);
      GraphTask.Arc arcDE = task.new Arc("D_E", vertexE, null);
      vertexD.setArc(arcDE);
      GraphTask.Arc arcEH = task.new Arc("E_H", vertexH, null);
      vertexE.setArc(arcEH);
      GraphTask.Arc arcGF = task.new Arc("G_F", vertexF, null);
      vertexG.setArc(arcGF);
      GraphTask.Arc arcFE = task.new Arc("F_E", vertexE, null);
      vertexF.setArc(arcFE);
      GraphTask.Arc arcIJ = task.new Arc("I_J", vertexJ, null);
      vertexI.setArc(arcIJ);
      GraphTask.Arc arcJH = task.new Arc("J_H", vertexH, null);
      vertexJ.setArc(arcJH);
      return task.new Graph("GRAPH", vertexA);
   }

}

