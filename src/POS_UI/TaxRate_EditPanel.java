package POS_UI;

import javax.swing.JFrame;
import javax.swing.JPanel;

import POS_PD.Store;
import POS_PD.TaxCategory;
import POS_PD.TaxRate;
import javax.swing.JLabel;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class TaxRate_EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;

	/**
	 * Create the panel.
	 * @param b 
	 * @param taxRate 
	 * @param myStore 
	 * @param myFrame 
	 */
	public TaxRate_EditPanel(JFrame myFrame, Store myStore, TaxCategory taxCategory, TaxRate taxRate, boolean b) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Edit Tax Rate");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(286, 71, 142, 30);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Tax Rate");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(174, 172, 88, 30);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Effective Date");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1_1.setBounds(174, 234, 88, 30);
		add(lblNewLabel_1_1);
		
		textField = new JTextField(taxRate.getTaxRate().toString());
		textField.setBounds(295, 176, 115, 26);
		add(textField);
		textField.setColumns(10);
		
		LocalDate date=taxRate.getEffectiveDate();
		String month=date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
		String day = String.valueOf(date.getDayOfMonth());
		String year = String.valueOf(date.getYear());
		
		
		
		String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.valueOf(i);
        }
		JComboBox<String> comboBox = new JComboBox<>(days);
		comboBox.setSelectedItem(day);
		comboBox.setBounds(295, 240, 57, 21);
		add(comboBox);
		
		String[] months = {"Jan", "Feb", "Mar", "Apr","May","Jun", "Jul", "Aug","Sep","Oct", "Nov","Dec"};
		JComboBox<String> comboBox_1 = new JComboBox<>(months);
		comboBox_1.setSelectedItem(month);
		comboBox_1.setBounds(369, 240, 59, 21);
		add(comboBox_1);
		
		String[] years=new String[50];
		for(int i=0;i<50;i++) {
			years[i]=String.valueOf(i+1980);
		}
		
		JComboBox<String> comboBox_2 = new JComboBox<>(years);
		comboBox_2.setBounds(444, 240, 69, 21);
		comboBox_2.setSelectedItem(year);
		add(comboBox_2);
		
		JButton btnNewButton = new JButton("Save");
		btnNewButton.setBackground(new Color(0, 128, 255));
		btnNewButton.setForeground(new Color(0, 0, 0));
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(244, 329, 100, 30);
		add(btnNewButton);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setForeground(new Color(255, 255, 255));
		btnCancel.setBackground(new Color(128, 128, 128));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				myFrame.getContentPane().removeAll();
				myFrame.getContentPane().add(new TaxCategory_EditPanel(myFrame,myStore,taxCategory,true ));
				myFrame.getContentPane().revalidate();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCancel.setBounds(398, 329, 100, 30);
		add(btnCancel);
		
		

	}
}
