package com.zinkworks.atm.exceptions;

import com.zinkworks.atm.common.IErrorMessages;
import com.zinkworks.atm.common.IErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<ApplicationException> handleDataAccessException(DataAccessException ex) {
        ApplicationException applicationException = new ApplicationException(IErrorStatus.DAO_EXCEPTION, ex.getMessage(), LocalDateTime.now());
        log.error("DAO Exception Occurred :: " +applicationException);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(applicationException);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApplicationException> handleEntityNotFoundException(EntityNotFoundException ex) {
        ApplicationException applicationException = new ApplicationException(IErrorStatus.NOT_FOUND, ex.getMessage(), LocalDateTime.now());
        log.error("DAO Exception Occurred :: " +applicationException);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(applicationException);
    }

   @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    protected ResponseEntity<ApplicationException> handleValidatorConstraintViolationException(javax.validation.ConstraintViolationException e) {
        ApplicationException applicationException = new ApplicationException(IErrorStatus.DATA_VALIDATION, e.getMessage(), LocalDateTime.now());
        log.error("Exception Occurred at Path and Request parameters :: " + applicationException);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(applicationException);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApplicationException> handleGenericException(Exception e) {
        ApplicationException applicationException = new ApplicationException(IErrorStatus.INTERNAL_SERVER_ERROR, IErrorMessages.UN_EXPECTED_ERROR, LocalDateTime.now());
        log.error("UnExcepted Occurred :: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(applicationException);
    }

  @ExceptionHandler(DataValidationException.class)
  protected ResponseEntity<ApplicationException> handleDataValidationException(Exception e) {
    ApplicationException applicationException = new ApplicationException(IErrorStatus.DATA_VALIDATION, e.getMessage(), LocalDateTime.now());
    log.error("Validation Occurred :: " + e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(applicationException);
  }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApplicationException> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        ApplicationException applicationException = null;
        List<FieldError> fieldErrors = result.getFieldErrors();
        fieldErrors = fieldErrors.stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fieldErrors)) {
            FieldError error = fieldErrors.get(0);
            applicationException = new ApplicationException(IErrorStatus.DATA_VALIDATION, error.getDefaultMessage(), LocalDateTime.now());
        }
        log.error("Method Argument Not Valid Exception Occurred :: " +applicationException);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(applicationException);
    }
}
