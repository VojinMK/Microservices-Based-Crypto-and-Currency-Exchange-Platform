package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.UserDto;

@FeignClient("users-service")
public interface UserProxy {

	@GetMapping("/users/email")
	ResponseEntity<UserDto> getUserByEmailFeign(@RequestParam(value="email") String email);
	
	@GetMapping("/users/userRole")
	ResponseEntity<String> getUserRoleFeign(@RequestParam(value="email") String email);
}
