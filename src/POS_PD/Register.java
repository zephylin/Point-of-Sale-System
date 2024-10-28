package POS_PD;

import java.util.ArrayList;

/**
 * Represents Register owned by the Store
 */
public class Register {

	/**
	 * Register number
	 */
	private String number;
	/**
	 * Session associated with Register
	 */
	private ArrayList<Session> sessions;
	/**
	 * Drawer, cash register used during session
	 */
	private CashDrawer cashDrawer;
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public CashDrawer getCashDrawer() {
		return cashDrawer;
	}

	public void setCashDrawer(CashDrawer cashDrawer) {
		this.cashDrawer = cashDrawer;
	}

	public ArrayList<Session> getSessions() {
		return sessions;
	}
	
	public void addSession(Session session)
	{
		sessions.add(session);
	}

	public void setSessions(ArrayList<Session> sessions) {
		this.sessions = sessions;
	}

	/**
	 * Default Constructor
	 */
	public Register() {
		number = "";
		cashDrawer = new CashDrawer();
		sessions = new ArrayList<Session>();
	}

	/**
	 * Constructor to initialize register number
	 * @param number Register number
	 */
	public Register(String number) {
		this();
		this.number = number;
	}

	/**
	 * Makes string representation of Register instance
	 * @return String representation of Register
	 */
	public String toString() {
		return new String(number);
	}

}