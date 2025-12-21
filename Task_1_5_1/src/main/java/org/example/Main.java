package org.example;

public class Main {
    public static void main(String[] args) {
        Element title = new Heading(1, TextStyle.bold(new PlainText("Markdown Generator")));
        Element nested = TextStyle.bold(TextStyle.italic(new PlainText("Bold+Italic nested")));
        Element link = new Link(new PlainText("click me"), "https://example.com");

        Element nestedOrdered = new ListElement(true,
                new PlainText("Nested 1"), new PlainText("Nested 2"));
        Element nestedItem = Sequence.lines(new PlainText("Nested"), nestedOrdered);

        Element list = new ListElement(false,
                new PlainText("First"),
                TextStyle.italic(new PlainText("Second")),
                new TaskItem(true, new PlainText("Done task")),
                nestedItem
        );

        Element table = Table.builder(new PlainText("Index"), new PlainText("Random"))
                .align(0, Table.Align.RIGHT)
                .addRow(new PlainText("1"), TextStyle.bold(new PlainText("8")))
                .addRow(new PlainText("2"), new PlainText("2"))
                .build();

        Element code = new CodeBlock("java", "System.out.println(\"Hello\");");

        Document doc = new MarkdownBuilder()
                .add(title)
                .add(nested)
                .add(link)
                .add(list)
                .add(table)
                .add(code)
                .build();

        System.out.println(doc.toMarkdown());
    }
}