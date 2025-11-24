package CryptoWallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"CryptoWallet","util.exceptions"})
@EnableFeignClients(basePackages = {"api.proxies"})
public class CryptoWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoWalletApplication.class, args);
	}

}
