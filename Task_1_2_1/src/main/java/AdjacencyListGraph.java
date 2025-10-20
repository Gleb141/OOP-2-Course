import java.util.ArrayList;
import java.util.List;

/**
 * Реализация ориентированного графа на списках смежности.
 */
public class AdjacencyListGraph implements Graph {
    /** Списки смежности для каждой вершины. */
    private List<List<Integer>> adjacency;

    /**
     * Создает граф с указанным числом вершин.
     *
     * @param n начальное число вершин.
     * @throws GraphException если n отрицательно.
     */
    public AdjacencyListGraph(int n) {
        if (n < 0) {
            throw new GraphException("Размер графа не может быть отрицательным.");
        }
        this.adjacency = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    /**
     * Создает пустой граф.
     */
    public AdjacencyListGraph() {
        this(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addVertex() {
        adjacency.add(new ArrayList<>());
        return adjacency.size() - 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeVertex(int v) {
        checkVertex(v);
        // Удаляем список смежности вершины v.
        adjacency.remove(v);
        // Удаляем рёбра, ведущие в v, и сдвигаем индексы > v на 1 вниз.
        for (List<Integer> list : adjacency) {
            list.removeIf(u -> u == v);
            for (int i = 0; i < list.size(); i++) {
                int u = list.get(i);
                if (u > v) {
                    list.set(i, u - 1);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        List<Integer> list = adjacency.get(from);
        if (!list.contains(to)) {
            list.add(to);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        adjacency.get(from).remove((Integer) to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        return adjacency.get(from).contains(to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getNeighbors(int v) {
        checkVertex(v);
        return new ArrayList<>(adacencyOf(v));
    }

    /**
     * Возвращает внутренний список смежности вершины.
     *
     * @param v вершина.
     * @return изменяемый список смежности.
     */
    private List<Integer> adacencyOf(int v) {
        return adjacency.get(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return adjacency.size();
    }

    /**
     * Проверяет индекс вершины на попадание в диапазон.
     *
     * @param v индекс вершины.
     * @throws GraphIndexException если индекс вне диапазона.
     */
    private void checkVertex(int v) {
        if (v < 0 || v >= adjacency.size()) {
            String msg = "Вершина " + v + " вне диапазона 0.."
                    + (adjacency.size() - 1);
            throw new GraphIndexException(msg);
        }
    }

    @Override
    public String toString() {
        return toStringDefault();
    }
}