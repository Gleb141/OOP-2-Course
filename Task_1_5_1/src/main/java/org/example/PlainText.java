package org.example;

import java.util.Objects;

/**
 * Element extension for plain text.
 */

public final class PlainText extends Element {
    private final String value;

    public PlainText(String value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    @Override
    public String toMarkdown() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlainText plainText = (PlainText) o;
        return Objects.equals(value, plainText.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}