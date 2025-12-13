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

/**
 * Text element.
 */

class Text extends Element {
    protected final String prefix;
    protected final String value;
    protected final String suffix;

    public Text(String value) {
        this("", value, "");
    }

    protected Text(String prefix, String value, String suffix) {
        this.prefix = prefix;
        this.value = value;
        this.suffix = suffix;
    }

    @Override
    public String toMarkdown() {
        return prefix + value + suffix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Text text = (Text) o;
        return Objects.equals(prefix, text.prefix)
                && Objects.equals(value, text.value)
                && Objects.equals(suffix, text.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, value, suffix);
    }

    static final class Plain extends Text {
        Plain(String value) {
            super(value);
        }
    }

    static final class Bold extends Text {
        Bold(String value) {
            super("**", value, "**");
        }
    }

    static final class Italic extends Text {
        Italic(String value) {
            super("*", value, "*");
        }
    }

    static final class Strike extends Text {
        Strike(String value) {
            super("~~", value, "~~");
        }
    }

    static final class Code extends Text {
        Code(String value) {
            super("`", value, "`");
        }
    }
}













