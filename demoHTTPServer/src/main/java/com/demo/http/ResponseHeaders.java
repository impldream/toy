package com.demo.http;

public class ResponseHeaders extends Headers {

    private int code;

    private String desc;

    private String contentType;

    private int contentLength;

    private String server;

    public ResponseHeaders(int code) {
        this.code = code;
        this.server = "demo-xxc-http";
        this.desc = StatusCode.queryDesc(code);
        setVersion("HTTP/1.1");
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String phrase) {
        this.desc = phrase;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %d %s\r\n", getVersion(), code, desc));
        sb.append(String.format("ContentType: %s\r\n", contentType));
        sb.append(String.format("ContentLength: %d\r\n", contentLength));
        sb.append(String.format("Server: %s\r\n", server));
        sb.append("\r\n");
        return sb.toString();
    }
}