import java.util.List;


/**
 * Element of a list.
 */

class ListElement extends Element {
    private final boolean ordered;
    private final List<Element> items;

    ListElement(boolean ordered, Element... items) {
        this.ordered = ordered;
        this.items = List.of(items);
    }

    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append(ordered ? (i + 1) + ". " : "- ");
            sb.append(items.get(i).toMarkdown());
            if (i + 1 < items.size()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
