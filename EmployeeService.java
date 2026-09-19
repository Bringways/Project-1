package com.example.devopspractice.service;

import com.example.devopspractice.exception.EmployeeNotFoundException;
import com.example.devopspractice.model.Employee;
import com.example.devopspractice.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The "service" layer holds business logic and sits between the controller
 * (which handles HTTP) and the repository (which handles the database).
 *
 * Why not just call the repository directly from the controller? Because
 * as an app grows, you'll want logic here that is NOT just "save to DB" -
 * e.g. checking business rules, calling other services, sending
 * notifications. Keeping that logic out of the controller keeps each layer
 * focused on one job. For this practice app the logic is intentionally
 * simple, but the separation is what a real production app looks like.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Constructor injection: Spring sees this constructor and automatically
    // supplies an EmployeeRepository bean. Constructor injection (rather
    // than @Autowired on a field) is the recommended style because it makes
    // this class easy to unit test - you can just call
    // "new EmployeeService(mockRepository)" without needing Spring at all.
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        Employee existing = getEmployeeById(id); // throws 404 if missing
        existing.setName(updatedEmployee.getName());
        existing.setEmail(updatedEmployee.getEmail());
        existing.setDepartment(updatedEmployee.getDepartment());
        existing.setSalary(updatedEmployee.getSalary());
        return employeeRepository.save(existing);
    }

    public void deleteEmployee(Long id) {
        Employee existing = getEmployeeById(id); // throws 404 if missing
        employeeRepository.delete(existing);
    }
}
