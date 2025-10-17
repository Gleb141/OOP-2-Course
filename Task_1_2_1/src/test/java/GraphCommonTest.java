import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GraphCommonTest {

    static Stream<Graph> impls() {
        return Stream.of(
                new AdjacencyListGraph(0),
                new AdjacencyMatrixGraph(0),
                new IncidenceMatrixGraph(0)
        );
    }

    @ParameterizedTest
    @MethodSource("impls")
    void addVertex_addEdge_neighbors(Graph g) {
        assertEquals(0, g.size());
        int v0 = g.addVertex();
        int v1 = g.addVertex();
        int v2 = g.addVertex();
        assertEquals(3, g.size());
        assertEquals(0, v0);
        assertEquals(1, v1);
        assertEquals(2, v2);

        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 2);

        assertIterableEquals(List.of(1, 2), g.getNeighbors(0));
        assertIterableEquals(List.of(2), g.getNeighbors(1));
        assertTrue(g.getNeighbors(2).isEmpty());

        g.removeEdge(0, 1);
        assertIterableEquals(List.of(2), g.getNeighbors(0));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void removeVertex_last(Graph g) {
        for (int i = 0; i < 4; i++) {
            g.addVertex(); // 0..3
        }
        g.addEdge(0, 1);
        g.addEdge(2, 3);
        assertEquals(4, g.size());
        g.removeVertex(3);
        assertEquals(3, g.size());
        assertThrows(GraphIndexException.class, () -> g.getNeighbors(3));
        assertIterableEquals(List.of(1), g.getNeighbors(0));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void removeVertex_middle_reindexes(Graph g) {
        for (int i = 0; i < 5; i++) {
            g.addVertex(); // 0..4
        }
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(3, 4);

        g.removeVertex(2); // old 3->2, old 4->3

        assertEquals(4, g.size());
        assertFalse(g.getNeighbors(0).contains(2));      // 0->2 (old) исчез
        assertIterableEquals(List.of(2), g.getNeighbors(1)); // 1->3 (old) стал 1->2
        assertIterableEquals(List.of(3), g.getNeighbors(2)); // 3->4 (old) стал 2->3
    }

    @ParameterizedTest
    @MethodSource("impls")
    void equalsGraph_across_impls(Graph g1) {
        Graph g2 = new AdjacencyMatrixGraph(3);
        Graph g3 = new IncidenceMatrixGraph(3);

        g1.addEdge(g1.addVertex(), g1.addVertex()); // 0->1
        g1.addVertex();                              // 2
        g1.addEdge(1, 2);                            // 1->2

        g2.addEdge(0, 1);
        g2.addEdge(1, 2);
        g3.addEdge(0, 1);
        g3.addEdge(1, 2);

        assertTrue(g1.equalsGraph(g2));
        assertTrue(g1.equalsGraph(g3));
        assertTrue(g2.equalsGraph(g3));

        g3.addEdge(2, 0);
        assertFalse(g1.equalsGraph(g3));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void equalsGraph_null_and_sizeMismatch(Graph g) {
        assertFalse(g.equalsGraph(null));
        g.addVertex();
        g.addVertex();                 // size=2
        Graph other = new AdjacencyListGraph(0);
        other.addVertex();
        other.addVertex();
        other.addVertex(); // size=3
        assertFalse(g.equalsGraph(other));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void topoSort_dag(Graph g) {
        for (int i = 0; i < 6; i++) {
            g.addVertex();
        }
        g.addEdge(5, 2);
        g.addEdge(5, 0);
        g.addEdge(4, 0);
        g.addEdge(4, 1);
        g.addEdge(2, 3);
        g.addEdge(3, 1);

        List<Integer> order = g.topoSort();

        Map<Integer, Integer> pos = new HashMap<>();
        for (int i = 0; i < order.size(); i++) {
            pos.put(order.get(i), i);
        }
        assertEquals(6, order.size());
        int[][] edges = {{5, 2}, {5, 0}, {4, 0}, {4, 1}, {2, 3}, {3, 1}};
        for (int[] e : edges) {
            assertTrue(pos.get(e[0]) < pos.get(e[1]), Arrays.toString(e));
        }
    }

    @ParameterizedTest
    @MethodSource("impls")
    void topoSort_cycle(Graph g) {
        for (int i = 0; i < 3; i++) {
            g.addVertex();
        }
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 0);
        assertThrows(GraphCycleException.class, g::topoSort);
    }

    @ParameterizedTest
    @MethodSource("impls")
    void topoSort_emptyGraph_and_toStringEmpty(Graph g) {
        assertEquals(0, g.size());
        assertTrue(g.topoSort().isEmpty());
        assertEquals("", g.toString());
    }

    @ParameterizedTest
    @MethodSource("impls")
    void toString_format(Graph g) {
        for (int i = 0; i < 3; i++) {
            g.addVertex();
        }
        g.addEdge(0, 2);
        g.addEdge(1, 2);
        String s = g.toString().replace("\r\n", "\n");
        assertTrue(s.startsWith("0: 2\n1: 2\n2:"));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void invalidIndices(Graph g) {
        assertThrows(GraphIndexException.class, () -> g.addEdge(0, 1));
        for (int i = 0; i < 2; i++) {
            g.addVertex();
        }
        assertThrows(GraphIndexException.class, () -> g.addEdge(-1, 0));
        assertThrows(GraphIndexException.class, () -> g.addEdge(0, 2));
        assertThrows(GraphIndexException.class, () -> g.removeEdge(0, 2));
        assertThrows(GraphIndexException.class, () -> g.getNeighbors(2));
        assertThrows(GraphIndexException.class, () -> g.removeVertex(2));
        // Доп. случаи
        assertThrows(GraphIndexException.class, () -> g.getNeighbors(-1));
        assertThrows(GraphIndexException.class, () -> g.removeVertex(-1));
        assertThrows(GraphIndexException.class, () -> g.addEdge(1, -1));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void addEdge_idempotent(Graph g) {
        for (int i = 0; i < 3; i++) {
            g.addVertex();
        }
        g.addEdge(0, 1);
        g.addEdge(0, 1);
        assertIterableEquals(List.of(1), g.getNeighbors(0));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void removeEdge_nonexistent_noThrow(Graph g) {
        for (int i = 0; i < 2; i++) {
            g.addVertex();
        }
        assertDoesNotThrow(() -> g.removeEdge(0, 1));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void neighbors_are_defensive_copies(Graph g) {
        for (int i = 0; i < 3; i++) {
            g.addVertex();
        }
        g.addEdge(0, 1);
        List<Integer> ns = g.getNeighbors(0);
        ns.add(99);                              // пробуем испортить копию
        assertIterableEquals(List.of(1), g.getNeighbors(0)); // оригинал не изменился
    }

    @ParameterizedTest
    @MethodSource("impls")
    void self_loop_allowed(Graph g) {
        for (int i = 0; i < 2; i++) {
            g.addVertex();
        }
        g.addEdge(1, 1);
        assertIterableEquals(List.of(1), g.getNeighbors(1));
    }
}