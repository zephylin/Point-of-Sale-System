package POS_UI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import POS_PD.*;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class TaxCategory_ListPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	JButton btnEdit, btnAdd, btnDelete;

	/**
	 * Create the panel.
	 */
	public TaxCategory_ListPanel(JPanel contentPane, Store myStore) {
		setLayout(null);
		
		DefaultListModel<TaxCategory> listModel = new DefaultListModel<>();
		
		for(TaxCategory taxCategory : myStore.getTaxCategories().values()) {
			listModel.addElement(taxCategory);
		}
		
		JList<TaxCategory> list = new JList<>(listModel);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) 
			{
				if(list.getSelectedValue() != null) {
					btnEdit.setEnabled(true);
					
					if(list.getSelectedValue().hasItem()) {
						btnDelete.setEnabled(false);
					}
					else {
						btnDelete.setEnabled(true);
					}
				}
			}
		});
		list.setBounds(246, 102, 191, 175);
		add(list);
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new TaxCategory_EditPanel(contentPane, myStore, list.getSelectedValue(),false));
				contentPane.revalidate();
			}
		});
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnEdit.setBounds(300, 350, 102, 33);
		add(btnEdit);
		btnEdit.setEnabled(false);
		
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new TaxCategory_EditPanel(contentPane, myStore, new TaxCategory(),true));
				contentPane.revalidate();
			}
		});
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAdd.setBounds(148, 350, 102, 33);
		add(btnAdd);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				myStore.removeTaxCategory(list.getSelectedValue());
				listModel.removeElement(list.getSelectedValue());
				btnDelete.setEnabled(false);
			}
		});
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnDelete.setBounds(433, 350, 102, 33);
		add(btnDelete);
		btnDelete.setEnabled(false);
		
		JButton btnNewButton = new JButton("Home");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				contentPane.removeAll();
				contentPane.add(new POS_Home(contentPane, myStore));
				contentPane.revalidate();			}
		});
		btnNewButton.setBackground(new Color(128, 255, 255));
		btnNewButton.setForeground(new Color(0, 0, 0));
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewButton.setBounds(42, 31, 85, 33);
		add(btnNewButton);
		
		
		
		
		
		

	}
}
