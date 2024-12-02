package POS_UI;

import javax.swing.JPanel;

import POS_PD.*;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JButton;
import com.github.lgooddatepicker.components.DatePicker;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.awt.event.ActionEvent;

public class Item_Report_Panel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 * @param myStore 
	 * @param contentPane 
	 */
	public Item_Report_Panel(JPanel contentPane, Store myStore) {
		setLayout(null);
		
		JLabel lblItemsReport = new JLabel("Items Report");
		lblItemsReport.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblItemsReport.setBounds(300, 32, 112, 23);
		add(lblItemsReport);
		
		JLabel lblNewLabel_1 = new JLabel("Select Date");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(163, 87, 75, 22);
		add(lblNewLabel_1);
		
		DatePicker datePicker = new DatePicker();
		datePicker.setBounds(261, 86, 184, 28);
		add(datePicker);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(128, 155, 426, 190);
		add(textArea);
		textArea.setEditable(false);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				HashMap<Item, Integer> map = new HashMap<>();
				textArea.setText("Item Report for: "+ datePicker.getDate().format(DateTimeFormatter.ofPattern("M/d/yyyy"))+"\n\n");
				textArea.append("Number\tDescription\t\tQuantity\n\n");
				for(Session session : myStore.getSessions()) 
				{
					if(session.getEndDateTime().toLocalDate().isEqual(datePicker.getDate())) 
					{
						for(Sale sale : session.getSales()) 
						{
							for(SaleLineItem sli : sale.getSaleLineItems()) 
							{
								if(map.containsKey(sli.getItem())) 
								{
								    map.put(sli.getItem(), map.get(sli.getItem()) + sli.getQuantity());
								} else 
								{
								    map.put(sli.getItem(), sli.getQuantity());
								}
							}
						}
						
					}
				}
				
				List<Item> sortedItems = new ArrayList<>(map.keySet());
				Collections.sort(sortedItems, new Comparator<Item>() {
					public int compare(Item item1 , Item item2) {
						return item1.getNumber().compareTo(item2.getNumber());
					}
				});
				
				for(Item item : sortedItems) 
				{
					textArea.append(item.getNumber() + "\t" + item.getDescription() 
					+(item.getDescription().length()<15 ? "\t\t" : "\t") + map.get(item) + "\n");
				}
			}
		});
		btnGenerate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnGenerate.setBounds(220, 386, 85, 21);
		add(btnGenerate);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new POS_Home(contentPane,myStore));
				contentPane.revalidate();
			}
		});
		btnClose.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnClose.setBounds(407, 387, 85, 21);
		add(btnClose);
		
		

	}
}
