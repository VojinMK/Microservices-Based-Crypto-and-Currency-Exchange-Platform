package CryptoWallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoWalletDto;
import api.dtos.UserDto;
import api.proxies.UserProxy;
import api.services.CryptoWalletService;
import feign.FeignException;
import util.exceptions.ConflictException;
import util.exceptions.NoDataFoundException;

@RestController
public class CryptoWalletServiceImpl implements CryptoWalletService {

	@Autowired
	private CryptoWalletRepository repo;

	@Autowired
	private UserProxy proxy;

	@Override
	public ResponseEntity<?> getAllCryptoWallets() {

		List<CryptoWalletModel> models = repo.findAll();
		List<CryptoWalletDto> dtos = new ArrayList<CryptoWalletDto>();

		if (!models.isEmpty()) {
			for (CryptoWalletModel model : models) {
				dtos.add(modelToDto(model));
			}
			return ResponseEntity.ok(dtos);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no crypto wallets.");

	}

	@Override
	public ResponseEntity<?> getCryptoWalletByEmail(String email) {

		CryptoWalletModel model = repo.findByEmail(email);
		if (model == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You don't have crypto wallet.");
		}
		return ResponseEntity.ok(modelToDto(model));

	}

	@Override
	public ResponseEntity<?> createCryptoWallet(CryptoWalletDto dto) {

		UserDto userResponse;
		String userRole;

		try {
			var userResp = proxy.getUserByEmailFeign(dto.getEmail());
			var roleResp = proxy.getUserRoleFeign(dto.getEmail());
			userResponse = (userResp != null) ? userResp.getBody() : null;
			userRole = (roleResp != null) ? roleResp.getBody() : null;
		} catch (FeignException.NotFound e) {
			throw new NoDataFoundException("User with this email is not found");
		}

		if (userResponse == null || userRole == null || !"USER".equalsIgnoreCase(userRole.trim())) {
			throw new NoDataFoundException("User with this email is not found");
		}

		if (repo.findByEmail(dto.getEmail()) != null) {
			throw new ConflictException("User already have crypto wallet.");
		}

		CryptoWalletModel newWallet = new CryptoWalletModel(dto.getEmail(), BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO);
		repo.save(newWallet);
		return ResponseEntity.status(HttpStatus.CREATED).body(newWallet);
	}

	@Override
	public ResponseEntity<?> deleteCryptoWallet(String email) {

		CryptoWalletModel model = repo.findByEmail(email);
		if (model == null) {
			throw new NoDataFoundException("Crypto wallet for this email doesn't exist.");
		}
		repo.deleteByEmail(email);
		return ResponseEntity.status(HttpStatus.OK).body("Crypto wallet deleted");

	}

	@Override
	public ResponseEntity<?> updateCryptoWallet(CryptoWalletDto dto) {

		CryptoWalletModel existingWallet = repo.findByEmail(dto.getEmail());

		if (existingWallet == null) {
			throw new NoDataFoundException("Crypto wallet for this email doesn't exist.");
		}
		if(dto.getBtcAmount().compareTo(BigDecimal.ZERO) < 0 ||
				   dto.getEthAmount().compareTo(BigDecimal.ZERO) < 0 ||
				   dto.getLtcAmount().compareTo(BigDecimal.ZERO) < 0 
				  ) {
				       return ResponseEntity.badRequest().body("Amounts cannot be negative");
				}

		repo.updateCryptoWallet(dto.getEmail(), dto.getBtcAmount(), dto.getEthAmount(), dto.getLtcAmount());
		return ResponseEntity.status(HttpStatus.OK).body(dto);

	}

	public CryptoWalletDto modelToDto(CryptoWalletModel model) {
		return new CryptoWalletDto(model.getEmail(), model.getBtcAmount(), model.getEthAmount(), model.getLtcAmount());
	}

	public CryptoWalletModel dtoToModel(CryptoWalletDto dto) {
		return new CryptoWalletModel(dto.getEmail(), dto.getBtcAmount(), dto.getEthAmount(), dto.getLtcAmount());
	}
}
