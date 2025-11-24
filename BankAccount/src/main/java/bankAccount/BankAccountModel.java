package bankAccount;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bank_account")
public class BankAccountModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(unique=true)
	private String email;

	private BigDecimal rsdAmount;
	
	private BigDecimal eurAmount;

	private BigDecimal usdAmount;

	private BigDecimal chfAmount;

	private BigDecimal gbpAmount;
	
	public BankAccountModel() {}
	
	public BankAccountModel(int id,
            String email,
            BigDecimal rsdAmount,
            BigDecimal eurAmount,
            BigDecimal usdAmount,
            BigDecimal chfAmount,
            BigDecimal gbpAmount) {
		this.id = id;
        this.email = email;
        this.rsdAmount = rsdAmount;
        this.eurAmount = eurAmount;
        this.usdAmount = usdAmount;
        this.chfAmount = chfAmount;
        this.gbpAmount = gbpAmount;
	}
	
	public BankAccountModel(String email,
            BigDecimal rsdAmount,
            BigDecimal eurAmount,
            BigDecimal usdAmount,
            BigDecimal chfAmount,
            BigDecimal gbpAmount) {
		this.email = email;
        this.rsdAmount = rsdAmount;
        this.eurAmount = eurAmount;
        this.usdAmount = usdAmount;
        this.chfAmount = chfAmount;
        this.gbpAmount = gbpAmount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getRsdAmount() {
		return rsdAmount;
	}

	public void setRsdAmount(BigDecimal rsdAmount) {
		this.rsdAmount = rsdAmount;
	}

	public BigDecimal getEurAmount() {
		return eurAmount;
	}

	public void setEurAmount(BigDecimal eurAmount) {
		this.eurAmount = eurAmount;
	}

	public BigDecimal getUsdAmount() {
		return usdAmount;
	}

	public void setUsdAmount(BigDecimal usdAmount) {
		this.usdAmount = usdAmount;
	}

	public BigDecimal getChfAmount() {
		return chfAmount;
	}

	public void setChfAmount(BigDecimal chfAmount) {
		this.chfAmount = chfAmount;
	}

	public BigDecimal getGbpAmount() {
		return gbpAmount;
	}

	public void setGbpAmount(BigDecimal gbpAmount) {
		this.gbpAmount = gbpAmount;
	}

	
}
