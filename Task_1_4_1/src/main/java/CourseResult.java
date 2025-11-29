public class CourseResult {

    private final String courseName;
    private final int semester;
    private final AssessmentType assessmentType;
    private final GradeValue grade;

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

    public int getSemester() {
        return semester;
    }

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public GradeValue getGrade() {
        return grade;
    }

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

