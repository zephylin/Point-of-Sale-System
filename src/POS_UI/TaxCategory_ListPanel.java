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

public class TaxCategory_ListPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	JButton btnEdit, btnAdd, btnDelete;

	/**
	 * Create the panel.
	 */
	public TaxCategory_ListPanel(JFrame myFrame, Store myStore) {
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
		list.setBounds(192, 83, 191, 175);
		add(list);
		
		btnEdit = new JButton("Edit");
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnEdit.setBounds(94, 331, 102, 33);
		add(btnEdit);
		btnEdit.setEnabled(false);
		
		btnAdd = new JButton("Add");
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAdd.setBounds(240, 331, 102, 33);
		add(btnAdd);
		
		btnDelete = new JButton("Delete");
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnDelete.setBounds(379, 331, 102, 33);
		add(btnDelete);
		btnDelete.setEnabled(false);
		
		
		
		
		
		

	}

}
