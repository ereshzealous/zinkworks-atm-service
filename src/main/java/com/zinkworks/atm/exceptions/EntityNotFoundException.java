package com.zinkworks.atm.exceptions;

public class EntityNotFoundException extends RuntimeException {
    private String code;

    public EntityNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }
}
