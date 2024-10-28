package POS_PD;

import java.math.BigDecimal;

/**
 * Representation of cash
 * @author Zephylin D.
 * @version 1.0
 */
public class Cash extends Payment {

	/**
	 * Default constructor
	 */
	public Cash() {
		this.setAmount(new BigDecimal(0));
		this.setAmtTendered(new BigDecimal(0));
	}

	/**
	 * Constructor that will set amount an amount tendered
	 * @param amount Total amount to be paid from a sale
	 * @param amtTendered Amount paid during a sale
	 */
	public Cash(String amount, BigDecimal amtTendered) {
		this.setAmount(new BigDecimal(amount));
		this.setAmtTendered(amtTendered);
	}
	
	public Cash(String amount, String amtTendered) 
	{
		this.setAmount(new BigDecimal(amount));
		this.setAmtTendered(new BigDecimal(amtTendered));
	}

	/**
	 * Checks whether the payment was cash or not
	 * @return True if the payment was cash otherwise false
	 */
	public Boolean countAsCash() {
		return true;
	}

	/**
	 * Makes string representation for a cash instance
	 * @return String representation for cash instance
	 */
	public String toString() {
		return new String(getAmtTendered().toString() + " out of " + getAmount().toString());
	}

}