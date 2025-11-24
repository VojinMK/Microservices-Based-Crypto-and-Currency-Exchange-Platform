package CryptoExchange;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoExchangeDto;
import api.services.CryptoExchangeService;
import util.exceptions.CryptoDoesntExistException;
import util.exceptions.NoDataFoundException;

@RestController
public class CryptoExchangeServiceImpl implements CryptoExchangeService {
	
	@Autowired
	private CryptoExchangeRepository repo;
	

	@Override
	public ResponseEntity<?> getCryptoExchange(String from, String to) {
		
		String missingCrypto=null;
	    List<String> validCryptos=repo.findAllDistinctCurrencies();
	    
	    if(!isValidCrypto(from.toUpperCase())) {
	    	missingCrypto=from;
	    }
	    else if(!isValidCrypto(to.toUpperCase())) {
	    	missingCrypto=to;
	    }
		if(missingCrypto!=null) {
			throw new CryptoDoesntExistException(String.format("Crypto %s does not exist in the database", missingCrypto),validCryptos);
		}
		
		CryptoExchangeModel dbResponse=repo.findByFromAndTo(from.toUpperCase(), to.toUpperCase());
		
		if(dbResponse==null) {
			throw new NoDataFoundException(String.format("Request exchange rate ( %s to %s) does not exist.", from, to));
		}
		CryptoExchangeDto dto=new CryptoExchangeDto(dbResponse.getFrom().toUpperCase(),dbResponse.getTo().toUpperCase(), dbResponse.getExchangeRate());
		return ResponseEntity.ok(dto);
	
	}
	
	public boolean isValidCrypto(String crypto) {
		List<String> cryptos = repo.findAllDistinctCurrencies();
		for (String s : cryptos) {
			if (s.equalsIgnoreCase(crypto)) {
				return true;
			}
		}
		return false;
	}

}
