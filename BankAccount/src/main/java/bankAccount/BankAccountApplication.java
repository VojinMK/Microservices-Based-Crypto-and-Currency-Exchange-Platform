package bankAccount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"bankAccount", "util.exceptions"})
@EnableFeignClients(basePackages = {"api.proxies"})
public class BankAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankAccountApplication.class, args);
	}

}
