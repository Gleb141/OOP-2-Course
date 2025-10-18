import java.util.ArrayList;
import java.util.List;

/** Реализация графа на матрице смежности. */
public class AdjacencyMatrixGraph implements Graph {
    private int[][] adjacencyMatrix;

    public AdjacencyMatrixGraph(int n) {
        if (n < 0) {
            throw new GraphException("Размер графа не может быть отрицательным.");
        }
        this.adjacencyMatrix = new int[n][n];
    }

    public AdjacencyMatrixGraph() {
        this(0);
    }

    @Override
    public int addVertex() {
        final int n = adjacencyMatrix.length;
        int[][] next = new int[n + 1][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(adjacencyMatrix[i], 0, next[i], 0, n);
        }
        adjacencyMatrix = next;
        return n;
    }

    @Override
    public void removeVertex(int v) {
        checkVertex(v);
        int n = adjacencyMatrix.length;
        if (n == 1) {
            adjacencyMatrix = new int[0][0];
            return;
        }
        int[][] next = new int[n - 1][n - 1];
        for (int i = 0, ni = 0; i < n; i++) {
            if (i == v) continue;
            for (int j = 0, nj = 0; j < n; j++) {
                if (j == v) continue;
                next[ni][nj++] = adjacencyMatrix[i][j];
            }
            ni++;
        }
        adjacencyMatrix = next;
    }

    @Override
    public void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        adjacencyMatrix[from][to] = 1;
    }

    @Override
    public void removeEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        adjacencyMatrix[from][to] = 0;
    }

    @Override
    public boolean hasEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        return adjacencyMatrix[from][to] != 0;
    }

    @Override
    public List<Integer> getNeighbors(int v) {
        checkVertex(v);
        List<Integer> neighbors = new ArrayList<>();
        for (int u = 0; u < adjacencyMatrix.length; u++) {
            if (adjacencyMatrix[v][u] != 0) {
                neighbors.add(u);
            }
        }
        return neighbors;
    }

    @Override
    public int size() {
        return adjacencyMatrix.length;
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= adjacencyMatrix.length) {
            throw new GraphIndexException("Вершина " + v + " вне диапазона 0.." + (adjacencyMatrix.length - 1));
        }
    }

    @Override
    public String toString() {
        return toStringDefault();
    }
}