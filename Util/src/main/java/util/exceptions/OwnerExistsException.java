package util.exceptions;

public class OwnerExistsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OwnerExistsException() {
		
	}
	public OwnerExistsException(String message) {
		super(message);
	}
}
