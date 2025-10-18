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

/** Интерфейс графа. */
public interface Graph {
    int addVertex();

    void removeVertex(int v);

    void addEdge(int from, int to);

    void removeEdge(int from, int to);

    List<Integer> getNeighbors(int v);

    int size();
    /** Топологическая сортировка матрицы. */

    default List<Integer> topoSort() {
        int n = size();
        int[] inDegree = new int[n];
        for (int v = 0; v < n; v++) {
            for (int to : getNeighbors(v)) {
                if (to < 0 || to >= n) {
                    throw new GraphIndexException("Некорректный сосед " + to + " у вершины " + v);
                }
                inDegree[to]++;
            }
        }
        Queue<Integer> q = new ArrayDeque<>();
        for (int v = 0; v < n; v++) {
            if (inDegree[v] == 0) {
                q.add(v);
            }
        }
        List<Integer> order = new ArrayList<>(n);
        while (!q.isEmpty()) {
            int v = q.remove();
            order.add(v);
            for (int to : getNeighbors(v)) {
                inDegree[to]--;
                if (inDegree[to] == 0) {
                    q.add(to);
                }
            }
        }
        if (order.size() != n) {
            String errormsg = "Топологическая сортировка невозможна:";
            errormsg += " в графе есть цикл";
            throw new GraphCycleException(errormsg);
        }
        return order;
    }
    /** Перевод в строку. */

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
            if (v + 1 < size()) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
    /** Репрезентация графа и чтение из файла. */

    enum Representation { ADJ_LIST, ADJ_MATRIX, INC_MATRIX }

    /** Репрезентация графа и чтение из файла. */

    static Graph fromFile(Path path, Representation rep) {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String header = br.readLine();
            if (header == null) {
                throw new GraphFormatException("Пустой файл");
            }
            String[] first = header.trim().split("\\s+");
            if (first.length < 2) {
                throw new GraphFormatException("Ожидаю 'n m' в первой строке");
            }
            int n;
            int m;
            try {
                n = Integer.parseInt(first[0]);
                m = Integer.parseInt(first[1]);
            } catch (NumberFormatException e) {
                throw new GraphFormatException("Некорректные числа в заголовке");
            }
            Graph g;
            if (rep == Representation.ADJ_LIST) {
                g = new AdjacencyListGraph(n);
            } else if (rep == Representation.ADJ_MATRIX) {
                g = new AdjacencyMatrixGraph(n);
            } else {
                g = new IncidenceMatrixGraph(n);
            }
            for (int i = 0; i < m; i++) {
                String line = br.readLine();
                if (line == null) {
                    String errormsg = "Ожидал ";
                    errormsg += m;
                    errormsg += "строк рёбер, но файл закончился раньше";
                    throw new GraphFormatException(errormsg);
                }
                String[] uv = line.trim().split("\\s+");
                if (uv.length < 2) {
                    String errormsg = "Каждая строка должна быть формата 'u v'";
                    throw new GraphFormatException(errormsg);
                }
                int u;
                int v;
                try {
                    u = Integer.parseInt(uv[0]);
                    v = Integer.parseInt(uv[1]);
                } catch (NumberFormatException e) {
                    throw new GraphFormatException("Некорректные числа в строке ребра: " + line);
                }
                g.addEdge(u, v);
            }
            return g;
        } catch (IOException e) {

            throw new GraphIoException("Ошибка чтения файла: " + path, e);
        }
    }
    /** Проверка равенства графов. */

    default boolean equalsGraph(Graph other) {
        if (other == null) {
            return false;
        }
        if (this.size() != other.size()) {
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
}

