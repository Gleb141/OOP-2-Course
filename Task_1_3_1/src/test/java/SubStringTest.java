import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;



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
        List<Long> indices = SubStringFinder.findInFile(file.toString(), "бра");
        assertEquals(Arrays.asList((long) 1, (long) 8), indices);
    }

    @Test
    void noMatches() throws IOException {
        Path file = createTempFileWithContent("hello world");
        List<Long> indices = SubStringFinder.findInFile(file.toString(), "xyz");
        assertTrue(indices.isEmpty());
    }

    @Test
    void overlappingMatches() throws IOException {
        Path file = createTempFileWithContent("aaaa");
        List<Long> indices = SubStringFinder.findInFile(file.toString(), "aa");
        assertEquals(Arrays.asList((long) 0, (long) 1, (long) 2), indices);
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
        List<Long> indices = SubStringFinder.findInFile(file.toString(), "XYZ");
        assertEquals(Collections.singletonList((long) 100_000), indices);
    }

    @Test
    void emptyFileNoMatches() throws IOException {
        Path file = createTempFileWithContent("");
        List<Long> indices = SubStringFinder.findInFile(file.toString(), "test");
        assertTrue(indices.isEmpty());
    }

    @Test
    void patternAtBeginningAndEnd() throws IOException {
        Path file = createTempFileWithContent("XYZ---XYZ");
        List<Long> indices = SubStringFinder.findInFile(file.toString(), "XYZ");
        assertEquals(Arrays.asList((long) 0, (long) 6), indices);
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
        List<Long> indices = SubStringFinder.findInFile(file.toString(), "ababa");
        assertEquals(Arrays.asList((long) 0, (long) 2), indices);
    }

    @Test
    void nonExistingFileThrowsIoException() {
        String fileName = "non_existing_file_" + System.nanoTime() + ".txt";
        assertThrows(IOException.class,
                () -> SubStringFinder.findInFile(fileName, "test"));
    }

    @Test
    void hugeFileLargerThanHeapSingleMatchAtEnd() throws IOException {
        long heapMaxBytes = Runtime.getRuntime().maxMemory();
        long targetSizeBytes = heapMaxBytes + 10_000_000L;

        Path file = Files.createTempFile("substring_finder_huge_", ".txt");

        char[] block = new char[8192];
        Arrays.fill(block, 'a');

        long written = 0;
        long matchIndex;

        try (java.io.BufferedWriter writer =
                     Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {

            while (written + block.length < targetSizeBytes) {
                writer.write(block);
                written += block.length;
            }

            matchIndex = written;
            writer.write("XYZ");
            written += 3;
        }
        List<Long> indices = SubStringFinder.findInFile(file.toString(), "XYZ");

        assertEquals(1, indices.size(), "Должно быть ровно одно совпадение");
        assertEquals((int) matchIndex, indices.get(0));
    }

}