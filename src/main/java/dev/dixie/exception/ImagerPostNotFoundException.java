package dev.dixie.exception;

public class ImagerPostNotFoundException extends RuntimeException {
    public ImagerPostNotFoundException() {
        super("Sorry, the requested post(s) is not found");
    }
}
