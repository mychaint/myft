package com.example.tolerance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ft")
public class ServiceDegreeController {

    @GetMapping("/status")
    public ResponseEntity<Object> setServiceStatus() {
        try {
            ServiceContext.serviceStatus = ServiceStatus.DEGREE;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid status parameters.");
        }
        return ResponseEntity.ok().body("updated");
    }
}
