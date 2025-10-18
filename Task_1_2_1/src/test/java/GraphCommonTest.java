import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Общие тесты для всех реализаций Graph. */
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
        final int v1 = g.addVertex();
        assertEquals(0, v0);
        assertEquals(1, v1);
        g.addEdge(0, 0);
        g.addEdge(0, 1);
        assertIterableEquals(List.of(0, 1), g.getNeighbors(0));
        g.removeEdge(0, 0);
        assertIterableEquals(List.of(1), g.getNeighbors(0));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void equalsGraph_across_impls(Graph g1) {
        g1.addVertex();
        g1.addVertex();
        g1.addEdge(0, 1);

        final Graph g2 = new AdjacencyMatrixGraph(2);
        g2.addEdge(0, 1);

        final Graph g3 = new IncidenceMatrixGraph(2);
        g3.addEdge(0, 1);

        assertTrue(g1.equalsGraph(g2));
        assertTrue(g2.equalsGraph(g3));
        assertTrue(g1.equalsGraph(g3));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void neighbors_unmodifiable_copy(Graph g) {
        g.addVertex();
        g.addVertex();
        g.addEdge(0, 1);
        List<Integer> ns = g.getNeighbors(0);
        ns.add(99); // пробуем испортить копию
        assertIterableEquals(List.of(1), g.getNeighbors(0)); // оригинал не изменился
    }

    @ParameterizedTest
    @MethodSource("impls")
    void self_loop_allowed(Graph g) {
        g.addVertex();
        g.addVertex();
        g.addEdge(1, 1);
        assertIterableEquals(List.of(1), g.getNeighbors(1));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void toString_format(Graph g) {
        g.addVertex();
        g.addVertex();
        g.addEdge(0, 1);
        String s = g.toString();
        assertTrue(s.contains("0: 1"));
        assertTrue(s.contains("1: "));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void topological_sort_detects_cycle(Graph g) {
        g.addVertex();
        g.addVertex();
        g.addEdge(0, 1);
        g.addEdge(1, 0);
        assertThrows(GraphCycleException.class, g::topologicalSort);
    }

    @ParameterizedTest
    @MethodSource("impls")
    void topological_sort_ok(Graph g) {
        for (int i = 0; i < 3; i++) {
            g.addVertex();
        }
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        assertDoesNotThrow(g::topologicalSort);
    }

    @ParameterizedTest
    @MethodSource("impls")
    void equalsGraph_null_and_size(Graph g) {
        Graph other = null;
        assertFalse(g.equalsGraph(other));
        g.addVertex();
        other = new AdjacencyListGraph(2);
        assertFalse(g.equalsGraph(other));
    }
}