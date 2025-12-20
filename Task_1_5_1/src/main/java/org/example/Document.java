package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Document extends Element {
    private final List<Element> blocks;

    public Document(List<Element> blocks) {
        this.blocks = List.copyOf(blocks);
    }

    public static Document of(Element... blocks) {
        return new Document(Arrays.asList(blocks));
    }

    @Override
    public String toMarkdown() {
        return String.join("\n\n", blocks.stream().map(Element::toMarkdown).toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(blocks, document.blocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blocks);
    }
}
