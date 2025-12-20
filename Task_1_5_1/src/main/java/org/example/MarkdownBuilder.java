package org.example;

import java.util.ArrayList;
import java.util.List;

public final class MarkdownBuilder {
    private final List<Element> blocks = new ArrayList<>();

    public MarkdownBuilder add(Element block) {
        blocks.add(Element.requireNonNull(block, "block"));
        return this;
    }

    public Document build() {
        return new Document(blocks);
    }
}
