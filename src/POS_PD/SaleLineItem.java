package POS_PD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;


/**
 * Representation of a SaleLineItem
 * @author Zephylin D.
 * @version 1.0
 */
public class SaleLineItem {

	/**
	 * Quantity of the item that saleLineItem knows
	 */
	private int quantity;
	/**
	 * Sale that saleLineItem knows
	 */
	private Sale sale;
	/**
	 * Item that saleLineItem knows
	 */
	private Item item;

	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
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
	public SaleLineItem() {
		item = new Item();
		sale = new Sale();
	}

	/**
	 * Constructor that will initialize sale, item and quantity
	 * @param sale Sale associated with saleLineItem
	 * @param item Item associated with saleLineItem
	 * @param quantity Quantity of items for saleLineItem
	 */
	public SaleLineItem(Sale sale, Item item, String quantity) {
		this.quantity=Integer.valueOf(quantity); //new Integer(quantity)
		setItem(item);
		setSale(sale);
		sale.addSaleLineItem(this);
	}
	
	public SaleLineItem(String itemNumber, String quantity, Store store) 
	{
		this();
		this.item = store.findItemForNumber(itemNumber);
		this.quantity = new Integer(quantity);
	}

	/**
	 * Calculates total of all items in the receipt without adding tax
	 * @return Subtotal of all items
	 */
	public BigDecimal calcSubTotal() {
		return item.calcAmountForDateQty(sale.getDateTime().toLocalDate(), quantity);
	}

	/**
	 * Calculates tax for all items in the receipt
	 * @return Total tax for the items
	 */
	public BigDecimal calcTax() {
		return this.calcSubTotal().multiply(item.getTaxRateForDate(sale.getDateTime().toLocalDate()));
	}

	/**
	 * Makes string representation for SaleLineItem instance
	 * @return String representation for SaleLineItem instance
	 */
	public String toString() {
		DateTimeFormatter formatter= DateTimeFormatter.ofPattern("EEE, MM-dd-yyyy HH:mm");
		//String result = new String("\nSale \nDate and Time of Sale: " + this.getDateTime().format(formatter));
		return new String("  -"+item.getNumber() + "  "
				+ item.getDescription() + " "
				+ quantity + "@" 
				+ String.format("$%.2f", item.getPriceForDate(sale.getDateTime().toLocalDate()).getPrice()) + " " 
				+ sale.getDateTime().format(formatter)+ " "
				+ String.format("$%.2f",calcSubTotal().setScale(2,RoundingMode.HALF_UP))+" "
				+ calcTax().setScale(2,RoundingMode.HALF_UP));
	}

}