import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** Тесты загрузки графа из файла. */
public class GraphFromFileTest {

    private Path write(String content) throws IOException {
        Path p = Files.createTempFile("graph", ".txt");
        Files.writeString(p, content);
        return p;
    }

    static Stream<Graph.GraphFactory> factories() {
        return Stream.of(
                AdjacencyListGraph::new,
                AdjacencyMatrixGraph::new,
                IncidenceMatrixGraph::new
        );
    }

    @ParameterizedTest
    @MethodSource("factories")
    @DisplayName("fromFile: OK (универсальный формат N M + рёбра)")
    void fromFile_ok_all(Graph.GraphFactory factory) throws Exception {
        Path p = write("4 3\n0 1\n1 2\n2 3\n");
        Graph g = Graph.fromFile(p, factory);
        assertEquals(4, g.size());
        assertEquals(List.of(1), g.getNeighbors(0));
        assertEquals(List.of(2), g.getNeighbors(1));
        assertEquals(List.of(3), g.getNeighbors(2));
        assertEquals(List.of(), g.getNeighbors(3));
    }

    @Test
    @DisplayName("fromFile: пустой файл → GraphFormatException")
    void fromFile_empty_file() throws Exception {
        Path p = write("");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, AdjacencyListGraph::new));
    }

    @Test
    @DisplayName("fromFile: плохой заголовок (число токенов)")
    void fromFile_bad_header_token_count() throws Exception {
        Path p = write("4\n0 1\n");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, AdjacencyListGraph::new));
    }

    @Test
    @DisplayName("fromFile: плохие числа в заголовке")
    void fromFile_bad_header_numbers() throws Exception {
        Path p = write("X Y\n");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, AdjacencyListGraph::new));
    }

    @Test
    @DisplayName("fromFile: рёбер меньше, чем M")
    void fromFile_short_edges_section() throws Exception {
        Path p = write("2 2\n0 1\n");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, AdjacencyListGraph::new));
    }

    @Test
    @DisplayName("fromFile: плохие числа в строке ребра")
    void fromFile_bad_edge_numbers() throws Exception {
        Path p = write("2 1\nA B\n");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, AdjacencyListGraph::new));
    }

    @Test
    @DisplayName("fromFile: недостаточно токенов на ребре")
    void fromFile_bad_edge_token_count() throws Exception {
        Path p = write("2 1\n0\n");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, AdjacencyListGraph::new));
    }

    @Test
    @DisplayName("fromFile: вершина вне границ")
    void fromFile_vertex_out_of_bounds() throws Exception {
        Path p = write("2 1\n0 5\n");
        assertThrows(GraphIndexException.class, () -> Graph.fromFile(p, AdjacencyListGraph::new));
    }
}