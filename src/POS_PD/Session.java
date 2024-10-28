package POS_PD;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a Session
 * @author Zephylin D.
 * @version 1.0
 */
public class Session {

	/**
	 * Time session started
	 */
	private LocalDateTime startDateTime;
	/**
	 * Time session ended
	 */
	private LocalDateTime endDateTime;
	/**
	 * Cashier for corresponding session
	 */
	private Cashier cashier;
	/**
	 * Register used for that session
	 */
	private Register register;
	/**
	 * Collection of sales made in session
	 */
	private ArrayList<Sale> sales;
	
	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public Cashier getCashier() {
		return cashier;
	}

	public void setCashier(Cashier cashier) {
		this.cashier = cashier;
	}

	public Register getRegister() {
		return register;
	}

	public void setRegister(Register register) {
		this.register = register;
	}

	
	public ArrayList<Sale> getSales() {
		return sales;
	}

	public void setSales(ArrayList<Sale> sales) {
		this.sales = sales;
	}

	/**
	 * Default Constructor
	 */
	public Session() {
		startDateTime = LocalDateTime.now();
		sales = new ArrayList<Sale>();
	}

	/**
	 * Constructor to initialize cashier and register
	 * @param cashier
	 * @param register
	 */
	public Session(Cashier cashier, Register register) {
		this();
		this.cashier = cashier;
		this.register = register;
		this.cashier.addSession(this);
		this.register.addSession(this);
	}
	
	public Session(String cashier, String register, Store store)
	{
		this();
		this.cashier = store.findCashierForNumber(cashier);
		this.register = store.getRegisters().get(register);
		this.cashier.addSession(this);
		this.register.addSession(this);
	}

	/**
	 * Adds a sale to the collection of sales in session
	 * @param sale New sale to be added to the collection
	 */
	public void addSale(Sale sale) {
		sales.add(sale);
	}

	/**
	 * Removes sale from the collection of sales
	 * @param sale Sale to be removed from the collection of sales
	 */
	public void removeSale(Sale sale) {
		sales.remove(sale);
	}

	/**
	 * Calculates the difference between the current cash in the drawer and the initial amount that was there.
	 * @param cash Cash in the drawer at the beginning of the session
	 * @return Difference between total current cash in the drawer and the cash that was there in the beginning
	 */
	public BigDecimal calcCashCountDiff(BigDecimal cash) {
		return cash.subtract(register.getCashDrawer().getCashAmount());
	
	}

	/**
	 * Calculate total sales made in session
	 */
	public BigDecimal calcTotal() {
		BigDecimal total= new BigDecimal(0);
		for(Sale sale : sales) {
			total=total.add(sale.calcTotal());
		}
		
		return total;
	}

	/**
	 * Makes string representation for Session instance
	 * @return String representation of Session
	 */
	public String toString() {
		String result = new  String("Session: Cashier: " + cashier.getPerson().getName() + "  Register: " + register.getNumber()
		+"  Total: "+calcTotal());
		
		//result += "\nSession Start: " + startDateTime.format(DateTimeFormatter.ofPattern("M/d/yyyy"));
		
		for(Sale s : sales)
		{
			result +="  "+ s.toString();
		}
		
		return result+"\n\n";
	}

}