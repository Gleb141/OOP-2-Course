import java.util.List;
import java.util.Objects;


/**
 * Markdown conversion setup.
 */

abstract class Element {
    public abstract String toMarkdown();

    @Override
    public String toString() {
        return toMarkdown();
    }
}















