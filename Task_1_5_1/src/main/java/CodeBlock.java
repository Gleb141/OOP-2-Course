/**
 * Markdown converison for a block of code.
 */

class CodeBlock extends Element {
    private final String language;
    private final String code;

    CodeBlock(String language, String code) {
        this.language = language;
        this.code = code;
    }

    @Override
    public String toMarkdown() {
        return "```" + (language == null ? "" : language) + "\n" + code + "\n```";
    }
}
