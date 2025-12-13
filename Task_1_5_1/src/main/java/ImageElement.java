import java.util.Objects;


/**
 * Image element url.
 */

class ImageElement extends Element {
    private final String alt;
    private final String url;

    ImageElement(String alt, String url) {
        this.alt = alt;
        this.url = url;
    }

    @Override
    public String toMarkdown() {
        return "![" + alt + "](" + url + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageElement that = (ImageElement) o;
        return Objects.equals(alt, that.alt)
                && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alt, url);
    }
}
