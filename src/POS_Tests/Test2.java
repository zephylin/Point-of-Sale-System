package POS_Tests;
import POS_PD.*;
import POS_DM.*;

public class Test2 {

	public static void main(String[] args) {
		Store store = new Store();
		DataManagement dm = new DataManagement();
		
		dm.loadStore(store);
		
		runTest(store);

	}
	
	public static void runTest(Store store) {
		 
		System.out.println("********************************\nStore\n**********");
		System.out.println(store.getName());
		
		// Printing cashiers 
		System.out.println("********************************\nCashiers\n**********");
		for(Cashier cashier : store.getCashiers().values()) {
			System.out.println(cashier.toString());
		}
		
		// Printing registers
		System.out.println("********************************\nRegisters\n**********");
		for(Register register : store.getRegisters().values()) {
			System.out.println(register.toString());
		}
	
		// Printing Items
		System.out.println("********************************\nItems\n**********");
		for(Item item : store.getItems().values()) {
			System.out.println(item.toString());
		}
		
		// Printing Sessions
		System.out.println("********************************\nSessions\n**********");
		for(Session session : store.getSessions()) {
			System.out.println(session.toString());
		}
		
		
	}

}
