package apiGateway.authentification;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import api.dtos.UserDto;

@Configuration
@EnableWebFluxSecurity
public class ApiGatewayAuthentification {

	@Bean
	SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		http
		.csrf(csrf -> csrf.disable())
		.authorizeExchange(exchange -> exchange
				.pathMatchers("/currency-exchange").hasAnyRole("ADMIN","OWNER","USER")
				.pathMatchers("/currency-conversion").hasRole("USER")
				.pathMatchers(HttpMethod.PUT,"/users/updateUser").hasAnyRole("ADMIN","OWNER")
				.pathMatchers(HttpMethod.POST, "/users/newUser").hasAnyRole("ADMIN","OWNER")
				.pathMatchers(HttpMethod.POST,"/users/**").hasRole("OWNER")
				.pathMatchers(HttpMethod.PUT,"/users/**").hasRole("OWNER")
				.pathMatchers(HttpMethod.DELETE,"/users/**").hasRole("OWNER")
				
				.pathMatchers(HttpMethod.GET, "/bank-accounts/email").hasRole("USER")
				.pathMatchers(HttpMethod.POST,"/bank-accounts/create").hasRole("ADMIN")
				.pathMatchers(HttpMethod.PUT,"/bank-accounts/update").hasRole("ADMIN")
				.pathMatchers(HttpMethod.GET,"/bank-accounts").hasRole("ADMIN")
				
				.pathMatchers(HttpMethod.GET, "/crypto-wallets/email").hasRole("USER")
				.pathMatchers(HttpMethod.POST,"/crypto-wallets/create").hasRole("ADMIN")
				.pathMatchers(HttpMethod.PUT,"/crypto-wallets/update").hasRole("ADMIN")
				.pathMatchers(HttpMethod.GET,"/crypto-wallets").hasRole("ADMIN")
				
				.pathMatchers("/crypto-conversion").hasRole("USER")
				.pathMatchers("/crypto-exchange").hasAnyRole("ADMIN","OWNER","USER")
				.pathMatchers("/trade-service").hasRole("USER")
				).httpBasic(Customizer.withDefaults());
		
		http.addFilterAfter(userHeadersFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
		
		return http.build();
	}
	
	@Bean
	public WebFilter userHeadersFilter() {
		ServerWebExchangeMatcher matcher = ServerWebExchangeMatchers.matchers(
			ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/users/newUser"),
			ServerWebExchangeMatchers.pathMatchers(HttpMethod.PUT,  "/users"),
			ServerWebExchangeMatchers.pathMatchers(HttpMethod.PUT,  "/users/**"),
			ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,  "/bank-accounts/**"),
			ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,  "/currency-conversion"),
			ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,  "/crypto-wallets/**"),
			ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,  "/crypto-conversion"),
			ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,  "/trade-service")
		);

		return (ServerWebExchange exchange, WebFilterChain chain) ->
			matcher.matches(exchange).flatMap(result -> {
				if (!result.isMatch()) return chain.filter(exchange);

				return exchange.getPrincipal()
					.cast(org.springframework.security.core.Authentication.class)
					.flatMap(auth -> {
						String email = auth.getName();
						String role  = auth.getAuthorities().stream()
							.map(GrantedAuthority::getAuthority)
							.findFirst().orElse("");

						var mutatedReq = exchange.getRequest().mutate()
							.header("X-User-Email", email)
							.header("X-User-Role",  role)
							.build();
						
						System.out.println("[GW] X-User-Email=" + email + ", X-User-Role=" + role);

						return chain.filter(exchange.mutate().request(mutatedReq).build());
					})
					.switchIfEmpty(chain.filter(exchange));
			});
	}

	@Bean
	ReactiveUserDetailsService reactiveUserDetailsService(WebClient.Builder webClientBuilder, BCryptPasswordEncoder encoder) {
	    WebClient client = webClientBuilder.baseUrl("http://localhost:8770").build();
		//WebClient client = webClientBuilder.baseUrl("http://users-service:8770").build();
	    
	    return user -> client.get()
	            .uri(uriBuilder -> uriBuilder
	                    .path("/users/email")
	                    .queryParam("email", user)
	                    .build()
	            )
	            .retrieve()
	            .onStatus(s -> s.value() == 404,
	                      resp -> reactor.core.publisher.Mono.error(
	                              new org.springframework.security.authentication.BadCredentialsException("User not found")))
	            .bodyToMono(UserDto.class)
	            .map(dto -> User.withUsername(dto.getEmail())
	                    .password(encoder.encode(dto.getPassword()))
	                    .roles(dto.getRole())
	                    .build()
	            );
	}
	
	@Bean
	BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}

}
