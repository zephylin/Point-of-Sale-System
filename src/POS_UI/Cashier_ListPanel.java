package POS_UI;

import javax.swing.JPanel;

import POS_PD.Store;

import javax.swing.JList;
import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

import POS_PD.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Cashier_ListPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	JButton btnEdit, btnDelete, btnAdd;
	

	/**
	 * Create the panel.
	 * @param myStore 
	 * @param myFrame 
	 */
	public Cashier_ListPanel(JFrame myFrame, Store myStore) {
		setLayout(null);
		
		JLabel lblCashierList = new JLabel("Cashier List");
		lblCashierList.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCashierList.setBounds(128, 10, 97, 23);
		add(lblCashierList);
		
		DefaultListModel<Cashier> listModel = new DefaultListModel<>();
		for(Cashier cashier : myStore.getCashiers().values()) 
		{
			listModel.addElement(cashier);
		}
		

		JList<Cashier> cashierList = new JList<>(listModel);
		cashierList.addListSelectionListener(new ListSelectionListener() 
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if(cashierList.getSelectedValue() != null) {
					if(cashierList.getSelectedValue() != null)
						btnEdit.setEnabled(true);
					if(cashierList.getSelectedValue().hasSession()) {
						btnDelete.setEnabled(false);
					}
					else {
						btnDelete.setEnabled(true);
					}
				}
				
				else {
					btnEdit.setEnabled(false);
					btnDelete.setEnabled(false);
				}
			}
		});
		cashierList.setBounds(90, 43, 184, 153);
		add(cashierList);
		
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				myFrame.getContentPane().removeAll();
				myFrame.getContentPane().add(new Cashier_EditPanel(myFrame, myStore, new Cashier(),true));
				myFrame.getContentPane().revalidate();
			}
		});
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAdd.setBounds(62, 229, 85, 33);
		add(btnAdd);
		
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				myFrame.getContentPane().removeAll();
				myFrame.getContentPane().add(new Cashier_EditPanel(myFrame, myStore, cashierList.getSelectedValue(),false));
				myFrame.getContentPane().revalidate();
			}
		});
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnEdit.setBounds(177, 229, 97, 33);
		add(btnEdit);
		btnEdit.setEnabled(false);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				myStore.removeCashier(cashierList.getSelectedValue());
				listModel.removeElement(cashierList.getSelectedValue());
				btnDelete.setEnabled(false);
			}
		});
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnDelete.setBounds(312, 229, 97, 33);
		add(btnDelete);
		btnDelete.setEnabled(false);
		
		
		
		
		

	}
}
