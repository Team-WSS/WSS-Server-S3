package org.websoso.s3.exception.validation;

public class InvalidBucketNameException extends RuntimeException {

  public InvalidBucketNameException(String message) {
    super(message);
  }

  public InvalidBucketNameException(String message, Throwable cause) {
    super(message, cause);
  }

}
