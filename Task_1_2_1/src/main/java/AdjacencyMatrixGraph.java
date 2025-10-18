import java.util.ArrayList;
import java.util.List;

/** Задание графа по матрице смежности */
public class AdjacencyMatrixGraph implements Graph {
    private int[][] a;

    public AdjacencyMatrixGraph(int n) {
        this.a = new int[n][n];
    }

    public AdjacencyMatrixGraph() {
        this(0);
    }

    @Override
    public int addVertex() {
        int n = a.length;
        int[][] na = new int[n + 1][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, na[i], 0, n);
        }
        a = na;
        return n;
    }

    @Override
    public void removeVertex(int v) {
        checkVertex(v);
        int n = a.length;
        int[][] na = new int[n - 1][n - 1];
        for (int i = 0, ii = 0; i < n; i++) {
            if (i == v) {
                continue;
            }
            for (int j = 0, jj = 0; j < n; j++) {
                if (j == v) {
                    continue;
                }
                na[ii][jj++] = a[i][j];
            }
            ii++;
        }
        a = na;
    }

    @Override
    public void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        a[from][to] = 1;                      // идемпотентно по природе матрицы
    }

    @Override
    public void removeEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        a[from][to] = 0;
    }

    @Override
    public List<Integer> getNeighbors(int v) {
        checkVertex(v);
        List<Integer> ns = new ArrayList<>();
        for (int to = 0; to < a.length; to++) {
            if (a[v][to] == 1) {
                ns.add(to);
            }
        }
        return ns;
    }

    @Override
    public int size() {
        return a.length;
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= a.length) {
            throw new GraphIndexException("Вершина " + v + " вне диапазона 0.." + (a.length - 1));
        }
    }

    @Override
    public String toString() {
        return toStringDefault();
    }
}