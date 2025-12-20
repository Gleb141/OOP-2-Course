package org.example;

import java.util.Objects;

public final class Link extends Element {
    private final Element label;
    private final String url;

    public Link(Element label, String url) {
        this.label = requireNonNull(label, "label");
        requireNonBlank(url, "url");
        this.url = url;
    }

    @Override
    public String toMarkdown() {
        return "[" + label.toMarkdown() + "](" + url + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(label, link.label) && Objects.equals(url, link.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, url);
    }
}
