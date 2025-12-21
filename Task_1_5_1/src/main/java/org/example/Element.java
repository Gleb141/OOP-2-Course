package org.example;

import java.util.Objects;

public abstract class Element {
    public abstract String toMarkdown();

    @Override
    public final String toString() {
        return toMarkdown();
    }

    protected static void requireNonBlank(String s, String name) {
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException(name + " must be non-blank");
        }
    }

    protected static <T> T requireNonNull(T v, String name) {
        return Objects.requireNonNull(v, name + " must not be null");
    }
}