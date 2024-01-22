package id.co.workers.Litera.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@RestController
public class GlobalExceptionController {

  private final Logger log = LogManager.getLogger(
    this.getClass().getSimpleName()
  );

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(value = RuntimeException.class)
  public ResponseEntity<Error> handleBaseException(RuntimeException e) {
    log.error("Error", e);
    Error error = new Error("Bad Request");
    return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(value = NoHandlerFoundException.class)
  public ResponseEntity<Error> handleNoHandlerFoundException(
    NoHandlerFoundException e
  ) {
    log.error("Error", e);
    Error error = new Error("Not Found");
    return new ResponseEntity<Error>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = AuthenticationCredentialsNotFoundException.class)
  public ResponseEntity<Error> handleAuthenticationCredentialsNotFoundException(
    AuthenticationCredentialsNotFoundException e
  ) {
    log.error("Error", e);
    Error error = new Error("Unauthorized");
    return new ResponseEntity<Error>(error, HttpStatus.UNAUTHORIZED);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(value = Unauthorized.class)
  public ResponseEntity<Error> handleUnauthorized(Unauthorized e) {
    log.error("Error", e);
    Error error = new Error("Unauthorized");
    return new ResponseEntity<Error>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<Error> handleException(Exception e) {
    log.error("Error", e);
    Error error = new Error("Internal Server Error");
    return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
