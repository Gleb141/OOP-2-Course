package org.example;

import java.util.Objects;

abstract class Text extends Element {
    protected final Element content;

    protected Text(Element content) {
        this.content = Objects.requireNonNull(content);
    }

    protected abstract String prefix();
    protected abstract String suffix();

    @Override
    public String toMarkdown() {
        return prefix() + content.toMarkdown() + suffix();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Text text = (Text) o;
        return Objects.equals(content, text.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), content);
    }

    static final class Plain extends Element {
        private final String value;

        Plain(String value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public String toMarkdown() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Plain plain = (Plain) o;
            return Objects.equals(value, plain.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    static final class Bold extends Text {
        Bold(Element content) {
            super(content);
        }

        @Override
        protected String prefix() {
            return "**";
        }

        @Override
        protected String suffix() {
            return "**";
        }
    }

    static final class Italic extends Text {
        Italic(Element content) {
            super(content);
        }

        @Override
        protected String prefix() {
            return "*";
        }

        @Override
        protected String suffix() {
            return "*";
        }
    }

    static final class Strike extends Text {
        Strike(Element content) {
            super(content);
        }

        @Override
        protected String prefix() {
            return "~~";
        }

        @Override
        protected String suffix() {
            return "~~";
        }
    }

    static final class Code extends Text {
        Code(Element content) {
            super(content);
        }

        @Override
        protected String prefix() {
            return "`";
        }

        @Override
        protected String suffix() {
            return "`";
        }
    }
}
