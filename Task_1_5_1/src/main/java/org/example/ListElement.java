package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ListElement extends Element {
    private final boolean ordered;
    private final List<Element> items;

    public ListElement(boolean ordered, Element... items) {
        this.ordered = ordered;
        this.items = List.copyOf(Arrays.asList(items));
    }

    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            String marker = ordered ? (i + 1) + ". " : "- ";
            String indent = " ".repeat(marker.length());

            String itemMd = items.get(i).toMarkdown();
            String[] lines = itemMd.split("\\R", -1);

            sb.append(marker).append(lines[0]);

            for (int l = 1; l < lines.length; l++) {
                sb.append("\n").append(indent).append(lines[l]);
            }

            if (i + 1 < items.size()) sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListElement that = (ListElement) o;
        return ordered == that.ordered && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordered, items);
    }
}
