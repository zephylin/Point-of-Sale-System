package POS_Tests;
import java.math.BigDecimal;

import POS_PD.*;

/**
 * Displays data about the Store and Registers
 * @author Zephylin D.
 * @version 1.0
 */
public class AC3 {

	public static void main(String[] args) {
		// Create a store
		Store store= new Store("123", "Peter's Shoes Store");
		
		// Create 2 registers
		Register register1 = new Register("1");
		Register register2 = new Register("2");
		
		// Create 2 cash drawer
		CashDrawer cashDrawer1 = new CashDrawer(new BigDecimal("100"));
		CashDrawer cashDrawer2 = new CashDrawer(new BigDecimal("200"));
		
		// Assign register to cash drawer
		register1.setCashDrawer(cashDrawer1);
		register2.setCashDrawer(cashDrawer2);
		
		// Add registers to the store
		store.addRegister(register1);
		store.addRegister(register2);
		
		//Displaying results
		System.out.println(store.toString());
		System.out.println("======================================\nRegisters\n==========");
		for(Register register : store.getRegisters().values())
			{
				System.out.println(register.toString()+"  "+register.getCashDrawer().toString());
			}
		
	}

}
