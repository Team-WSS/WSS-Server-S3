package org.websoso.s3.exception;

public class InvalidBucketNameException extends RuntimeException {

  public InvalidBucketNameException(String message) {
    super(message);
  }

  public InvalidBucketNameException(String message, Throwable cause) {
    super(message, cause);
  }

}
