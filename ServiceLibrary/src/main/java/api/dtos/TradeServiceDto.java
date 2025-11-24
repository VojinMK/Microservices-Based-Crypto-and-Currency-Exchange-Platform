package api.dtos;

public class TradeServiceDto {

	private String message;
	private BankAccountDto bankAccount;
	private CryptoWalletDto cryptoWallet;
	
	private TradeServiceDto() {}
	
	public TradeServiceDto(String message, BankAccountDto bankAccount) {
		this.message=message;
		this.bankAccount=bankAccount;
	}
	
	public TradeServiceDto(String message, CryptoWalletDto cryptoWallet) {
		this.message=message;
		this.cryptoWallet=cryptoWallet;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BankAccountDto getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccountDto bankAccount) {
		this.bankAccount = bankAccount;
	}

	public CryptoWalletDto getCryptoWallet() {
		return cryptoWallet;
	}

	public void setCryptoWallet(CryptoWalletDto cryptoWallet) {
		this.cryptoWallet = cryptoWallet;
	}
	
	
}
