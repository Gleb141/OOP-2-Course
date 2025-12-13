import java.util.Objects;


/**
 * Top heading in markdown.
 */

class Heading extends Element {
    private final int level;
    private final Element text;

    Heading(int level, Element text) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException();
        }
        this.level = level;
        this.text = text;
    }

    @Override
    public String toMarkdown() {
        return "#".repeat(level) + " " + text.toMarkdown();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Heading heading = (Heading) o;
        return level == heading.level && Objects.equals(text, heading.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, text);
    }
}
