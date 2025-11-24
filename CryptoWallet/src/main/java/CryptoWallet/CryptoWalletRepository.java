package CryptoWallet;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import jakarta.transaction.Transactional;

public interface CryptoWalletRepository extends JpaRepository<CryptoWalletModel, Integer>{

	CryptoWalletModel findByEmail(String email);

	@Modifying
	@Transactional
	@Query("DELETE FROM CryptoWalletModel c WHERE c.email = :email")
	int deleteByEmail(@org.springframework.data.repository.query.Param("email") String email);
	
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query("""
			    UPDATE CryptoWalletModel c
			       SET c.btcAmount = ?2,
			           c.ethAmount = ?3,
			           c.ltcAmount = ?4
			     WHERE c.email = ?1
			""")
	void updateCryptoWallet(String email, BigDecimal btc, BigDecimal eth, BigDecimal ltc);
}
