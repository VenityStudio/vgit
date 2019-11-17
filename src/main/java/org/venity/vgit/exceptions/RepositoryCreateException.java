package org.venity.vgit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Repository create error!")
public class RepositoryCreateException extends Exception {
    public RepositoryCreateException() {
        super();
    }

    public RepositoryCreateException(String message) {
        super(message);
    }
}
