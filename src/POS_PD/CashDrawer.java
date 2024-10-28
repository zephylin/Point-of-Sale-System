package POS_PD;

import java.math.BigDecimal;

/**
 * Represents CashDrawer held by a Register
 * @author Zephylin D.
 * @version 1.0
 */
public class CashDrawer {

	/**
	 * Cash in the drawer
	 */
	private BigDecimal cashAmount;
	/**
	 * Represents position of the drawer in the cash register system
	 */
	private int position;

	
	public BigDecimal getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Default Constructor
	 */
	public CashDrawer() {
		cashAmount = new BigDecimal(0);
		position = 0;
	}

	public CashDrawer(BigDecimal cash) {
		cashAmount = cash;
		
	}
	/**
	 * Removes cash from the drawer
	 * @param cash Amount of cash to be removed
	 */
	public void removeCash(BigDecimal cash) {
		cashAmount.subtract(cash);
	}

	/**
	 * Adds cash to the drawer
	 * @param cash Amount of cash to be added to the drawer
	 */
	public void addCash(BigDecimal cash) {
		cashAmount = cashAmount.add(cash);
	}

	/**
	 * Makes string representation for CashDrawer instance
	 * @return String representation of  CashDrawer
	 */
	public String toString() {
		return new String("\nCash in drawer: " + cashAmount.toString());
	}

}