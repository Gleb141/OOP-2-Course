import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Full coverage tests for {@link SubStringFinder}.
 */
public class SubStringTest {

    private Path createTempFileWithContent(String content) throws IOException {
        Path tempFile = Files.createTempFile("substring_finder_test_", ".txt");
        Files.write(tempFile, content.getBytes(StandardCharsets.UTF_8));
        return tempFile;
    }

    @Test
    void exampleFromTask() throws IOException {
        Path file = createTempFileWithContent("абракадабра");
        List<Integer> indices = SubStringFinder.findInFile(file.toString(), "бра");
        assertEquals(Arrays.asList(1, 8), indices);
    }

    @Test
    void noMatches() throws IOException {
        Path file = createTempFileWithContent("hello world");
        List<Integer> indices = SubStringFinder.findInFile(file.toString(), "xyz");
        assertTrue(indices.isEmpty());
    }

    @Test
    void overlappingMatches() throws IOException {
        Path file = createTempFileWithContent("aaaa");
        List<Integer> indices = SubStringFinder.findInFile(file.toString(), "aa");
        assertEquals(Arrays.asList(0, 1, 2), indices);
    }

    @Test
    void bigFileSingleMatchInMiddle() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100_000; i++) {
            sb.append('a');
        }
        sb.append("XYZ");
        for (int i = 0; i < 100_000; i++) {
            sb.append('a');
        }

        Path file = createTempFileWithContent(sb.toString());
        List<Integer> indices = SubStringFinder.findInFile(file.toString(), "XYZ");
        assertEquals(Collections.singletonList(100_000), indices);
    }

    @Test
    void emptyFileNoMatches() throws IOException {
        Path file = createTempFileWithContent("");
        List<Integer> indices = SubStringFinder.findInFile(file.toString(), "test");
        assertTrue(indices.isEmpty());
    }

    @Test
    void patternAtBeginningAndEnd() throws IOException {
        Path file = createTempFileWithContent("XYZ---XYZ");
        List<Integer> indices = SubStringFinder.findInFile(file.toString(), "XYZ");
        assertEquals(Arrays.asList(0, 6), indices);
    }

    @Test
    void nullPatternThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> SubStringFinder.findInFile("dummy.txt", null));
    }

    @Test
    void emptyPatternThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> SubStringFinder.findInFile("dummy.txt", ""));
    }

    @Test
    void patternWithSelfOverlap() throws IOException {
        Path file = createTempFileWithContent("abababaca");
        List<Integer> indices = SubStringFinder.findInFile(file.toString(), "ababa");
        assertEquals(Arrays.asList(0, 2), indices);
    }

    @Test
    void nonExistingFileThrowsIoException() {
        String fileName = "non_existing_file_" + System.nanoTime() + ".txt";
        assertThrows(IOException.class,
                () -> SubStringFinder.findInFile(fileName, "test"));
    }
}