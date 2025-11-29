public class Main {

    public static void main(String[] args) {
        GradeBook gradeBook = new GradeBook(
                "Иванов Иван Иванович",
                true,
                40
        );

        gradeBook.addCourseResult(new CourseResult(
                "Математика",
                1,
                AssessmentType.EXAM,
                GradeValue.EXCELLENT
        ));

        gradeBook.addCourseResult(new CourseResult(
                "Информатика",
                1,
                AssessmentType.DIFFERENTIATED_CREDIT,
                GradeValue.GOOD
        ));

        gradeBook.addCourseResult(new CourseResult(
                "Физика",
                2,
                AssessmentType.EXAM,
                GradeValue.GOOD
        ));

        gradeBook.addCourseResult(new CourseResult(
                "История",
                2,
                AssessmentType.EXAM,
                GradeValue.EXCELLENT
        ));

        System.out.printf("Средний балл: %.2f%n", gradeBook.calculateGPA());
        System.out.println("Можно перевестись на бюджет? "
                + gradeBook.canTransferToBudget());
        System.out.println("Красный диплом ещё возможен? "
                + gradeBook.isRedDiplomaPossible());
        System.out.println("Повышенная стипендия возможна? "
                + gradeBook.canGetIncreasedScholarship());

        gradeBook.setQualificationWorkGrade(GradeValue.EXCELLENT);
        System.out.println("После ВКР на 5 красный диплом возможен? "
                + gradeBook.isRedDiplomaPossible());
    }
}
