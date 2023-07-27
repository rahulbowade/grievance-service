/**
 *
 */
package org.upsmf.grievance.exceptions;

/**
 * @author Juhi Agarwal This exception will capture missing data and will create
 *         message according to missing data field
 */
public class RequestDataMissingException extends RuntimeException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private final String message;

	/**
	 * @param code
	 *            the code to set
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @param message
	 */
	public RequestDataMissingException(String message) {
		this.message = message;
	}

}
