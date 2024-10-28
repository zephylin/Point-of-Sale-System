package POS_PD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class represents TaxCategory
 * @author Zephylin D.
 * @version 1.0
 */
public class TaxCategory {

	/**
	 * Category name
	 */
	private String category;
	/**
	 * Collection of TaxRates
	 */
	private TreeSet<TaxRate> taxRates;
	/**
	 * Collection of items
	 */
	private TreeMap<String, Item> items;

	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public TreeSet<TaxRate> getTaxRates() {
		return taxRates;
	}

	public void setTaxRates(TreeSet<TaxRate> taxRates) {
		this.taxRates = taxRates;
	}

	public TreeMap<String, Item> getItems() {
		return items;
	}

	public void setItems(TreeMap<String, Item> items) {
		this.items = items;
	}

	/**
	 * Default constructor for taxCategory class
	 */
	public TaxCategory() {
		category = "";
		taxRates = new TreeSet<TaxRate>();
	}

	/**
	 * Constructor for taxCategory that sets category
	 * @param category tax Category name
	 */
	public TaxCategory(String category) {
		this();
		this.category = category;
	}

	/**
	 * Gets the taxRate for the specified date
	 * @param date Date to determine taxRate
	 * @return taxRate for given date
	 */
	public BigDecimal getTaxRateforDate(LocalDate date) {
		BigDecimal result = new BigDecimal(0);
		
		for(TaxRate taxRate : taxRates)
		{
			if(taxRate.isEffective(date))
				result = taxRate.getTaxRate();
		}
		
		return result;
	}

	/**
	 * Adds new tax rate to the collection of tax Rates
	 * @param taxRate Tax Rate to be added to the collection of tax rates
	 */
	public void addTaxRate(TaxRate taxRate) {
		taxRates.add(taxRate);
	}

	/**
	 * Removes a tax rate from the collection of tax rates
	 * @param taxRate tax rate to be removed from the collection of tax rates
	 */
	public void removeTaxRate(TaxRate taxRate) {
		taxRates.remove(taxRate);
	}

	/**
	 * Makes a string representation of tax category
	 * @return String representation of tax category instance
	 */
	public String toString() {
		return new String(category);
	}

}