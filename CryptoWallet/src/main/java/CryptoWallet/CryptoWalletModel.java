package CryptoWallet;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "crypto_wallet")
public class CryptoWalletModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(unique = true)
	private String email;

	@Column(precision=19, scale=8)
	private BigDecimal btcAmount;

	@Column(precision=19, scale=8)
	private BigDecimal ethAmount;

	@Column(precision=19, scale=8)
	private BigDecimal ltcAmount;

	private CryptoWalletModel() {
	}

	public CryptoWalletModel(String email, BigDecimal btcAmount, BigDecimal ethAmount, BigDecimal ltcAmount) {
		this.email = email;
		this.btcAmount = btcAmount;
		this.ethAmount = ethAmount;
		this.ltcAmount = ltcAmount;
	}

	public CryptoWalletModel(int id, String email, BigDecimal btcAmount, BigDecimal ethAmount, BigDecimal ltcAmount) {
		this.id = id;
		this.email = email;
		this.btcAmount = btcAmount;
		this.ethAmount = ethAmount;
		this.ltcAmount = ltcAmount;
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

	public BigDecimal getBtcAmount() {
		return btcAmount;
	}

	public void setBtcAmount(BigDecimal btcAmount) {
		this.btcAmount = btcAmount;
	}

	public BigDecimal getEthAmount() {
		return ethAmount;
	}

	public void setEthAmount(BigDecimal ethAmount) {
		this.ethAmount = ethAmount;
	}

	public BigDecimal getLtcAmount() {
		return ltcAmount;
	}

	public void setLtcAmount(BigDecimal ltcAmount) {
		this.ltcAmount = ltcAmount;
	}

}
