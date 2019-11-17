package org.venity.vgit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT, reason = "Leave me alone - I'm a teapot")
public class ImTeapotException extends Exception {
}
