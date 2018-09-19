package com.xiao.aidlexample;

public class ServiceBoundFailureException extends RuntimeException {
    public ServiceBoundFailureException(String message) {
        super(message);
    }
}
