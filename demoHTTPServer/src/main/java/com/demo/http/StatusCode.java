package com.demo.http;

public enum StatusCode {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private int code;

    private String desc;

    StatusCode(int code, String phrase) {
        this.code = code;
        this.desc = phrase;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String queryDesc(int code) {
        for (StatusCode statusCode : StatusCode.values()) {
            if (statusCode.getCode() == code) {
                return statusCode.getDesc();
            }
        }

        return null;
    }
}
