package POS_PD;

/**
 * Representation of UPC( Universal Product Code)
 * @author Zephylin D.
 * @version 1.0
 */
public class UPC {

	/**
	 * Item specific UPC
	 */
	private String uPC;
	/**
	 * Specific Item in the store
	 */
	private Item item;

	
	public String getuPC() {
		return uPC;
	}

	public void setuPC(String uPC) {
		this.uPC = uPC;
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
	public UPC() {
		uPC = "";
	}

	/**
	 * Constructor that will initialize upc
	 * @param upc
	 */
	public UPC(String upc) {
		uPC = upc;
		//this.item = item;
		//this.item.addUPC(this);
	}
	
	public UPC(String upc, Item item) {
		uPC = upc;
		this.item = item;
		this.item.addUPC(this);
	}

	/**
	 * Makes string representation for UPC instance
	 * @return String representation for UPC instance
	 */
	public String toString() {
		return new String(getuPC());
	}

}