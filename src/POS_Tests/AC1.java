package POS_Tests;
import java.time.LocalDate;

import POS_PD.*;
/**
 * Displays data about the Store, Items, Tax Category, UPC and Prices (for today's date)
 * @author Zephylin D.
 * @version 1.0
 */
public class AC1 {

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
        
        //Display Store data including Items, Tax Category, UPC, and Price
        System.out.println("VERSION 1\n==========\n");
        System.out.println(store.toString());
		System.out.println("-----------------------------------\nItems\n----------");
		for(Item item : store.getItems().values())
			{
				System.out.println(item.toString());
			}
		System.out.println("-----------------------------------\nTaxCategory\n----------");
		for(TaxCategory category : store.getTaxCategories().values())
			{
				System.out.println(category.toString());
			}
		System.out.println("------------------------------------\nUPC\n----------");
		for(UPC upc: store.getUpcs().values())
			{
				System.out.println(upc.toString());
			}

		
		// Version 2
		System.out.println("\nVERSION 2\n==========\n");
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

}
