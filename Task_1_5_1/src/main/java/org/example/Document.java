package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Document extends Element {
    private final List<Element> blocks;

    public Document(List<Element> blocks) {
        this.blocks = List.copyOf(blocks);
        for (Element b : this.blocks) {
            requireNonNull(b, "block");
        }
    }

    public static Document of(Element... blocks) {
        return new Document(Arrays.asList(blocks));
    }

    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            if (i > 0) {
                sb.append("\n\n");
            }
            sb.append(blocks.get(i).toMarkdown());
        }
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
        Document document = (Document) o;
        return Objects.equals(blocks, document.blocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blocks);
    }
}