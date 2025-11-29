/**
 * Tests for Task 141
 */

public class GradeBookTest {

    /**
     * main demo
     */

    public static void main(String[] args) {
        testTransferToBudget();
        testRedDiplomaPossible();
        testIncreasedScholarship();
    }

    /**
     * transfer to budget tests
     */

    private static void testTransferToBudget() {
        GradeBook gb = new GradeBook("Студент Платный", true, 10);

        gb.addCourseResult(new CourseResult(
                "Предмет 1",
                1,
                AssessmentType.EXAM,
                GradeValue.EXCELLENT
        ));

        gb.addCourseResult(new CourseResult(
                "Предмет 2",
                2,
                AssessmentType.EXAM,
                GradeValue.SATISFACTORY
        ));

        boolean canTransfer = gb.canTransferToBudget();
        System.out.println("[testTransferToBudget] Ожидаем false, получили: "
                + canTransfer);
    }

    /**
     * red diploma tests
     */

    private static void testRedDiplomaPossible() {
        GradeBook gb = new GradeBook("Отличник", true, 8);

        gb.addCourseResult(new CourseResult(
                "Математика",
                1,
                AssessmentType.EXAM,
                GradeValue.EXCELLENT
        ));

        gb.addCourseResult(new CourseResult(
                "Физика",
                1,
                AssessmentType.EXAM,
                GradeValue.GOOD
        ));

        gb.addCourseResult(new CourseResult(
                "Программирование",
                2,
                AssessmentType.EXAM,
                GradeValue.EXCELLENT
        ));

        boolean possible = gb.isRedDiplomaPossible();
        System.out.println("[testRedDiplomaPossible] Ожидаем true, получили: "
                + possible);

        gb.addCourseResult(new CourseResult(
                "Философия",
                2,
                AssessmentType.EXAM,
                GradeValue.SATISFACTORY
        ));

        possible = gb.isRedDiplomaPossible();
        System.out.println("[testRedDiplomaPossible] После '3' ожидаем false, получили: "
                + possible);
    }

    /**
     * tests for increased scholarship
     */

    private static void testIncreasedScholarship() {
        GradeBook gb = new GradeBook("Стипендиат", false, 6);

        gb.addCourseResult(new CourseResult(
                "Предмет 1",
                1,
                AssessmentType.EXAM,
                GradeValue.GOOD
        ));

        gb.addCourseResult(new CourseResult(
                "Предмет 2",
                2,
                AssessmentType.EXAM,
                GradeValue.EXCELLENT
        ));

        gb.addCourseResult(new CourseResult(
                "Предмет 3",
                2,
                AssessmentType.DIFFERENTIATED_CREDIT,
                GradeValue.EXCELLENT
        ));

        boolean scholarship = gb.canGetIncreasedScholarship();
        System.out.println("[testIncreasedScholarship] Ожидаем true, получили: "
                + scholarship);

        gb.addCourseResult(new CourseResult(
                "Предмет 4",
                2,
                AssessmentType.EXAM,
                GradeValue.GOOD
        ));

        scholarship = gb.canGetIncreasedScholarship();
        System.out.println("[testIncreasedScholarship] После '4' ожидаем false, получили: "
                + scholarship);
    }
}
