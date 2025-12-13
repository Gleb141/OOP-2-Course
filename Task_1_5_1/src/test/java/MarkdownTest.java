import org.junit.jupiter.api.Test;

/**
 * Tests for the markdown converter.
 */

public class MarkdownTest {

    @Test
    void testText() {
        assert new Text.Plain("a").toMarkdown().equals("a");
        assert new Text.Bold("b").toMarkdown().equals("**b**");
        assert new Text.Italic("c").toMarkdown().equals("*c*");
        assert new Text.Strike("d").toMarkdown().equals("~~d~~");
        assert new Text.Code("e").toMarkdown().equals("`e`");
    }

    @Test
    void testHeading() {
        assert new Heading(1, new Text.Plain("Hi"))
                .toMarkdown().equals("# Hi");
        assert new Heading(6, new Text.Bold("X"))
                .toMarkdown().equals("###### **X**");
    }

    @Test
    void testLists() {
        Element ul = new ListElement(false,
                new Text.Plain("a"),
                new Text.Bold("b")
        );

        assert ul.toMarkdown().equals("- a\n- **b**");

        Element ol = new ListElement(true,
                new Text.Plain("x"),
                new Text.Plain("y")
        );

        assert ol.toMarkdown().equals("1. x\n2. y");
    }

    @Test
    void testBlockQuote() {
        Element quote = new BlockQuote(
                new Text.Plain("line1\nline2")
        );

        assert quote.toMarkdown().equals("> line1\n> line2");
    }

    @Test
    void testLinksAndImages() {
        assert new Link("A", "url")
                .toMarkdown().equals("[A](url)");

        assert new ImageElement("img", "src")
                .toMarkdown().equals("![img](src)");
    }

    @Test
    void testTaskItem() {
        assert new TaskItem(true, new Text.Plain("done"))
                .toMarkdown().equals("- [x] done");

        assert new TaskItem(false, new Text.Plain("todo"))
                .toMarkdown().equals("- [ ] todo");
    }

    @Test
    void testCodeBlock() {
        assert new CodeBlock(null, "x")
                .toMarkdown().equals("```\nx\n```");

        assert new CodeBlock("java", "int a;")
                .toMarkdown().equals("```java\nint a;\n```");
    }

    @Test
    void testEquals() {
        assert new Text.Bold("a").equals(new Text.Bold("a"));
        assert !new Text.Bold("a").equals(new Text.Bold("b"));

        assert new Heading(2, new Text.Plain("x"))
                .equals(new Heading(2, new Text.Plain("x")));

        assert new Link("a", "b")
                .equals(new Link("a", "b"));

        assert new TaskItem(true, new Text.Plain("x"))
                .equals(new TaskItem(true, new Text.Plain("x")));
    }

    @Test
    void testNegative() {
        boolean thrown = false;
        try {
            new Heading(0, new Text.Plain("bad"));
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assert thrown;

        thrown = false;
        try {
            new Heading(7, new Text.Plain("bad"));
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assert thrown;
    }
}

