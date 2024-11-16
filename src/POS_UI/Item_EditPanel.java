package POS_UI;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JTextField;
import POS_PD.*;
import javax.swing.JComboBox;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;

public class Item_EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	JButton btnEdit, btnDelete, btnEdit_1, btnDelete_1;
	JComboBox<TaxCategory> comboBox;
	JPanel currentPanel = this;
	DefaultListModel<UPC> upcListModel;
	DefaultListModel<Price> priceListModel;

	/**
	 * Create the panel.
	 */
	public Item_EditPanel(JPanel contentPane, Store myStore,Item item, boolean isAdd) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Item Number*");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(137, 137, 89, 24);
		add(lblNewLabel);
		
		JLabel lblItemDescription = new JLabel("Item Description*");
		lblItemDescription.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblItemDescription.setBounds(137, 196, 109, 24);
		add(lblItemDescription);
		
		JLabel lblItem = new JLabel("Tax Category*");
		lblItem.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblItem.setBounds(137, 249, 89, 24);
		add(lblItem);
		
		JLabel lblItemUpc = new JLabel("Item UPC");
		lblItemUpc.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblItemUpc.setBounds(389, 137, 89, 24);
		add(lblItemUpc);
		
		JLabel lblItemPrice = new JLabel("Item Price");
		lblItemPrice.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblItemPrice.setBounds(389, 249, 89, 24);
		add(lblItemPrice);
		
		upcListModel = new DefaultListModel<>();
		for(UPC upc : item.getuPCs().values()) {
			upcListModel.addElement(upc);
		}
		
		JList<UPC> list = new JList<>(upcListModel);
		list.addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) 
			{
				upcListModel = new DefaultListModel<>();
				for(UPC upc : item.getuPCs().values()) {
					upcListModel.addElement(upc);
				}
				
				list.setModel(upcListModel);
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) 
			{
				if(list.getSelectedValue()!=null) {
					btnEdit.setEnabled(true);
					btnDelete.setEnabled(true);
				}
				else {
					btnEdit.setEnabled(false);
					btnDelete.setEnabled(false);
				}
			}
		});
		list.setBounds(488, 143, 124, 55);
		add(list);
		
		
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new UPC_EditPanel(contentPane, currentPanel,  myStore, item, new UPC(), true));
				contentPane.revalidate();	
			}
		});
		btnAdd.setBounds(413, 208, 60, 21);
		add(btnAdd);
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new UPC_EditPanel(contentPane,currentPanel, myStore,  item, list.getSelectedValue(), false));
				contentPane.revalidate();
			}
		});
		btnEdit.setBounds(498, 208, 60, 21);
		add(btnEdit);
		btnEdit.setEnabled(false);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				item.removeUPC(list.getSelectedValue());
				upcListModel.removeElement(list.getSelectedValue());
			}
		});
		btnDelete.setBounds(582, 208, 60, 21);
		add(btnDelete);
		btnDelete.setEnabled(false);
		
		
		priceListModel = new DefaultListModel<>();
		for(Price price : item.getPrices()) {
			priceListModel.addElement(price);
		}
		JList<Price> list_1 = new JList<>(priceListModel);
		list_1.addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) 
			{
				priceListModel = new DefaultListModel<Price>();
				for(Price price : item.getPrices()) {
					priceListModel.addElement(price);
				}
				list_1.setModel(priceListModel);
				
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});
		list_1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) 
			{
				if(list_1.getSelectedValue()!=null) {
					btnEdit_1.setEnabled(true);
					btnDelete_1.setEnabled(true);
				}
				else {
					btnEdit_1.setEnabled(false);
					btnDelete_1.setEnabled(false);
					
				}
				
			}
		});
		list_1.setBounds(488, 255, 124, 55);
		add(list_1);
		
		JButton btnAdd_1 = new JButton("Add");
		btnAdd_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new Price_EditPanel(contentPane,currentPanel, myStore,  item, new Price(), true));
				contentPane.revalidate();
			}
		});
		btnAdd_1.setBounds(413, 320, 60, 21);
		add(btnAdd_1);
		
		
		btnEdit_1 = new JButton("Edit");
		btnEdit_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new Price_EditPanel(contentPane,currentPanel, myStore,  item, list_1.getSelectedValue(), false));
				contentPane.revalidate();
			}
		});
		btnEdit_1.setBounds(498, 320, 60, 21);
		add(btnEdit_1);
		btnEdit_1.setEnabled(false);
		
		btnDelete_1 = new JButton("Delete");
		btnDelete_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				item.removePrice(list_1.getSelectedValue());
				priceListModel.removeElement(list_1.getSelectedValue());
			}
		});
		btnDelete_1.setBounds(582, 320, 60, 21);
		add(btnDelete_1);
		btnDelete_1.setEnabled(false);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				item.setNumber(textField.getText());
				item.setDescription(textField_1.getText());
				item.setTaxCategory((TaxCategory)comboBox.getSelectedItem());
				
				
				if(item.getTaxCategory()==null) {
					JOptionPane.showMessageDialog(null, "Please Select the item Tax Category", "Message", JOptionPane.INFORMATION_MESSAGE);
					contentPane.removeAll();
					contentPane.add(new Item_EditPanel(contentPane, myStore, new Item(), true));
					contentPane.revalidate();
				}
				else {
					if(isAdd) {
						myStore.addItem(item);
					}
					contentPane.removeAll();
					contentPane.add(new Item_ListPanel(contentPane, myStore));
					contentPane.revalidate();
				}
				
				
			}
		});
		btnSave.setForeground(new Color(0, 0, 0));
		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnSave.setBackground(new Color(0, 128, 255));
		btnSave.setBounds(205, 395, 97, 34);
		add(btnSave);
		
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
					contentPane.add(new Item_ListPanel(contentPane, myStore));
					contentPane.revalidate();
				}
				else {
					contentPane.repaint();
					}
			
				
			}
		});
		btnCancel.setBackground(new Color(128, 128, 128));
		btnCancel.setForeground(new Color(255, 255, 255));
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCancel.setBounds(413, 395, 97, 34);
		add(btnCancel);
		
		textField = new JTextField(item.getNumber());
		textField.setBounds(256, 137, 103, 24);
		add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField(item.getDescription());
		textField_1.setColumns(10);
		textField_1.setBounds(256, 196, 103, 24);
		add(textField_1);
		
		DefaultComboBoxModel<TaxCategory> taxListModel = new DefaultComboBoxModel<>();
		for(TaxCategory taxCategory : myStore.getTaxCategories().values()) {
			taxListModel.addElement(taxCategory);
		}
		
		comboBox = new JComboBox<>(taxListModel);
		if(isAdd) {
			comboBox.setSelectedItem(null);
		}
		else
			comboBox.setSelectedItem(item.getTaxCategory());
		comboBox.setBounds(260, 252, 99, 21);
		add(comboBox);

	}
}
