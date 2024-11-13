package POS_UI;

import javax.swing.JPanel;

import POS_PD.Session;
import POS_PD.Store;
import javax.swing.JLabel;
import java.awt.Font;
import java.math.BigDecimal;

import javax.swing.JTextField;

import POS_PD.*;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EndSession_Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;

	/**
	 * Create the panel.
	 * @param session 
	 * @param myStore 
	 * @param contentPane 
	 */
	public EndSession_Panel(JPanel contentPane, Store myStore, Session session) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Session Summary");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(243, 53, 136, 21);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Cashier");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(157, 118, 74, 21);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Register");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1_1.setBounds(157, 149, 74, 21);
		add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_1_2 = new JLabel(session.getCashier().toString());
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1_2.setBounds(223, 118, 74, 21);
		add(lblNewLabel_1_2);
		
		JLabel lblNewLabel_1_3 = new JLabel(session.getRegister().toString());
		lblNewLabel_1_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1_3.setBounds(223, 149, 74, 21);
		add(lblNewLabel_1_3);
		
		JLabel lblNewLabel_2 = new JLabel("Number of Sales");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(157, 203, 107, 21);
		add(lblNewLabel_2);
		
		JLabel lblNewLabel_2_1 = new JLabel("Total Sales");
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2_1.setBounds(157, 244, 107, 21);
		add(lblNewLabel_2_1);
		
		JLabel lblNewLabel_2_2 = new JLabel("Enter Cash");
		lblNewLabel_2_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2_2.setBounds(157, 278, 107, 21);
		add(lblNewLabel_2_2);
		
		JLabel lblNewLabel_2_3 = new JLabel("Cash Count Diff");
		lblNewLabel_2_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2_3.setBounds(157, 309, 107, 21);
		add(lblNewLabel_2_3);
		
		textField = new JTextField(String.valueOf(session.getSales().size()));
		textField.setBounds(296, 205, 96, 19);
		add(textField);
		textField.setColumns(10);
		textField.setEnabled(false);
		
		BigDecimal totalCash = new BigDecimal(0);
		for(Sale sale : session.getSales())
		{
			totalCash = totalCash.add(sale.getTotalPayments());		
		}
		
		textField_1 = new JTextField(totalCash.toString());
		textField_1.setColumns(10);
		textField_1.setBounds(296, 246, 96, 19);
		add(textField_1);
		textField_1.setEnabled(false);
		
		textField_2 = new JTextField();
		textField_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				textField_3.setText(session.calcCashCountDiff(new BigDecimal(textField_2.getText())).toString());
			}
		});
		textField_2.setColumns(10);
		textField_2.setBounds(296, 280, 96, 19);
		add(textField_2);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(296, 311, 96, 19);
		add(textField_3);
		textField_3.setEnabled(false);
		
		JButton btnNewButton = new JButton("End Session");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new POS_Home(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewButton.setBounds(243, 372, 96, 27);
		add(btnNewButton);

	}

}
