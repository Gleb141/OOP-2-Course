import java.util.ArrayList;
import java.util.List;

/**
 * Реализация ориентированного графа на матрице инцидентности.
 * Для ребра (u→v) в столбце стоит +1 в строке u и −1 в строке v.
 * Для самопетли (v→v) фиксируем только +1; факт ребра хранится в списке edges.
 */
public class IncidenceMatrixGraph implements Graph {
    /** Матрица инцидентности N×M. */
    private int[][] incidenceMatrix;
    /** Список рёбер (from, to) — источник истины. */
    private final List<int[]> edges;

    /**
     * Создает граф с указанным числом вершин.
     *
     * @param n начальное число вершин.
     * @throws GraphException если n отрицательно.
     */
    public IncidenceMatrixGraph(int n) {
        if (n < 0) {
            throw new GraphException("Размер графа не может быть отрицательным.");
        }
        this.incidenceMatrix = new int[n][0];
        this.edges = new ArrayList<>();
    }

    /**
     * Создает пустой граф.
     */
    public IncidenceMatrixGraph() {
        this(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addVertex() {
        final int n = incidenceMatrix.length;
        int[][] next = new int[n + 1][edges.size()];
        for (int i = 0; i < n; i++) {
            System.arraycopy(incidenceMatrix[i], 0, next[i], 0, edges.size());
        }
        incidenceMatrix = next;
        return n;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeVertex(int v) {
        checkVertex(v);
        // Удаляем рёбра, касающиеся v, и сдвигаем индексы > v на 1 вниз.
        List<int[]> newEdges = new ArrayList<>();
        for (int[] e : edges) {
            int from = e[0];
            int to = e[1];
            if (from == v || to == v) {
                continue;
            }
            if (from > v) {
                from--;
            }
            if (to > v) {
                to--;
            }
            newEdges.add(new int[]{from, to});
        }
        edges.clear();
        edges.addAll(newEdges);
        rebuildFromEdges(incidenceMatrix.length - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        edges.add(new int[]{from, to});
        rebuildFromEdges(incidenceMatrix.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        for (int i = 0; i < edges.size(); i++) {
            int[] e = edges.get(i);
            if (e[0] == from && e[1] == to) {
                edges.remove(i);
                rebuildFromEdges(incidenceMatrix.length);
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        for (int[] e : edges) {
            if (e[0] == from && e[1] == to) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getNeighbors(int v) {
        checkVertex(v);
        List<Integer> neighbors = new ArrayList<>();
        for (int[] e : edges) {
            if (e[0] == v) {
                neighbors.add(e[1]);
            }
        }
        return neighbors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return incidenceMatrix.length;
    }

    /**
     * Перестраивает матрицу инцидентности из списка рёбер.
     *
     * @param newN новое число строк (вершин).
     */
    private void rebuildFromEdges(int newN) {
        if (newN < 0) {
            throw new GraphException("Размер графа не может быть отрицательным.");
        }
        int m = edges.size();
        int[][] next = new int[newN][m];
        for (int j = 0; j < m; j++) {
            int from = edges.get(j)[0];
            int to = edges.get(j)[1];
            next[from][j] = 1;
            if (from != to) {
                next[to][j] = -1;
            }
        }
        incidenceMatrix = next;
    }

    /**
     * Проверяет индекс вершины на попадание в диапазон.
     *
     * @param v индекс вершины.
     * @throws GraphIndexException если индекс вне диапазона.
     */
    private void checkVertex(int v) {
        if (v < 0 || v >= incidenceMatrix.length) {
            String msg = "Вершина " + v + " вне диапазона 0.."
                    + (incidenceMatrix.length - 1);
            throw new GraphIndexException(msg);
        }
    }

    @Override
    public String toString() {
        return toStringDefault();
    }
}