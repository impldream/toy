package com.demo.toyRpc.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response {
    private String requestId;
    private Object result;
    private Throwable error;

    public boolean isError() {
        return error != null;
    }
}
