package com.zinkworks.atm.common;

public interface IErrorStatus {
    String DATA_VALIDATION = "VALIDATION_ERROR";
    String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    String DAO_EXCEPTION = "DAO_EXCEPTION";
    String NOT_FOUND = "NOT_FOUND";
    String CONFLICT = "CONFLICT";
}
