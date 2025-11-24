package usersService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.CryptoWalletDto;
import api.dtos.UserDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoWalletProxy;
import api.services.UsersService;
import feign.FeignException;
import util.exceptions.AccessException;
import util.exceptions.DataIntegrityViolationException;
import util.exceptions.OperationException;
import util.exceptions.OwnerExistsException;

@RestController
public class UserServieImpl implements UsersService {

	@Autowired
	private UserRepository repo;

	@Autowired
	private BankAccountProxy bankProxy;

	@Autowired
	private CryptoWalletProxy walletProxy;

	@Override
	public ResponseEntity<?> getUsers() {
		List<UserModel> models = repo.findAll();
		List<UserDto> dtos = new ArrayList<UserDto>();
		if (!models.isEmpty()) {
			for (UserModel model : models) {
				dtos.add(convertModelToDto(model));
			}
			return ResponseEntity.ok(dtos);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no users.");

	}

	@Override
	public ResponseEntity<?> getUserByEmail(String email) {
		UserModel model = repo.findByEmail(email);
		if (model == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this email doesn't exist.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(convertModelToDto(model));
	}

	@Override
	public ResponseEntity<?> createAdmin(UserDto dto) {
		if (!(dto.getRole().toUpperCase().equals("ADMIN"))) {
			throw new DataIntegrityViolationException("Only ADMIN users can be created here.");
		}
		if (repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("ADMIN");
			UserModel model = convertDtoToModel(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin with passed email already exists");
		}

	}

	@Override
	public ResponseEntity<?> createUser(UserDto dto) {
		if (!(dto.getRole().toUpperCase().equals("USER"))) {
			throw new DataIntegrityViolationException("Only USER users can be created here.");
		}
		if (repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("USER");
			UserModel model = convertDtoToModel(dto);
			UserModel createdUser = repo.save(model);

			BankAccountDto bankAccountDto = new BankAccountDto(createdUser.getEmail(), BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
			ResponseEntity<?> responseBank = bankProxy.createBankAccountFeign(bankAccountDto);

			if (responseBank.getStatusCode().is2xxSuccessful()) {
				// ovde ce jos biti posla
				CryptoWalletDto walletDto = new CryptoWalletDto(createdUser.getEmail(), BigDecimal.ZERO.setScale(8),
						BigDecimal.ZERO.setScale(8), BigDecimal.ZERO.setScale(8));

				ResponseEntity<?> responseWallet = walletProxy.createCryptoWalletFeign(walletDto);

				if (!responseWallet.getStatusCode().is2xxSuccessful()) {
					bankProxy.deleteBankAccountFeign(createdUser.getEmail());
					repo.delete(createdUser);
					throw new OperationException("User and bank account deleted due failing to create crypto wallet.");
				}

				return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
			} else {
				repo.delete(createdUser);
				throw new OperationException("User deleted due failing to create bank account");
			}

		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email already exists");
		}
	}

	@Override
	public ResponseEntity<?> updateUser(UserDto dto, @RequestHeader("X-User-Role") String role) {
		Set<String> allowed_roles = Set.of("ADMIN", "OWNER", "USER");
		if (!allowed_roles.contains(dto.getRole().toUpperCase())) {
			throw new DataIntegrityViolationException("That role doesn't exist.");
		}

		if (repo.findByEmail(dto.getEmail()) != null) {
			if (role.equals("ROLE_ADMIN")) {
				if (repo.findByEmail(dto.getEmail()).getRole().toUpperCase().equals("ADMIN")
						|| repo.findByEmail(dto.getEmail()).getRole().toUpperCase().equals("OWNER")) {
					throw new AccessException("Admin can only update USER users.");
				}
			}

			if (dto.getRole().toUpperCase().equals("OWNER") && repo.existsByRole(dto.getRole().toUpperCase())) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("There is already one OWNER in database.");
			}

////			if(repo.findByEmail(dto.getEmail())!=null) {
////				return ResponseEntity.status(HttpStatus.CONFLICT).body("There is already one user with that email in database.");
////			}
////			
//			
			// pvde posla
			repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole().toUpperCase());
			return ResponseEntity.status(HttpStatus.OK).body(dto);

		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email doesn't exists");
		}
	}

	public UserDto convertModelToDto(UserModel model) {
		return new UserDto(model.getEmail(), model.getPassword(), model.getRole());
	}

	public UserModel convertDtoToModel(UserDto dto) {
		return new UserModel(dto.getEmail(), dto.getPassword(), dto.getRole());
	}

	@Override
	public ResponseEntity<?> createOwner(UserDto dto) {
		if (dto.getRole().toUpperCase().equals("OWNER")) {
			if (repo.existsByRole("OWNER")) {
				throw new OwnerExistsException("OWNER already exists. There could be only one OWNER.");
			}
			if (repo.findByEmail(dto.getEmail()) == null) {
				dto.setRole("OWNER");
				UserModel model = convertDtoToModel(dto);
				return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email already exists");
			}

		} else {
			throw new DataIntegrityViolationException("Only OWNER users can be created here.");
		}
	}

	@Override
	public ResponseEntity<?> removeUser(String email) {
		UserModel model = repo.findByEmail(email);
		if (model == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this email doesn't exist.");
		}

		if ("USER".equalsIgnoreCase(model.getRole())) {

			ResponseEntity<?> responseBank = bankProxy.deleteBankAccountFeign(email);
			if (responseBank == null || !responseBank.getStatusCode().is2xxSuccessful()) {
				throw new OperationException("Failed to delete bank account, user wasn't deleted.");
			}
			ResponseEntity<?> responseWallet = walletProxy.deleteCryptoWalletFeign(email);
			if (responseWallet == null || !responseWallet.getStatusCode().is2xxSuccessful()) {
				throw new OperationException("Failed to delete crypto wallet, user wasn't deleted.");
			}

		}

		repo.delete(model);
		return ResponseEntity.status(HttpStatus.OK).body("User is deleted.");
	}

	@Override
	public ResponseEntity<?> getUserRole(String email) {
		UserModel model = repo.findByEmail(email);
		if (model == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(model.getRole());
	}

}
