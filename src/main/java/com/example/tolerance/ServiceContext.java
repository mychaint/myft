package com.example.tolerance;

import org.springframework.stereotype.Component;

@Component
public class ServiceContext {
    public volatile static ServiceStatus serviceStatus = ServiceStatus.NORMAL;
}
