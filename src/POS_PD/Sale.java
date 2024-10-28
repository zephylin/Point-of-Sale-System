package POS_PD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


/**
 * Represents a sale
 * @author Zephylin D.
 * @version 1.0
 */
public class Sale {

	/**
	 * Specifies if sale was tax free or not
	 */
	private Boolean taxFree;
	/**
	 * Collection of payments associated with the sale
	 */
	private ArrayList<Payment> payments;
	/**
	 * Collection of saleLineItems known by the sale
	 * It can be used to generate receipts
	 */
	private ArrayList<SaleLineItem> saleLineItems;
	/**
	 * Date the sale happened
	 */
	private LocalDateTime dateTime;

	
	public Boolean getTaxFree() {
		return taxFree;
	}

	public void setTaxFree(Boolean taxFree) {
		this.taxFree = taxFree;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public ArrayList<Payment> getPayments() {
		return payments;
	}

	public void setPayments(ArrayList<Payment> payments) {
		this.payments = payments;
	}

	public ArrayList<SaleLineItem> getSaleLineItems() {
		return saleLineItems;
	}

	public void setSaleLineItems(ArrayList<SaleLineItem> saleLineItems) {
		this.saleLineItems = saleLineItems;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * Default constructor
	 */
	public Sale() {
		this.dateTime = LocalDateTime.now();
		payments = new ArrayList<Payment>();
		saleLineItems = new ArrayList<SaleLineItem>();
	}

	/**
	 * Constructor for sale that sets taxFree
	 * @param taxFree
	 */
	
	public Sale(String taxFree) {
		this();
		this.taxFree=Boolean.valueOf(taxFree);
	}

	/**
	 * Adds payment to the collection of payments
	 * @param payment Payment to be added to the collection of payments
	 */
	public void addPayment(Payment payment) {
		payments.add(payment);
	}

	/**
	 * Removes payment from payments collection
	 * @param payment Payment to be removed from payments collection
	 */
	public void removePayment(Payment payment) {
		payments.remove(payment);
	}

	/**
	 * Adds saleLineItems to the collection of saleLineItems
	 * @param sli saleLineItem to be added to the collection of SLI
	 */
	public void addSaleLineItem(SaleLineItem sli) {
		saleLineItems.add(sli);
	}

	/**
	 * Removes saleLineItem from the collection of saleLineItems
	 * @param sli saleLineItem to be removed from the collection of SLI
	 */
	public void removeSaleLineItem(SaleLineItem sli) {
		saleLineItems.remove(sli);
	}
	/**
	 * Calculates total sale amount sub-total plus tax
	 * @return Total sale amount
	 */
	public BigDecimal calcTotal() {
		return calcSubTotal().add(calcTax().setScale(2, RoundingMode.HALF_UP));
	}

	/**
	 * Calculates sub total amount without tax
	 * @return Sub total without adding tax
	 */
	public BigDecimal calcSubTotal() {
		BigDecimal subTotal = new BigDecimal(0);
		for(SaleLineItem saleLineItem : saleLineItems)
		{
			subTotal = subTotal.add(saleLineItem.calcSubTotal());
		}
		
		return subTotal;
	}

	/**
	 * Calculates tax for a sale
	 * @return Tax for a sale
	 */
	public BigDecimal calcTax() {
		BigDecimal tax = new BigDecimal(0);
		if(!taxFree)
		{
			for(SaleLineItem saleLineItem : saleLineItems)
			{
				tax = tax.add(saleLineItem.calcTax().setScale(2, RoundingMode.HALF_UP));
			}
		}
		
		return tax;
	}

	/**
	 * Gets total payments made by customer for corresponding sale
	 * @return Total payments for a sale
	 */
	public BigDecimal getTotalPayments() {
		BigDecimal totalPayment = new BigDecimal(0);
		
		for(Payment p : payments)
		{
			totalPayment = totalPayment.add(p.getAmtTendered());
		}
		
		//return result.setScale(2, BigDecimal.ROUND_HALF_UP);
		return totalPayment.setScale(2,RoundingMode.HALF_UP);
	}

	/**
	 * Checks whether the payment is enough
	 * @return true if payment is enough otherwise false.
	 */
	public Boolean isPaymentEnough() {
		if(this.getTotalPayments().compareTo(this.calcTotal())>=0) {
			return true;
		}
		
		return false;
	}

	/**
	 * Calculates amount to be paid for a sale
	 * @param amtTendered
	 * @return Amount to be paid for a sale
	 */
	public BigDecimal calcAmountForPayment(BigDecimal amtTendered) {
		// TODO - implement Sale.calcAmountForPayment
		throw new UnsupportedOperationException();
	}

	/**
	 * Calculates amount owed to the customer
	 * @return Amount to be given back to the customer
	 */
	public BigDecimal calcChange() {
		BigDecimal result = new BigDecimal(0);
		result = getTotalPayments().subtract(calcTotal());
	
		return result;
	}

	/**
	 * Calculates amount of money given by a customer in a Sale
	 * @return Amount tendered during a sale
	 */
	public BigDecimal calcAmtTendered() {
		BigDecimal result = new BigDecimal(0);
		
		for(Payment payment : payments)
		{
			result = result.add(payment.getAmtTendered());
		}
		
		return result;
	}

	/**
	 * Makes string representation of a Sale instance
	 * @return String representation of a sale
	 */
	public String toString() {
		DateTimeFormatter formatter= DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		//String result = new String("\nSale \nDate and Time of Sale: " + this.getDateTime().format(formatter));
		String result = new String("\n  Sale:");
		result += "\tSubTotal=" + this.calcSubTotal().toString() 
				+ "\tTax=" + this.calcTax().toString()
				+ "\tTotal=" + this.calcTotal().toString()
				+ "\tPayment=" + this.getTotalPayments().toString() 
				+ "\tChange=" + this.calcChange().toString();
		for(SaleLineItem s : saleLineItems)
		{
			result +="\n  " + s.toString();
		}
		
		
		
		
		
		return result;
	}

}