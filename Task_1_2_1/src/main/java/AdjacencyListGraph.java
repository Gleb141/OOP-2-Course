import java.util.ArrayList;
import java.util.List;

/** Реализация графа на списках смежности. */
public class AdjacencyListGraph implements Graph {
    /** Списки смежности для каждой вершины. */
    private List<List<Integer>> adjacency;

    public AdjacencyListGraph(int n) {
        if (n < 0) {
            throw new GraphException("Размер графа не может быть отрицательным.");
        }
        this.adjacency = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    public AdjacencyListGraph() {
        this(0);
    }

    @Override
    public int addVertex() {
        adjacency.add(new ArrayList<>());
        return adjacency.size() - 1;
    }

    @Override
    public void removeVertex(int v) {
        checkVertex(v);
        // Удаляем список смежности вершины v.
        adjacency.remove(v);
        // Удаляем рёбра, ведущие в v, и сдвигаем индексы > v на 1 вниз.
        for (List<Integer> list : adjacency) {
            // Удаляем все вхождения v
            list.removeIf(u -> u == v);
            // Сдвигаем индексы > v
            for (int i = 0; i < list.size(); i++) {
                int u = list.get(i);
                if (u > v) {
                    list.set(i, u - 1);
                }
            }
        }
    }

    @Override
    public void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        List<Integer> list = adjacency.get(from);
        // Предотвращаем дублирование ребра
        if (!list.contains(to)) {
            list.add(to);
        }
    }

    @Override
    public void removeEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        adjacency.get(from).remove((Integer) to);
    }

    @Override
    public boolean hasEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        return adjacency.get(from).contains(to);
    }

    @Override
    public List<Integer> getNeighbors(int v) {
        checkVertex(v);
        // Возвращаем копию, чтобы внешние изменения не влияли на граф
        return new ArrayList<>(adacencyOf(v));
    }

    private List<Integer> adacencyOf(int v) {
        return adjacency.get(v);
    }

    @Override
    public int size() {
        return adjacency.size();
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= adjacency.size()) {
            throw new GraphIndexException(
                    "Вершина " + v + " вне диапазона 0.." + (adjacency.size() - 1)
            );
        }
    }

    @Override
    public String toString() {
        return toStringDefault();
    }
}