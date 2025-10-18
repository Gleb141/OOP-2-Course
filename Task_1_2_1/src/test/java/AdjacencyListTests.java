import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Категория тестов списков смежности. */
public class AdjacencyListTests {

    @Test
    void addVertex_increasesSize() {
        Graph g = new AdjacencyListGraph();
        g.addVertex();
        assertEquals(1, g.size());
    }

    @Test
    void add_and_remove_edge() {
        Graph g = new AdjacencyListGraph();
        g.addVertex();
        g.addVertex();
        g.addEdge(0, 1);
        assertIterableEquals(List.of(1), g.getNeighbors(0));
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