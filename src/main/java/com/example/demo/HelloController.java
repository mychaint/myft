package com.example.demo;

import com.example.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final DemoService demoService;

    @Autowired
    public HelloController(
            @Qualifier("myservice") DemoService demoService
    ) {
       this.demoService = demoService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
       return ResponseEntity.ok().body(this.demoService.call());
    }
}
