package POS_Tests;

import java.math.BigDecimal;

import java.time.LocalDate;

import POS_PD.*;
public class Test1 {

	public static void main(String[] args) {
		//Create a Store
				Store store=new Store("123", "Zephylin's Convenience Store");
				
				//Create taxCategory
				TaxCategory foodCategory = new TaxCategory("Food");
		        TaxCategory beverageCategory = new TaxCategory("Beverage");
		        TaxCategory snackCategory = new TaxCategory("Snack");
		        
		        // Create Tax Rates
		        TaxRate foodCategoryTaxRate1 = new TaxRate("1/1/2024", "0.06"); // 6% effective from January 1, 2024
		        TaxRate foodCategoryTaxRate2 = new TaxRate("7/1/2024", "0.07"); // 7% effective from July 1, 2024
		        TaxRate beverageCategoryTaxRate1 = new TaxRate("1/1/2024", "0.08"); // 8% effective from January 1, 2024
		        TaxRate beverageCategoryTaxRate2 = new TaxRate("7/1/2024", "0.09"); // 9% effective from July 1, 2024
		        TaxRate snackCategoryTaxRate1 = new TaxRate("1/1/2024", "0.06"); // 6% effective from January 1, 2024
		        TaxRate snackCategoryTaxRate2 = new TaxRate("7/1/2024", "0.07"); // 7% effective from July 1, 2024
				
		        // Add tax rate to the taxCategory
		        foodCategory.addTaxRate(foodCategoryTaxRate1);
		        foodCategory.addTaxRate(foodCategoryTaxRate2);
		        beverageCategory.addTaxRate(beverageCategoryTaxRate1);
		        beverageCategory.addTaxRate(beverageCategoryTaxRate2);
		        snackCategory.addTaxRate(snackCategoryTaxRate1);
		        snackCategory.addTaxRate(snackCategoryTaxRate2);
		        
		        // Create three Items with UPC, TaxCategory, and Price
		        Item water = new Item("001", "Bottle of Water");
		        Item apple = new Item("002", "Apple Fruit");
		        Item chips = new Item("003", "French Fries");

		        // Assign Tax Categories
		        apple.setTaxCategory(foodCategory);
		        water.setTaxCategory(beverageCategory);
		        chips.setTaxCategory(snackCategory);
		        
		     // Create prices
		        Price waterPrice1 = new Price("2.50", "1/5/2024"); 
		        Price waterPrice2 = new Price("1.50", "2/20/2024");      
		        Price applePrice1 = new Price("1.00","1/5/2024");
		        Price applePrice2 = new Price("0.80","12/20/2024");
		        Price chipsPrice1 = new Price("1.50", "1/5/2024"); 
		        Price chipsPrice2 = new Price("1.00", "12/20/2024"); 
		        
		        // Add prices to the items
		        water.addPrice(waterPrice1);
		        water.addPrice(waterPrice2);
		        apple.addPrice(applePrice1);
		        apple.addPrice(applePrice2);
		        chips.addPrice(chipsPrice1);
		        chips.addPrice(chipsPrice2);
		        
		        // Create UPCs for each Item
		        UPC upc1 = new UPC("123456789012");
		        UPC upc2 = new UPC("987654321098");
		        UPC upc3 = new UPC("456123789654");

		        // Set Items in UPCs
		        upc1.setItem(water);
		        upc2.setItem(apple);
		        upc3.setItem(chips);

		        // Add UPCs to the Items
		        water.addUPC(upc1);
		        apple.addUPC(upc2);
		        chips.addUPC(upc3);

		        // Add Items to the Store
		        store.addItem(water);
		        store.addItem(apple);
		        store.addItem(chips);

		        // Add TaxCategories to the Store
		        store.addTaxCategory(foodCategory);
		        store.addTaxCategory(beverageCategory);
		        store.addTaxCategory(snackCategory);
		        
		        // Add UPCs to the store
		        store.addUPC(upc1);
		        store.addUPC(upc2);
		        store.addUPC(upc3);
		        
		        // Create cashiers
		        Person person1 = new Person("Smith Mugisha", "Kigali 123Ave", "Kigali", "Kicukiro", "234234", "123-456-7890", "444-44-4444");
				Cashier cashier1 = new Cashier("0001", person1, "password123");
				Person person2 = new Person("Lyla Kendra", "Bongpo ri", "Gangwon", "Sokcho", "123456", "789-456-1230", "333-44-1111");
				Cashier cashier2 = new Cashier("0002", person2, "secret@password");
				Person person3 = new Person("Adam Johnson", "Memorial Rd", "Edmond", "Oklahoma", "124578", "405-405-4040", "111-11-1111");
				Cashier cashier3 = new Cashier("0003", person3, "hash@hash");
				
				store.addCashier(cashier1);
				store.addCashier(cashier2);
				store.addCashier(cashier3);
				
				// Create 2 registers
				Register register1 = new Register("1");
				Register register2 = new Register("2");
				
				// Create 2 cash drawer
				CashDrawer cashDrawer1 = new CashDrawer(new BigDecimal("100"));
				CashDrawer cashDrawer2 = new CashDrawer(new BigDecimal("200"));
				
				// Assign register to cash drawer
				register1.setCashDrawer(cashDrawer1);
				register2.setCashDrawer(cashDrawer2);
				
				// Create Session
				Session session1 = new Session(cashier1,register1);
				
				// Create Sale
		        Sale mySale = new Sale("false");
		        
		        //Create sale line items for Sale
		        SaleLineItem waterSLI = new SaleLineItem(mySale, water, "3"); // 3 Bottles of water
		        SaleLineItem appleSLI = new SaleLineItem(mySale, apple, "2");  // 2 apple fruits
		        
		        // Add Sale to session
		        session1.addSale(mySale);
		        
		        // Add session to the store
		        store.addSession(session1);
				
				// Add registers to the store
				store.addRegister(register1);
				store.addRegister(register2);
		        System.out.println("Acceptance Criteria 1\n*************************************");
		        AC1(store);
		        System.out.println("\n\nAcceptance Criteria 2\n*************************************");
		        AC2(store);
		        System.out.println("\n\nAcceptance Criteria 3\n*************************************");
		        AC3(store);
		        System.out.println("\n\nAcceptance Criteria 4\n*************************************");
		        AC4(store);

	}
	
	public static void AC1(Store store) {
		System.out.println("Store Name: " + store.getName());
        System.out.println("Store Number: " + store.getNumber());
        
        System.out.println("\nItems in Store:\n----------------");
        for (Item item : store.getItems().values()) {
            Price currentPrice = item.getPriceForDate(LocalDate.now());
            System.out.println("Item Number: " + item.getNumber());
            System.out.println("Description: " + item.getDescription());
            System.out.println("Tax Category: " + item.getTaxCategory().getCategory());
            System.out.println("Price (effective today): " + currentPrice.getPrice());
            System.out.println("UPC: " + item.getuPCs().firstEntry().getValue().getuPC());
            System.out.println("----------------------");
    		}

		}
	
	public static void AC2(Store store) {
		System.out.println(store.toString());
		System.out.println("===================================\nCashiers\n==========");
		for(Cashier cashier : store.getCashiers().values())
			{
				System.out.println(cashier.toString());
			}
	}
	
	public static void AC3(Store store) {
	
		System.out.println(store.toString());
		System.out.println("======================================\nRegisters\n==========");
		for(Register register : store.getRegisters().values())
			{
				System.out.println(register.toString()+"  "+register.getCashDrawer().toString());
			}
	}
	
	public static void AC4(Store store) {
		for(Session session : store.getSessions()) {
			System.out.print(session.toString());
		}
	}
	
}
