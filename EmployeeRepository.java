package com.example.devopspractice.repository;

import com.example.devopspractice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This is the "repository" layer - the piece that talks to the database.
 *
 * Notice there is NO implementation here, just an interface. That's the
 * magic of Spring Data JPA: at startup, Spring generates a real
 * implementation of this interface behind the scenes, giving us methods
 * like save(), findById(), findAll(), and deleteById() for free, just
 * because we extended JpaRepository<Employee, Long>
 *   (Employee = the entity type, Long = the type of its @Id field).
 *
 * If we ever needed a custom query, we could add a method signature here,
 * e.g. List<Employee> findByDepartment(String department), and Spring Data
 * JPA would generate the SQL from the method name alone.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
