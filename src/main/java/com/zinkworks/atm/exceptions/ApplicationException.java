package com.zinkworks.atm.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@ToString
public class ApplicationException {
    private String code;
    private String message;
    private LocalDateTime timestamp;
}
