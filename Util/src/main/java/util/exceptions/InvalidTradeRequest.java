package util.exceptions;

public class InvalidTradeRequest extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidTradeRequest() {}
	
	public InvalidTradeRequest(String message) {
		super(message);
	}

}
