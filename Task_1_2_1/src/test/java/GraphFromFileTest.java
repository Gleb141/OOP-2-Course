import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Тестирование графа из файла*/
public class GraphFromFileTest {

    private Path write(String content) throws IOException {
        Path p = Files.createTempFile("graph", ".txt");
        Files.writeString(p, content);
        return p;
    }

    @Test
    @DisplayName("Чтение списка из файла")
    void fromFile_ok_list() throws Exception {
        Path p = write("4 3\n0 1\n1 2\n2 3\n");
        Graph g = Graph.fromFile(p, Graph.Representation.ADJ_LIST);
        assertEquals(4, g.size());
        assertEquals(List.of(1), g.getNeighbors(0));
        assertEquals(List.of(2), g.getNeighbors(1));
        assertEquals(List.of(3), g.getNeighbors(2));
        assertEquals(List.of(), g.getNeighbors(3));
    }

    @Test
    @DisplayName("Чтение матрицы из файла")
    void fromFile_ok_matrix() throws Exception {
        Path p = write("3 2\n0 1\n0 2\n");
        Graph g = Graph.fromFile(p, Graph.Representation.ADJ_MATRIX);
        assertEquals(List.of(1, 2), g.getNeighbors(0));
    }

    @Test
    @DisplayName("Чтение списка инцидентности из файла")
    void fromFile_ok_incidence() throws Exception {
        Path p = write("3 3\n0 1\n0 2\n1 2\n");
        Graph g = Graph.fromFile(p, Graph.Representation.INC_MATRIX);
        assertEquals(List.of(1, 2), g.getNeighbors(0));
    }

    @Test
    @DisplayName("Чтение пустого файла")
    void fromFile_empty_file() throws Exception {
        Path p = write("");
        assertThrows(GraphFormatException.class, () ->
                Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    @DisplayName("Чтение пустого файла")
    void fromFile_bad_header_token_count() throws Exception {
        Path p = write("4\n0 1\n");
        assertThrows(GraphFormatException.class, () ->
                Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    @DisplayName("Чтение файла с плохими цифрами в хедере")
    void fromFile_bad_header_numbers() throws Exception {
        Path p = write("X Y\n");
        assertThrows(GraphFormatException.class, () ->
                Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    @DisplayName("Чтение файла с короткими рёбрами")
    void fromFile_short_edges_section() throws Exception {
        Path p = write("2 2\n0 1\n");
        assertThrows(GraphFormatException.class, () ->
                Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    @DisplayName("Чтение файла с плохими цифрами на рёбрах")
    void fromFile_bad_edge_numbers() throws Exception {
        Path p = write("2 1\nA B\n");
        assertThrows(GraphFormatException.class, () ->
                Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    @DisplayName("Чтение файла с недостаточным числом токенов")
    void fromFile_bad_edge_token_count() throws Exception {
        Path p = write("2 1\n0\n");
        assertThrows(GraphFormatException.class, () ->
                Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    @DisplayName("Чтение файла с вершиной вне границ")
    void fromFile_vertex_out_of_bounds() throws Exception {
        Path p = write("2 1\n0 5\n");
        // addEdge внутри fromFile бросит GraphIndexException
        assertThrows(GraphIndexException.class, () ->
                Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    @DisplayName("Чтение файла с ошибкой")
    void fromFile_io_error() {
        Path missing = Path.of("definitely_missing_1234567890.txt");
        assertThrows(GraphIoException.class, () ->
                Graph.fromFile(missing, Graph.Representation.ADJ_LIST));
    }
}