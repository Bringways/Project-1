package com.example.devopspractice.controller;

import com.example.devopspractice.model.Employee;
import com.example.devopspractice.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The controller is the "front door" of the API - it maps HTTP requests
 * (verb + URL) to Java method calls, and has no business logic of its own.
 * Its only job is:
 *   1. Receive the HTTP request
 *   2. Hand off to the service layer
 *   3. Wrap the result in the right HTTP status code
 *
 * @RestController = @Controller + @ResponseBody, meaning every method's
 * return value is automatically converted to JSON (via the Jackson library,
 * which spring-boot-starter-web pulls in) and written to the response body.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // GET /api/employees -> 200 OK with a JSON array
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // GET /api/employees/{id} -> 200 OK, or 404 (handled by GlobalExceptionHandler)
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // POST /api/employees -> 201 Created
    // @Valid triggers the Bean Validation annotations on Employee (@NotBlank etc.)
    // before this method body even runs. If validation fails, Spring throws
    // MethodArgumentNotValidException, which GlobalExceptionHandler turns into 400.
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        Employee saved = employeeService.createEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/employees/{id} -> 200 OK, or 404 if the id doesn't exist
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id,
                                                    @Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    // DELETE /api/employees/{id} -> 204 No Content, or 404 if the id doesn't exist
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
