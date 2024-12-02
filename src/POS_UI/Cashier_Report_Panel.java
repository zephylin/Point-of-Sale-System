package POS_UI;

import javax.swing.JPanel;


import POS_PD.*;
import javax.swing.JLabel;
import java.awt.Font;
import com.github.lgooddatepicker.components.DatePicker;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionEvent;

public class Cashier_Report_Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Create the panel.
	 * @param myStore 
	 * @param contentPane 
	 */
	public Cashier_Report_Panel(JPanel contentPane, Store myStore) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Cashier Report");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel.setBounds(273, 28, 112, 23);
		add(lblNewLabel);
		
		DatePicker datePicker = new DatePicker();
		datePicker.setBounds(221, 83, 186, 27);
		add(datePicker);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(101, 151, 426, 190);
		add(textArea);
		textArea.setEditable(false);
		
		JLabel lblNewLabel_1 = new JLabel("Select Date");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(136, 83, 75, 22);
		add(lblNewLabel_1);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				textArea.setText("Cashier Report for: "+ datePicker.getDate().format(DateTimeFormatter.ofPattern("M/d/yyyy"))+"\n\n");
				textArea.append("Number\tName\tCount\tAmount\tDiff\n");
				
				BigDecimal total = new BigDecimal(0);
				int totalCount = 0;
				for(Session session : myStore.getSessions()) 
				{
					if(session.getEndDateTime().toLocalDate().isEqual(datePicker.getDate())) 
					{
						BigDecimal saleAmount = new BigDecimal(0);	
						int count=0;
						for(Sale sale : session.getSales()) 
						{
							
							count++;
							saleAmount=saleAmount.add(sale.calcAmtTendered().subtract(sale.calcChange()));
							//total=total.add(sale.getTotalPayments());
						}
						
						totalCount+=count;
						total = total.add(saleAmount);
						
						textArea.append(session.getRegister()+"\t"+session.getCashier().getPerson().getName()+"\t"
						+count+ "\t" +saleAmount.toString()+"\t"+"0.00\n");
					}

				}
				
				textArea.append("\nTotal: \t\t"+totalCount+"\t"+total.toString());
				
			}
		});
		btnGenerate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnGenerate.setBounds(193, 382, 85, 21);
		add(btnGenerate);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new POS_Home(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		btnClose.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnClose.setBounds(380, 383, 85, 21);
		add(btnClose);
		
		
	

	}
}
