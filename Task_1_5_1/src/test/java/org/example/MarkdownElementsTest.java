package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownElementsTest {

    @Test
    void plainText_serialization() {
        Element text = new Text.Plain("Hello");
        assertEquals("Hello", text.toMarkdown());
    }

    @Test
    void nestedTextFormatting_serialization() {
        Element text = new Text.Bold(new Text.Italic(new Text.Plain("Hello")));
        assertEquals("***Hello***", text.toMarkdown());
    }

    @Test
    void textEquality() {
        Element a = new Text.Bold(new Text.Plain("A"));
        Element b = new Text.Bold(new Text.Plain("A"));
        Element c = new Text.Italic(new Text.Plain("A"));
        assertEquals(a, b);
        assertNotEquals(a, c);
    }

    @Test
    void headingSerialization() {
        Element h = new Heading(2, new Text.Plain("Title"));
        assertEquals("## Title", h.toMarkdown());
    }

    @Test
    void linkWithFormattedLabel() {
        Element link = new Link(new Text.Italic(new Text.Plain("click")), "https://example.com");
        assertEquals("[*click*](https://example.com)", link.toMarkdown());
    }

    @Test
    void imageSerialization() {
        Element img = new ImageElement("alt", "img.png");
        assertEquals("![alt](img.png)", img.toMarkdown());
    }

    @Test
    void codeBlockWithLanguage() {
        Element code = new CodeBlock("java", "System.out.println(\"Hi\");");
        assertEquals("```java\nSystem.out.println(\"Hi\");\n```", code.toMarkdown());
    }

    @Test
    void codeBlockWithoutLanguage() {
        Element code = new CodeBlock(null, "code");
        assertEquals("```\ncode\n```", code.toMarkdown());
    }

    @Test
    void multiLineBlockQuote() {
        Element quote = new BlockQuote(new Text.Plain("Line1\nLine2"));
        assertEquals("> Line1\n> Line2", quote.toMarkdown());
    }

    @Test
    void unorderedListSerialization() {
        Element list = new ListElement(false, new Text.Plain("A"), new Text.Plain("B"));
        assertEquals("- A\n- B", list.toMarkdown());
    }

    @Test
    void orderedListSerialization() {
        Element list = new ListElement(true, new Text.Plain("A"), new Text.Plain("B"));
        assertEquals("1. A\n2. B", list.toMarkdown());
    }

    @Test
    void nestedListSerialization() {
        Element list = new ListElement(false, new Text.Plain("Item"), new ListElement(false, new Text.Plain("Nested")));
        assertEquals("- Item\n- - Nested", list.toMarkdown());
    }

    @Test
    void taskItemSerialization() {
        Element task = new TaskItem(true, new Text.Plain("Done"));
        assertEquals("- [x] Done", task.toMarkdown());
    }

    @Test
    void documentBuilderSerialization() {
        Document doc = new MarkdownBuilder()
                .add(new Heading(1, new Text.Plain("Title")))
                .add(new Text.Plain("Text"))
                .add(new TaskItem(false, new Text.Plain("Task")))
                .build();

        assertEquals("# Title\n\nText\n\n- [ ] Task", doc.toMarkdown());
    }

    @Test
    void documentEquality() {
        Document a = Document.of(new Text.Plain("A"), new Text.Plain("B"));
        Document b = Document.of(new Text.Plain("A"), new Text.Plain("B"));
        assertEquals(a, b);
    }
}
