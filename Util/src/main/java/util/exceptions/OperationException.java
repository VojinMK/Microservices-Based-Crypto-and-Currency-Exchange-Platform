package util.exceptions;

public class OperationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public OperationException() {}
	
	public OperationException(String message) {
		super(message);
	}

}
