package org.websoso.s3.exception.aws.s3;

/**
 * Amazon S3 Error Codes
 * 본 enum 의 errorCode 와 description 은 Amazon S3 API Reference 에 정의된 내용을 발췌하였습니다.
 */
public enum S3AwsErrorCodes {
    ACCESS_CONTROL_LIST_NOT_SUPPORTED("AccessControlListNotSupported", "The bucket does not allow ACLs."),
    ACCESS_DENIED("AccessDenied", "Access Denied"),
    ACCESS_POINT_ALREADY_OWNED_BY_YOU("AccessPointAlreadyOwnedByYou", "An access point with an identical name already exists in your account."),
    ACCOUNT_PROBLEM("AccountProblem", "There is a problem with your AWS account that prevents the operation."),
    ALL_ACCESS_DISABLED("AllAccessDisabled", "All access to this Amazon S3 resource has been disabled."),
    AMBIGUOUS_GRANT_BY_EMAIL_ADDRESS("AmbiguousGrantByEmailAddress", "The email address that you provided is associated with more than one account."),
    AUTHORIZATION_HEADER_MALFORMED("AuthorizationHeaderMalformed", "The authorization header that you provided is not valid."),
    AUTHORIZATION_QUERY_PARAMETERS_ERROR("AuthorizationQueryParametersError", "The authorization query parameters that you provided are not valid."),
    BAD_DIGEST("BadDigest", "The Content-MD5 or checksum value did not match."),
    BUCKET_ALREADY_EXISTS("BucketAlreadyExists", "The requested bucket name is not available."),
    BUCKET_ALREADY_OWNED_BY_YOU("BucketAlreadyOwnedByYou", "The bucket you tried to create already exists, and you own it."),
    BUCKET_HAS_ACCESS_POINTS_ATTACHED("BucketHasAccessPointsAttached", "The bucket you tried to delete has access points attached."),
    BUCKET_NOT_EMPTY("BucketNotEmpty", "The bucket that you tried to delete is not empty."),
    CLIENT_TOKEN_CONFLICT("ClientTokenConflict", "Your Multi-Region Access Point idempotency token was already used for a different request."),
    CONNECTION_CLOSED_BY_REQUESTER("ConnectionClosedByRequester", "Error while reading the WriteGetObjectResponse body."),
    CONDITIONAL_REQUEST_CONFLICT("ConditionalRequestConflict", "A conflicting operation occurred."),
    CREDENTIALS_NOT_SUPPORTED("CredentialsNotSupported", "This request does not support credentials."),
    CROSS_LOCATION_LOGGING_PROHIBITED("CrossLocationLoggingProhibited", "Cross-Region logging is not allowed."),
    DEVICE_NOT_ACTIVE_ERROR("DeviceNotActiveError", "The device is not currently active."),
    ENDPOINT_NOT_FOUND("EndpointNotFound", "Direct requests to the correct endpoint."),
    ENTITY_TOO_SMALL("EntityTooSmall", "Your proposed upload is smaller than the minimum allowed object size."),
    ENTITY_TOO_LARGE("EntityTooLarge", "Your proposed upload exceeds the maximum allowed object size."),
    EXPIRED_TOKEN("ExpiredToken", "The provided token has expired."),
    ILLEGAL_LOCATION_CONSTRAINT_EXCEPTION("IllegalLocationConstraintException", "Region mismatch in request."),
    ILLEGAL_VERSIONING_CONFIGURATION_EXCEPTION("IllegalVersioningConfigurationException", "The versioning configuration specified in the request is not valid."),
    INCOMPLETE_BODY("IncompleteBody", "You did not provide the number of bytes specified by the Content-Length header."),
    INCORRECT_ENDPOINT("IncorrectEndpoint", "The specified bucket exists in another Region."),
    INCORRECT_NUMBER_OF_FILES_IN_POST_REQUEST("IncorrectNumberOfFilesInPostRequest", "POST requires exactly one file upload per request."),
    INLINE_DATA_TOO_LARGE("InlineDataTooLarge", "The inline data exceeds the maximum allowed size."),
    INTERNAL_ERROR("InternalError", "An internal error occurred. Try again."),
    INVALID_ACCESS_KEY_ID("InvalidAccessKeyId", "The AWS access key ID that you provided does not exist."),
    INVALID_ACCESS_POINT("InvalidAccessPoint", "The specified access point name or account is not valid."),
    INVALID_ACCESS_POINT_ALIAS_ERROR("InvalidAccessPointAliasError", "The specified access point alias name is not valid."),
    INVALID_ADDRESSING_HEADER("InvalidAddressingHeader", "You must specify the Anonymous role."),
    INVALID_ARGUMENT("InvalidArgument", "The specified argument was not valid or missing."),
    INVALID_BUCKET_ACL_WITH_OBJECT_OWNERSHIP("InvalidBucketAclWithObjectOwnership", "Bucket cannot have ACLs with ObjectOwnership's BucketOwnerEnforced setting."),
    INVALID_BUCKET_NAME("InvalidBucketName", "The specified bucket is not valid."),
    INVALID_BUCKET_OWNER_AWS_ACCOUNT_ID("InvalidBucketOwnerAWSAccountID", "Expected bucket owner must be an AWS account ID."),
    INVALID_BUCKET_STATE("InvalidBucketState", "The request is not valid for the current state of the bucket."),
    INVALID_DIGEST("InvalidDigest", "The Content-MD5 or checksum value is not valid."),
    INVALID_ENCRYPTION_ALGORITHM_ERROR("InvalidEncryptionAlgorithmError", "The encryption request is not valid (only AES256 supported)."),
    INVALID_HOST_HEADER("InvalidHostHeader", "The host headers used the incorrect style addressing."),
    INVALID_HTTP_METHOD("InvalidHttpMethod", "Unexpected HTTP method used."),
    INVALID_LOCATION_CONSTRAINT("InvalidLocationConstraint", "The specified Region constraint is not valid."),
    INVALID_OBJECT_STATE("InvalidObjectState", "The operation is not valid for the current state of the object."),
    INVALID_PART("InvalidPart", "One or more parts could not be found or ETag mismatch."),
    INVALID_PART_ORDER("InvalidPartOrder", "The list of parts was not in ascending order."),
    INVALID_PAYER("InvalidPayer", "All access to this object has been disabled."),
    INVALID_POLICY_DOCUMENT("InvalidPolicyDocument", "The content of the form does not meet the conditions specified in the policy document."),
    INVALID_RANGE("InvalidRange", "The requested range cannot be satisfied."),
    INVALID_REGION("InvalidRegion", "You've attempted to create a Multi-Region Access Point in a Region that you haven't opted in to."),
    INVALID_REQUEST("InvalidRequest", "The request is invalid for multiple possible reasons."),
    INVALID_SESSION_EXCEPTION("InvalidSessionException", "Returned if the session doesn't exist anymore because it timed out or expired."),
    INVALID_SIGNATURE("InvalidSignature", "The request signature does not match."),
    INVALID_SECURITY("InvalidSecurity", "The provided security credentials are not valid."),
    INVALID_SOAP_REQUEST("InvalidSOAPRequest", "The SOAP request body is not valid."),
    INVALID_STORAGE_CLASS("InvalidStorageClass", "The storage class specified is not valid."),
    INVALID_TARGET_BUCKET_FOR_LOGGING("InvalidTargetBucketForLogging", "The target bucket for logging does not exist or is not owned by you."),
    INVALID_TOKEN("InvalidToken", "The provided token is malformed or not valid."),
    INVALID_URI("InvalidURI", "The specified URI could not be parsed."),
    KEY_TOO_LONG_ERROR("KeyTooLongError", "Your key is too long."),
    KMS_DISABLED_EXCEPTION("KMS.DisabledException", "The specified KMS key is not enabled."),
    KMS_INVALID_KEY_USAGE_EXCEPTION("KMS.InvalidKeyUsageException", "The request was rejected due to incompatible KeyUsage."),
    KMS_INVALID_STATE_EXCEPTION("KMS.KMSInvalidStateException", "The state of the KMS key is not valid for this request."),
    KMS_NOT_FOUND_EXCEPTION("KMS.NotFoundException", "The specified entity or resource could not be found."),
    MALFORMED_ACL_ERROR("MalformedACLError", "The ACL provided was not well formed."),
    MALFORMED_POST_REQUEST("MalformedPOSTRequest", "The body of your POST request is not well-formed multipart/form-data."),
    MALFORMED_XML("MalformedXML", "The XML you provided was not well formed."),
    MAX_MESSAGE_LENGTH_EXCEEDED("MaxMessageLengthExceeded", "Your request was too large."),
    MAX_POST_PRE_DATA_LENGTH_EXCEEDED_ERROR("MaxPostPreDataLengthExceededError", "Your POST request fields preceding the upload file were too large."),
    METADATA_TOO_LARGE("MetadataTooLarge", "Your metadata headers exceed the maximum allowed metadata size."),
    METHOD_NOT_ALLOWED("MethodNotAllowed", "The specified method is not allowed against this resource."),
    MISSING_ATTACHMENT("MissingAttachment", "A SOAP attachment was expected, but none was found."),
    MISSING_AUTHENTICATION_TOKEN("MissingAuthenticationToken", "The request was not signed."),
    MISSING_CONTENT_LENGTH("MissingContentLength", "You must provide the Content-Length HTTP header."),
    MISSING_REQUEST_BODY_ERROR("MissingRequestBodyError", "You sent an empty XML document as a request."),
    MISSING_SECURITY_ELEMENT("MissingSecurityElement", "The SOAP 1.1 request is missing a security element."),
    MISSING_SECURITY_HEADER("MissingSecurityHeader", "Your request is missing a required header."),
    NO_LOGGING_STATUS_FOR_KEY("NoLoggingStatusForKey", "There is no such thing as a logging status subresource for a key."),
    NO_SUCH_ASYNC_REQUEST("NoSuchAsyncRequest", "The specified request was not found."),
    NO_SUCH_BUCKET("NoSuchBucket", "The specified bucket does not exist."),
    NO_SUCH_BUCKET_POLICY("NoSuchBucketPolicy", "The specified bucket does not have a bucket policy."),
    NO_SUCH_CORS_CONFIGURATION("NoSuchCORSConfiguration", "The specified bucket does not have a CORS configuration."),
    NO_SUCH_KEY("NoSuchKey", "The specified key does not exist."),
    NO_SUCH_LIFECYCLE_CONFIGURATION("NoSuchLifecycleConfiguration", "The specified lifecycle configuration does not exist."),
    NO_SUCH_MULTI_REGION_ACCESS_POINT("NoSuchMultiRegionAccessPoint", "The specified Multi-Region Access Point does not exist."),
    NO_SUCH_OBJECT_LOCK_CONFIGURATION("NoSuchObjectLockConfiguration", "The specified object does not have an ObjectLock configuration."),
    NO_SUCH_WEBSITE_CONFIGURATION("NoSuchWebsiteConfiguration", "The specified bucket does not have a website configuration."),
    NO_SUCH_TAG_SET("NoSuchTagSet", "The specified tag does not exist."),
    NO_SUCH_UPLOAD("NoSuchUpload", "The specified multipart upload does not exist."),
    NO_SUCH_VERSION("NoSuchVersion", "The version ID specified in the request does not match."),
    NOT_DEVICE_OWNER_ERROR("NotDeviceOwnerError", "The device that generated the token is not owned by the authenticated user."),
    NOT_IMPLEMENTED("NotImplemented", "A header implies functionality not implemented."),
    NOT_MODIFIED("NotModified", "The resource was not changed."),
    NO_TRANSFORMATION_DEFINED("NoTransformationDefined", "No transformation found for this Object Lambda Access Point."),
    NOT_SIGNED_UP("NotSignedUp", "Your account is not signed up for the Amazon S3 service."),
    OBJECT_LOCK_CONFIGURATION_NOT_FOUND_ERROR("ObjectLockConfigurationNotFoundError", "The Object Lock configuration does not exist for this bucket."),
    OWNERSHIP_CONTROLS_NOT_FOUND_ERROR("OwnershipControlsNotFoundError", "The bucket ownership controls were not found."),
    OPERATION_ABORTED("OperationAborted", "A conflicting conditional operation is currently in progress."),
    PERMANENT_REDIRECT("PermanentRedirect", "The bucket you are attempting to access must be addressed using the specified endpoint."),
    PERMANENT_REDIRECT_CONTROL_ERROR("PermanentRedirectControlError", "The API operation must be addressed using the specified endpoint."),
    PRECONDITION_FAILED("PreconditionFailed", "At least one of the preconditions that you specified did not hold."),
    REDIRECT("Redirect", "Temporary redirect while DNS server is updated."),
    REQUEST_HEADER_SECTION_TOO_LARGE("RequestHeaderSectionTooLarge", "The request header and query parameters exceed the maximum allowed size."),
    REQUEST_IS_NOT_MULTIPART_CONTENT("RequestIsNotMultiPartContent", "A bucket POST request must be of type multipart/form-data."),
    REQUEST_TIMEOUT("RequestTimeout", "Socket connection timed out."),
    REQUEST_TIME_TOO_SKEWED("RequestTimeTooSkewed", "The difference between the request time and the server's time is too large."),
    REQUEST_TORRENT_OF_BUCKET_ERROR("RequestTorrentOfBucketError", "Requesting the torrent file of a bucket is not permitted."),
    RESPONSE_INTERRUPTED("ResponseInterrupted", "Error while reading the WriteGetObjectResponse body."),
    RESTORE_ALREADY_IN_PROGRESS("RestoreAlreadyInProgress", "The object restore is already in progress."),
    SERVER_SIDE_ENCRYPTION_CONFIGURATION_NOT_FOUND_ERROR("ServerSideEncryptionConfigurationNotFoundError", "The server-side encryption configuration was not found."),
    SERVICE_UNAVAILABLE("ServiceUnavailable", "Service is unable to handle request."),
    SIGNATURE_DOES_NOT_MATCH("SignatureDoesNotMatch", "The request signature does not match."),
    SLOW_DOWN("SlowDown", "Please reduce your request rate."),
    TAG_POLICY_EXCEPTION("TagPolicyException", "The tag policy does not allow the specified value."),
    TEMPORARY_REDIRECT("TemporaryRedirect", "Temporary redirect while DNS updated."),
    TOKEN_CODE_INVALID_ERROR("TokenCodeInvalidError", "The serial number and/or token code provided is not valid."),
    TOKEN_REFRESH_REQUIRED("TokenRefreshRequired", "The provided token must be refreshed."),
    TOO_MANY_ACCESS_POINTS("TooManyAccessPoints", "You have attempted to create more access points than allowed."),
    TOO_MANY_BUCKETS("TooManyBuckets", "You have attempted to create more buckets than allowed."),
    TOO_MANY_MULTI_REGION_ACCESS_POINT_REGIONS_ERROR("TooManyMultiRegionAccessPointregionsError", "You have attempted to create a Multi-Region Access Point with more Regions than allowed."),
    TOO_MANY_MULTI_REGION_ACCESS_POINTS("TooManyMultiRegionAccessPoints", "You have attempted to create more Multi-Region Access Points than allowed."),
    UNAUTHORIZED_ACCESS_ERROR("UnauthorizedAccessError", "(China Regions only) Returned when a bucket does not have an ICP license."),
    UNEXPECTED_CONTENT("UnexpectedContent", "This request contains unsupported content."),
    UNEXPECTED_IP_ERROR("UnexpectedIPError", "(China Regions only) The request was rejected because the IP was unexpected."),
    UNSUPPORTED_ARGUMENT("UnsupportedArgument", "The request contained an unsupported argument."),
    UNSUPPORTED_SIGNATURE("UnsupportedSignature", "The provided request is signed with an unsupported signature version."),
    UNRESOLVABLE_GRANT_BY_EMAIL_ADDRESS("UnresolvableGrantByEmailAddress", "The email address does not match any account on record."),
    USER_KEY_MUST_BE_SPECIFIED("UserKeyMustBeSpecified", "The bucket POST request must contain the specified field name."),
    NO_SUCH_ACCESS_POINT("NoSuchAccessPoint", "The specified access point does not exist."),
    INVALID_TAG("InvalidTag", "Request contains invalid tag input."),
    MALFORMED_POLICY("MalformedPolicy", "Your policy contains a principal that is not valid.");

    private final String errorCode;
    private final String description;

    S3AwsErrorCodes(String errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    public static S3AwsErrorCodes fromCode(String errorCode) {
        for (S3AwsErrorCodes c : values()) {
            if (c.errorCode.equals(errorCode)) return c;
        }
        return null;
    }

}
