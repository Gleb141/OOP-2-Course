package org.example;

import java.util.Objects;

public final class BlockQuote extends Element {
    private final Element body;

    public BlockQuote(Element body) {
        this.body = requireNonNull(body, "body");
    }

    @Override
    public String toMarkdown() {
        String[] lines = body.toMarkdown().split("\\R", -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append("> ").append(lines[i]);
            if (i + 1 < lines.length) {
                sb.append("\n");
            }
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
        BlockQuote blockQuote = (BlockQuote) o;
        return Objects.equals(body, blockQuote.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}