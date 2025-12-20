package org.example;

import java.util.Objects;

public final class Heading extends Element {
    private final int level;
    private final Element text;

    public Heading(int level, Element text) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("Heading level must be 1..6");
        }
        this.level = level;
        this.text = requireNonNull(text, "text");
    }

    @Override
    public String toMarkdown() {
        return "#".repeat(level) + " " + text.toMarkdown();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Heading heading = (Heading) o;
        return level == heading.level && Objects.equals(text, heading.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, text);
    }
}
