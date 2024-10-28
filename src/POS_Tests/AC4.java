package POS_Tests;

import POS_PD.*;
;
/**
 * Displays data about the Sale (Date and Time, Sale Line Item), the Subtotal, Tax, and Total
 * @author Zephylin D.
 * @version 1.0
 */
public class AC4 {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// Create a Person and Cashier
		Person person1 = new Person("John Smith", "2501 Memorial Rd", "Edmond", "Oklahoma", "12345", "405-456-7890", "987-65-4321");
		Cashier cashier1 = new Cashier("0001", person1, "password123");
		
		// Create Register
		Register register1 = new Register("1");
		
		// Create Session
		Session session1 = new Session(cashier1,register1);
		
		// Create 2 items
		Item water = new Item("123", "Bottle of Water");
		Item apple = new Item("456", "Apple");
		
		//Create taxCategory
		TaxCategory foodCategory = new TaxCategory("Food");
        TaxCategory beverageCategory = new TaxCategory("Beverage");
        
        // Create Tax Rates
        TaxRate foodCategoryTaxRate1 = new TaxRate("1/1/2024", "0.06"); // 6% effective from January 1, 2024
        TaxRate foodCategoryTaxRate2 = new TaxRate("7/1/2024", "0.07"); // 7% effective from July 1, 2024
        TaxRate beverageCategoryTaxRate1 = new TaxRate("1/1/2024", "0.08"); // 8% effective from January 1, 2024
        TaxRate beverageCategoryTaxRate2 = new TaxRate("7/1/2024", "0.09"); // 9% effective from July 1, 2024
		
        // Add tax rate to the taxCategory
        foodCategory.addTaxRate(foodCategoryTaxRate1);
        foodCategory.addTaxRate(foodCategoryTaxRate2);
        beverageCategory.addTaxRate(beverageCategoryTaxRate1);
        beverageCategory.addTaxRate(beverageCategoryTaxRate2);
        
        // Assign Tax Categories
        water.setTaxCategory(foodCategory);
        apple.setTaxCategory(beverageCategory);
        
        // Create prices
        Price waterPrice1 = new Price("2.50", "1/5/2024"); 
        Price waterPrice2 = new Price("1.50", "12/20/2024");      
        Price applePrice1 = new Price("1.00","1/5/2024");
        Price applePrice2 = new Price("0.80","12/20/2024");
        
        // Add prices to the items
        water.addPrice(waterPrice1);
        water.addPrice(waterPrice2);
        apple.addPrice(applePrice1);
        apple.addPrice(applePrice2);
        
        // Create Sale
        Sale mySale = new Sale("false");
        
        //Create sale line items for Sale
        SaleLineItem waterSLI = new SaleLineItem(mySale, water, "3"); // 3 Bottles of water
        SaleLineItem appleSLI = new SaleLineItem(mySale, apple, "2");  // 2 apple fruits
        
        // Add Sale to session
        session1.addSale(mySale);
        
        // Display Data about Sale
        System.out.print(session1.toString());
        
	}

}
