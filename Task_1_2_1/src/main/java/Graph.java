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

/** Интерфейс графа и утилиты работы с ним. */
public interface Graph {

    enum Representation {
        ADJ_LIST,
        ADJ_MATRIX,
        INC_MATRIX
    }

    int addVertex();

    void removeVertex(int v);

    void addEdge(int from, int to);

    void removeEdge(int from, int to);

    boolean hasEdge(int from, int to);

    List<Integer> getNeighbors(int v);

    int size();

    /** Топологическая сортировка ориентированного ациклического графа. */
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
                if (--indeg[u] == 0) {
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

    /** Строковое представление:
     *  0: 1 2
     *  1: 2
     *  2:
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

    /** Сравнение структуры двух графов (по наборам смежности). */
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

    /** Загрузка графа из файла с заголовком: N M, затем M строк "u v". */
    static Graph fromFile(Path path, Representation repr) {
        Graph g;
        switch (repr) {
            case ADJ_LIST:
                g = new AdjacencyListGraph(0);
                break;
            case ADJ_MATRIX:
                g = new AdjacencyMatrixGraph(0);
                break;
            case INC_MATRIX:
                g = new IncidenceMatrixGraph(0);
                break;
            default:
                throw new IllegalArgumentException("Unknown representation: " + repr);
        }

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
                throw new GraphFormatException("Некорректные числа в заголовке: " + header);
            }
            if (n < 0 || m < 0) {
                throw new GraphFormatException("N и M должны быть неотрицательны.");
            }
            for (int i = 0; i < n; i++) {
                g.addVertex();
            }
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
                g.addEdge(from, to); // может бросить GraphIndexException
                readEdges++;
            }
            if (readEdges < m) {
                throw new GraphFormatException(
                        "Недостаточно строк рёбер: ожидалось " + m + ", прочитано " + readEdges + "."
                );
            }
            return g;
        } catch (IOException ioe) {
            throw new GraphIoException("Ошибка чтения файла: " + path, ioe);
        }
    }
}