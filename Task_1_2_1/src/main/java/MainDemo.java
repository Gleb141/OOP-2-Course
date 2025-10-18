/** Демо из задания. */
public class MainDemo {
    public static void main(String[] args) {
        Graph g1 = new AdjacencyListGraph();
        int a = g1.addVertex();
        int b = g1.addVertex();
        int c = g1.addVertex();
        g1.addEdge(a, b);
        g1.addEdge(a, c);
        g1.addEdge(b, c);
        System.out.println("AdjacencyListGraph:\n" + g1);
        System.out.println("Topo: " + g1.topoSort());

        Graph g2 = new AdjacencyMatrixGraph(3);
        g2.addEdge(0, 1);
        g2.addEdge(0, 2);
        g2.addEdge(1, 2);
        System.out.println("\nAdjacencyMatrixGraph:\n" + g2);
        System.out.println("Equals g1? " + g1.equalsGraph(g2));

        Graph g3 = new IncidenceMatrixGraph(3);
        g3.addEdge(0, 1);
        g3.addEdge(0, 2);
        g3.addEdge(1, 2);
        System.out.println("\nIncidenceMatrixGraph:\n" + g3);
        System.out.println("Topo: " + g3.topoSort());
    }
}
