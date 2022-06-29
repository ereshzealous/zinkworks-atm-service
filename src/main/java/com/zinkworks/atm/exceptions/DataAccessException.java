package com.zinkworks.atm.exceptions;

public class DataAccessException extends RuntimeException {
    private String code;

    public DataAccessException(String message, String code) {
        super(message);
        this.code = code;
    }
}
