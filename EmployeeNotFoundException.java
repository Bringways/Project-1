package com.example.devopspractice.exception;

/**
 * A small custom exception, thrown by the service layer whenever someone
 * asks for an employee id that doesn't exist. We catch this in
 * GlobalExceptionHandler and turn it into a clean 404 response instead of
 * letting a generic stack trace leak out to the API caller.
 */
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
    }
}
