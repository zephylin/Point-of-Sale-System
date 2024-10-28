package POS_PD;

/**
 * Representation of Authorized Payment
 * @author Zephylin D.
 * @version 1.0
 */
public abstract class AuthorizedPayment extends Payment {

	/**
	 * Authorization code
	 */
	private String authorizationCode;

	
	public String getAuthorizationCode() {
		return authorizationCode;
	}


	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}


	/**
	 * Checks whether the payment is authorized or not
	 * @return True if payment is authorized otherwise returns false
	 */
	public abstract Boolean isAuthorized();
	
	public Boolean countAsCash() {
		return false;
	}

}