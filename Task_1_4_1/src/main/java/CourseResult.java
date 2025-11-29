/**
 * Course result variables.
 */

public class CourseResult {

    private final String courseName;
    private final int semester;
    private final AssessmentType assessmentType;
    private final GradeValue grade;

    /**
     * gets the course results
     */

    public CourseResult(String courseName,
                        int semester,
                        AssessmentType assessmentType,
                        GradeValue grade) {
        this.courseName = courseName;
        this.semester = semester;
        this.assessmentType = assessmentType;
        this.grade = grade;
    }

    public String getCourseName() {
        return courseName;
    }

    /**
     * gets the semester
     */

    public int getSemester() {
        return semester;
    }

    /**
     * returns the assessment type
     */

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    /**
     * gets the grade
     */

    public GradeValue getGrade() {
        return grade;
    }

    /**
     * convert and output as a string
     */

    @Override
    public String toString() {
        return "CourseResult{"
                + "courseName='" + courseName + '\''
                + ", semester=" + semester
                + ", assessmentType=" + assessmentType
                + ", grade=" + grade
                + '}';
    }
}

