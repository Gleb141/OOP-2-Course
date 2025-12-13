import java.util.Objects;


/**
 * URL Link.
 */

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
