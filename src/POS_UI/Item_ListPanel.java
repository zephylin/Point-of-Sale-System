package POS_UI;

import javax.swing.JPanel;

import POS_PD.*;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class Item_ListPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	JButton btnEdit, btnDelete;

	/**
	 * Create the panel.
	 * @param myStore 
	 * @param contentPane 
	 */
	public Item_ListPanel(JPanel contentPane, Store myStore) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Item List");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel.setBounds(269, 77, 167, 29);
		add(lblNewLabel);
		
		DefaultListModel<Item> listModel = new DefaultListModel<>();
		for (Item item : myStore.getItems().values()) {
			listModel.addElement(item);
		}
		
		JList<Item> list = new JList<>(listModel);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
			if(list.getSelectedValue()!=null) {
				btnEdit.setEnabled(true);
				if((list.getSelectedValue().getPrices().isEmpty()||list.getSelectedValue().getuPCs().isEmpty())) {
					btnDelete.setEnabled(true);
				}
				else {
					btnDelete.setEnabled(false);
				}
			}
			else {
				btnEdit.setEnabled(false);
				btnDelete.setEnabled(false);
			}
			}
		});
		list.setBounds(195, 142, 324, 202);
		add(list);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new Item_EditPanel(contentPane, myStore,new Item(), true));
				contentPane.revalidate();
			}
		});
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAdd.setBounds(149, 399, 99, 29);
		add(btnAdd);
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				contentPane.removeAll();
				contentPane.add(new Item_EditPanel(contentPane, myStore,list.getSelectedValue(), false));
				contentPane.revalidate();
			}
		});
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnEdit.setBounds(301, 399, 99, 29);
		add(btnEdit);
		btnEdit.setEnabled(false);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				myStore.removeItem(list.getSelectedValue());
				listModel.removeElement(list.getSelectedValue());
			}
		});
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnDelete.setBounds(454, 399, 99, 29);
		add(btnDelete);
		btnDelete.setEnabled(false);
		
		JButton btnHome = new JButton("Home");
		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new POS_Home(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		btnHome.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnHome.setBackground(new Color(128, 255, 255));
		btnHome.setBounds(26, 24, 97, 33);
		add(btnHome);

	}
}
