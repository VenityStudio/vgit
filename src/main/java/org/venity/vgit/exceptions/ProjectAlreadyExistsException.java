package org.venity.vgit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Project Already Exists")
public class ProjectAlreadyExistsException extends Exception {
}
