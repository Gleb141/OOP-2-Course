package org.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Smoke-тест main: один раунд, затем выход. */
class MainSmokeTest {

    @Test
    @DisplayName("Main.main: run один раунд → n → завершается корректно")
    void mainRunsOneRoundAndExits() throws Exception {
        String input = String.join("\n", "0", "n") + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        PrintStream originalOut = System.out;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bout, true, StandardCharsets.UTF_8));

        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        String out = bout.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains("Раунд"), "Ожидается печать номера раунда.");
        assertTrue(out.contains("Спасибо за игру!"), "Должно корректно завершаться.");
    }
}