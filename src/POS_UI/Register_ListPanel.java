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

public class Register_ListPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	JButton btnAdd, btnEdit, btnDelete;
	private JButton btnHome;

	/**
	 * Create the panel.
	 * @param myStore 
	 * @param contentPane 
	 */
	public Register_ListPanel(JPanel contentPane, Store myStore) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Register List");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(310, 108, 75, 21);
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
		list.setBounds(276, 151, 152, 112);
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
		btnAdd.setBounds(203, 297, 85, 26);
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
		btnEdit.setBounds(324, 297, 85, 26);
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
		btnDelete.setBounds(440, 297, 85, 26);
		add(btnDelete);
		btnDelete.setEnabled(false);
		
		btnHome = new JButton("Home");
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
		btnHome.setBounds(31, 25, 97, 33);
		add(btnHome);
		

	}

}
