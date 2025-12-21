package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Element extension for lists.
 */

public final class ListElement extends Element {
    private final boolean ordered;
    private final List<Element> items;

    public ListElement(boolean ordered, Element... items) {
        this.ordered = ordered;
        this.items = List.copyOf(Arrays.asList(items));
        for (Element e : this.items) {
            requireNonNull(e, "item");
        }
    }

    @Override
    public String toMarkdown() {
        if (items.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            Element item = items.get(i);

            if (!ordered && item instanceof TaskItem) {
                sb.append(item.toMarkdown());
            } else {
                String marker = ordered ? (i + 1) + ". " : "- ";
                String indent = " ".repeat(marker.length());
                String[] lines = item.toMarkdown().split("\\R", -1);

                sb.append(marker).append(lines[0]);
                for (int l = 1; l < lines.length; l++) {
                    sb.append("\n").append(indent).append(lines[l]);
                }
            }

            if (i + 1 < items.size()) {
                sb.append("\n");
            }
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
        ListElement that = (ListElement) o;
        return ordered == that.ordered && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordered, items);
    }
}