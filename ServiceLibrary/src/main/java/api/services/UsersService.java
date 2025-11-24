package api.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.UserDto;
@Service
public interface UsersService {

	@GetMapping("/users")
	ResponseEntity<?> getUsers();

	@GetMapping("/users/email")
	ResponseEntity<?> getUserByEmail(@RequestParam String email);

	@PostMapping("/users/newAdmin")
	ResponseEntity<?> createAdmin(@RequestBody UserDto dto);

	@PostMapping("/users/newUser")
	ResponseEntity<?> createUser(@RequestBody UserDto dto);
	
	@PostMapping("/users/newOwner")
	ResponseEntity<?> createOwner(@RequestBody UserDto dto);

	@PutMapping("/users/updateUser")
	ResponseEntity<?> updateUser(@RequestBody UserDto dto,@RequestHeader("X-User-Role")  String role);
	
	@DeleteMapping("/users/removeUser")
	ResponseEntity<?> removeUser(@RequestParam String email);
	
	@GetMapping("/users/userRole")
	ResponseEntity<?> getUserRole(@RequestParam String email);
}
