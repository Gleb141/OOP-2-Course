import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/** Граф по списку инцидентности. */
public class AdjacencyListGraph implements Graph {
    private final List<List<Integer>> adj;
    /** Граф по списку инцидентности. */

    public AdjacencyListGraph(int n) {
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }
    /** Граф по списку инцидентности. */

    public AdjacencyListGraph() {
        this(0);
    }


    @Override
    public int addVertex() {
        adj.add(new ArrayList<>());
        return adj.size() - 1;
    }

    @Override
    public void removeVertex(int v) {
        checkVertex(v);
        // Удаляем саму вершину (её список исходящих)
        adj.remove(v);
        // Обновляем все списки смежности:
        for (List<Integer> row : adj) {
            ListIterator<Integer> it = row.listIterator();
            while (it.hasNext()) {
                int to = it.next();
                if (to == v) {
                    it.remove();                  // удаляем ребро, ведущее в удалённую вершину
                } else if (to > v) {
                    it.set(to - 1);               // сдвигаем индексы правее
                }
            }
        }
    }

    @Override
    public void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        List<Integer> row = adj.get(from);
        if (!row.contains(to)) {                   // идемпотентность: без дублей
            row.add(to);
        }
    }

    @Override
    public void removeEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        // удаляем все вхождения на всякий случай
        adj.get(from).removeIf(x -> x == to);
    }

    @Override
    public List<Integer> getNeighbors(int v) {
        checkVertex(v);
        return new ArrayList<>(adj.get(v));        // возвращаем копию
    }

    @Override
    public int size() {
        return adj.size();
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= adj.size()) {
            throw new GraphIndexException("Вершина " + v + " вне диапазона 0.." + (adj.size() - 1));
        }
    }

    @Override
    public String toString() {
        return toStringDefault();
    }
}