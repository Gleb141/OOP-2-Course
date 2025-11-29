import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CourseResultTest {

    @Test
    void gettersReturnValuesPassedToConstructor() {
        CourseResult result = new CourseResult(
                "Математика",
                1,
                AssessmentType.EXAM,
                GradeValue.GOOD
        );

        assertEquals("Математика", result.getCourseName());
        assertEquals(1, result.getSemester());
        assertEquals(AssessmentType.EXAM, result.getAssessmentType());
        assertEquals(GradeValue.GOOD, result.getGrade());
    }

    @Test
    void toStringContainsAllFields() {
        CourseResult result = new CourseResult(
                "Математика",
                1,
                AssessmentType.DIFFERENTIATED_CREDIT,
                GradeValue.EXCELLENT
        );

        String str = result.toString();
        assertTrue(str.contains("courseName='Математика'"));
        assertTrue(str.contains("semester=1"));
        assertTrue(str.contains("assessmentType=" + AssessmentType.DIFFERENTIATED_CREDIT));
        assertTrue(str.contains("grade=" + GradeValue.EXCELLENT));
    }
}
