package bankAccount;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface BankAccountRepository extends JpaRepository<BankAccountModel, Integer> {

	BankAccountModel findByEmail(String email);

	@Modifying
	@Transactional
	void deleteByEmail(String email);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query("""
			    UPDATE BankAccountModel b
			       SET b.rsdAmount = ?2,
			           b.eurAmount = ?3,
			           b.usdAmount = ?4,
			           b.chfAmount = ?5,
			           b.gbpAmount = ?6
			     WHERE b.email = ?1
			""")
	void updateBankAccount(String email, BigDecimal rsd, BigDecimal eur, BigDecimal usd, BigDecimal chf,
			BigDecimal gbp);
}
