package api.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BankAccountDto {

	private String email;
	
	private BigDecimal rsdAmount;
	
	private BigDecimal eurAmount;

	private BigDecimal usdAmount;

	private BigDecimal chfAmount;

	private BigDecimal gbpAmount;
	
	public BankAccountDto(String email, BigDecimal rsdAmount, BigDecimal eurAmount, BigDecimal usdAmount, BigDecimal chfAmount, BigDecimal gbpAmount) {
		this.email=email;
		this.rsdAmount=rsdAmount;
		this.eurAmount=eurAmount;
		this.usdAmount=usdAmount;
		this.chfAmount=chfAmount;
		this.gbpAmount=gbpAmount;
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
