package POS_PD;

import java.util.ArrayList;

import java.util.TreeMap;


/**
 * This class represents our convenience store
 * @author Zephylin
 * @version 1.0
 */
public class Store {

	/**
	 * Number that identifies the store
	 */
	private String number;
	/**
	 * Name of the store
	 */
	private String name;
	/**
	 * Collection of TaxCategories known by the store
	 */
	private TreeMap<String, TaxCategory> taxCategories;
	/**
	 * Collection of all items sold by the store
	 */
	private TreeMap<String, Item> items;
	/**
	 * Collection of cashiers working in the store
	 */
	private TreeMap<String, Cashier> cashiers;
	/**
	 * Collection of all sessions occurred in the store
	 */
	private TreeMap<String, Register> registers;
	/**
	 * Collection  of sessions known by the store
	 */
	private ArrayList<Session> sessions;
	/**
	 * Collection of Universal Product Codes of every product in the store
	 */
	private TreeMap<String, UPC> upcs;
	
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public TaxCategory getTaxCategory(String taxCategory)
	{
		return taxCategories.get(taxCategory);
	}
	
	public TreeMap<String, TaxCategory> getTaxCategories() {
		return taxCategories;
	}

	public void setTaxCategories(TreeMap<String, TaxCategory> taxCategories) {
		this.taxCategories = taxCategories;
	}

	public TreeMap<String, Item> getItems() {
		return items;
	}

	public void setItems(TreeMap<String, Item> items) {
		this.items = items;
	}

	public TreeMap<String, Cashier> getCashiers() {
		return cashiers;
	}

	public void setCashiers(TreeMap<String, Cashier> cashiers) {
		this.cashiers = cashiers;
	}

	public TreeMap<String, Register> getRegisters() {
		return registers;
	}

	public void setRegisters(TreeMap<String, Register> registers) {
		this.registers = registers;
	}

	public ArrayList<Session> getSessions() {
		return sessions;
	}

	public void setSessions(ArrayList<Session> sessions) {
		this.sessions = sessions;
	}

	public TreeMap<String, UPC> getUpcs() {
		return upcs;
	}

	public void setUpcs(TreeMap<String, UPC> upcs) {
		this.upcs = upcs;
	}

	/**
	 * Default constructor for Store class
	 */
	public Store() {
		items = new TreeMap<String, Item>();
		upcs = new TreeMap<String, UPC>();
		taxCategories = new TreeMap<String, TaxCategory>();
		registers = new TreeMap<String, Register>();
		sessions = new ArrayList<Session>();
		cashiers = new TreeMap<String, Cashier>();
	}

	/**
	 * Constructor to initialize the number and name of the store
	 * @param number Number of the store
	 * @param name Name of the store
	 */
	public Store(String number, String name) {
		this();
		this.number = number;
		this.name = name;
	}

	/**
	 * Adds new item to the collection of items
	 * @param item New item to be added to the collection
	 */
	public void addItem(Item item) {
		items.put(item.getNumber(),item);
	}

	/**
	 * Removes an item from the collection of items
	 * @param item Represents an item to be removed
	 */
	public void removeItem(Item item) {
		items.remove(item.getNumber());
	}

	/**
	 * Adds new UPC to the collection of UPCs
	 * @param up UPC to be added to the collection of UPCs
	 */
	public void addUPC(UPC upc) {
		upcs.put(upc.getuPC(), upc);
	}

	/**
	 * Removes upc from the collection of UPCs
	 * @param upc UPC to be removed
	 */
	public void removeUPC(UPC upc) {
		upcs.remove(upc.getuPC());
	}

	/**
	 * Adds new register to the collection of registers owned by the store
	 * @param register New register to be added to the collection
	 */
	public void addRegister(Register register) {
		registers.put(register.getNumber(),register);
	}

	/**
	 * Removes register from the collection of registers
	 * @param register Register to be removed from the collection
	 */
	public void removeRegister(Register register) {
		registers.remove(register.getNumber());
	}

	/**
	 * Adds new cashier to the collection of cashiers working in the store
	 * @param cashier New Cashier to be added to the collection
	 */
	public void addCashier(Cashier cashier) {
		cashiers.put(cashier.getNumber(), cashier);
	}

	/**
	 * Removes cashier from the collection of cashiers working in the store
	 * @param cashier Cashier to be removed from the collection
	 */
	public void removeCashier(Cashier cashier) {
		cashiers.remove(cashier.getNumber());
	}

	/**
	 * Adds new taxCategory to the collection of taxCategories in the store
	 * @param taxCategory taxCategory to be added to the collection
	 */
	public void addTaxCategory(TaxCategory taxCategory) {
		taxCategories.put(taxCategory.getCategory(), taxCategory);
	}

	/**
	 * Removes taxCategory from the collection of taxCategory in the store
	 * @param taxCategory taxCategory to be removed from the collection
	 */
	public void removeTaxCategory(TaxCategory taxCategory) {
		taxCategories.remove(taxCategory.getCategory());
	}

	/**
	 * Adds new session to the collection of sessions in the store
	 * @param session New Session to be added to the collection
	 */
	public void addSession(Session session) {
		sessions.add(session);
	}

	/**
	 * Removes session from the collection of sessions in the store
	 * @param session Session to be removed from the collection
	 */
	public void removeSession(Session session) {
		sessions.remove(session);
	}

	/**
	 * Find register using register number 
	 * @param number Register number
	 * @return Register details
	 */
	public Register findRegisterByNumber(String number) {
		return registers.get(number);
	}

	/**
	 * Find item using upc (Universal Product Code)
	 * @param upc Universal Product Code
	 * @return Item details
	 */
	public Item findItemForUPC(String upc) {
		 // Iterate through the values of the TreeMap (items)
        for (Item item : items.values()) {
            // Directly check if the UPC is contained in the item's internal TreeMap
            if (item.getuPCs().containsKey(upc)) { 
                return item; // Return the matching item directly
            }
        }
        return null; // Return null if no matching UPC is found
	}

	/**
	 * Finds item using item number
	 * @param number Item Number
	 * @return Item details
	 */
	public Item findItemForNumber(String number) {
		return items.get(number);
	}

	/**
	 * Finds Cashier using cashier number
	 * @param number Cashier number
	 * @return Cashier details
	 */
	public Cashier findCashierForNumber(String number) {
		return cashiers.get(number);
	}

	/**
	 * Find taxCategory using category name
	 * @param category Category name
	 * @return taxCategory details
	 */
	public TaxCategory findTaxCategoryByName(String category) {
		return taxCategories.get(category);
	}

	/**
	 * Builds string text representing Store class
	 * @return String representation of the store
	 */
	public String toString() {
		StringBuilder  result=new StringBuilder(getNumber()+"  "+getName());	
		
		return result.toString();
	}

}