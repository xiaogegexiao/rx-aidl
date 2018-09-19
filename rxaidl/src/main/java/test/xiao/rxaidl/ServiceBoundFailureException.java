package test.xiao.rxaidl;

public class ServiceBoundFailureException extends RuntimeException {
    public ServiceBoundFailureException(String message) {
        super(message);
    }
}
