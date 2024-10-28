package POS_PD;

/**
 * Represents a person
 * @author Zephylin D.
 * @version 1.0
 */
public class Person {

	/**
	 * Name of the person
	 */
	private String name;
	/**
	 * Address of the person
	 */
	private String address;
	/**
	 * City of the person
	 */
	private String city;
	/**
	 * State of the person
	 */
	private String state;
	/**
	 * Zip code of the person
	 */
	private String zip;
	/**
	 * Phone number of the person
	 */
	private String phone;
	/**
	 * Social Security Number of the person
	 */
	private String sSN;
	/**
	 * Cashier a person knows, could be himself
	 */
	private Cashier cashier;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getsSN() {
		return sSN;
	}

	public void setsSN(String sSN) {
		this.sSN = sSN;
	}

	public Cashier getCashier() {
		return cashier;
	}

	public void setCashier(Cashier cashier) {
		this.cashier = cashier;
	}

	/**
	 * Default constructor
	 */
	public Person() {
		name = "";
		address = "";
		phone = "";
		sSN = "";
		
	}

	/**
	 * Constructor to initialize Person's info
	 * @param name Name of the person
	 * @param address Address of the person
	 * @param city City of the person
	 * @param state
	 * @param zip Zip code of the person
	 * @param phone Phone number of the person
	 * @param sSN Social Security Number of the person
	 * @param cashier Cashier a person may know
	 */
	public Person(String name, String address, String city, String state, String zip, String phone, String sSN) {
		this();
		this.name = name;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phone = phone;
		this.sSN = sSN;
		//this.cashier = cashier;
	}

	/**
	 * 
	 * @return Person  full details
	 */
	public String toString() {
		return new String("\nName: " + this.name + "\nAddress: " + this.address + "\nPhone: " + this.phone + "\nSSN: " + sSN + "\nCashier: " + cashier.getNumber());
	}

}