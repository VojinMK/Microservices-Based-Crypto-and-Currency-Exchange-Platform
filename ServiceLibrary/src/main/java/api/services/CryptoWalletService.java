package api.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CryptoWalletDto;

@Service
public interface CryptoWalletService {
	
	@GetMapping("/crypto-wallets")
	ResponseEntity<?> getAllCryptoWallets();
	
	//zeli da pregleda svoj wallet, samo useri mogu
	@GetMapping("/crypto-wallets/email")
	ResponseEntity<?> getCryptoWalletByEmail(@RequestHeader("X-User-Email") String email);
	
	@PostMapping("/crypto-wallets/create")
	ResponseEntity<?> createCryptoWallet(@RequestBody CryptoWalletDto dto);
	
	@DeleteMapping("/crypto-wallets/remove")
	ResponseEntity<?> deleteCryptoWallet(@RequestParam String email);
	
	
	@PutMapping("/crypto-wallets/update")
	ResponseEntity<?> updateCryptoWallet(@RequestBody CryptoWalletDto dto);
}
