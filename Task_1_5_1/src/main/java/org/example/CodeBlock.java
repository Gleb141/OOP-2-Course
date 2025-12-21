package org.example;

import java.util.Objects;

public final class CodeBlock extends Element {
    private final String language;
    private final String code;

    public CodeBlock(String language, String code) {
        this.language = (language == null || language.isBlank()) ? "" : language;
        this.code = Objects.requireNonNull(code, "code");
    }

    @Override
    public String toMarkdown() {
        return "```" + language + "\n" + code + "\n```";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeBlock codeBlock = (CodeBlock) o;
        return Objects.equals(language, codeBlock.language) && Objects.equals(code, codeBlock.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, code);
    }
}