package POS_UI;

import javax.swing.JPanel;

import POS_PD.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.awt.event.ActionEvent;
import javax.swing.JButton;

public class Price_EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	Price price;

	/**
	 * Create the panel.
	 * @param isAdd 
	 * @param price 
	 * @param item 
	 * @param myStore 
	 * @param currentPanel 
	 * @param contentPane 
	 */
	public Price_EditPanel(JPanel contentPane, JPanel currentPanel, Store myStore, Item item, Price myPrice, boolean isAdd)
	{
		setLayout(null);
		price=myPrice;
		JLabel lblNewLabel = new JLabel("Edit Price");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(160, 88, 74, 25);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Price");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(160, 168, 74, 25);
		add(lblNewLabel_1);
		
		textField = new JTextField(price.getPrice().toString());
		textField.setBounds(281, 171, 96, 19);
		add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Effective Date");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(160, 230, 86, 19);
		add(lblNewLabel_2);
		
		textField_1 = new JTextField(price.getEffectiveDate().format(DateTimeFormatter.ofPattern("M/d/yyyy")));
		textField_1.setBounds(281, 230, 96, 19);
		add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblEndDate = new JLabel("End Date");
		lblEndDate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEndDate.setBounds(456, 230, 74, 19);
		add(lblEndDate);
		lblEndDate.setVisible(false);
		
		textField_2 = new JTextField();
		textField_2.setBounds(548, 230, 96, 19);
		add(textField_2);
		textField_2.setColumns(10);
		textField_2.setVisible(false);
		
		JCheckBox chckbxPromoPrice = new JCheckBox("Promo Price");
		if(price instanceof PromoPrice) 
		{
			chckbxPromoPrice.setSelected(true);
			textField_2.setText(((PromoPrice)price).getEndDate().format(DateTimeFormatter.ofPattern("M/d/yyyy")));
			lblEndDate.setVisible(true);
			textField_2.setVisible(true);
		}
		chckbxPromoPrice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(chckbxPromoPrice.isSelected())
				{
					price = new PromoPrice();
					lblEndDate.setVisible(true);
					textField_2.setVisible(true);
				}
				else
				{
					price = new Price();
					lblEndDate.setVisible(false);
					textField_2.setVisible(false);
				}
			}
		});
	
		chckbxPromoPrice.setFont(new Font("Tahoma", Font.PLAIN, 12));
		chckbxPromoPrice.setBounds(456, 170, 93, 21);
		add(chckbxPromoPrice);
		chckbxPromoPrice.setVisible(false);

		if(isAdd) {
			chckbxPromoPrice.setVisible(true);
		}
		
		
		JButton btnNewButton = new JButton("Save");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				price.setPrice(new BigDecimal(textField.getText()));
				price.setEffectiveDate(LocalDate.parse(textField_1.getText(), DateTimeFormatter.ofPattern("M/d/yyyy")));
				if(price instanceof PromoPrice)
					((PromoPrice) price).setEndDate(LocalDate.parse(textField_2.getText(), DateTimeFormatter.ofPattern("M/d/yyyy")));
				if(isAdd)
					item.addPrice(price);
				contentPane.removeAll();
				contentPane.add(currentPanel);
				contentPane.repaint();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewButton.setBounds(247, 354, 86, 25);
		add(btnNewButton);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				
				int response = JOptionPane.showConfirmDialog(
			            null,
			            "Do you want to cancel?",
			            "Cancel Confirmation",
			            JOptionPane.YES_NO_OPTION,
			            JOptionPane.QUESTION_MESSAGE
			        );
				
				if(response==JOptionPane.YES_OPTION) {
					contentPane.removeAll();
					contentPane.add(currentPanel);
					contentPane.repaint();
				}
				else {
					contentPane.repaint();
					}
				
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCancel.setBounds(411, 357, 86, 25);
		add(btnCancel);
		
		JLabel lblNewLabel_3 = new JLabel("m/d/yyyy");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_3.setBounds(382, 234, 50, 13);
		add(lblNewLabel_3);
		

	}
}
