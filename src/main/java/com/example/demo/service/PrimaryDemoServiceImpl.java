package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service("myservice")
public class PrimaryDemoServiceImpl implements DemoService {

    @Override
    public String call() {
        return "Primary service";
    }
}
