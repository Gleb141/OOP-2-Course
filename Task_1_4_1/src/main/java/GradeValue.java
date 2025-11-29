enum GradeValue {
    UNSATISFACTORY(2, "неудовлетворительно"),
    SATISFACTORY(3, "удовлетворительно"),
    GOOD(4, "хорошо"),
    EXCELLENT(5, "отлично");

    private final int numericValue;
    private final String displayName;

    GradeValue(int numericValue, String displayName) {
        this.numericValue = numericValue;
        this.displayName = displayName;
    }

    public int getNumericValue() {
        return numericValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
