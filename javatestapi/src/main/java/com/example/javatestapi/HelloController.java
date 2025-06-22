package com.example.javatestapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
// @CrossOrigin(origins = "http://localhost:3000") // Uncomment for local testing with React
public class HelloController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from javatest-api!";
    }
}
