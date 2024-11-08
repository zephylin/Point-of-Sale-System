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

public class Register_ListPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	JButton btnAdd, btnEdit, btnDelete;

	/**
	 * Create the panel.
	 * @param myStore 
	 * @param contentPane 
	 */
	public Register_ListPanel(JPanel contentPane, Store myStore) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Register List");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(246, 92, 75, 21);
		add(lblNewLabel);
		
		DefaultListModel<Register> listModel = new DefaultListModel<>();
		for(Register register : myStore.getRegisters().values()) {
			listModel.addElement(register);
		}
		
		JList<Register> list = new JList<>(listModel);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) 
			{
				if(list.getSelectedValue()!=null) {
					btnEdit.setEnabled(true);
					if(list.getSelectedValue().getSessions().size()==0) {
						btnDelete.setEnabled(true);
					}
				}
			}
		});
		list.setBounds(212, 135, 152, 112);
		add(list);
		
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new Register_EditPanel(contentPane, myStore,new Register(),true));
				contentPane.revalidate();
			}
		});
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnAdd.setBounds(139, 281, 85, 26);
		add(btnAdd);
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new Register_EditPanel(contentPane, myStore, list.getSelectedValue(), false));
				contentPane.revalidate();
			}
		});
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnEdit.setBounds(260, 281, 85, 26);
		add(btnEdit);
		btnEdit.setEnabled(false);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				myStore.removeRegister(list.getSelectedValue());
				listModel.removeElement(list.getSelectedValue());
			}
		});
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnDelete.setBounds(376, 281, 85, 26);
		add(btnDelete);
		btnDelete.setEnabled(false);
		

	}

}
