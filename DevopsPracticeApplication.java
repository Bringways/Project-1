package com.example.devopspractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the entry point of the entire application.
 *
 * @SpringBootApplication is actually three annotations combined:
 *   1. @Configuration      - this class can define Spring beans
 *   2. @EnableAutoConfiguration - Spring Boot auto-configures things like the
 *                            embedded web server and the database connection
 *                            based on what's on the classpath (from pom.xml)
 *   3. @ComponentScan      - Spring will scan this package and all sub-packages
 *                            (controller, service, repository, model) for
 *                            classes annotated with @RestController, @Service,
 *                            @Repository, etc., and wire them together.
 *
 * When you run "java -jar app.jar", the main() method below is what actually
 * executes inside the container.
 */
@SpringBootApplication
public class DevopsPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevopsPracticeApplication.class, args);
    }

}
