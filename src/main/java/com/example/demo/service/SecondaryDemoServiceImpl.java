package com.example.demo.service;

import com.example.tolerance.Fallback;
import org.springframework.stereotype.Service;

@Service
@Fallback(serviceQualifier = "myservice")
public class SecondaryDemoServiceImpl implements DemoService {

    @Override
    public String call() {
        return "Secondary service";
    }
}
