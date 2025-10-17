import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;


public class GraphFromFileTest {

    private Path write(String content) throws IOException {
        Path p = Files.createTempFile("graph", ".txt");
        Files.writeString(p, content);
        return p;
    }

    @Test
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
    void fromFile_ok_matrix() throws Exception {
        Path p = write("3 2\n0 1\n0 2\n");
        Graph g = Graph.fromFile(p, Graph.Representation.ADJ_MATRIX);
        assertEquals(List.of(1, 2), g.getNeighbors(0));
    }

    @Test
    void fromFile_ok_incidence() throws Exception {
        Path p = write("3 3\n0 1\n0 2\n1 2\n");
        Graph g = Graph.fromFile(p, Graph.Representation.INC_MATRIX);
        assertEquals(List.of(1, 2), g.getNeighbors(0));
    }

    @Test
    void fromFile_empty_file() throws Exception {
        Path p = write("");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    void fromFile_bad_header_token_count() throws Exception {
        Path p = write("4\n0 1\n");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    void fromFile_bad_header_numbers() throws Exception {
        Path p = write("X Y\n");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    void fromFile_short_edges_section() throws Exception {
        Path p = write("2 2\n0 1\n");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    void fromFile_bad_edge_numbers() throws Exception {
        Path p = write("2 1\nA B\n");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    void fromFile_bad_edge_token_count() throws Exception {
        Path p = write("2 1\n0\n");
        assertThrows(GraphFormatException.class, () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    void fromFile_vertex_out_of_bounds() throws Exception {
        Path p = write("2 1\n0 5\n");
        // addEdge внутри fromFile бросит GraphIndexException
        assertThrows(GraphIndexException.class, () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST));
    }

    @Test
    void fromFile_io_error() {
        Path missing = Path.of("definitely_missing_1234567890.txt");
        assertThrows(GraphIoException.class, () -> Graph.fromFile(missing, Graph.Representation.ADJ_LIST));
    }
}