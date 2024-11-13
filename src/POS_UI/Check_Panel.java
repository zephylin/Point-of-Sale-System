package POS_UI;

import javax.swing.JPanel;

import POS_PD.Cash;
import POS_PD.Check;
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

public class Check_Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;

	/**
	 * Create the panel.
	 * @param check 
	 * @param sale 
	 * @param session 
	 * @param myStore 
	 * @param PaymentPanel 
	 * @param contentPane 
	 */
	public Check_Panel(JPanel contentPane, Payment_Panel paymentPanel, Store myStore, Session session, Sale sale, Check check) {
		Check_Panel myPanel = this;
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Enter Check");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(198, 22, 87, 19);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Amount");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_1.setBounds(87, 60, 87, 19);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Routing Nbr");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_1_1.setBounds(87, 92, 87, 19);
		add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_1_2 = new JLabel("Account Nbr");
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_1_2.setBounds(87, 121, 87, 19);
		add(lblNewLabel_1_2);
		
		JLabel lblNewLabel_1_3 = new JLabel("Check Nbr");
		lblNewLabel_1_3.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_1_3.setBounds(87, 154, 87, 19);
		add(lblNewLabel_1_3);
		
		textField = new JTextField();
		textField.setBounds(189, 60, 96, 19);
		add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(189, 92, 96, 19);
		add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(189, 121, 96, 19);
		add(textField_2);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(189, 154, 96, 19);
		add(textField_3);
		
		JButton btnNewButton = new JButton("Save");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				check.setAmtTendered(new BigDecimal(textField.getText()));
				check.setRoutingNumber(textField_1.getText());
				check.setAccountNumber(textField_2.getText());
				check.setCheckNumber(textField_3.getText());
				sale.addPayment(check);
				paymentPanel.remove(myPanel);
				contentPane.removeAll();
				contentPane.add(paymentPanel);
				contentPane.repaint();
			}
		});
		btnNewButton.setBounds(118, 198, 85, 21);
		add(btnNewButton);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				paymentPanel.remove(myPanel);
				contentPane.removeAll();
				contentPane.add(paymentPanel);
				contentPane.repaint();
			}
		});
		btnCancel.setBounds(243, 198, 85, 21);
		add(btnCancel);

	}

}
