package com.example.tolerance;

import org.springframework.stereotype.Component;

@Component
public class ServiceContext {
    public static ServiceStatus serviceStatus = ServiceStatus.NORMAL;
}
