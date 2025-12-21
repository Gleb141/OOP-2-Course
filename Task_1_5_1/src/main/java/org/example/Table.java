package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Table extends Element {

    public enum Align { LEFT, RIGHT, CENTER }

    private final List<Element> headers;
    private final List<List<Element>> rows;
    private final List<Align> aligns;
    private final int rowLimit;

    private Table(List<Element> headers, List<List<Element>> rows, List<Align> aligns, int rowLimit) {
        this.headers = List.copyOf(headers);
        this.rows = List.copyOf(rows);
        this.aligns = List.copyOf(aligns);
        this.rowLimit = rowLimit;
    }

    public static Builder builder(Element... headers) {
        return new Builder(headers);
    }

    @Override
    public String toMarkdown() {
        int cols = headers.size();
        if (cols == 0) {
            return "";
        }

        List<String> headerStr = new ArrayList<>();
        for (Element h : headers) {
            headerStr.add(cell(h));
        }

        int maxRows = Math.min(rows.size(), rowLimit);

        List<List<String>> rowStr = new ArrayList<>();
        for (int r = 0; r < maxRows; r++) {
            List<Element> row = rows.get(r);
            List<String> converted = new ArrayList<>();
            for (Element c : row) {
                converted.add(cell(c));
            }
            rowStr.add(converted);
        }

        int[] widths = new int[cols];
        for (int c = 0; c < cols; c++) {
            widths[c] = headerStr.get(c).length();
        }
        for (List<String> r : rowStr) {
            for (int c = 0; c < cols; c++) {
                widths[c] = Math.max(widths[c], r.get(c).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(renderRow(headerStr, widths, aligns)).append("\n");
        sb.append(renderSeparator(widths, aligns));

        for (List<String> r : rowStr) {
            sb.append("\n").append(renderRow(r, widths, aligns));
        }

        return sb.toString();
    }

    private static String renderRow(List<String> cells, int[] widths, List<Align> aligns) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for (int i = 0; i < cells.size(); i++) {
            String padded = pad(cells.get(i), widths[i], aligns.get(i));
            sb.append(" ").append(padded).append(" |");
        }
        return sb.toString();
    }

    private static String renderSeparator(int[] widths, List<Align> aligns) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for (int i = 0; i < widths.length; i++) {
            sb.append(" ").append(separatorCell(widths[i], aligns.get(i))).append(" |");
        }
        return sb.toString();
    }

    private static String separatorCell(int width, Align align) {
        int dashCount = Math.max(width, 3);
        if (align == Align.RIGHT) {
            return "-".repeat(dashCount - 1) + ":";
        }
        if (align == Align.CENTER) {
            return ":" + "-".repeat(dashCount - 2) + ":";
        }
        return "-".repeat(dashCount);
    }

    private static String pad(String s, int width, Align align) {
        int diff = width - s.length();
        if (diff <= 0) {
            return s;
        }

        if (align == Align.RIGHT) {
            return " ".repeat(diff) + s;
        }
        if (align == Align.CENTER) {
            int left = diff / 2;
            int right = diff - left;
            return " ".repeat(left) + s + " ".repeat(right);
        }
        return s + " ".repeat(diff);
    }

    private static String cell(Element e) {
        String s = e.toMarkdown();
        s = s.replace("\r\n", "\n").replace("\r", "\n");
        s = s.replace("\n", "<br>");
        s = s.replace("|", "\\|");
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Table table = (Table) o;
        return rowLimit == table.rowLimit
                && Objects.equals(headers, table.headers)
                && Objects.equals(rows, table.rows)
                && Objects.equals(aligns, table.aligns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers, rows, aligns, rowLimit);
    }

    public static final class Builder {
        private final List<Element> headers;
        private final List<List<Element>> rows = new ArrayList<>();
        private final List<Align> aligns;
        private int rowLimit = Integer.MAX_VALUE;

        public Builder(Element... headers) {
            this.headers = List.copyOf(Arrays.asList(headers));
            if (this.headers.isEmpty()) {
                throw new IllegalArgumentException("headers must not be empty");
            }
            for (Element h : this.headers) {
                requireNonNull(h, "header");
            }
            this.aligns = new ArrayList<>();
            for (int i = 0; i < this.headers.size(); i++) {
                this.aligns.add(Align.LEFT);
            }
        }

        public Builder align(int col, Align align) {
            if (col < 0 || col >= headers.size()) {
                throw new IndexOutOfBoundsException("column index out of range");
            }
            this.aligns.set(col, requireNonNull(align, "align"));
            return this;
        }

        public Builder rowLimit(int limit) {
            if (limit < 0) {
                throw new IllegalArgumentException("rowLimit must be >= 0");
            }
            this.rowLimit = limit;
            return this;
        }

        public Builder addRow(Element... cells) {
            List<Element> row = List.copyOf(Arrays.asList(cells));
            if (row.size() != headers.size()) {
                throw new IllegalArgumentException("row cells count must match headers count");
            }
            for (Element c : row) {
                requireNonNull(c, "cell");
            }
            rows.add(row);
            return this;
        }

        public Table build() {
            return new Table(headers, rows, aligns, rowLimit);
        }
    }
}

