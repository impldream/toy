package com.demo.toyRpc.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Request {
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
