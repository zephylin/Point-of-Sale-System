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
			}
		});
		list.setBounds(214, 142, 204, 202);
		add(list);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new Item_EditPanel(contentPane, myStore,new Item(), false));
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
				contentPane.add(new Item_EditPanel(contentPane, myStore,list.getSelectedValue(), true));
				
				contentPane.revalidate();
			}
		});
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnEdit.setBounds(301, 399, 99, 29);
		add(btnEdit);
		btnEdit.setEnabled(false);
		
		btnDelete = new JButton("Delete");
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnDelete.setBounds(454, 399, 99, 29);
		add(btnDelete);
		btnDelete.setEnabled(false);

	}
}
