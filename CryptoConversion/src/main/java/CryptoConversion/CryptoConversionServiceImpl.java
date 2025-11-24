package CryptoConversion;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoExchangeDto;
import api.dtos.CryptoWalletDto;
import api.proxies.CryptoExchangeProxy;
import api.proxies.CryptoWalletProxy;
import api.services.CryptoConversionService;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import util.exceptions.CryptoDoesntExistException;

@RestController
public class CryptoConversionServiceImpl implements CryptoConversionService {

	@Autowired
	private CryptoExchangeProxy exchangeProxy;
	
	@Autowired
	private CryptoWalletProxy walletProxy;
	
	Retry retry;
	
	public CryptoConversionServiceImpl(RetryRegistry registry) {
		retry=registry.retry("default");
	}
	
	@Override
	public ResponseEntity<?> getCryptoConversion(String from, String to, BigDecimal quantity, String email) {
		
		ResponseEntity<CryptoWalletDto> cryptoWalletResponse=retry.executeSupplier(() -> walletProxy.getCryptoWalletByEmailFeign(email));
		
		if(!cryptoWalletResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(cryptoWalletResponse.getStatusCode())
					.body("Unable to fetch crypto wallet details.");
		}
		if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
		    return ResponseEntity.badRequest().body("Quantity must be positive number.");
		}
		
		CryptoWalletDto cryptoWallet=cryptoWalletResponse.getBody();
		if(cryptoWallet==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Crypto wallet wasn't found for this user.");
		}
		
		BigDecimal fromAmount=getAmount(cryptoWallet,from.toUpperCase());
		BigDecimal toAmount=getAmount(cryptoWallet, to.toUpperCase());
		
		if(fromAmount==null || fromAmount.compareTo(quantity)<0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("You don't have enough founds for this actions. You have " + fromAmount);
		}

		ResponseEntity<CryptoExchangeDto> cryptoExchangeResponse= retry.executeSupplier(() -> exchangeProxy.getCryptoExchange(from.toUpperCase(), to.toUpperCase()));
		CryptoExchangeDto dto=cryptoExchangeResponse.getBody();
		BigDecimal exchangeRate=dto.getExchangeRate();
		
		BigDecimal newFromAmount=fromAmount.subtract(quantity);
		BigDecimal convertedAmount=quantity.multiply(exchangeRate);
		BigDecimal newToAmount=toAmount.add(convertedAmount);
		
		setAmount(cryptoWallet,from.toUpperCase(),newFromAmount);
		setAmount(cryptoWallet, to.toUpperCase(),newToAmount);
		
		if (from.toUpperCase().equals(to.toUpperCase())) {
			setAmount(cryptoWallet, from.toUpperCase(), fromAmount);
			setAmount(cryptoWallet, to.toUpperCase(), toAmount);
		}
		
	    walletProxy.updateCryptoWalletFeign(cryptoWallet);
	    
	    ResponseEntity<CryptoWalletDto> updatedWalletResponse=walletProxy.getCryptoWalletByEmailFeign(email);
	    CryptoWalletDto updatedWallet=updatedWalletResponse.getBody();
	    
	    Map<String, Object> body = new HashMap<>();
		body.put("message", String.format("Transaction is successful: Exchanged %s: %s for %s: %s", from.toUpperCase(),
				quantity, to.toUpperCase(), convertedAmount));
		body.put("cryptoWallet", updatedWallet);

		return ResponseEntity.ok(body);
	}
	
	private BigDecimal getAmount(CryptoWalletDto wallet, String currency) {
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

	private void setAmount(CryptoWalletDto wallet, String currency, BigDecimal amount) {
		switch (currency) {
		case "BTC" -> wallet.setBtcAmount(amount);
		case "ETH" -> wallet.setEthAmount(amount);
		case "LTC" -> wallet.setLtcAmount(amount);
		}
	}

}
