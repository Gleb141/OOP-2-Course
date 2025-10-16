import java.util.*;

public class IncidenceMatrixGraph implements Graph {
    private int m;                 // число столбцов (рёбер)
    private int[][] b;             // матрица инцидентности (n x m): источник=1, приёмник=-1
    private final List<int[]> edges; // список рёбер (from,to) — источник истины

    public IncidenceMatrixGraph(int n) {
        this.b = new int[n][0];
        this.m = 0;
        this.edges = new ArrayList<>();
    }

    public IncidenceMatrixGraph() {
        this(0);
    }

    @Override
    public int addVertex() {
        int n = b.length;
        int[][] nb = new int[n + 1][m];
        for (int i = 0; i < n; i++) {
            System.arraycopy(b[i], 0, nb[i], 0, m);
        }
        b = nb;
        return n;
    }

    @Override
    public void removeVertex(int v) {
        checkVertex(v);
        int oldN = b.length;

        // 1) Фильтруем рёбра, инцидентные v, и переиндексируем оставшиеся
        List<int[]> newEdges = new ArrayList<>();
        for (int[] e : edges) {
            int from = e[0], to = e[1];
            if (from == v || to == v) continue;   // выбрасываем рёбра, инцидентные удаляемой вершине
            if (from > v) from--;
            if (to > v) to--;
            newEdges.add(new int[]{from, to});
        }
        edges.clear();
        edges.addAll(newEdges);

        // 2) Перестраиваем матрицу заново по списку рёбер
        int newN = oldN - 1;
        int newM = edges.size();
        int[][] nb = new int[newN][newM];
        for (int col = 0; col < newM; col++) {
            int[] e = edges.get(col);
            nb[e[0]][col] = 1;
            nb[e[1]][col] = -1;
        }
        b = nb;
        m = newM;
    }

    @Override
    public void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);

        // Идемпотентность — если ребро уже есть, ничего не делаем
        for (int[] e : edges) {
            if (e[0] == from && e[1] == to) return;
        }

        // Добавляем в список рёбер
        edges.add(new int[]{from, to});

        // Добавляем новый столбец в матрицу
        int oldM = m;
        m++;
        int n = b.length;
        int[][] nb = new int[n][m];
        for (int v = 0; v < n; v++) {
            System.arraycopy(b[v], 0, nb[v], 0, oldM);
        }
        nb[from][oldM] = 1;
        nb[to][oldM]   = -1;
        b = nb;
    }

    @Override
    public void removeEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);

        // Находим и удаляем ребро из списка
        int idx = -1;
        for (int i = 0; i < edges.size(); i++) {
            int[] e = edges.get(i);
            if (e[0] == from && e[1] == to) { idx = i; break; }
        }
        if (idx < 0) return; // нет такого ребра — допускаем тихое завершение

        edges.remove(idx);

        // Перестраиваем матрицу по актуальному списку рёбер
        int n = b.length;
        m = edges.size();
        int[][] nb = new int[n][m];
        for (int col = 0; col < m; col++) {
            int[] e = edges.get(col);
            nb[e[0]][col] = 1;
            nb[e[1]][col] = -1;
        }
        b = nb;
    }

    @Override
    public List<Integer> getNeighbors(int v) {
        checkVertex(v);
        // Для сохранения порядка добавления опираемся на список рёбер
        List<Integer> ns = new ArrayList<>();
        for (int[] e : edges) {
            if (e[0] == v) ns.add(e[1]);
        }
        return ns;
    }

    @Override
    public int size() {
        return b.length;
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= b.length) {
            throw new GraphIndexException("Вершина " + v + " вне диапазона 0.." + (b.length - 1));
        }
    }

    @Override
    public String toString() {
        return toStringDefault();
    }
}