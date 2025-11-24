package api.dtos;

import java.math.BigDecimal;

public class CryptoWalletDto {
	
	private String email;

	private BigDecimal btcAmount;

	private BigDecimal ethAmount;

	private BigDecimal ltcAmount;
	
	public CryptoWalletDto(String email, BigDecimal btcAmount, BigDecimal ethAmount, BigDecimal ltcAmount) {
		this.email = email;
		this.btcAmount = btcAmount;
		this.ethAmount = ethAmount;
		this.ltcAmount = ltcAmount;
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
