import java.util.Objects;

/**
 * Task is done and its text.
 */

class TaskItem extends Element {
    private final boolean done;
    private final Element text;

    TaskItem(boolean done, Element text) {
        this.done = done;
        this.text = text;
    }

    @Override
    public String toMarkdown() {
        return "- [" + (done ? "x" : " ") + "] " + text.toMarkdown();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskItem taskItem = (TaskItem) o;
        return done == taskItem.done && Objects.equals(text, taskItem.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(done, text);
    }
}
