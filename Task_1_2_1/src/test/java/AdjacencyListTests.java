import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Дополнительные параметризованные тесты для всех реализаций Graph.
 * Сфокусированы на removeVertex (перенумерация вершин и рёбер) и hasEdge.
 */
public class AdjacencyListTests {

    /**
     * Поставщик реализаций графа.
     *
     * @return поток реализаций.
     */
    static Stream<Graph> impls() {
        return Stream.of(
                new AdjacencyListGraph(0),
                new AdjacencyMatrixGraph(0),
                new IncidenceMatrixGraph(0)
        );
    }

    /**
     * После удаления вершины индексы > v сдвигаются на 1 вниз,
     * рёбра перекидываются корректно.
     */
    @ParameterizedTest
    @MethodSource("impls")
    void removeVertex_reindexes_edges(Graph g) {
        // Вершины: 0,1,2
        g.addVertex();
        g.addVertex();
        g.addVertex();
        // Рёбра: 0->1, 0->2, 2->0
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(2, 0);

        // Удаляем вершину 1: новая нумерация станет {0,1(был 2)}
        g.removeVertex(1);

        // Было 0->1, 0->2 — останется 0->1 (т.к. старый "2" стал "1")
        assertIterableEquals(List.of(1), g.getNeighbors(0));
        // Было 2->0 — стало 1->0
        assertIterableEquals(List.of(0), g.getNeighbors(1));
    }

    /**
     * hasEdge корректно отражает наличие/отсутствие дуги.
     */
    @ParameterizedTest
    @MethodSource("impls")
    void hasEdge_works(Graph g) {
        g.addVertex();
        g.addVertex();
        g.addEdge(0, 1);
        assertTrue(g.hasEdge(0, 1));
        // удаляем и проверяем отсутствие
        g.removeEdge(0, 1);
        assertIterableEquals(List.of(), g.getNeighbors(0));
    }
}