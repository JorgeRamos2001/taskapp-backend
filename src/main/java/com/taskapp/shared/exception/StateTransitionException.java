package com.taskapp.shared.exception;

public class StateTransitionException extends RuntimeException {
    public StateTransitionException(String message) {
        super(message);
    }
}
