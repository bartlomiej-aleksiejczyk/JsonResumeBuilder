package com.example.jsonresumebuilderspring.cvbuildscheduler.exceptions;

public class CelerySerializationException extends Exception {
    public CelerySerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}