package CryptoExchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"CryptoExchange","util.exceptions"})
public class CryptoExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoExchangeApplication.class, args);
	}

}
