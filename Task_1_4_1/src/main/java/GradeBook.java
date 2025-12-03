import java.util.ArrayList;
import java.util.List;

public class GradeBook {

    private final String studentName;
    private boolean paidEducation;
    private final List<CourseResult> courseResults = new ArrayList<>();
    private final int totalPlannedCourses;
    private GradeValue qualificationWorkGrade;

    public GradeBook(String studentName,
                     boolean paidEducation,
                     int totalPlannedCourses) {
        this.studentName = studentName;
        this.paidEducation = paidEducation;
        this.totalPlannedCourses = totalPlannedCourses;
    }

    public void addCourseResult(CourseResult result) {
        courseResults.add(result);
    }

    public void setQualificationWorkGrade(GradeValue grade) {
        this.qualificationWorkGrade = grade;
    }

    public void setPaidEducation(boolean paidEducation) {
        this.paidEducation = paidEducation;
    }

    public boolean isPaidEducation() {
        return paidEducation;
    }

    public double calculateGpa() {
        return courseResults.stream()
                .mapToInt(result -> result.getGrade().getNumericValue())
                .average()
                .orElse(0.0);
    }

    public boolean canTransferToBudget() {
        if (!paidEducation) {
            return false;
        }

        if (courseResults.isEmpty()) {
            return false;
        }

        int lastSemester = courseResults.stream()
                .mapToInt(CourseResult::getSemester)
                .max()
                .orElse(0);

        if (lastSemester < 2) {
            return false;
        }

        int previousSemester = lastSemester - 1;

        boolean hasBadExam = courseResults.stream()
                .filter(result -> {
                    int sem = result.getSemester();
                    return sem == lastSemester || sem == previousSemester;
                })
                .filter(result -> result.getAssessmentType() == AssessmentType.EXAM)
                .anyMatch(result -> result.getGrade() == GradeValue.SATISFACTORY);

        return !hasBadExam;
    }

    public boolean isRedDiplomaPossible() {

        boolean hasThree = courseResults.stream()
                .anyMatch(result -> result.getGrade() == GradeValue.SATISFACTORY);

        if (hasThree) {
            return false;
        }

        if (qualificationWorkGrade != null &&
                qualificationWorkGrade != GradeValue.EXCELLENT) {
            return false;
        }

        int completedCourses = courseResults.size();

        long currentExcellent = courseResults.stream()
                .filter(result -> result.getGrade() == GradeValue.EXCELLENT)
                .count();

        int planned = Math.max(totalPlannedCourses, completedCourses);

        int requiredExcellent = (int) Math.ceil(0.75 * planned);

        long maxPossibleExcellent =
                currentExcellent + (planned - completedCourses);

        return maxPossibleExcellent >= requiredExcellent;
    }

    public boolean canGetIncreasedScholarship() {
        if (courseResults.isEmpty()) {
            return false;
        }

        int currentSemester = courseResults.stream()
                .mapToInt(CourseResult::getSemester)
                .max()
                .orElse(0);

        if (currentSemester == 0) {
            return false;
        }

        boolean hasAnyControl = courseResults.stream()
                .filter(result -> result.getSemester() == currentSemester)
                .filter(result ->
                        result.getAssessmentType() == AssessmentType.EXAM ||
                                result.getAssessmentType() == AssessmentType.DIFFERENTIATED_CREDIT)
                .findAny()
                .isPresent();

        if (!hasAnyControl) {
            return false;
        }

        return courseResults.stream()
                .filter(result -> result.getSemester() == currentSemester)
                .filter(result ->
                        result.getAssessmentType() == AssessmentType.EXAM ||
                                result.getAssessmentType() == AssessmentType.DIFFERENTIATED_CREDIT)
                .allMatch(result -> result.getGrade() == GradeValue.EXCELLENT);
    }

    @Override
    public String toString() {
        return "GradeBook{"
                + "studentName='" + studentName + '\''
                + ", paidEducation=" + paidEducation
                + ", totalPlannedCourses=" + totalPlannedCourses
                + ", qualificationWorkGrade=" + qualificationWorkGrade
                + ", courseResults=" + courseResults
                + '}';
    }
}

