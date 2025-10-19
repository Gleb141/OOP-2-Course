import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Интерфейс графа и утилиты работы с ним.
 */
public interface Graph {

    /**
     * Фабрика графов. Используйте ссылку на конструктор реализации
     * (например, {@code AdjacencyListGraph::new}).
     */
    @FunctionalInterface
    interface GraphFactory {
        /**
         * Создает граф с указанным числом вершин.
         *
         * @param vertices начальное число вершин
         * @return новый граф
         */
        Graph create(int vertices);
    }

    /** Добавляет вершину и возвращает её индекс. */
    int addVertex();

    /** Удаляет вершину по индексу. */
    void removeVertex(int v);

    /** Добавляет ориентированное ребро {@code from -> to}. */
    void addEdge(int from, int to);

    /** Удаляет ориентированное ребро {@code from -> to}. */
    void removeEdge(int from, int to);

    /** Проверяет наличие ребра {@code from -> to}. */
    boolean hasEdge(int from, int to);

    /** Возвращает список соседей вершины (куда идут дуги). */
    List<Integer> getNeighbors(int v);

    /** Возвращает число вершин. */
    int size();

    /**
     * Топологическая сортировка ориентированного ациклического графа.
     *
     * @return порядок вершин
     * @throws GraphCycleException если в графе есть цикл
     */
    default List<Integer> topologicalSort() {
        int n = size();
        int[] indeg = new int[n];
        for (int v = 0; v < n; v++) {
            for (int u : getNeighbors(v)) {
                indeg[u]++;
            }
        }
        Queue<Integer> q = new ArrayDeque<>();
        for (int v = 0; v < n; v++) {
            if (indeg[v] == 0) {
                q.add(v);
            }
        }
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int v = q.poll();
            order.add(v);
            for (int u : getNeighbors(v)) {
                indeg[u]--;
                if (indeg[u] == 0) {
                    q.add(u);
                }
            }
        }
        if (order.size() != n) {
            throw new GraphCycleException(
                    "Топологическая сортировка невозможна: в графе есть цикл."
            );
        }
        return order;
    }

    /**
     * Строковое представление графа в виде списков смежности.
     *
     * @return строка с описанием графа
     */
    default String toStringDefault() {
        StringBuilder sb = new StringBuilder();
        for (int v = 0; v < size(); v++) {
            sb.append(v).append(": ");
            List<Integer> ns = getNeighbors(v);
            for (int i = 0; i < ns.size(); i++) {
                if (i > 0) {
                    sb.append(' ');
                }
                sb.append(ns.get(i));
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Сравнивает структуру двух графов по наборам смежности.
     *
     * @param other другой граф
     * @return true, если структуры совпадают
     */
    default boolean equalsGraph(Graph other) {
        if (other == null || other.size() != size()) {
            return false;
        }
        int n = size();
        for (int v = 0; v < n; v++) {
            Set<Integer> a = new HashSet<>(getNeighbors(v));
            Set<Integer> b = new HashSet<>(other.getNeighbors(v));
            if (!a.equals(b)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Загружает граф из файла формата: первая строка — {@code N M},
     * далее M строк {@code "u v"}. Граф создается через переданную фабрику.
     *
     * @param path    путь к файлу
     * @param factory фабрика для создания графа на N вершинах
     * @return созданный и заполненный граф
     */
    static Graph fromFile(Path path, GraphFactory factory) {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String header = br.readLine();
            if (header == null || header.trim().isEmpty()) {
                throw new GraphFormatException("Пустой файл.");
            }
            String[] ht = header.trim().split("\\s+");
            if (ht.length != 2) {
                throw new GraphFormatException(
                        "Некорректный заголовок: ожидалось два числа N M."
                );
            }
            int n;
            int m;
            try {
                n = Integer.parseInt(ht[0]);
                m = Integer.parseInt(ht[1]);
            } catch (NumberFormatException ex) {
                throw new GraphFormatException(
                        "Некорректные числа в заголовке: " + header
                );
            }
            if (n < 0 || m < 0) {
                throw new GraphFormatException("N и M должны быть неотрицательны.");
            }

            Graph g = factory.create(n);

            int readEdges = 0;
            String line;
            while ((line = br.readLine()) != null && readEdges < m) {
                String s = line.trim();
                if (s.isEmpty() || s.startsWith("#")) {
                    continue;
                }
                String[] parts = s.split("\\s+");
                if (parts.length != 2) {
                    throw new GraphFormatException(
                            "Ожидалось два числа для ребра, строка: '" + s + "'"
                    );
                }
                int from;
                int to;
                try {
                    from = Integer.parseInt(parts[0]);
                    to = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ex) {
                    throw new GraphFormatException(
                            "Некорректные номера вершин в строке: '" + s + "'"
                    );
                }
                g.addEdge(from, to);
                readEdges++;
            }
            if (readEdges < m) {
                String msg = "Недостаточно строк рёбер: ожидалось "
                        + m + ", прочитано " + readEdges + ".";
                throw new GraphFormatException(msg);
            }
            return g;
        } catch (IOException ioe) {
            throw new GraphIoException("Ошибка чтения файла: " + path, ioe);
        }
    }

    /**
     * Загружает данные из файла формата {@code N M + рёбра} в переданный граф.
     * В методе в граф добавятся N вершин и M рёбер.
     *
     * @param path   путь к файлу
     * @param target целевой граф
     */
    static void loadInto(Path path, Graph target) {
        fromFile(path, n -> {
            for (int i = 0; i < n; i++) {
                target.addVertex();
            }
            return target;
        });
    }
}