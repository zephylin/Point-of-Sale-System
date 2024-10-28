package POS_PD;

import java.util.ArrayList;


/**
 * Representation of a cashier
 * @author Zephylin D.
 * @version 1.0
 */
public class Cashier {

	/**
	 * Cashier unique identification number
	 */
	private String number;
	/**
	 * Cashier's password
	 */
	private String password;
	/**
	 * Cashier details as a Person
	 */
	private Person person;
	/**
	 * Collection of sessions cashier has worked
	 */
	private ArrayList<Session> sessions;

	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	
	public ArrayList<Session> getSessions() {
		return sessions;
	}

	public void setSessions(ArrayList<Session> sessions) {
		this.sessions = sessions;
	}

	/**
	 * Default constructor
	 */
	public Cashier() {
		sessions = new ArrayList<Session>();
		person = new Person();
	}

//	public Cashier(String number, String name, String sSN, String address, String city, String state, String zip, String phone, String password) {
//		this();
//		this.number = number;
//		this.person = new Person(name, sSN, address, city, state, zip, phone, this);
//		this.password = password;
//	
//	}
	/**
	 * Constructor that sets cashier number, password and details as a person
	 * @param number
	 * @param person
	 * @param password
	 */
	
	public Cashier(String number, Person person, String password) 
	{
		this();
		this.number = number;
		this.person = person;
		this.password = password;
	}

	/**
	 * Adds new session done by the cashier
	 * @param session Session to be added to the collection of sessions
	 */
	public void addSession(Session session) {
		sessions.add(session);
	}

	/**
	 * Removes session from the collection of sessions
	 * @param session Session to be removed from the collection of sessions
	 */
	public void removeSession(Session session) {
		sessions.remove(session);
	}

	/**
	 * Checks whether the given password is equivalent to the stored one
	 * @param password Given password to be compared to the stored cashier password
	 * @return true if credentials are correct otherwise false
	 */
	public Boolean isAuthorized(String password) {
		return this.password.equals(password);
	}

	/**
	 * Makes string representation for cashier instance
	 * @return String representation for a cashier
	 */
	public String toString() {
		return person.getName();
	}

}