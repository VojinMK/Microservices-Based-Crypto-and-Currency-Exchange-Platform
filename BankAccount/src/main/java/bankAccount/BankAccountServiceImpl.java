package bankAccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.UserDto;
import api.proxies.UserProxy;
import api.services.BankAccountService;
import feign.FeignException;
import util.exceptions.ConflictException;
import util.exceptions.NoDataFoundException;

@RestController
public class BankAccountServiceImpl implements BankAccountService{

	@Autowired
	private BankAccountRepository repo;
	
	@Autowired
	private UserProxy proxy;
	
	@Override
	public ResponseEntity<?> getAllBankAccounts() {
		List<BankAccountModel> models=repo.findAll();
		List<BankAccountDto> dtos=new ArrayList<BankAccountDto>();
		if(!models.isEmpty()) {
			for(BankAccountModel model: models) {
				dtos.add(convertModelToDto(model));
			}
			return ResponseEntity.ok(dtos);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no bank accounts");
		
	}

	@Override
	public ResponseEntity<?> getBankAccountByEmail(@RequestHeader("X-User-Email") String email) {
		BankAccountModel account=repo.findByEmail(email);
		if(account==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You don't have bank account.");
		}
		return ResponseEntity.ok(convertModelToDto(account));
	}

	@Override
	public ResponseEntity<?> createBankAccount(BankAccountDto dto) {
		
		UserDto userResponse;
	    String userRole;

	    try {
	        var userResp = proxy.getUserByEmailFeign(dto.getEmail());
	        var roleResp = proxy.getUserRoleFeign(dto.getEmail());
	        userResponse = (userResp != null) ? userResp.getBody() : null;
	        userRole     = (roleResp != null) ? roleResp.getBody() : null;
	    } catch (FeignException.NotFound e) {
	        throw new NoDataFoundException("User with this email is not found");
	    }

	    if (userResponse == null || userRole == null || !"USER".equalsIgnoreCase(userRole.trim())) {
	        throw new NoDataFoundException("User with this email is not found");
	    }

	    if (repo.findByEmail(dto.getEmail()) != null) {
	        throw new ConflictException("User already have bank account");
	    }
		
		BankAccountModel newAccount=new BankAccountModel(
				dto.getEmail(),
	            BigDecimal.ZERO,
	            BigDecimal.ZERO,
	            BigDecimal.ZERO,
	            BigDecimal.ZERO,
	            BigDecimal.ZERO
				);
		repo.save(newAccount);
		return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);		
	}

	@Override
	public ResponseEntity<?> deleteBankAccount(String email) {
		BankAccountModel account=repo.findByEmail(email);
	    if(account==null) {
	    	throw new NoDataFoundException("Bank account for this email doesn't exist.");
	    }
	    repo.deleteByEmail(email);
	    
	    return ResponseEntity.status(HttpStatus.OK).body("Bank account deleted");
	}

	@Override
	public ResponseEntity<?> updateBankAccount(BankAccountDto dto) {
		BankAccountModel existingAccount=repo.findByEmail(dto.getEmail());
		
		if(existingAccount==null) {
			throw new NoDataFoundException("Bank account for this email doesn't exist.");
		}
       repo.updateBankAccount(dto.getEmail(), dto.getRsdAmount(), dto.getEurAmount(), dto.getUsdAmount(), dto.getChfAmount(), dto.getGbpAmount());
       return ResponseEntity.status(HttpStatus.OK).body(dto);
	}
	
	public BankAccountDto convertModelToDto(BankAccountModel model) {
		return new BankAccountDto(model.getEmail(), model.getRsdAmount(),model.getEurAmount(), model.getUsdAmount(),model.getChfAmount(),model.getGbpAmount());
	}

	public BankAccountModel convertDtoToModel(BankAccountDto dto) {
		return new BankAccountModel(dto.getEmail(),dto.getRsdAmount(),dto.getEurAmount(),dto.getUsdAmount(),dto.getChfAmount(),dto.getGbpAmount());
	}

}
