package org.example;

import java.util.Objects;

/**
 * Element extension for styles of texts.
 */

public final class TextStyle extends Element {

    /**
     * Text style methods.
     */

    public enum Kind {
        BOLD("**", "**"),
        ITALIC("*", "*"),
        STRIKE("~~", "~~"),
        CODE("`", "`");

        final String prefix;
        final String suffix;

        Kind(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }

    /**
     * Text style methods (kind).
     */

    private final Kind kind;

    /**
     * Text style methods (content).
     */

    private final Element content;

    /**
     * Text style methods (text style).
     */

    private TextStyle(Kind kind, Element content) {
        this.kind = Objects.requireNonNull(kind, "kind");
        this.content = Objects.requireNonNull(content, "content");
    }

    /**
     * Bold text.
     */

    public static TextStyle bold(Element content) {

        return new TextStyle(Kind.BOLD, content);

    }

    /**
     * Italic text.
     */

    public static TextStyle italic(Element content) {

        return new TextStyle(Kind.ITALIC, content);

    }

    /**
     * stike a text.
     */

    public static TextStyle strike(Element content) {

        return new TextStyle(Kind.STRIKE, content);

    }

    /**
     * code text.
     */

    public static TextStyle code(Element content) {

        return new TextStyle(Kind.CODE, content);

    }

    @Override
    public String toMarkdown() {
        return kind.prefix + content.toMarkdown() + kind.suffix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TextStyle textStyle = (TextStyle) o;
        return kind == textStyle.kind && Objects.equals(content, textStyle.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, content);
    }
}