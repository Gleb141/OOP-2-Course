import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.Test;

import java.util.List;

public class AdjacencyListTests {

    @Test
    void addVertex_increasesSize() {
        Graph g = new AdjacencyListGraph();
        g.addVertex();
        assertEquals(1, g.size());
    }

    @Test
    void removeVertex_decreasesSize() {
        Graph g = new AdjacencyListGraph();
        g.addVertex();
        g.removeVertex(0);
        assertEquals(0, g.size());
    }

    @Test
    void addEdge_addsNeighbor() {
        Graph g = new AdjacencyListGraph();
        g.addVertex();
        g.addVertex();
        g.addEdge(0, 1);
        assertIterableEquals(List.of(1), g.getNeighbors(0));
    }

    @Test
    void removeEdge_removesNeighbor() {
        Graph g = new AdjacencyListGraph();
        g.addVertex();
        g.addVertex();
        g.addEdge(0, 1);
        g.removeEdge(0, 1);
        assertIterableEquals(List.of(), g.getNeighbors(0));
    }

    @Test
    void size_returnsVertexCount() {
        Graph g = new AdjacencyListGraph();
        g.addVertex();
        g.addVertex();
        assertEquals(2, g.size());
    }
}