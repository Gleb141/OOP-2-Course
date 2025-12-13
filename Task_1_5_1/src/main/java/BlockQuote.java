import java.util.Objects;

/**
 * Quote block.
 */

class BlockQuote extends Element {
    private final Element body;

    BlockQuote(Element body) {
        this.body = body;
    }

    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        for (String line : body.toMarkdown().split("\\r?\\n")) {
            sb.append("> ").append(line).append("\n");
        }
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
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
        BlockQuote that = (BlockQuote) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}
