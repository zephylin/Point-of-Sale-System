package POS_PD;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Representation of a Check
 * @author Zephylin D.
 * @version 1.0
 */
public class Check extends AuthorizedPayment {

	/**
	 * Routing number of the check
	 */
	private String routingNumber;
	/**
	 * Account number on the check to be credited
	 */
	private String accountNumber;
	/**
	 * Check identification number
	 */
	private String checkNumber;

	
	public String getRoutingNumber() {
		return routingNumber;
	}

	public void setRoutingNumber(String routingNumber) {
		this.routingNumber = routingNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	/**
	 * Default constructor
	 */
	public Check() {
		this.setAmount(new BigDecimal(0));
		routingNumber = "";
		accountNumber = "";
		checkNumber = "";
	}

	/**
	 * Constructor that will initialize amount, account number and check number
	 * @param amount Amount to be credited
	 * @param accountNumber Account number of the check owner
	 * @param checkNumber Check unique number
	 */
	public Check(String amount,String amtTendered,  String accountNumber, String checkNumber) {
		this();
		this.setAmount(new BigDecimal(amount));
		this.setAmtTendered(new BigDecimal(amtTendered));
		this.accountNumber = accountNumber;
		this.checkNumber = checkNumber;
	}

	/**
	 * Checks whether the check is authorized
	 * @return True if the check payment is authorized otherwise false
	 */
	public Boolean isAuthorized() {
		return true;
//		Random rand = new Random();
//		Boolean result = true;
//		
//		if(rand.nextInt(100) + 1 > 85)
//		{
//			result = false;
//		}
//		
//		return result
	}

	/**
	 * Makes string representation for a Check instance
	 * @return String representation for a Check instance
	 */
	public String toString() {
		return new String("\n Amount: " + this.getAmount() 
				+  "\nRouting #: " + routingNumber 
				+ "\nAccount #: " + accountNumber 
				+ "\nCheck #: " + checkNumber);
	}

}