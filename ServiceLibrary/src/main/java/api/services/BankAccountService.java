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

import api.dtos.BankAccountDto;
@Service
public interface BankAccountService {
	
	@GetMapping("/bank-accounts")
	ResponseEntity<?> getAllBankAccounts();
	
	//zeli da pregleda svoj racun, samo useri mogu
	@GetMapping("/bank-accounts/email")
	ResponseEntity<?> getBankAccountByEmail(@RequestHeader("X-User-Email") String email);
	
	@PostMapping("/bank-accounts/create")
	ResponseEntity<?> createBankAccount(@RequestBody BankAccountDto dto);
	
	@DeleteMapping("/bank-accounts/remove")
	ResponseEntity<?> deleteBankAccount(@RequestParam String email);
	
	
	@PutMapping("/bank-accounts/update")
	ResponseEntity<?> updateBankAccount(@RequestBody BankAccountDto dto);
	
}
