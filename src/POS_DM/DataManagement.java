package POS_DM;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import POS_PD.*;

public class DataManagement {
	
	public static Store loadStore(Store store) {
		Sale sale=null;
		String fileName="StoreData.csv";
		String line=null;
		String dataType;
		String[] splitter;
		FileReader fileReader;
		BufferedReader bufferedReader;
		
		try {
			fileReader= new FileReader(fileName);
			bufferedReader= new BufferedReader(fileReader);
			
			while((line=bufferedReader.readLine())!=null) {
				
				splitter=line.split(",");
				dataType=splitter[0];
				
				switch(dataType) {
				case "Store" :
					store.setName(splitter[1]);
					
					break;
					
				case "TaxCategory" : 
					TaxCategory taxCategory= new TaxCategory(splitter[1]);
					TaxRate taxrate= new TaxRate(splitter[3], splitter[2]);
					taxCategory.addTaxRate(taxrate);
					store.addTaxCategory(taxCategory);
					
					break;
					
				case "Cashier" :
					Person person = new Person(splitter[2], splitter[4], splitter[5], splitter[6], splitter[7], splitter[8], splitter[3]);
					Cashier cashier = new Cashier(splitter[1],person, splitter[9]);
					store.addCashier(cashier);
					
					break;
					
				case "Item" :
					Item item = new Item(splitter[1],splitter[3]);
					Price price = new Price(splitter[5],splitter[6]);
					UPC upc = new UPC(splitter[2], item);
					
					item.setTaxCategory(splitter[4], store);
					item.addPrice(price);
					
					// check if promo price exists
					if(splitter.length > 7) {
						PromoPrice promoPrice = new PromoPrice(splitter[7], splitter[8], splitter[9]);
						item.addPrice(promoPrice);
					}
					
					store.addItem(item);
					
					break;
					
				case "Register" :
					store.addRegister(new Register(splitter[1]));
					
					break;
					
				case "Session" :
					{
					Session session = new Session (splitter[1], splitter[2], store);
					session.setEndDateTime(LocalDateTime.now());
					store.addSession(session);
					}
					break;
					
				case "Sale" :
					
					//adding sale to current session
					{
					String taxFree;
					if(splitter[1].equals("Y"))
						taxFree="true";
					else
						taxFree="false";
					sale = new Sale(taxFree);
					Session currentSession= store.getSessions().get(store.getSessions().size() - 1);
					currentSession.addSale(sale);
					}
					break;
					
				case "SaleLineItem" :
					// get current session
					{
					Session currentSession = store.getSessions().get(store.getSessions().size() - 1);
					// get current sale
					Sale currentSale = currentSession.getSales().get(currentSession.getSales().size() - 1);
					currentSale.addSaleLineItem(new SaleLineItem(splitter[1], splitter[2], store ));
					}	
					break;
					
				case "Payment" :
					dataType=splitter[1];
					switch(dataType) {
					
					case "Cash" :
						{
						Session session = store.getSessions().get(store.getSessions().size() - 1);
						Sale sale1=session.getSales().get(session.getSales().size() - 1);
						sale1.addPayment(new Cash(splitter[2], splitter[3]));
						}	
						break;
						
					case "Credit" :
						{
						Session session = store.getSessions().get(store.getSessions().size() - 1);
						Credit credit = new Credit(splitter[2], splitter[4], splitter[5], splitter[6]);
						credit.setAmount(new BigDecimal(splitter[2]));
						credit.setAmtTendered(new BigDecimal(splitter[3]));
						session.getSales().get(session.getSales().size() - 1).addPayment(credit);
						}
						break;
						
					case "Check" : 
						{
						Session currentSession = store.getSessions().get(store.getSessions().size()-1);
						Check check = new Check(splitter[2], splitter[3], splitter[5], splitter[6]);
						check.setRoutingNumber(splitter[4]);
						currentSession.getSales().get(currentSession.getSales().size()-1).addPayment(check);
						}
						break; 
						}
					}
				
			}
			bufferedReader.close();
			
			}
		
		catch(FileNotFoundException e) {
			System.out.println("Unable to open file '"+ fileName  + "'");
		}
		
		catch(IOException e) {
			System.out.println("Error reading file '" + fileName + "'");
		}
		
		
		return store;
	}

}
