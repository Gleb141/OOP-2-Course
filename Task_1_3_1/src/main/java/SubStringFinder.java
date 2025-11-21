import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * Recieves and finds the required substring.
 */

public class SubStringFinder {
    private final String fileName;
    private final String pattern;

    /**
     * Function that finds the required substring.
     */

    public SubStringFinder(String fileName, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Искомая строка не должна быть пустой");
        }
        this.fileName = fileName;
        this.pattern = pattern;
    }

    /**
     * Find the needed substring in file.
     */

    public static List<Integer> findInFile(String fileName, String pattern) throws IOException {
        SubStringFinder finder = new SubStringFinder(fileName, pattern);
        return finder.find();
    }

    /**
     * Find function.
     */

    public List<Integer> find() throws IOException {
        int[] prefixFunction = buildPrefixFunction(pattern);
        int patternLength = pattern.length();
        List<Integer> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            int positionInText = 0;
            int j = 0;
            char[] buffer = new char[4096];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                for (int i = 0; i < charsRead; i++) {
                    char currentChar = buffer[i];
                    while (j > 0 && currentChar != pattern.charAt(j)) {
                        j = prefixFunction[j - 1];
                    }
                    if (currentChar == pattern.charAt(j)) {
                        j++;
                    }
                    if (j == patternLength) {
                        int startIndex = positionInText - patternLength + 1;
                        result.add(startIndex);
                        j = prefixFunction[j - 1];
                    }
                    positionInText++;
                }
            }
        }
        return result;
    }

    /**
     * Function to build a prefix.
     */

    private int[] buildPrefixFunction(String pattern) {
        int m = pattern.length();
        int[] pi = new int[m];
        pi[0] = 0;
        int j = 0;
        for (int i = 1; i < m; i++) {
            while (j > 0 && pattern.charAt(i) != pattern.charAt(j)) {
                j = pi[j - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(j)) {
                j++;
            }
            pi[i] = j;
        }
        return pi;
    }
}
