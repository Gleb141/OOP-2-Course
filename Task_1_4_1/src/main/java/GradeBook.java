import java.util.ArrayList;
import java.util.List;


/**
 * Gradebook.
 */

public class GradeBook {

    private final String studentName;
    private boolean paidEducation;
    private final List<CourseResult> courseResults = new ArrayList<>();
    private final int totalPlannedCourses;
    private GradeValue qualificationWorkGrade;

    /**
     * Gradebook methods.
     */

    public GradeBook(String studentName,
                     boolean paidEducation,
                     int totalPlannedCourses) {
        this.studentName = studentName;
        this.paidEducation = paidEducation;
        this.totalPlannedCourses = totalPlannedCourses;
    }

    /**
     * Method that adds course results for a student.
     */

    public void addCourseResult(CourseResult result) {
        courseResults.add(result);
    }

    /**
     * Set qualification work grade.
     */

    public void setQualificationWorkGrade(GradeValue grade) {
        this.qualificationWorkGrade = grade;
    }

    public void setPaidEducation(boolean paidEducation) {
        this.paidEducation = paidEducation;
    }

    public boolean isPaidEducation() {
        return paidEducation;
    }

    /**
     * Calculates average mark.
     */

    public double calculateGpa() {
        if (courseResults.isEmpty()) {
            return 0.0;
        }

        int sum = 0;
        for (CourseResult result : courseResults) {
            sum += result.getGrade().getNumericValue();
        }

        return (double) sum / courseResults.size();
    }

    /**
     * determines if a student can study on state budget.
     */

    public boolean canTransferToBudget() {
        if (!paidEducation) {
            return false;
        }

        if (courseResults.isEmpty()) {
            return false;
        }

        int lastSemester = 0;
        for (CourseResult result : courseResults) {
            if (result.getSemester() > lastSemester) {
                lastSemester = result.getSemester();
            }
        }
        int previousSemester = lastSemester - 1;

        for (CourseResult result : courseResults) {
            int sem = result.getSemester();

            boolean isLastTwo = sem == lastSemester
                    || (previousSemester > 0
                    && sem == previousSemester);

            if (!isLastTwo) {
                continue;
            }

            if (result.getAssessmentType() == AssessmentType.EXAM
                    && result.getGrade() == GradeValue.SATISFACTORY) {
                return false;
            }
        }

        return true;
    }

    /**
     * Can student get a red diploma.
     */

    public boolean isRedDiplomaPossible() {
        for (CourseResult result : courseResults) {
            if (result.getGrade() == GradeValue.SATISFACTORY) {
                return false;
            }
        }

        if (qualificationWorkGrade != null
                && qualificationWorkGrade != GradeValue.EXCELLENT) {
            return false;
        }

        int completedCourses = courseResults.size();
        int currentExcellent = 0;

        for (CourseResult result : courseResults) {
            if (result.getGrade() == GradeValue.EXCELLENT) {
                currentExcellent++;
            }
        }

        int planned = Math.max(totalPlannedCourses, completedCourses);

        int requiredExcellent =
                (int) Math.ceil(0.75 * planned);

        int maxPossibleExcellent =
                currentExcellent + (planned - completedCourses);

        return maxPossibleExcellent >= requiredExcellent;
    }

    /**
     * Can a student get an increase in scholarship.
     */

    public boolean canGetIncreasedScholarship() {
        if (courseResults.isEmpty()) {
            return false;
        }


        int currentSemester = 0;
        for (CourseResult result : courseResults) {
            currentSemester = Math.max(currentSemester, result.getSemester());
        }

        boolean hasAnyControl = false;

        for (CourseResult result : courseResults) {
            if (result.getSemester() != currentSemester) {
                continue;
            }

            if (result.getAssessmentType() == AssessmentType.EXAM
                    || result.getAssessmentType() == AssessmentType.DIFFERENTIATED_CREDIT) {

                hasAnyControl = true;

                if (result.getGrade() != GradeValue.EXCELLENT) {
                    return false;
                }
            }
        }

        if (!hasAnyControl) {
            return false;
        }

        return true;
    }

    /**
     * String conversion and output.
     */

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

