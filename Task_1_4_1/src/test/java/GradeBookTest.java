import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for GradeBook.
 */
public class GradeBookTest {

    @Test
    void gpaOfEmptyGradeBookIsZero() {
        GradeBook gb = new GradeBook("Студент", true, 10);
        assertEquals(0.0, gb.calculateGpa(), 1e-9);
    }

    @Test
    void gpaIsAverageOfNumericGrades() {
        GradeBook gb = new GradeBook("Студент", true, 10);

        gb.addCourseResult(new CourseResult(
                "Математика", 1, AssessmentType.EXAM, GradeValue.EXCELLENT));   // 5
        gb.addCourseResult(new CourseResult(
                "Физика", 1, AssessmentType.EXAM, GradeValue.GOOD));            // 4
        gb.addCourseResult(new CourseResult(
                "Философия", 1, AssessmentType.EXAM, GradeValue.SATISFACTORY)); // 3

        assertEquals((5 + 4 + 3) / 3.0, gb.calculateGpa(), 1e-9);
    }

    @Test
    void canTransferToBudgetFalseWhenEducationIsNotPaid() {
        GradeBook gb = new GradeBook("Студент", false, 10);
        gb.addCourseResult(new CourseResult(
                "Математика", 1, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));

        assertFalse(gb.canTransferToBudget());
    }

    @Test
    void canTransferToBudgetFalseWhenNoCourses() {
        GradeBook gb = new GradeBook("Студент", true, 10);
        assertFalse(gb.canTransferToBudget());
    }

    @Test
    void canTransferToBudgetTrueWhenNoSatisfactoryExamsInLastTwoSemesters() {
        GradeBook gb = new GradeBook("Студент", true, 10);

        // Старый семестр с "3" — не должен влиять (не входит в последние два)
        gb.addCourseResult(new CourseResult(
                "История", 1, AssessmentType.EXAM, GradeValue.SATISFACTORY
        ));

        // Последние два семестра: 2 и 3
        gb.addCourseResult(new CourseResult(
                "Математика", 2, AssessmentType.EXAM, GradeValue.GOOD
        ));
        gb.addCourseResult(new CourseResult(
                "Физика", 3, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));
        // Дифф. зачёт с "3" не запрещает перевод, метод смотрит только на экзамены
        gb.addCourseResult(new CourseResult(
                "Практика", 3, AssessmentType.DIFFERENTIATED_CREDIT, GradeValue.SATISFACTORY
        ));

        assertTrue(gb.canTransferToBudget());
    }

    @Test
    void canTransferToBudgetFalseWhenSatisfactoryExamInLastTwoSemesters() {
        GradeBook gb = new GradeBook("Студент", true, 10);

        gb.addCourseResult(new CourseResult(
                "Математика", 1, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));
        gb.addCourseResult(new CourseResult(
                "Физика", 2, AssessmentType.EXAM, GradeValue.SATISFACTORY
        ));

        assertFalse(gb.canTransferToBudget());
    }

    @Test
    void redDiplomaPossibleWhenEnoughExcellentMarksAndNoThrees() {
        GradeBook gb = new GradeBook("Студент", true, 8);

        gb.addCourseResult(new CourseResult(
                "Математика", 1, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));
        gb.addCourseResult(new CourseResult(
                "Физика", 1, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));
        gb.addCourseResult(new CourseResult(
                "Программирование", 2, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));
        gb.addCourseResult(new CourseResult(
                "Философия", 2, AssessmentType.EXAM, GradeValue.GOOD
        ));

        // ВКР ещё нет, по формуле 75% "5" достижимы
        assertTrue(gb.isRedDiplomaPossible());
    }

    @Test
    void redDiplomaImpossibleIfThereIsSatisfactory() {
        GradeBook gb = new GradeBook("Студент", true, 4);

        gb.addCourseResult(new CourseResult(
                "Математика", 1, AssessmentType.EXAM, GradeValue.SATISFACTORY
        ));

        assertFalse(gb.isRedDiplomaPossible());
    }

    @Test
    void redDiplomaImpossibleIfQualificationWorkIsNotExcellent() {
        GradeBook gb = new GradeBook("Студент", true, 4);

        gb.addCourseResult(new CourseResult(
                "Математика", 1, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));
        gb.setQualificationWorkGrade(GradeValue.GOOD);

        assertFalse(gb.isRedDiplomaPossible());
    }

    @Test
    void redDiplomaImpossibleIfNotEnoughExcellentEvenInBestCase() {
        GradeBook gb = new GradeBook("Студент", true, 4);

        gb.addCourseResult(new CourseResult(
                "Математика", 1, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));
        gb.addCourseResult(new CourseResult(
                "Физика", 1, AssessmentType.EXAM, GradeValue.GOOD
        ));
        gb.addCourseResult(new CourseResult(
                "Программирование", 2, AssessmentType.EXAM, GradeValue.GOOD
        ));
        gb.addCourseResult(new CourseResult(
                "Философия", 2, AssessmentType.EXAM, GradeValue.GOOD
        ));

        // Сейчас 1 "5" из 4, в идеале максимум 1 "5" из 4 — до 75% не дотянуться
        assertFalse(gb.isRedDiplomaPossible());
    }

    @Test
    void increasedScholarshipFalseForEmptyGradeBook() {
        GradeBook gb = new GradeBook("Студент", true, 6);
        assertFalse(gb.canGetIncreasedScholarship());
    }

    @Test
    void increasedScholarshipTrueIfAllControlsInCurrentSemesterAreExcellent() {
        GradeBook gb = new GradeBook("Студент", true, 6);

        // Предыдущий семестр – просто шум
        gb.addCourseResult(new CourseResult(
                "Математика", 1, AssessmentType.EXAM, GradeValue.GOOD
        ));

        // Текущий семестр (2): все экзамены/дифф. зачёты на "5"
        gb.addCourseResult(new CourseResult(
                "Физика", 2, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));
        gb.addCourseResult(new CourseResult(
                "Программирование", 2, AssessmentType.DIFFERENTIATED_CREDIT, GradeValue.EXCELLENT
        ));

        assertTrue(gb.canGetIncreasedScholarship());
    }

    @Test
    void increasedScholarshipFalseIfAnyControlInCurrentSemesterIsNotExcellent() {
        GradeBook gb = new GradeBook("Студент", true, 6);

        gb.addCourseResult(new CourseResult(
                "Математика", 1, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));
        gb.addCourseResult(new CourseResult(
                "Физика", 2, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));
        gb.addCourseResult(new CourseResult(
                "Программирование", 2, AssessmentType.DIFFERENTIATED_CREDIT, GradeValue.GOOD
        ));

        assertFalse(gb.canGetIncreasedScholarship());
    }

    @Test
    void increasedScholarshipFalseIfNoControlsInCurrentSemester() {
        GradeBook gb = new GradeBook("Студент", true, 6);

        // Семестр 1 – обычный экзамен
        gb.addCourseResult(new CourseResult(
                "Математика", 1, AssessmentType.EXAM, GradeValue.EXCELLENT
        ));

        // Семестр 2 – текущий, но тип контроля null, hasAnyControl останется false
        gb.addCourseResult(new CourseResult(
                "Практика", 2, null, GradeValue.EXCELLENT
        ));

        assertFalse(gb.canGetIncreasedScholarship());
    }

    @Test
    void setPaidEducationAndIsPaidEducationWork() {
        GradeBook gb = new GradeBook("Студент", true, 4);
        assertTrue(gb.isPaidEducation());

        gb.setPaidEducation(false);
        assertFalse(gb.isPaidEducation());
    }

    @Test
    void toStringContainsKeyFields() {
        GradeBook gb = new GradeBook("Студент", true, 4);

        String str = gb.toString();
        assertTrue(str.contains("Студент"));
        assertTrue(str.contains("paidEducation=true"));
        assertTrue(str.contains("totalPlannedCourses=4"));
    }
}