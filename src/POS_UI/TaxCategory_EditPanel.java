package POS_UI;

import javax.swing.JFrame;

import javax.swing.JPanel;
import POS_PD.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;

public class TaxCategory_EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	JButton btnEdit, btnDelete, btnAdd;
	
	JPanel thisPanel;
	DefaultListModel<TaxRate> listModel;
	JList<TaxRate> list;

	/**
	 * Create the panel.
	 */
	public TaxCategory_EditPanel(JPanel contentPane, Store myStore, TaxCategory taxCategory, boolean isAdd) {
		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) 
			{
				listModel = new DefaultListModel<>();
				for(TaxRate taxRate : taxCategory.getTaxRates())
					listModel.addElement(taxRate);
				list.setModel(listModel);
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});
		setLayout(null);
		thisPanel=this;
		
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
		
		listModel =new DefaultListModel<>();
		for(TaxRate taxRate : taxCategory.getTaxRates()) {
			listModel.addElement(taxRate);
		}
		
		list = new JList<>(listModel);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) 
			{
				if(list.getSelectedValue() != null) {
					btnEdit.setEnabled(true);
					if(list.getSelectedValue().isUsed()) {
						btnDelete.setEnabled(false);
					}
					else{
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
				
				contentPane.removeAll();
				contentPane.add(new TaxRate_EditPanel(contentPane, thisPanel, myStore, taxCategory, new TaxRate(),true));
				contentPane.revalidate();
				
			}
		});
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAdd.setBounds(309, 226, 70, 24);
		add(btnAdd);
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new TaxRate_EditPanel(contentPane,thisPanel,  myStore,taxCategory, list.getSelectedValue(),false));
				contentPane.revalidate();
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
				listModel.removeElement(list.getSelectedValue());
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
				if(list!=null) {
					for (int i = 0; i < listModel.size(); i++) {
					    taxCategory.addTaxRate(listModel.get(i));
					    
					}
				}
				
				if(isAdd) {
					taxCategory.setCategory(textField.getText());
					if(list!=null) {
						for (int i = 0; i < listModel.size(); i++) {
						    taxCategory.addTaxRate(listModel.get(i));
						    
						}
					}
					
					myStore.addTaxCategory(taxCategory);
				}
				
				contentPane.removeAll();
				contentPane.add(new TaxCategory_ListPanel(contentPane,myStore));
				contentPane.revalidate();
				
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
				int response = JOptionPane.showConfirmDialog(
			            null,
			            "Do you want to cancel?",
			            "Cancel Confirmation",
			            JOptionPane.YES_NO_OPTION,
			            JOptionPane.QUESTION_MESSAGE
			        );
				
				if(response==JOptionPane.YES_OPTION) {
					contentPane.removeAll();
					contentPane.add(new TaxCategory_ListPanel(contentPane, myStore));
					contentPane.revalidate();
				}
				else {
					contentPane.repaint();
					}
				
				
			}
		});
		btnNewButton_1_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnNewButton_1_1.setBounds(321, 307, 118, 41);
		add(btnNewButton_1_1);
		
		

	}
}
