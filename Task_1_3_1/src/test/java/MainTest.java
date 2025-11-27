import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Main}.
 */
public class MainTest {

    private static final Path INPUT_PATH = Path.of("input.txt");

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(INPUT_PATH);
    }

    @Test
    void mainCreatesExampleFileAndPrintsResult() throws IOException {
        // гарантируем, что файла нет -> ветка Files.notExists(path) == true
        Files.deleteIfExists(INPUT_PATH);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(out));

            Main.main(new String[0]);

            // файл создан с нужным содержимым
            assertTrue(Files.exists(INPUT_PATH));
            String fileContent =
                    new String(Files.readAllBytes(INPUT_PATH), StandardCharsets.UTF_8);
            assertEquals("абракадабра", fileContent);

            // и корректный вывод в консоль
            String stdout = out.toString();
            assertTrue(stdout.contains("Искомая подстрока: \"бра\""));
            assertTrue(stdout.contains("Вхождения (индексы с нуля): [1, 8]"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void mainHandlesIoException() throws IOException {
        // создаём директорию "input.txt" -> ensureExampleFileExists не переписывает её,
        // а SubStringFinder.findInFile бросает IOException
        Files.deleteIfExists(INPUT_PATH);
        Files.createDirectory(INPUT_PATH);

        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;

        try {
            System.setErr(new PrintStream(err));

            Main.main(new String[0]);

            String stderr = err.toString();
            assertTrue(stderr.contains("Ошибка ввода-вывода"));
        } finally {
            System.setErr(originalErr);
        }
    }
}

