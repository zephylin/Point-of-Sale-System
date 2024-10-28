package POS_PD;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

/**
 * Representation of a Price
 * @author Zephylin D.
 * @version 1.0
 */
public class Price implements Comparable<Price> {

	/**
	 * Price for the item
	 */
	private BigDecimal price;
	/**
	 * Date the price will be effective
	 */
	private LocalDate effectiveDate;
	/**
	 * Item that price is referring to
	 */
	private Item item;

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * Default constructor
	 */
	public Price() {
		price = new BigDecimal(0);
		effectiveDate = LocalDate.parse("1/1/1111", DateTimeFormatter.ofPattern("M/d/yyyy"));
	}

	/**
	 * Constructor that will initialize price and effective date
	 * @param price Price of the item
	 * @param effectiveDate Date the price will take effect
	 */
	public Price(String price, String effectiveDate) {
		this();
		this.price = new BigDecimal(price);
		this.effectiveDate = LocalDate.parse(effectiveDate, DateTimeFormatter.ofPattern("M/d/yy"));
	}

	/**
	 * Checks whether the date is effective or not
	 * @param date Given date to check whether the price is effective or not
	 * @return True if it's effective and false otherwise
	 */
	public Boolean isEffective(LocalDate date) {

		return this.effectiveDate.isBefore(date) || this.effectiveDate.equals(date);
	}

	/**
	 * Calculates amount for given quantity of item
	 * @param quantity Quantity of item
	 * @return Amount for given quantity of item
	 */
	public BigDecimal calcAmountForQty(int quantity) {
		BigDecimal result;
		
		result = price.multiply(new BigDecimal(quantity));
		
		return result;
	}

	/**
	 * Compares given date with effective date
	 * @param price Price on given date to be compared to the effective date
	 * @return 1 if given date>effective date, -1 if it's less than effective date and 0 if they are equal
	 */
	public int compareTo(Price price) {
		return this.effectiveDate.compareTo(price.effectiveDate);
	}

	/**
	 * Makes string representation for Price instance
	 * @return String representation for Price instance
	 */
	public String toString() {
		return new String(price.toString() + " " 
				+ effectiveDate.format(DateTimeFormatter.ofPattern("M/d/yyyy")));
	}

}