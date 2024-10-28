package POS_PD;

import java.math.BigDecimal;

/**
 * Representation of a payment
 * @author Zephylin D.
 * @version 1.0
 */
public abstract class Payment {

	/**
	 * Total amount from a sale
	 */
	private BigDecimal amount;
	/**
	 * Amount of money paid during a sale
	 */
	private BigDecimal amtTendered;
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getAmtTendered() {
		return amtTendered;
	}
	public void setAmtTendered(BigDecimal amtTendered) {
		this.amtTendered = amtTendered;
	}
	
    public abstract Boolean countAsCash();
	
	

}