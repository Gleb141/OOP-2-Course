import java.util.List;
import java.util.Objects;

abstract class Element {
    public abstract String toMarkdown();

    @Override
    public String toString() {
        return toMarkdown();
    }
}

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

class BlockQuote extends Element {
    private final Element body;

    BlockQuote(Element body) {
        this.body = body;
    }

    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        for (String line : body.toMarkdown().split("\\r?\\n")) {
            sb.append("> ").append(line).append("\n");
        }
        if (!sb.isEmpty()) sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockQuote that = (BlockQuote) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}

class Link extends Element {
    private final String label;
    private final String url;

    Link(String label, String url) {
        this.label = label;
        this.url = url;
    }

    @Override
    public String toMarkdown() {
        return "[" + label + "](" + url + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(label, link.label)
                && Objects.equals(url, link.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, url);
    }
}

class ImageElement extends Element {
    private final String alt;
    private final String url;

    ImageElement(String alt, String url) {
        this.alt = alt;
        this.url = url;
    }

    @Override
    public String toMarkdown() {
        return "![" + alt + "](" + url + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageElement that = (ImageElement) o;
        return Objects.equals(alt, that.alt)
                && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alt, url);
    }
}

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

class ListElement extends Element {
    private final boolean ordered;
    private final List<Element> items;

    ListElement(boolean ordered, Element... items) {
        this.ordered = ordered;
        this.items = List.of(items);
    }

    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append(ordered ? (i + 1) + ". " : "- ");
            sb.append(items.get(i).toMarkdown());
            if (i + 1 < items.size()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}

class CodeBlock extends Element {
    private final String language;
    private final String code;

    CodeBlock(String language, String code) {
        this.language = language;
        this.code = code;
    }

    @Override
    public String toMarkdown() {
        return "```" + (language == null ? "" : language) + "\n" + code + "\n```";
    }
}