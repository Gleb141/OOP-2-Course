
/** Исключение по наличию цикла в графе */
public class GraphCycleException extends GraphException {
    public GraphCycleException(String message) {
        super(message);
    }
}


