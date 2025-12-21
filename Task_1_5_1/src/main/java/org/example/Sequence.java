package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Element extension for sequences.
 */

public final class Sequence extends Element {
    private final String separator;
    private final List<Element> elements;

    /**
     * Sequence methods.
     */

    public Sequence(String separator, Element... elements) {
        this.separator = requireNonNull(separator, "separator");
        this.elements = List.copyOf(Arrays.asList(elements));
        for (Element e : this.elements) {
            requireNonNull(e, "element");
        }
    }

    public static Sequence lines(Element... elements) {
        return new Sequence("\n", elements);
    }

    public static Sequence concat(Element... elements) {
        return new Sequence("", elements);
    }

    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(elements.get(i).toMarkdown());
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
        Sequence sequence = (Sequence) o;
        return Objects.equals(separator, sequence.separator)
                && Objects.equals(elements, sequence.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(separator, elements);
    }
}