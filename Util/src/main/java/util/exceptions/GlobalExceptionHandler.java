package util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<?> handleHttpClientException(HttpClientErrorException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ExceptionModel("Requested currencies not found", "Please make sure to enter currency from the list [CHF, EUR, GBP, RSD, USD]", HttpStatus.NOT_FOUND));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<?> handleMissingServletRequestParamen(MissingServletRequestParameterException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionModel(ex.getMessage(),
				"Make sure to enter all requested parameters", HttpStatus.BAD_REQUEST));
	}

	@ExceptionHandler(CurrencyDoesntExistException.class)
	public ResponseEntity<?> handleInvalidCurrency(CurrencyDoesntExistException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ExceptionModel(ex.getMessage(),
						String.format("Please make sure to enter currency from the list %s", ex.getCurrencies()),
						HttpStatus.NOT_FOUND));
	}
	
	@ExceptionHandler(CryptoDoesntExistException.class)
	public ResponseEntity<?> handleInvalidCrypto(CryptoDoesntExistException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ExceptionModel(ex.getMessage(),
						String.format("Please make sure to enter crypto from the list %s", ex.getCryptos()),
						HttpStatus.NOT_FOUND));
	}

	@ExceptionHandler(NoDataFoundException.class)
	public ResponseEntity<?> handleInvalidExchangeRate(NoDataFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ExceptionModel(ex.getMessage(),
						String.format("No data found, check your inputs."),
						HttpStatus.NOT_FOUND));
	}

	@ExceptionHandler(InvalidQuantityException.class)
	public ResponseEntity<?> handleInvalidQuantity(InvalidQuantityException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionModel(ex.getMessage(),
				"This value for quantity can't be used.", HttpStatus.BAD_REQUEST));
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> handleInvalidInput(DataIntegrityViolationException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionModel(ex.getMessage(),
				"Please enter valid input.", HttpStatus.BAD_REQUEST));
	}
	@ExceptionHandler(OwnerExistsException.class)
	public ResponseEntity<?> handleOwnerExists(OwnerExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionModel(ex.getMessage(),
				"Owner allready exits. There could be only one OWNER.", HttpStatus.BAD_REQUEST));
	}
	
	@ExceptionHandler(AccessException.class)
	public ResponseEntity<?> handleOwnerExists(AccessException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionModel(ex.getMessage(),
				"You don't have this permission.", HttpStatus.BAD_REQUEST));
	}
	
	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<?> handleConflict(ConflictException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionModel(ex.getMessage(),
				"Conflict happened.", HttpStatus.BAD_REQUEST));
	}
	
	@ExceptionHandler(OperationException.class)
	public ResponseEntity<?> handleCreationException(OperationException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionModel(ex.getMessage(),
				"Failed to create.", HttpStatus.BAD_REQUEST));
	}
	
	@ExceptionHandler(InvalidTradeRequest.class)
	public ResponseEntity<?> handleInvalidTradeRequestException(InvalidTradeRequest ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionModel(ex.getMessage(),
				"Only valid combinations are: crypto->fiat & fiat->crypto", HttpStatus.BAD_REQUEST));
	}

	// public String fineTuneMessage(String message) {
	// String [] partsOfMessage=message.split("\"");
	// return partsOfMessage[4];
	// }
}
