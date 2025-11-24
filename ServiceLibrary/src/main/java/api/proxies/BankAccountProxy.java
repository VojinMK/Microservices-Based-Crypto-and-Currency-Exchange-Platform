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

import api.dtos.BankAccountDto;

@FeignClient("bank-account")
public interface BankAccountProxy {
	
	@PostMapping("/bank-accounts/create")
	ResponseEntity<?> createBankAccountFeign(@RequestBody BankAccountDto dto);
	
	@DeleteMapping("/bank-accounts/remove")
	ResponseEntity<String> deleteBankAccountFeign(@RequestParam(value="email") String email);
	
	@GetMapping("/bank-accounts/email")
	ResponseEntity<BankAccountDto> getBankAccountByEmailFeign(@RequestHeader("X-User-Email") String email);
	
	@PutMapping("/bank-accounts/update")
	ResponseEntity<?> updateBankAccount(@RequestBody BankAccountDto dto);

}
