package POS_UI;
import POS_DM.StoreData;
import POS_PD.*;

public class POS_Start {

	public static void main(String[] args) {
		
		Store myStore = new Store();
		StoreData.loadStore(myStore);
		
		//POS_Frame storeFrame = new POS_Frame(myStore);
		POS_Frame.run(myStore);
		
		
		
		

	}

}
