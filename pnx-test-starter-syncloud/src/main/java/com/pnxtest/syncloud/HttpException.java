package com.pnxtest.syncloud;

public class HttpException extends Exception{
    private static final long serialVersionUID = 1558844946828754128L;
    private final int statusCode;

    public HttpException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public boolean isClientError() {
        return 400 <= this.statusCode && this.statusCode < 500;
    }
}
