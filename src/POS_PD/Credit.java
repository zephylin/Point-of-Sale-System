package POS_PD;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Representation of a Credit card
 * @author Zephylin D.
 * @version 1.0
 */
public class Credit extends AuthorizedPayment {

	/**
	 * Type of the card making payment
	 */
	private String cardType;
	/**
	 * Account number to be credited
	 */
	private String acctNumber;
	/**
	 * Card Expire date
	 */
	private LocalDate expireDate;

	
	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getAcctNumber() {
		return acctNumber;
	}

	public void setAcctNumber(String acctNumber) {
		this.acctNumber = acctNumber;
	}

	public LocalDate getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(LocalDate expireDate) {
		this.expireDate = expireDate;
	}

	/**
	 * Default constructor
	 */
	public Credit() {
		cardType = "";
		acctNumber = "";
		expireDate = LocalDate.parse("1/1/1111", DateTimeFormatter.ofPattern("M/d/yyyy"));
	}

	/**
	 * Constructor that will initialize card type, account number and expire date
	 * @param amount
	 * @param cardType
	 * @param accNumber
	 * @param expireDate
	 */
	public Credit(String amount, String cardType, String acctNumber, String expireDate) {
		this();
		this.cardType = cardType;
		this.acctNumber = acctNumber;
		this.expireDate = LocalDate.parse(expireDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
	}

	/**
	 * Check whether the credit card payment is authorized
	 * @return True if it's authorized otherwise false
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
//		return result;
	}

	/**
	 * Makes string representation for a Credit instance
	 * @return String representation for Credit instance
	 */
	public String toString() {
		return new String(" Amount: "+ getAmount()
				+"\n Account Number: "+ getAcctNumber()
				+"\n Expire date: "+getExpireDate());
	}

}