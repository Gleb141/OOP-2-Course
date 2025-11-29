import java.util.ArrayList;
import java.util.List;

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

enum AssessmentType {
    EXAM,
    DIFFERENTIATED_CREDIT
}

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

    public double calculateGPA() {
        if (courseResults.isEmpty()) {
            return 0.0;
        }

        int sum = 0;
        for (CourseResult result : courseResults) {
            sum += result.getGrade().getNumericValue();
        }

        return (double) sum / courseResults.size();
    }

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

            boolean isLastTwo =
                    sem == lastSemester ||
                            (previousSemester > 0 && sem == previousSemester);

            if (!isLastTwo) {
                continue;
            }

            if (result.getAssessmentType() == AssessmentType.EXAM &&
                    result.getGrade() == GradeValue.SATISFACTORY) {
                return false;
            }
        }

        return true;
    }

    public boolean isRedDiplomaPossible() {
        for (CourseResult result : courseResults) {
            if (result.getGrade() == GradeValue.SATISFACTORY) {
                return false;
            }
        }

        if (qualificationWorkGrade != null &&
                qualificationWorkGrade != GradeValue.EXCELLENT) {
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

            if (result.getAssessmentType() == AssessmentType.EXAM ||
                    result.getAssessmentType() == AssessmentType.DIFFERENTIATED_CREDIT) {

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

    @Override
    public String toString() {
        return "GradeBook{" +
                "studentName='" + studentName + '\'' +
                ", paidEducation=" + paidEducation +
                ", totalPlannedCourses=" + totalPlannedCourses +
                ", qualificationWorkGrade=" + qualificationWorkGrade +
                ", courseResults=" + courseResults +
                '}';
    }
}

