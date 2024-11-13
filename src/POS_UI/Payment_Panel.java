package POS_UI;

import javax.swing.JPanel;

import POS_PD.*;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.event.AncestorListener;

import javax.swing.event.AncestorEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Payment_Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	JButton btnCompletePayment;
	
	Cash_Panel cashPanel;
	Check_Panel checkPanel;
	Credit_Panel creditPanel;

	/**
	 * Create the panel.
	 * @param sale 
	 * @param session 
	 * @param myStore 
	 * @param currentPanel 
	 * @param contentPane 
	 */
	public Payment_Panel(JPanel contentPane, JPanel currentPanel, Store myStore, Session session, Sale sale) {
		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) 
			{
				textField.setText(sale.calcTotal().toString());
				textField_1.setText(sale.calcAmtTendered().toString());
				btnCompletePayment.setEnabled(sale.isPaymentEnough());
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});
		Payment_Panel myPanel = this;
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Payment");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(290, 34, 83, 25);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Payment Due");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(78, 103, 109, 18);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Amount Tendered");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1_1.setBounds(78, 160, 109, 18);
		add(lblNewLabel_1_1);
		
		textField = new JTextField(sale.calcTotal().toString());
		textField.setBounds(78, 131, 96, 19);
		add(textField);
		textField.setColumns(10);
		textField.setEditable(false);
		
		
		textField_1 = new JTextField(sale.calcAmtTendered().toString());
		textField_1.setColumns(10);
		textField_1.setBounds(78, 184, 96, 19);
		add(textField_1);
		textField_1.setEditable(false);
		
		JButton btnNewButton = new JButton("Cash");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				cashPanel = new Cash_Panel(contentPane, myPanel, myStore, session, sale, new Cash());
				cashPanel.setBounds(300,88,400,300);
				add(cashPanel);
				if(checkPanel!=null) {
					remove(checkPanel);
				}
				if(creditPanel!=null) {
					remove(creditPanel);
				}
				revalidate();
				repaint();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewButton.setBounds(139, 239, 88, 25);
		add(btnNewButton);
		
		JButton btnCheck = new JButton("Check");
		btnCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				checkPanel = new Check_Panel(contentPane, myPanel, myStore, session, sale, new Check());
				checkPanel.setBounds(300,88,400,300);
				add(checkPanel);
				if(cashPanel!=null) {
					remove(cashPanel);
				}
				if(creditPanel!=null) {
					remove(creditPanel);
				}
				revalidate();
				repaint();
			}
		});
		btnCheck.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCheck.setBounds(139, 283, 88, 25);
		add(btnCheck);
		
		JButton btnCredit = new JButton("Credit");
		btnCredit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				creditPanel = new Credit_Panel( contentPane, myPanel, myStore, session, sale, new Credit());
				creditPanel.setBounds(300,88,400,300);
				add(creditPanel);
				if(cashPanel!=null) {
					remove(cashPanel);
				}
				if(checkPanel!=null) {
					remove(checkPanel);
				}
				revalidate();
				repaint();
			}
		});
		btnCredit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCredit.setBounds(139, 332, 88, 25);
		add(btnCredit);
		
		btnCompletePayment = new JButton("Complete Payment");
		btnCompletePayment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(currentPanel);
				contentPane.repaint();
			}
		});
		btnCompletePayment.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCompletePayment.setBounds(277, 421, 137, 25);
		add(btnCompletePayment);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new Sale_Panel(contentPane, myStore, session, new Sale()));
				contentPane.revalidate();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCancel.setBounds(459, 421, 96, 25);
		add(btnCancel);

	}

}
