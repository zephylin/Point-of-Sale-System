package POS_PD;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Represents an item
 * @author Zephlylin D.
 * @version 1.0
 */
public class Item {

	/**
	 * Item number determined by the store
	 */
	private String number;
	/**
	 * Item description
	 */
	private String description;
	/**
	 * Collection of tax category known by the item
	 */
	private TaxCategory taxCategory;
	/**
	 * Collection of UPCs known by item
	 */
	private TreeMap<String, UPC> uPCs;
	/**
	 * Collection of prices known by the item
	 */
	private TreeSet<Price> prices;
	/**
	 * Collection of saleLineItems known by the item
	 */
	private ArrayList<SaleLineItem> saleLineItems;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TreeMap<String, UPC> getuPCs() {
		return uPCs;
	}

	
	public TaxCategory getTaxCategory() {
		return taxCategory;
	}

	public void setTaxCategory(TaxCategory taxCategory) {
		this.taxCategory=taxCategory;
	}
	
	public void setTaxCategory(String taxCategory, Store store)
	{
		this.taxCategory = store.getTaxCategory(taxCategory);
	}

	public TreeSet<Price> getPrices() {
		return prices;
	}

	public void setPrices(TreeSet<Price> prices) {
		this.prices = prices;
	}

	public ArrayList<SaleLineItem> getSaleLineItems() {
		return saleLineItems;
	}

	public void setSaleLineItems(ArrayList<SaleLineItem> saleLineItems) {
		this.saleLineItems = saleLineItems;
	}

	public void setuPCs(TreeMap<String, UPC> uPCs) {
		this.uPCs = uPCs;
	}

	/**
	 * Default constructor
	 */
	public Item() {
		taxCategory = new TaxCategory("");
		prices = new TreeSet<Price>();
		uPCs = new TreeMap<String, UPC>();
	}

	/**
	 * Constructor that sets item number and description
	 * @param number Item number
	 * @param description Item description
	 */
	public Item(String number, String description) {
		this();
		this.number = number;
		this.description = description;
	}

	/**
	 * Adds new price to the price collection
	 * @param price Price to be added to the price collection
	 */
	public void addPrice(Price price) {
		price.setItem(this);
		prices.add(price);
	}

	/**
	 * Removes price from price collection
	 * @param price Price to be removed from price collection
	 */
	public void removePrice(Price price) {
		prices.remove(price);
	}

	/**
	 * Adds new upc to the UPCs collection
	 * @param upc upc to be added to the collection
	 */
	public void addUPC(UPC upc) {
		uPCs.put(upc.getuPC(), upc);
	}

	/**
	 * Removes upc from UPCs collection
	 * @param upc upc to be removed from the collection
	 */
	public void removeUPC(UPC upc) {
		uPCs.remove(upc.getuPC());
	}

	/**
	 * Gets the price of the item for a given date
	 * @param date Date used to find the price of item for that date
	 * @return Price of the item for the given date
	 */
	public Price getPriceForDate(LocalDate date) {
		Price result = null;
		for(Price price : prices)
		{
			if(price.isEffective(date))
				 result = price;
		}
		
		return result;
	}

	/**
	 * Gets the taxRate for a given date
	 * @param date Given date to find its taxRate
	 * @return taxRate for that date
	 */
	public BigDecimal getTaxRateForDate(LocalDate date) {
		return getTaxCategory().getTaxRateforDate(date);
	}

	/**
	 * Calculates amount to charge for quantity of item for a given date
	 * @param date Date to be used to check for price
	 * @param quantity Quantity of item
	 * @return Amount to charge for the items
	 */
	public BigDecimal calcAmountForDateQty(LocalDate date, int quantity) {
		return getPriceForDate(date).calcAmountForQty(quantity);
	}

	/**
	 * Adds new saleLineItems to the collection of saleLineItems
	 * @param saleLineItem Sale Line Item to be added to the collection
	 */
	public void addSaleLineItem(SaleLineItem saleLineItem) {
		saleLineItems.add(saleLineItem);
	}

	/**
	 * Removes Sale Line Item from collection of SLI
	 * @param saleLineItem Sale Line Item to be removed from SLI collection
	 */
	public void removeSaleLineItem(SaleLineItem saleLineItem) {
		saleLineItems.remove(saleLineItem);
	}
	
	/**
	 * Makes string representation for Item instance
	 * @return String representation for Item instance
	 */
	public String toString() {
		return new String(number + " " + description + " " 
			+ "Price: "+"$"+this.getPriceForDate(LocalDate.now()).getPrice()+" "
			+ "Tax Rate: "+taxCategory.getTaxRateforDate(LocalDate.now())+" "
			+ this.getuPCs().firstKey());
	}

}