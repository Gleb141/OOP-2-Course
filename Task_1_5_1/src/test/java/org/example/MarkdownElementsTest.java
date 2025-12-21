package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownElementsTest {

    @Test
    void plainText_toMarkdown_toString_equals_hash() {
        PlainText a = new PlainText("Hello");
        PlainText b = new PlainText("Hello");
        PlainText c = new PlainText("World");

        assertEquals("Hello", a.toMarkdown());
        assertEquals("Hello", a.toString());
        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
    }

    @Test
    void textStyle_all_kinds_and_nesting_and_equals() {
        Element base = new PlainText("X");

        assertEquals("**X**", TextStyle.bold(base).toMarkdown());
        assertEquals("*X*", TextStyle.italic(base).toMarkdown());
        assertEquals("~~X~~", TextStyle.strike(base).toMarkdown());
        assertEquals("`X`", TextStyle.code(base).toMarkdown());

        Element nested = TextStyle.bold(TextStyle.italic(new PlainText("Hi")));
        assertEquals("***Hi***", nested.toMarkdown());

        TextStyle s1 = TextStyle.bold(new PlainText("A"));
        TextStyle s2 = TextStyle.bold(new PlainText("A"));
        TextStyle s3 = TextStyle.italic(new PlainText("A"));

        assertEquals(s1, s1);
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotEquals(s1, s3);
        assertNotEquals(s1, null);

        assertThrows(NullPointerException.class, () -> TextStyle.bold(null));
    }

    @Test
    void heading_valid_and_invalid_and_equals() {
        Heading h = new Heading(2, new PlainText("Title"));
        assertEquals("## Title", h.toMarkdown());
        assertEquals("## Title", h.toString());

        assertThrows(IllegalArgumentException.class, () -> new Heading(0, new PlainText("X")));
        assertThrows(IllegalArgumentException.class, () -> new Heading(7, new PlainText("X")));
        assertThrows(NullPointerException.class, () -> new Heading(1, null));

        Heading h1 = new Heading(1, new PlainText("X"));
        Heading h2 = new Heading(1, new PlainText("X"));
        Heading h3 = new Heading(2, new PlainText("X"));

        assertEquals(h1, h1);
        assertEquals(h1, h2);
        assertEquals(h1.hashCode(), h2.hashCode());
        assertNotEquals(h1, h3);
        assertNotEquals(h1, null);
        assertNotEquals(h1, new PlainText("X"));
    }

    @Test
    void link_formatted_label_invalid_url_equals() {
        Link link = new Link(TextStyle.italic(new PlainText("click")), "https://example.com");
        assertEquals("[*click*](https://example.com)", link.toMarkdown());

        assertThrows(IllegalArgumentException.class, () -> new Link(new PlainText("x"), ""));
        assertThrows(IllegalArgumentException.class, () -> new Link(new PlainText("x"), "   "));
        assertThrows(IllegalArgumentException.class, () -> new Link(new PlainText("x"), null));
        assertThrows(NullPointerException.class, () -> new Link(null, "u"));

        Link a = new Link(new PlainText("x"), "u");
        Link b = new Link(new PlainText("x"), "u");
        Link c = new Link(new PlainText("y"), "u");
        Link d = new Link(new PlainText("x"), "v");

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, d);
        assertNotEquals(a, null);
        assertNotEquals(a, new PlainText("x"));
    }

    @Test
    void image_valid_invalid_equals() {
        ImageElement img = new ImageElement("alt", "img.png");
        assertEquals("![alt](img.png)", img.toMarkdown());

        assertThrows(IllegalArgumentException.class, () -> new ImageElement("", "img.png"));
        assertThrows(IllegalArgumentException.class, () -> new ImageElement("   ", "img.png"));
        assertThrows(IllegalArgumentException.class, () -> new ImageElement("alt", ""));
        assertThrows(IllegalArgumentException.class, () -> new ImageElement("alt", "   "));
        assertThrows(IllegalArgumentException.class, () -> new ImageElement(null, "img.png"));
        assertThrows(IllegalArgumentException.class, () -> new ImageElement("alt", null));

        ImageElement a = new ImageElement("a", "u");
        ImageElement b = new ImageElement("a", "u");
        ImageElement c = new ImageElement("b", "u");

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, new PlainText("a"));
    }

    @Test
    void codeBlock_language_variants_and_equals() {
        CodeBlock a = new CodeBlock("java", "code");
        assertEquals("```java\ncode\n```", a.toMarkdown());

        CodeBlock b = new CodeBlock(null, "code");
        assertEquals("```\ncode\n```", b.toMarkdown());

        CodeBlock c = new CodeBlock("   ", "code");
        assertEquals("```\ncode\n```", c.toMarkdown());

        CodeBlock a2 = new CodeBlock("java", "code");
        assertEquals(a, a);
        assertEquals(a, a2);
        assertEquals(a.hashCode(), a2.hashCode());
        assertNotEquals(a, b);
        assertNotEquals(a, null);
        assertNotEquals(a, new PlainText("x"));

        assertThrows(NullPointerException.class, () -> new CodeBlock("java", null));
    }

    @Test
    void blockQuote_multiline_nested_equals() {
        BlockQuote q = new BlockQuote(new PlainText("Line1\nLine2"));
        assertEquals("> Line1\n> Line2", q.toMarkdown());

        BlockQuote nested = new BlockQuote(new BlockQuote(new PlainText("X")));
        assertEquals("> > X", nested.toMarkdown());

        BlockQuote a = new BlockQuote(new PlainText("X"));
        BlockQuote b = new BlockQuote(new PlainText("X"));
        BlockQuote c = new BlockQuote(new PlainText("Y"));

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, new PlainText("X"));

        assertThrows(NullPointerException.class, () -> new BlockQuote(null));
    }

    @Test
    void taskItem_checked_unchecked_equals() {
        TaskItem done = new TaskItem(true, new PlainText("Done"));
        TaskItem todo = new TaskItem(false, new PlainText("Todo"));

        assertEquals("- [x] Done", done.toMarkdown());
        assertEquals("- [ ] Todo", todo.toMarkdown());

        TaskItem a = new TaskItem(true, new PlainText("A"));
        TaskItem b = new TaskItem(true, new PlainText("A"));
        TaskItem c = new TaskItem(false, new PlainText("A"));
        TaskItem d = new TaskItem(true, new PlainText("B"));

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, d);
        assertNotEquals(a, null);
        assertNotEquals(a, new PlainText("A"));

        assertThrows(NullPointerException.class, () -> new TaskItem(true, null));
    }

    @Test
    void list_unordered_ordered_empty_multiline_task_specialcase_equals() {
        ListElement empty = new ListElement(false);
        assertEquals("", empty.toMarkdown());

        ListElement ul = new ListElement(false, new PlainText("A"), new PlainText("B"));
        assertEquals("- A\n- B", ul.toMarkdown());

        ListElement ol = new ListElement(true, new PlainText("A"), new PlainText("B"));
        assertEquals("1. A\n2. B", ol.toMarkdown());

        ListElement multi = new ListElement(false, new PlainText("A\nB"));
        assertEquals("- A\n  B", multi.toMarkdown());

        ListElement withTask = new ListElement(false, new PlainText("A"),
                new TaskItem(true, new PlainText("T")));
        assertEquals("- A\n- [x] T", withTask.toMarkdown());

        assertThrows(NullPointerException.class, () ->
                new ListElement(false, new PlainText("A"), null));

        ListElement x1 = new ListElement(false, new PlainText("A"));
        ListElement x2 = new ListElement(false, new PlainText("A"));
        ListElement x3 = new ListElement(true, new PlainText("A"));

        assertEquals(x1, x1);
        assertEquals(x1, x2);
        assertEquals(x1.hashCode(), x2.hashCode());
        assertNotEquals(x1, x3);
        assertNotEquals(x1, null);
        assertNotEquals(x1, new PlainText("A"));
    }

    @Test
    void sequence_lines_concat_equals_and_null_separator() {
        Sequence s1 = Sequence.lines(new PlainText("A"), new PlainText("B"));
        assertEquals("A\nB", s1.toMarkdown());

        Sequence s2 = Sequence.concat(new PlainText("A"), new PlainText("B"));
        assertEquals("AB", s2.toMarkdown());

        Sequence a = new Sequence(",", new PlainText("A"), new PlainText("B"));
        Sequence b = new Sequence(",", new PlainText("A"), new PlainText("B"));
        Sequence c = new Sequence(";", new PlainText("A"), new PlainText("B"));

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, new PlainText("A"));

        assertThrows(NullPointerException.class, () -> new Sequence(null, new PlainText("X")));
        assertThrows(NullPointerException.class, () ->
                new Sequence(",", new PlainText("X"), null));
    }

    @Test
    void table_sample_alignment_rowlimit_invalids_equals() {
        Table t = Table.builder(new PlainText("Index"), new PlainText("Random"))
                .align(0, Table.Align.RIGHT)
                .addRow(new PlainText("1"), TextStyle.bold(new PlainText("8")))
                .addRow(new PlainText("2"), new PlainText("2"))
                .build();

        String expected =
                "| Index | Random |\n"
                        + "| ----: | ------ |\n"
                        + "|     1 | **8**  |\n"
                        + "|     2 | 2      |";

        assertEquals(expected, t.toMarkdown());

        Table limited = Table.builder(new PlainText("A"), new PlainText("B"))
                .rowLimit(1)
                .addRow(new PlainText("1"), new PlainText("2"))
                .addRow(new PlainText("3"), new PlainText("4"))
                .build();

        String expectedLimited =
                "| A | B |\n"
                        + "| --- | --- |\n"
                        + "| 1 | 2 |";

        assertEquals(expectedLimited, limited.toMarkdown());

        Table center = Table.builder(new PlainText("C"))
                .align(0, Table.Align.CENTER)
                .addRow(new PlainText("X"))
                .build();

        assertTrue(center.toMarkdown().contains(":"));

        assertThrows(IllegalArgumentException.class, () -> Table.builder());
        assertThrows(IndexOutOfBoundsException.class, () ->
                Table.builder(new PlainText("A")).align(1, Table.Align.LEFT));
        assertThrows(NullPointerException.class, () ->
                Table.builder(new PlainText("A")).align(0, null));
        assertThrows(IllegalArgumentException.class, () ->
                Table.builder(new PlainText("A")).rowLimit(-1));
        assertThrows(IllegalArgumentException.class, () ->
                Table.builder(new PlainText("A")).addRow(new PlainText("1"), new PlainText("2")));

        Table e1 = Table.builder(new PlainText("A")).addRow(new PlainText("1")).build();
        Table e2 = Table.builder(new PlainText("A")).addRow(new PlainText("1")).build();
        Table e3 = Table.builder(new PlainText("A")).addRow(new PlainText("2")).build();

        assertEquals(e1, e1);
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1, e3);
        assertNotEquals(e1, null);
        assertNotEquals(e1, new PlainText("A"));
    }

    @Test
    void document_and_builder_and_main() {
        Document empty = Document.of();
        assertEquals("", empty.toMarkdown());

        Document a = Document.of(new PlainText("A"), new PlainText("B"));
        assertEquals("A\n\nB", a.toMarkdown());

        Document b =
                new MarkdownBuilder().add(new PlainText("A")).add(new PlainText("B")).build();
        assertEquals("A\n\nB", b.toMarkdown());

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertThrows(NullPointerException.class, () -> new MarkdownBuilder().add(null));
        assertThrows(NullPointerException.class, () ->
                new Document(java.util.List.of(new PlainText("X"), null)));

        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}