
package POS_Tests;
import POS_PD.*;

/**
 * Displays data about the Store and Cashiers
 * @author Zephylin D.
 * @version 1.0
 */
public class AC2 {
	
//	private Store store;
//	private Cashier worker1, worker2, worker3;
//	private Person person1, person2, person3;

	public static void main(String[] args) {

		Store store = new Store( "123", "Zephylin's Convenience Store");
		Person person1 = new Person("Smith Mugisha", "Kigali 123Ave", "Kigali", "Kicukiro", "234234", "123-456-7890", "444-44-4444");
		Cashier cashier1 = new Cashier("0001", person1, "password123");
		Person person2 = new Person("Lyla Kendra", "Bongpo ri", "Gangwon", "Sokcho", "123456", "789-456-1230", "333-44-1111");
		Cashier cashier2 = new Cashier("0002", person2, "secret@password");
		Person person3 = new Person("Adam Johnson", "Memorial Rd", "Edmond", "Oklahoma", "124578", "405-405-4040", "111-11-1111");
		Cashier cashier3 = new Cashier("0003", person3, "hash@hash");
		
		store.addCashier(cashier1);
		store.addCashier(cashier2);
		store.addCashier(cashier3);
		
		System.out.println(store.toString());
		System.out.println("===================================\nCashiers\n==========");
		for(Cashier cashier : store.getCashiers().values())
			{
				System.out.println(cashier.toString());
			}
		

	}

}
