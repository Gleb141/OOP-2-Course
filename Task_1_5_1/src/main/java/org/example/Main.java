package org.example;

public class Main {
    public static void main(String[] args) {
        Element title = new Heading(1, TextStyle.bold(new PlainText("Markdown Generator")));

        Element nested = TextStyle.bold(
                TextStyle.italic(new PlainText("Bold+Italic nested"))
        );

        Element link = new Link(
                TextStyle.strike(new PlainText("click me")),
                "https://example.com"
        );

        Element list = new ListElement(false,
                new PlainText("First"),
                TextStyle.italic(new PlainText("Second")),
                new TaskItem(true, new PlainText("Done task")),
                new ListElement(true, // вложенный список
                        new PlainText("Nested 1"),
                        new PlainText("Nested 2")
                )
        );

        Element doc = new MarkdownBuilder()
                .add(title)
                .add(nested)
                .add(link)
                .add(list)
                .add(new CodeBlock("java", "System.out.println(\"Hello\");"))
                .build();

        System.out.println(doc.toMarkdown());
    }
}
