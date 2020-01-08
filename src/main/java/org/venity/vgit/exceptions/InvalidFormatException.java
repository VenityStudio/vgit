package org.venity.vgit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid Format")
public class InvalidFormatException extends Exception {
    public InvalidFormatException() {
    }

    public InvalidFormatException(String message) {
        super(message);
    }
}
