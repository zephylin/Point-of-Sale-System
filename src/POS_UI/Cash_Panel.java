package POS_UI;

import javax.swing.JPanel;

import POS_PD.Cash;
import POS_PD.Sale;
import POS_PD.Session;
import POS_PD.Store;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.awt.event.ActionEvent;

public class Cash_Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;

	/**
	 * Create the panel.
	 * @param cash 
	 * @param sale 
	 * @param session 
	 * @param myStore 
	 * @param paymentPanel 
	 * @param contentPane 
	 */
	public Cash_Panel(JPanel contentPane, Payment_Panel paymentPanel, Store myStore, Session session, Sale sale, Cash cash) {
		Cash_Panel myPanel = this;
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Enter Cash Payment");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(136, 35, 118, 21);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Amount Tendered");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(81, 91, 110, 21);
		add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(218, 93, 96, 19);
		add(textField);
		textField.setColumns(10);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				cash.setAmtTendered(new BigDecimal(textField.getText()));
				cash.setAmount(sale.calcAmountForPayment(cash.getAmtTendered()));
				sale.addPayment(cash);
				paymentPanel.remove(myPanel);
				contentPane.removeAll();
				contentPane.add(paymentPanel);
				contentPane.repaint();
			}
		});
		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnSave.setBounds(107, 148, 85, 21);
		add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				paymentPanel.remove(myPanel);
				paymentPanel.repaint();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnCancel.setBounds(229, 148, 85, 21);
		add(btnCancel);

	}

}
