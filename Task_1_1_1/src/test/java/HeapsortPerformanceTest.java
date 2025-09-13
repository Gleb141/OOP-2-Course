import static java.lang.Math.log;
import static java.lang.Math.max;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Непрерывный тест производительности.
 * По умолчанию не запускается в CI: помечен тегом "perf".
 *
 * Результат: печатает CSV-таблицу в stdout и пишет файл
 * build/reports/perf/heapsort_times.csv.
 *
 * Идея: для разных N измеряем медианное время сортировки массива случайных int.
 * Проверка асимптотики: метрика time/(N*log2(N)) должна оставаться примерно постоянной.
 */
public class HeapsortPerformanceTest {

    @Tag("perf")
    @Test
    void measureScalingAndExportCsv() throws IOException {
        int[] sizes = new int[] { 1000, 2000, 4000, 8000, 16000, 32000, 64000 };
        int warmupRuns = 3;
        int measureRuns = 5;

        List<String> rows = new ArrayList<>();
        rows.add("N,median_ms,avg_ms,nlog2n,time_per_nlog2n_ns");

        for (int n : sizes) {
            long[] samples = new long[measureRuns];

            // Базовый случайный массив для этой размерности
            int[] base = ThreadLocalRandom.current().ints(n).toArray();

            // Прогрев (не учитываем)
            for (int w = 0; w < warmupRuns; w++) {
                int[] a = Arrays.copyOf(base, base.length);
                Heapsort.sort(a);
            }

            // Измерение
            long sum = 0;
            for (int r = 0; r < measureRuns; r++) {
                int[] a = Arrays.copyOf(base, base.length);
                long t0 = System.nanoTime();
                Heapsort.sort(a);
                long t1 = System.nanoTime();
                long dt = t1 - t0;
                samples[r] = dt;
                sum += dt;
            }

            Arrays.sort(samples);
            long medianNs = samples[measureRuns / 2];
            double avgNs = sum / (double) measureRuns;

            // Нормализация по n*log2(n)
            double nlog2n = n * (log(max(2, n)) / log(2)); // log2(n)
            double perNlog2nNs = medianNs / nlog2n;

            String line = String.format("%d,%.3f,%.3f,%.3f,%.3f",
                    n, medianNs / 1e6, avgNs / 1e6, nlog2n, perNlog2nNs);
            rows.add(line);
        }

        // Печать в stdout (можно скопировать в Excel)
        for (String row : rows) {
            System.out.println(row);
        }

        // Выгрузка CSV в файл
        Path outDir = Path.of("build", "reports", "perf");
        Files.createDirectories(outDir);
        Files.writeString(outDir.resolve("heapsort_times.csv"),
                String.join(System.lineSeparator(), rows));
    }
}