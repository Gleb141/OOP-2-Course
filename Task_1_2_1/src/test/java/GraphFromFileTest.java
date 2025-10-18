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

/**
 * Тесты загрузки графа из файла. Успешные кейсы параметризованы по представлению.
 */
public class GraphFromFileTest {

    /**
     * Вспомогательная запись файла.
     *
     * @param content текст.
     * @return путь к временному файлу.
     * @throws IOException ошибка записи.
     */
    private Path write(String content) throws IOException {
        Path p = Files.createTempFile("graph", ".txt");
        Files.writeString(p, content);
        return p;
    }

    /**
     * Поставщик трёх представлений для параметризованных тестов.
     *
     * @return поток представлений графа.
     */
    static Stream<Graph.Representation> reprs() {
        return Stream.of(
                Graph.Representation.ADJ_LIST,
                Graph.Representation.ADJ_MATRIX,
                Graph.Representation.INC_MATRIX
        );
    }

    /**
     * Успешная загрузка графа с рёбрами 0→1, 1→2, 2→3 для всех представлений.
     */
    @ParameterizedTest
    @MethodSource("reprs")
    @DisplayName("fromFile: OK (универсальный формат N M + рёбра)")
    void fromFile_ok_all(Graph.Representation repr) throws Exception {
        Path p = write("4 3\n0 1\n1 2\n2 3\n");
        Graph g = Graph.fromFile(p, repr);
        assertEquals(4, g.size());
        assertEquals(List.of(1), g.getNeighbors(0));
        assertEquals(List.of(2), g.getNeighbors(1));
        assertEquals(List.of(3), g.getNeighbors(2));
        assertEquals(List.of(), g.getNeighbors(3));
    }

    /**
     * Пустой файл — ошибка формата (repr роли не играет).
     */
    @Test
    @DisplayName("fromFile: пустой файл → GraphFormatException")
    void fromFile_empty_file() throws Exception {
        Path p = write("");
        assertThrows(
                GraphFormatException.class,
                () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST)
        );
    }

    /**
     * Некорректный заголовок — ошибка формата.
     */
    @Test
    @DisplayName("fromFile: плохой заголовок (число токенов) → GraphFormatException")
    void fromFile_bad_header_token_count() throws Exception {
        Path p = write("4\n0 1\n");
        assertThrows(
                GraphFormatException.class,
                () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST)
        );
    }

    /**
     * Некорректные числа в заголовке — ошибка формата.
     */
    @Test
    @DisplayName("fromFile: плохие числа в заголовке → GraphFormatException")
    void fromFile_bad_header_numbers() throws Exception {
        Path p = write("X Y\n");
        assertThrows(
                GraphFormatException.class,
                () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST)
        );
    }

    /**
     * Обещано M рёбер, но строк меньше — ошибка формата.
     */
    @Test
    @DisplayName("fromFile: рёбер меньше, чем M → GraphFormatException")
    void fromFile_short_edges_section() throws Exception {
        Path p = write("2 2\n0 1\n");
        assertThrows(
                GraphFormatException.class,
                () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST)
        );
    }

    /**
     * Плохие числа в строке ребра — ошибка формата.
     */
    @Test
    @DisplayName("fromFile: плохие числа в строке ребра → GraphFormatException")
    void fromFile_bad_edge_numbers() throws Exception {
        Path p = write("2 1\nA B\n");
        assertThrows(
                GraphFormatException.class,
                () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST)
        );
    }

    /**
     * Недостаточно токенов в строке ребра — ошибка формата.
     */
    @Test
    @DisplayName("fromFile: недостаточно токенов на ребре → GraphFormatException")
    void fromFile_bad_edge_token_count() throws Exception {
        Path p = write("2 1\n0\n");
        assertThrows(
                GraphFormatException.class,
                () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST)
        );
    }

    /**
     * Вершина вне диапазона — исключение индекса.
     */
    @Test
    @DisplayName("fromFile: вершина вне границ → GraphIndexException")
    void fromFile_vertex_out_of_bounds() throws Exception {
        Path p = write("2 1\n0 5\n");
        assertThrows(
                GraphIndexException.class,
                () -> Graph.fromFile(p, Graph.Representation.ADJ_LIST)
        );
    }

    /**
     * IO-ошибка — оборачивается в GraphIoException.
     */
    @Test
    @DisplayName("fromFile: IO-ошибка → GraphIoException")
    void fromFile_io_error() {
        Path missing = Path.of("definitely_missing_1234567890.txt");
        assertThrows(
                GraphIoException.class,
                () -> Graph.fromFile(missing, Graph.Representation.ADJ_LIST)
        );
    }
}