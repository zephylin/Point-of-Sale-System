package POS_UI;

import javax.swing.JPanel;

import POS_PD.Credit;
import POS_PD.Sale;
import POS_PD.Session;
import POS_PD.Store;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionEvent;

public class Credit_Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Create the panel.
	 * @param credit 
	 * @param sale 
	 * @param session 
	 * @param myStore 
	 * @param myPanel 
	 * @param contentPane 
	 */
	public Credit_Panel(JPanel contentPane, Payment_Panel paymentPanel, Store myStore, Session session, Sale sale, Credit credit) {
		Credit_Panel myPanel = this;
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Enter Credit Payment");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(144, 28, 112, 19);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Amount");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_1.setBounds(62, 72, 82, 19);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Card Type");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_1_1.setBounds(62, 101, 82, 19);
		add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_1_2 = new JLabel("Account Nbr");
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_1_2.setBounds(62, 137, 82, 19);
		add(lblNewLabel_1_2);
		
		JLabel lblNewLabel_1_3 = new JLabel("Expire Date");
		lblNewLabel_1_3.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_1_3.setBounds(62, 166, 82, 19);
		add(lblNewLabel_1_3);
		
		textField = new JTextField();
		textField.setBounds(160, 72, 96, 19);
		add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(160, 137, 96, 19);
		add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(160, 166, 96, 19);
		add(textField_2);
		
		DefaultComboBoxModel<String> cardModel = new DefaultComboBoxModel<>();
		String[] cardType = {"Visa", "MasterCard", "American Express", "Discover"};
		for(String card : cardType) {
			cardModel.addElement(card);
		}
		JComboBox<String> comboBox = new JComboBox<>(cardModel);
		comboBox.setBounds(183, 100, 73, 21);
		add(comboBox);
		
		JButton btnNewButton = new JButton("Save");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				credit.setCardType((String)comboBox.getSelectedItem());
				credit.setAmtTendered(new BigDecimal(textField.getText()));
				credit.setAcctNumber(textField_1.getText());
				credit.setExpireDate(LocalDate.parse(textField_2.getText(), DateTimeFormatter.ofPattern("M/d/yyyy")));
				sale.addPayment(credit);
				paymentPanel.remove(myPanel);
				contentPane.removeAll();
				contentPane.add(paymentPanel);
				contentPane.repaint();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnNewButton.setBounds(98, 205, 85, 21);
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
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnCancel.setBounds(213, 205, 85, 21);
		add(btnCancel);
		
		JLabel lblNewLabel_2 = new JLabel("m/d/yyyy");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_2.setBounds(265, 169, 45, 13);
		add(lblNewLabel_2);
		
	}
}
