package POS_UI;

import javax.swing.JPanel;


import POS_PD.*;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JButton;
import com.github.lgooddatepicker.components.DatePicker;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionEvent;

public class Sale_Report_Panel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 * @param myStore 
	 * @param contentPane 
	 */
	public Sale_Report_Panel(JPanel contentPane, Store myStore) {
		setLayout(null);
		
		JLabel lblDailySalesReport = new JLabel("Daily Sales Report");
		lblDailySalesReport.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblDailySalesReport.setBounds(303, 24, 141, 23);
		add(lblDailySalesReport);
		
		JLabel lblNewLabel_1 = new JLabel("Select Date");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(166, 79, 75, 22);
		add(lblNewLabel_1);
		
		DatePicker datePicker = new DatePicker();
		datePicker.setBounds(283, 83, 161, 23);
		add(datePicker);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(131, 147, 426, 190);
		add(textArea);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				textArea.setText("Daily Sales Report for " + datePicker.getDate().format(DateTimeFormatter.ofPattern("M/d/yyyy")) + "\n\n");
				textArea.append("Sales\tCash\tCheck\tCredit\tTotal\n");
				BigDecimal cash,check,credit;
				BigDecimal totalCash = new BigDecimal(0);
				BigDecimal totalCheck = new BigDecimal(0);
				BigDecimal totalCredit = new BigDecimal(0);
				BigDecimal total = new BigDecimal(0);
				int count=0;
				for(Session session : myStore.getSessions())
				{
					if(session.getEndDateTime().toLocalDate().equals(datePicker.getDate()))
					{
						
						for(Sale sale : session.getSales())
						{
							count++;
							cash=new BigDecimal(0);
							check = new BigDecimal(0);
							credit = new BigDecimal(0);
							
	
							for(Payment payment : sale.getPayments())
							{
								if(payment instanceof Cash)
								{
									cash = cash.add(payment.getAmount());
								}
								else if (payment instanceof Check)
								{
									check = check.add(payment.getAmtTendered());
								}
								else if (payment instanceof Credit)
								{
									credit = credit.add(payment.getAmtTendered());
								}
							}
							totalCash = totalCash.add(cash);
							totalCheck = totalCheck.add(check);
							totalCredit = totalCredit.add(credit);
							textArea.append(count+"\t"+cash+"\t"+check+"\t"+credit+"\t"+sale.calcTotal()+"\n");
							total = total.add(sale.calcTotal());
						}
					}
				}
				textArea.append("\nTotal"+"\t"+totalCash+"\t"+totalCheck+"\t"+totalCredit+"\t"+total+"\n");
			}
			
		});
		btnGenerate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnGenerate.setBounds(223, 378, 85, 21);
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
		btnClose.setBounds(410, 379, 85, 21);
		add(btnClose);
		
		

	}
}
