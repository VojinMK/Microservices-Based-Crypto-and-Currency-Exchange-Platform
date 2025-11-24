package util.exceptions;

import java.util.List;

public class CryptoDoesntExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> cryptos;

	public CryptoDoesntExistException() {}
	
	public CryptoDoesntExistException(String message, List<String> crypto) {
		super(message);
		this.cryptos=crypto;
	}

	public List<String> getCryptos() {
		return cryptos;
	}

	public void setCryptos(List<String> cryptos) {
		this.cryptos = cryptos;
	}
	
	
}
