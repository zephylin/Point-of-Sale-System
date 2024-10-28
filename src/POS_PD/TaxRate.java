package POS_PD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a TaxRate
 * @author Zephylin D.
 * @version 1.0
 */
public class TaxRate implements Comparable<TaxRate> {

	/**
	 * TaxRate
	 */
	private BigDecimal taxRate;
	/**
	 * The date the tax rate will be effective
	 */
	private LocalDate effectiveDate;

	
	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	/**
	 * Default constructor
	 */
	public TaxRate() {
		taxRate = new BigDecimal(0);
		effectiveDate = LocalDate.parse("1/1/1111", DateTimeFormatter.ofPattern("M/d/yyyy"));
	}


	public TaxRate(String effectiveDate, String rate)
	{
		taxRate = new BigDecimal(rate);
		this.effectiveDate = LocalDate.parse(effectiveDate, DateTimeFormatter.ofPattern("M/d/yy"));
	}
	
	/**
	 * Constructor that initializes effective date and tax rate
	 * @param effectiveDate Effective date for given tax rate
	 * @param rate Corresponding tax rate
	 */
	public TaxRate(LocalDate effectiveDate, BigDecimal rate) {
		taxRate = rate;
		this.effectiveDate = effectiveDate;
	}

	/**
	 * true, if tax rate is effective, otherwise, false.
	 * @param date Date to determine whether tax rate is effective
	 */
	public boolean isEffective(LocalDate date) {
		return date.isAfter(this.effectiveDate);
	}

	/**
	 * Compares two tax rates
	 * @param taxRate Tax rate to compare with this.taxRate
	 * @return 0 if they are equal, 1 if given tax rate is greater than this.taxRate, -1 if it's less than this.taxRate
	 */
	public int compareTo(TaxRate taxRate) {
		return this.effectiveDate.compareTo(taxRate.effectiveDate);
		//return taxRate.getTaxRate().compareTo(this.getTaxRate());
	}

	/**
	 * Makes string text to represent taxRate instance
	 * @return String representation of taxRate instance
	 */
	public String toString() {
		return new String(taxRate.toString() + " " + effectiveDate.format(DateTimeFormatter.ofPattern("M/d/yyyy")));
	}

}