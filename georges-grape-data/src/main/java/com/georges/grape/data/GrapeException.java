package com.georges.grape.data;

/**
 * The class <code>GrapeException</code> is a form of <code>Exception</code>
 * that encapsulates exception in Grape server and the Grape SDK.
 * 
 * REST web service needs to return a proper HTTP Status code once something is
 * wrong. <code>GrapeException</code> defines error code to map these HTTP
 * Status code with the format below:
 * 
 * HTTP_STATUS_CODE+GRAPE_INTERNAL_ERROR_CODE
 * 
 * For example 404001 has HTTP Status code 404("Not Found"). With this way,
 * whenever server gets something wrong, it will throw a
 * <code>GrapeException</code> with proper error code, and the exception handler
 * in the server can extract the HTTP status code from the error code.
 * 
 * We only maps error code to two HTTP Status series: CLIENT_ERROR and
 * SERVER_ERROR.
 * 
 * The following is some standard HTTP Status definition. 
 * 
 * 400 Bad Request
 * 401 Unauthorized 
 * 402 Payment Required 
 * 403 Forbidden 
 * 404 Not Found 
 * 405 Method Not Allowed 
 * 406 Not Acceptable 
 * 407 Proxy Authentication Required 
 * 408 Request Timeout 
 * 409 Conflict 
 * 
 * 
 * @author Alex Zuo
 * 
 */
public class GrapeException extends RuntimeException {

	private static final long serialVersionUID = -3291003345927254820L;

	public enum ErrorStatus {
		INVALID_PARAMETER_ERROR(400001, "invalid parameters"),
		UNAUTHENTICATED_ERROR(401002,"authentication needed"),
		INVALID_TOKEN_ERROR(401003,"invalid token"),
		TOKEN_EXPIRED_ERROR(401004,"token expired"),
		INVALID_SIGNAGURE_ERROR(401005,"user id does not match signature"),
        UNAUTHORIZED_ERROR(403006, "unauthorized user or action"),
        NO_EVENT_ERROR(404007,"event does not exist"),
        NO_EVENT_POST_PICTURE(404008,"event does not has a post picture"),
		INTERANL_SERVER_ERROR(500009, "internal server error");
				
		private final int value;
		private final String description;
		
		private ErrorStatus(int value, String description) {
			this.value = value;
			this.description = description;
		}
		
		public int value() {
			return this.value;
		}
		public String description() {
			return this.description;
		}
		public  int httpstatus() {
			return Integer.parseInt(new Integer(value).toString().substring(0, 3));
		}
		
		public static ErrorStatus valueOf(int errValue) {
			for (ErrorStatus e : values()) {
				if (e.value == errValue) {
					return e;
				}
			}
			throw new IllegalArgumentException("No matching constant for [" + errValue + "]");
		}
	}
		
	private ErrorStatus errorStatus;
	
    /**
     * Constructs a new exception with the specified error status
     *
     * @param   errorStatus the error status
     */
	public GrapeException(ErrorStatus errorStatus) {
		this.errorStatus = errorStatus;
	}
	
    /**
     * Constructs a new exception with the specified error status and 
     * detail message.  
     *
     * @param   errorStatus the error status
     * @param   detailMessage   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
	public GrapeException(ErrorStatus errorStatus, String detailMessage) {
		super(detailMessage);
		this.errorStatus = errorStatus;
	}
	
	public ErrorStatus getErrorStatus() {
		return errorStatus;
	}

	@Override
	public String toString() {
		return "GrapeException [errorStatus=" + errorStatus + ", detailMessage="+this.getMessage()+"]";
	}
}
