/**
 * Демонстрационная программа.
 */
public class MainDemo {
    /**
     * Точка входа в программу.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        // Список смежности
        Graph g1 = new AdjacencyListGraph();
        for (int i = 0; i < 3; i++) {
            g1.addVertex();
        }
        g1.addEdge(0, 1);
        g1.addEdge(1, 2);
        System.out.println("AdjacencyListGraph:");
        System.out.println(g1);
        try {
            System.out.println("Topo: " + g1.topologicalSort());
        } catch (GraphCycleException ex) {
            System.out.println("Topo: cycle detected");
        }

        // Матрица смежности
        Graph g2 = new AdjacencyMatrixGraph(3);
        g2.addEdge(0, 1);
        g2.addEdge(0, 2);
        System.out.println("AdjacencyMatrixGraph:");
        System.out.println(g2);
        try {
            System.out.println("Topo: " + g2.topologicalSort());
        } catch (GraphCycleException ex) {
            System.out.println("Topo: cycle detected");
        }

        // Матрица инцидентности (цикл 0->1->2->0)
        Graph g3 = new IncidenceMatrixGraph(3);
        g3.addEdge(0, 1);
        g3.addEdge(1, 2);
        g3.addEdge(2, 0);
        System.out.println("IncidenceMatrixGraph:");
        System.out.println(g3);
        try {
            System.out.println("Topo: " + g3.topologicalSort());
        } catch (GraphCycleException ex) {
            System.out.println("Topo: cycle detected");
        }
    }
}