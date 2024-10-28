package POS_PD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representation of a PromoPrice
 * @author Zephylin D.
 * @version 1.0
 */
public class PromoPrice extends Price {

	/**
	 * Date at which the promotion price will end
	 */
	private LocalDate endDate;

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	/**
	 * Default constructor
	 */
	public PromoPrice() {
		super();
		endDate = LocalDate.parse("1/1/1111", DateTimeFormatter.ofPattern("M/d/yyyy"));
	}

	/**
	 * Constructor that will initialize price, effective date and promotion end date
	 * @param price Price of the item
	 * @param effectiveDate Effective date the price will be in effect
	 * @param endDate End date for promotion price
	 */
	public PromoPrice(String price, String effectiveDate, String endDate) {
		this();
		this.setPrice(new BigDecimal(price));
		this.setEffectiveDate(LocalDate.parse(effectiveDate, DateTimeFormatter.ofPattern("M/d/yy"))); 
		this.endDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("M/d/yy"));
	}

	/**
	 * Checks whether the given date is effective
	 * @param date Date to check if it effective
	 * @return True if the date is effective otherwise false
	 */
	public Boolean isEffective(LocalDate date) {
		
		return this.getEffectiveDate().isBefore(date) || this.getEffectiveDate().equals(date) && this.getEndDate().isAfter(date);
	}

	/**
	 * Makes string representation for PromoPrice instance
	 * @return String representation for PromoPrice instance
	 */
	public String toString() {
		return new String(getPrice().toString() + " " 
				+ getEffectiveDate().format(DateTimeFormatter.ofPattern("M/d/yyyy")) + " " 
				+ endDate.format(DateTimeFormatter.ofPattern("M/d/yyyy")));
	}

}