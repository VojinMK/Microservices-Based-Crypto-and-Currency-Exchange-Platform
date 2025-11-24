package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CryptoWalletDto;

@FeignClient("crypto-wallet")
public interface CryptoWalletProxy {
	
	@PostMapping("/crypto-wallets/create")
	ResponseEntity<CryptoWalletDto> createCryptoWalletFeign(@RequestBody CryptoWalletDto dto);
	
	@DeleteMapping("/crypto-wallets/remove")
	ResponseEntity<String> deleteCryptoWalletFeign(@RequestParam(value="email") String email);
	
	@GetMapping("/crypto-wallets/email")
	ResponseEntity<CryptoWalletDto> getCryptoWalletByEmailFeign(@RequestHeader("X-User-Email") String email);
	
	@PutMapping("/crypto-wallets/update")
	ResponseEntity<?> updateCryptoWalletFeign(@RequestBody CryptoWalletDto dto);

}
