package currency_conversion;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import api.dtos.BankAccountDto;
import api.dtos.CurrencyConversionDto;
import api.dtos.CurrencyExchangeDto;
import api.proxies.BankAccountProxy;
import api.proxies.CurrencyExchangeProxy;
import api.services.CurrencyConversionService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import util.exceptions.CurrencyDoesntExistException;
import util.exceptions.InvalidQuantityException;

@RestController
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

	private RestTemplate template = new RestTemplate();

	@Autowired
	private CurrencyExchangeProxy proxy;

	@Autowired
	private BankAccountProxy bankProxy;

	Retry retry; // referenca
	CurrencyExchangeDto response;

	public CurrencyConversionServiceImpl(RetryRegistry registry) {
		retry = registry.retry("default"); // nazivi iz konfig fajla propertires
	}

	@Override
	@CircuitBreaker(name = "cb", fallbackMethod = "fallback")
	public ResponseEntity<?> getConversionFeign(String from, String to, BigDecimal quantity,
			@RequestHeader("X-User-Email") String email) {

		ResponseEntity<BankAccountDto> bankAccountResponse = bankProxy.getBankAccountByEmailFeign(email);

		if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
		    return ResponseEntity.badRequest().body("Quantity must be positive number.");
		}
		if (!bankAccountResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(bankAccountResponse.getStatusCode())
					.body("Unable to fetch bank account details.");
		}
		BankAccountDto bankAccount = bankAccountResponse.getBody();

		if (bankAccount == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank account wasn't found for this user.");
		}


		BigDecimal fromAmount = getAmount(bankAccount, from.toUpperCase());
		BigDecimal toAmount = getAmount(bankAccount, to.toUpperCase());

		if (fromAmount == null || fromAmount.compareTo(quantity) < 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("You don't have enough founds for this actions. You have " + fromAmount);
		}

		ResponseEntity<CurrencyExchangeDto> currencyExchageResponse = proxy.getExchangeFeign(from.toUpperCase(), to.toUpperCase());
		CurrencyExchangeDto dto = currencyExchageResponse.getBody();
		BigDecimal exchangeRate = dto.getExchangeRate();

		BigDecimal newFromAmount = fromAmount.subtract(quantity);
		BigDecimal convertedAmount = quantity.multiply(exchangeRate);
		BigDecimal newToAmount = toAmount.add(convertedAmount);

		setAmount(bankAccount, from.toUpperCase(), newFromAmount);
		setAmount(bankAccount, to.toUpperCase(), newToAmount);

		if (from.toUpperCase().equals(to.toUpperCase())) {
			setAmount(bankAccount, from.toUpperCase(), fromAmount);
			setAmount(bankAccount, to.toUpperCase(), toAmount);
		}
		bankProxy.updateBankAccount(bankAccount);

		ResponseEntity<BankAccountDto> updatedBankAccount = bankProxy.getBankAccountByEmailFeign(email);
		BankAccountDto updatedBank = updatedBankAccount.getBody();

		Map<String, Object> body = new HashMap<>();
		body.put("message", String.format("Transaction is successful: Exchanged %s: %s for %s: %s", from.toUpperCase(),
				quantity, to.toUpperCase(), convertedAmount));
		body.put("bankAccount", updatedBank);

		return ResponseEntity.ok(body);

	}

	public ResponseEntity<?> fallback(CallNotPermittedException ex) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body("Currency conversion service is currently unavailable. Circuit braker is in OPEN state");
	}

	@Override
	public ResponseEntity<?> getConversion(String from, String to, BigDecimal quantity) {

		if (quantity.compareTo(BigDecimal.valueOf(300.0)) == 1) {
			throw new InvalidQuantityException(String.format("Quantity of %s is too large", quantity));
		}

		String endPoint = "http://localhost:8000/currency-exchange?from=" + from + "&to=" + to;
		ResponseEntity<CurrencyExchangeDto> response = template.getForEntity(endPoint, CurrencyExchangeDto.class);

		return ResponseEntity.ok(new CurrencyConversionDto(response.getBody(), quantity));
	}

	private BigDecimal getAmount(BankAccountDto bank, String currency) {
		return switch (currency) {
		case "RSD" -> bank.getRsdAmount();
		case "EUR" -> bank.getEurAmount();
		case "USD" -> bank.getUsdAmount();
		case "CHF" -> bank.getChfAmount();
		case "GBP" -> bank.getGbpAmount();
		default -> {
			throw new CurrencyDoesntExistException("Currency " + currency + " doesn't exist.",
					List.of("RSD", "EUR", "USD", "CHF", "GBP"));
		}
		};

	}

	private void setAmount(BankAccountDto bank, String currency, BigDecimal amount) {
		switch (currency) {
		case "RSD" -> bank.setRsdAmount(amount);
		case "EUR" -> bank.setEurAmount(amount);
		case "USD" -> bank.setUsdAmount(amount);
		case "CHF" -> bank.setChfAmount(amount);
		case "GBP" -> bank.setGbpAmount(amount);
		}
	}

}
