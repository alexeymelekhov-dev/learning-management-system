package com.alexeymelekhov.lms.exception;

public enum ErrorMessage {

    STUDENT_NOT_FOUND("Student not found with id: %s"),
    STUDENTS_NOT_FOUND("One or more students not found"),
    COURSE_NOT_FOUND("Course not found with id: %s"),
    TEACHER_NOT_FOUND("Teacher not found with id: %s"),
    SCHEDULE_NOT_FOUND("Schedule not found with id: %s"),
    GROUPS_NOT_FOUND("One or more groups not found");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return message.formatted(args);
    }

    public String getMessage() {
        return message;
    }
}
