import java.util.ArrayList;
import java.util.List;

/** Реализация графа на матрице инцидентности: для дуги (u->v) u=1, v=-1. */
public class IncidenceMatrixGraph implements Graph {
    /** Матрица инцидентности размера N x M (N — вершины, M — рёбра). */
    private int[][] incidenceMatrix;
    /** Список рёбер в порядке добавления: [from, to]. Используется как источник истины. */
    private final List<int[]> edges;

    public IncidenceMatrixGraph(int n) {
        if (n < 0) {
            throw new GraphException("Размер графа не может быть отрицательным.");
        }
        this.incidenceMatrix = new int[n][0];
        this.edges = new ArrayList<>();
    }

    public IncidenceMatrixGraph() {
        this(0);
    }

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
            if (from > v) from--;
            if (to > v) to--;
            newEdges.add(new int[]{from, to});
        }
        edges.clear();
        edges.addAll(newEdges);
        rebuildFromEdges(incidenceMatrix.length - 1);
    }

    @Override
    public void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        edges.add(new int[]{from, to});
        rebuildFromEdges(incidenceMatrix.length);
    }

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
        // если ребра нет — просто выходим
    }

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

    @Override
    public int size() {
        return incidenceMatrix.length;
    }

    private void rebuildFromEdges(int newN) {
        if (newN < 0) {
            throw new GraphException("Размер графа не может быть отрицательным.");
        }
        int m = edges.size();
        int[][] next = new int[newN][m];
        for (int j = 0; j < m; j++) {
            int from = edges.get(j)[0];
            int to = edges.get(j)[1];
            // Для самопетли (v->v) кладём +1 в строку v (−1 в ту же ячейку невозможен).
            // Обнаружение соседей и наличие ребра делаем по списку edges, так что информация не теряется.
            next[from][j] = 1;
            if (from != to) {
                next[to][j] = -1;
            }
        }
        incidenceMatrix = next;
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= incidenceMatrix.length) {
            throw new GraphIndexException("Вершина " + v + " вне диапазона 0.." + (incidenceMatrix.length - 1));
        }
    }

    @Override
    public String toString() {
        return toStringDefault();
    }
}