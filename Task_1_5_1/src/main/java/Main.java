
/**
 * Main class.
 */

public class Main {

    /**
     * Main demo.
     */

    public static void main(String[] args) {

        Element title = new Heading(1, new Text.Bold("Markdown Generator"));

        Element list = new ListElement(false,
                new Text.Plain("First"),
                new Text.Italic("Second"),
                new TaskItem(true, new Text.Plain("Done task"))
        );

        System.out.println(title);
        System.out.println();
        System.out.println(list);
    }
}
