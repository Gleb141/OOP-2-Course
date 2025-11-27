import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Main demo file.
 */

public class Main {

    /**
     * Demo itself.
     */

    public static void main(String[] args) {
        String fileName = "input.txt";
        String pattern = "бра";

        try {

            ensureExampleFileExists(fileName);

            List<Long> indices = SubStringFinder.findInFile(fileName, pattern);
            System.out.println("Искомая подстрока: \"" + pattern + "\"");
            System.out.println("Файл: " + fileName);
            System.out.println("Вхождения (индексы с нуля): " + indices);
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void ensureExampleFileExists(String fileName) throws IOException {
        Path path = Path.of(fileName);
        if (Files.notExists(path)) {
            Files.write(path, "абракадабра".getBytes(StandardCharsets.UTF_8));
        }
    }
}
