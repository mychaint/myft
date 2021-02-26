package com.example.tolerance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ToleranceBeanInvocationHandler implements InvocationHandler {

    private final Object primary;
    private final Object secondary;

    public ToleranceBeanInvocationHandler(Object primary, Object secondary) {
       this.primary = primary;
       this.secondary = secondary;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (ServiceContext.serviceStatus) {
            case NORMAL:
                return method.invoke(this.primary, args);
            case DEGREE:
                return method.invoke(this.secondary, args);
            case DOWN:
                throw new ServiceNotAvailableException();
            default:
                return null;
        }
    }
}
