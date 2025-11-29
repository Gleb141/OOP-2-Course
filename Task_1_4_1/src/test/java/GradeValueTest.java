import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for GradeValue.
 */

public class GradeValueTest {

    @Test
    void numericValuesAreCorrect() {
        assertEquals(2, GradeValue.UNSATISFACTORY.getNumericValue());
        assertEquals(3, GradeValue.SATISFACTORY.getNumericValue());
        assertEquals(4, GradeValue.GOOD.getNumericValue());
        assertEquals(5, GradeValue.EXCELLENT.getNumericValue());
    }

    @Test
    void displayNameAndToStringMatch() {
        for (GradeValue gv : GradeValue.values()) {
            assertEquals(gv.getDisplayName(), gv.toString());
        }
    }
}

