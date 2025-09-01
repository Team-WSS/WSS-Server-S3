package org.websoso.s3.exception.validation;

public class InvalidSecretKeyException extends RuntimeException {

  public InvalidSecretKeyException(String message) {
    super(message);
  }

  public InvalidSecretKeyException(String message, Throwable cause) {
    super(message, cause);
  }

}
