package POS_UI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import POS_PD.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class TaxCategory_EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	JButton btnEdit, btnDelete, btnAdd;

	/**
	 * Create the panel.
	 */
	public TaxCategory_EditPanel(JFrame myFrame, Store myStore, TaxCategory taxCategory, boolean isAdd) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Tax Category");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(58, 94, 90, 24);
		add(lblNewLabel);
		
		textField = new JTextField(taxCategory.toString());
		textField.setBounds(158, 92, 141, 32);
		add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Tax Rate");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(360, 90, 96, 24);
		add(lblNewLabel_1);
		
		DefaultListModel<TaxRate> listModel =new DefaultListModel<>();
		for(TaxRate taxRate : taxCategory.getTaxRates()) {
			listModel.addElement(taxRate);
		}
		
		JList<TaxRate> list = new JList<>(listModel);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) 
			{
				if(list.getSelectedValue() != null) {
					btnEdit.setEnabled(true);
					if(!taxCategory.hasTaxRate(list.getSelectedValue())) {
						btnDelete.setEnabled(true);
					}
					
				}
				
				else {
					btnEdit.setEnabled(false);
					btnDelete.setEnabled(false);
				}
			}
		});
		list.setBounds(341, 133, 185, 71);
		add(list);
		
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				
				myFrame.getContentPane().removeAll();
				myFrame.getContentPane().add(new TaxRate_EditPanel(myFrame, myStore, taxCategory, new TaxRate(),  true));
				myFrame.getContentPane().revalidate();
				
			}
		});
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAdd.setBounds(309, 226, 70, 24);
		add(btnAdd);
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				myFrame.getContentPane().removeAll();
				myFrame.getContentPane().add(new TaxRate_EditPanel(myFrame, myStore,taxCategory, list.getSelectedValue(),false));
				myFrame.getContentPane().revalidate();
			}
		});
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnEdit.setBounds(407, 226, 70, 24);
		add(btnEdit);
		btnEdit.setEnabled(false);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				taxCategory.removeTaxRate(list.getSelectedValue());
			}
		});
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnDelete.setBounds(502, 226, 70, 24);
		add(btnDelete);
		btnDelete.setEnabled(false);
		
		JButton btnNewButton_1 = new JButton("Save");
		btnNewButton_1.setBackground(new Color(0, 128, 255));
		btnNewButton_1.setForeground(new Color(0, 0, 0));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				taxCategory.setCategory(textField.getText());
				taxCategory.addTaxRate((TaxRate)list.getModel());
				
				myFrame.getContentPane().removeAll();
				myFrame.getContentPane().add(new TaxCategory_ListPanel(myFrame,myStore));
				myFrame.getContentPane().revalidate();
				
			}
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnNewButton_1.setBounds(117, 307, 118, 41);
		add(btnNewButton_1);
		
		JButton btnNewButton_1_1 = new JButton("Cancel");
		btnNewButton_1_1.setForeground(new Color(255, 255, 255));
		btnNewButton_1_1.setBackground(new Color(128, 128, 128));
		btnNewButton_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				myFrame.getContentPane().removeAll();
				myFrame.getContentPane().add(new TaxCategory_ListPanel(myFrame, myStore));
				myFrame.getContentPane().revalidate();
			}
		});
		btnNewButton_1_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnNewButton_1_1.setBounds(321, 307, 118, 41);
		add(btnNewButton_1_1);
		
		

	}
}
