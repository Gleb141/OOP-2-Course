import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Markdown tests
 */

public class MarkdownTest {
    @Test
    void testTextMarkdownAndEquality() {
        assertEquals("a", new Text.Plain("a").toMarkdown());
        assertEquals("**b**", new Text.Bold("b").toMarkdown());
        assertEquals("*c*", new Text.Italic("c").toMarkdown());
        assertEquals("~~d~~", new Text.Strike("d").toMarkdown());
        assertEquals("`e`", new Text.Code("e").toMarkdown());

        assertEquals(new Text.Plain("x"), new Text.Plain("x"));
        assertEquals(new Text.Plain("x").hashCode(), new Text.Plain("x").hashCode());

        assertNotEquals(new Text.Plain("x"), new Text.Plain("y"));
        assertNotEquals(new Text.Plain("x"), new Text.Bold("x"));
        assertNotEquals(new Text.Plain("x"), null);
        assertEquals(new Text.Plain("x"), new Text.Plain("x"));
    }

    @Test
    void testElementToStringUsesMarkdown() {
        Element e = new Text.Bold("Z");
        assertEquals("**Z**", e.toString());
    }

    @Test
    void testHeadingMarkdownEqualityAndValidation() {
        assertEquals("# Hi", new Heading(1, new Text.Plain("Hi")).toMarkdown());
        assertEquals("###### **X**", new Heading(6, new Text.Bold("X")).toMarkdown());

        Heading h1 = new Heading(2, new Text.Plain("A"));
        Heading h2 = new Heading(2, new Text.Plain("A"));
        Heading h3 = new Heading(3, new Text.Plain("A"));

        assertEquals(h1, h2);
        assertEquals(h1.hashCode(), h2.hashCode());
        assertNotEquals(h1, h3);
        assertNotEquals(h1, null);
        assertNotEquals(h1, new Object());

        assertThrows(IllegalArgumentException.class, () -> new Heading(0, new Text.Plain("bad")));
        assertThrows(IllegalArgumentException.class, () -> new Heading(7, new Text.Plain("bad")));
    }

    @Test
    void testListsMarkdownEqualityAndEdgeCases() {
        Element ul = new ListElement(false, new Text.Plain("a"), new Text.Bold("b"));
        assertEquals("- a\n- **b**", ul.toMarkdown());

        Element ol = new ListElement(true, new Text.Plain("x"), new Text.Plain("y"));
        assertEquals("1. x\n2. y", ol.toMarkdown());

        Element single = new ListElement(false, new Text.Plain("one"));
        assertEquals("- one", single.toMarkdown());

        Element empty = new ListElement(false);
        assertEquals("", empty.toMarkdown());

        ListElement l1 = new ListElement(false, new Text.Plain("a"), new Text.Bold("b"));
        ListElement l2 = new ListElement(false, new Text.Plain("a"), new Text.Bold("b"));
        ListElement l3 = new ListElement(true, new Text.Plain("a"), new Text.Bold("b"));

        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
        assertNotEquals(l1, l3);
        assertNotEquals(l1, null);
    }

    @Test
    void testBlockQuoteMarkdownAndEquality() {
        Element quote = new BlockQuote(new Text.Plain("line1\nline2"));
        assertEquals("> line1\n> line2", quote.toMarkdown());

        Element quoteWin = new BlockQuote(new Text.Plain("a\r\nb"));
        assertEquals("> a\n> b", quoteWin.toMarkdown());

        Element quoteEmpty = new BlockQuote(new Text.Plain(""));
        assertEquals("> ", quoteEmpty.toMarkdown());

        BlockQuote q1 = new BlockQuote(new Text.Plain("x"));
        BlockQuote q2 = new BlockQuote(new Text.Plain("x"));
        BlockQuote q3 = new BlockQuote(new Text.Plain("y"));

        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
        assertNotEquals(q1, q3);
        assertNotEquals(q1, null);
        assertNotEquals(q1, new Object());
    }

    @Test
    void testLinksImagesEquality() {
        Link a1 = new Link("A", "url");
        Link a2 = new Link("A", "url");
        Link a3 = new Link("B", "url");

        assertEquals("[A](url)", a1.toMarkdown());
        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1, a3);
        assertNotEquals(a1, null);

        ImageElement i1 = new ImageElement("img", "src");
        ImageElement i2 = new ImageElement("img", "src");
        ImageElement i3 = new ImageElement("img2", "src");

        assertEquals("![img](src)", i1.toMarkdown());
        assertEquals(i1, i2);
        assertEquals(i1.hashCode(), i2.hashCode());
        assertNotEquals(i1, i3);
        assertNotEquals(i1, null);
    }

    @Test
    void testTaskItemMarkdownAndEquality() {
        TaskItem done1 = new TaskItem(true, new Text.Plain("done"));
        TaskItem done2 = new TaskItem(true, new Text.Plain("done"));
        TaskItem todo = new TaskItem(false, new Text.Plain("done"));

        assertEquals("- [x] done", done1.toMarkdown());
        assertEquals("- [ ] done", todo.toMarkdown());

        assertEquals(done1, done2);
        assertEquals(done1.hashCode(), done2.hashCode());
        assertNotEquals(done1, todo);
        assertNotEquals(done1, null);
        assertNotEquals(done1, new Object());
    }

    @Test
    void testCodeBlockMarkdownAndEquality() {
        assertEquals("```\nx\n```", new CodeBlock(null, "x").toMarkdown());
        assertEquals("```java\nSystem.out.println(1);\n```",
                new CodeBlock("java", "System.out.println(1);").toMarkdown());

        CodeBlock c1 = new CodeBlock(null, "x");
        CodeBlock c2 = new CodeBlock(null, "x");
        CodeBlock c3 = new CodeBlock("java", "x");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
        assertNotEquals(c1, null);
        assertNotEquals(c1, new Object());
    }

    @Test
    void testMainCoversToStringPrintPath() {
        PrintStream old = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(old);
        }

        String expected = "# **Markdown Generator**\n\n- First\n- *Second*\n- - [x] Done task\n";
        assertEquals(expected, out.toString());
    }
}