package TradeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.CryptoWalletDto;
import api.dtos.CurrencyExchangeDto;
import api.dtos.TradeServiceDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoWalletProxy;
import api.proxies.CurrencyExchangeProxy;
import api.services.TradeService;
import util.exceptions.CryptoDoesntExistException;
import util.exceptions.CurrencyDoesntExistException;
import util.exceptions.InvalidQuantityException;
import util.exceptions.InvalidTradeRequest;
import util.exceptions.NoDataFoundException;

@RestController
public class TradeServiceImpl implements TradeService {

	@Autowired
	private BankAccountProxy bankProxy;

	@Autowired
	private CryptoWalletProxy walletProxy;

	@Autowired
	private TradeServiceRepository repo;

	@Autowired
	private CurrencyExchangeProxy currencyExchangeProxy;

	@Override
	public ResponseEntity<?> trade(String from, String to, BigDecimal quantity, String email) {

		from = from.toUpperCase();
		to = to.toUpperCase();

		if (quantity.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidQuantityException("Quantity can't be negative number");
		}

		if (isFiatCurrency(from) && isCryptoCurrency(to)) {
			return tradeFiatToCrypto(from, to, quantity, email);
		} else if (isCryptoCurrency(from) && isFiatCurrency(to)) {
			return tradeCryptoToFiat(from, to, quantity, email);
		}
		throw new InvalidTradeRequest("Invalid currency combination. Can't trade from " + from + " to " + to);
	}

	private ResponseEntity<?> tradeFiatToCrypto(String from, String to, BigDecimal quantity, String email) {

		ResponseEntity<BankAccountDto> bankAccountResponse = bankProxy.getBankAccountByEmailFeign(email);
		if (!bankAccountResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(bankAccountResponse.getStatusCode())
					.body("Unable to fetch bank account details.");
		}
		BankAccountDto bankAccount = bankAccountResponse.getBody();
		if (bankAccount == null) {
			throw new NoDataFoundException("This user doesn't have bank account.");
		}
		BigDecimal fromAmount = getAmountBank(bankAccount, from);
		if (fromAmount == null || fromAmount.compareTo(quantity) < 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("You don't have enough founds for this actions. You have " + fromAmount);
		}

		BigDecimal finalQuantity;
		String defaultCurrency;

		if (from.equals("EUR")) {
			finalQuantity = quantity;
			defaultCurrency = "EUR";
		} else if (from.equals("USD")) {
			finalQuantity = quantity;
			defaultCurrency = "USD";
		} else {
			ResponseEntity<CurrencyExchangeDto> exchangeResponse = currencyExchangeProxy.getExchangeFeign(from, "EUR");
			if (!exchangeResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(exchangeResponse.getStatusCode())
						.body("Unable to to conversion of your FROM value currently. Please use EUR or USD.");
			}
			BigDecimal exchangeRate = exchangeResponse.getBody().getExchangeRate();
			finalQuantity = quantity.multiply(exchangeRate);
			defaultCurrency = "EUR";
		}

		TradeServiceModel tradeModel = repo.findByFromAndTo(defaultCurrency, to);
		if (tradeModel == null) {
			throw new NoDataFoundException("Exchange rate " + defaultCurrency + " to " + to + " is not found");
		}
		BigDecimal toAmount = finalQuantity.multiply(tradeModel.getExchange_rate());

		// update accoutn
		BigDecimal newFromAmount = fromAmount.subtract(quantity);
		setAmountBank(bankAccount, from, newFromAmount);
		bankProxy.updateBankAccount(bankAccount);

		ResponseEntity<CryptoWalletDto> cryptoWalletResponse = walletProxy.getCryptoWalletByEmailFeign(email);
		if (!cryptoWalletResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(cryptoWalletResponse.getStatusCode())
					.body("Unable to fetch crypto wallet details.");
		}
		CryptoWalletDto cryptoWallet = cryptoWalletResponse.getBody();
		if (cryptoWallet == null) {
			throw new NoDataFoundException("This user don't have crypto wallet.");
		}

		// update wallet
		BigDecimal currentCryptoAmount = getAmountWallet(cryptoWallet, to);
		BigDecimal newCryptoAmount = currentCryptoAmount.add(toAmount);
		setAmountWallet(cryptoWallet, to, newCryptoAmount);
		walletProxy.updateCryptoWalletFeign(cryptoWallet);

		String message = String.format("Successfully: Exchanged %s: %s for %s: %s", from, quantity, to,
				toAmount);
		TradeServiceDto dto = new TradeServiceDto(message, cryptoWallet);
		return ResponseEntity.ok(dto);

	}

	private ResponseEntity<?> tradeCryptoToFiat(String from, String to, BigDecimal quantity, String email) {

		ResponseEntity<CryptoWalletDto> cryptoWalletResponse = walletProxy.getCryptoWalletByEmailFeign(email);
		if (!cryptoWalletResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(cryptoWalletResponse.getStatusCode())
					.body("Unable to fetch crypto wallet details.");
		}
		CryptoWalletDto cryptoWallet = cryptoWalletResponse.getBody();
		if (cryptoWallet == null) {
			throw new NoDataFoundException("This user don't have crypto wallet.");
		}
		BigDecimal fromAmount = getAmountWallet(cryptoWallet, from);
		if (fromAmount == null || fromAmount.compareTo(quantity) < 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("You don't have enough founds for this actions. You have " + fromAmount);
		}

		TradeServiceModel tradeModel;
		String defaultCurrency;
         
		tradeModel = repo.findByFromAndTo(from, "EUR");
		defaultCurrency = "EUR";

		if (to.equals("USD")) {
			tradeModel = repo.findByFromAndTo(from, "USD");
			defaultCurrency = "USD";
		}
		if (tradeModel == null) {
			throw new NoDataFoundException("Exchange rate for is not found");
		}

		BigDecimal baseAmount = quantity.multiply(tradeModel.getExchange_rate());
		BigDecimal finalToAmount;

		if (to.equals("EUR") || to.equals("USD")) {
			finalToAmount = baseAmount;
		} else {
			ResponseEntity<CurrencyExchangeDto> exchangeRateResponse = currencyExchangeProxy
					.getExchangeFeign(defaultCurrency, to);
			if (!exchangeRateResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(exchangeRateResponse.getStatusCode())
						.body("Unable to to conversion of values currently.");
			}
			BigDecimal exchangeRate = exchangeRateResponse.getBody().getExchangeRate();
			finalToAmount = baseAmount.multiply(exchangeRate);
		}

		// updating wallet
		BigDecimal newCryptoAmount = fromAmount.subtract(quantity);
		setAmountWallet(cryptoWallet, from, newCryptoAmount);
		walletProxy.updateCryptoWalletFeign(cryptoWallet);

		ResponseEntity<BankAccountDto> bankAccountResponse = bankProxy.getBankAccountByEmailFeign(email);
		if (!bankAccountResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(bankAccountResponse.getStatusCode())
					.body("Unable to fetch bank account details.");
		}
		BankAccountDto bankAccount = bankAccountResponse.getBody();
		if (bankAccount == null) {
			throw new NoDataFoundException("This user doesn't have bank account");
		}

		// updating bank account
		BigDecimal currentToAmount = getAmountBank(bankAccount, to);
		BigDecimal newToAmount = currentToAmount.add(finalToAmount);
		setAmountBank(bankAccount, to, newToAmount);
		bankProxy.updateBankAccount(bankAccount);

		String message = String.format("Successfully: Exchanged %s: %s for %s: %s", from,
				quantity, to, finalToAmount);
		TradeServiceDto dto = new TradeServiceDto(message, bankAccount);

		return ResponseEntity.ok(dto);
	}

	private BigDecimal getAmountBank(BankAccountDto bank, String currency) {
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

	private void setAmountBank(BankAccountDto bank, String currency, BigDecimal amount) {
		switch (currency) {
		case "RSD" -> bank.setRsdAmount(amount);
		case "EUR" -> bank.setEurAmount(amount);
		case "USD" -> bank.setUsdAmount(amount);
		case "CHF" -> bank.setChfAmount(amount);
		case "GBP" -> bank.setGbpAmount(amount);
		}
	}

	private BigDecimal getAmountWallet(CryptoWalletDto wallet, String currency) {
		return switch (currency) {
		case "BTC" -> wallet.getBtcAmount();
		case "ETH" -> wallet.getEthAmount();
		case "LTC" -> wallet.getLtcAmount();
		default -> {
			throw new CryptoDoesntExistException("Currency " + currency + " doesn't exist.",
					List.of("BTC", "ETH", "LTC"));
		}
		};

	}

	private void setAmountWallet(CryptoWalletDto wallet, String currency, BigDecimal amount) {
		switch (currency) {
		case "BTC" -> wallet.setBtcAmount(amount);
		case "ETH" -> wallet.setEthAmount(amount);
		case "LTC" -> wallet.setLtcAmount(amount);
		}
	}

	public boolean isFiatCurrency(String currency) {
		if (Arrays.asList("RSD", "EUR", "USD", "CHF", "GBP").contains(currency)) {
			return true;
		}
		return false;
	}

	public boolean isCryptoCurrency(String currency) {
		if (Arrays.asList("BTC", "ETH", "LTC").contains(currency)) {
			return true;
		}
		return false;
	}
}
