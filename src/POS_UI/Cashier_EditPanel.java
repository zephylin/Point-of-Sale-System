package POS_UI;

import javax.swing.JFrame;
import javax.swing.JPanel;

import POS_PD.Cashier;
import POS_PD.Store;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class Cashier_EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField numberTextField;
	private JTextField nameTextField;
	private JTextField addressTextField;
	private JTextField cityTextField;
	private JTextField stateTextField;
	private JTextField zipTextField;
	private JTextField phoneTextField;
	private JTextField pwdTextField;
	private JTextField ssnTextField;

	/**
	 * Create the panel.
	 * @param myStore 
	 * @param myFrame 
	 * @param cashier 
	 */
	public Cashier_EditPanel(JFrame myFrame, Store myStore, Cashier cashier, boolean isAdd) {
		setLayout(null);
		
		JLabel lblEditCashier = new JLabel("Edit Cashier");
		lblEditCashier.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEditCashier.setBounds(224, 35, 123, 25);
		add(lblEditCashier);
		
		JLabel lblCashierName = new JLabel("Name");
		lblCashierName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCashierName.setBounds(74, 105, 86, 25);
		add(lblCashierName);
		
		JLabel lblNumber = new JLabel("Number");
		lblNumber.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNumber.setBounds(74, 70, 86, 25);
		add(lblNumber);
		
		JLabel lblAddress = new JLabel("Address");
		lblAddress.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblAddress.setBounds(74, 140, 86, 25);
		add(lblAddress);
		
		JLabel lblCity = new JLabel("City");
		lblCity.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCity.setBounds(128, 175, 86, 25);
		add(lblCity);
		
		JLabel lblState = new JLabel("State");
		lblState.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblState.setBounds(128, 210, 86, 25);
		add(lblState);
		
		JLabel lblZip = new JLabel("Zip");
		lblZip.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblZip.setBounds(128, 245, 86, 25);
		add(lblZip);
		
		JLabel lblSsn = new JLabel("SSN");
		lblSsn.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblSsn.setBounds(331, 70, 86, 25);
		add(lblSsn);
		
		JLabel lblPhone = new JLabel("Phone");
		lblPhone.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPhone.setBounds(74, 280, 86, 25);
		add(lblPhone);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPassword.setBounds(74, 315, 86, 25);
		add(lblPassword);
		
		numberTextField = new JTextField(cashier.getNumber());
		numberTextField.setBounds(141, 76, 106, 19);
		add(numberTextField);
		numberTextField.setColumns(10);
		
		nameTextField = new JTextField(cashier.getPerson().getName());
		nameTextField.setColumns(10);
		nameTextField.setBounds(141, 109, 106, 19);
		add(nameTextField);
		
		addressTextField = new JTextField(cashier.getPerson().getAddress());
		addressTextField.setColumns(10);
		addressTextField.setBounds(141, 144, 106, 19);
		add(addressTextField);
		
		cityTextField = new JTextField(cashier.getPerson().getCity());
		cityTextField.setColumns(10);
		cityTextField.setBounds(177, 179, 106, 19);
		add(cityTextField);
		
		stateTextField = new JTextField(cashier.getPerson().getState());
		stateTextField.setColumns(10);
		stateTextField.setBounds(177, 214, 106, 19);
		add(stateTextField);
		
		zipTextField = new JTextField(cashier.getPerson().getZip());
		zipTextField.setColumns(10);
		zipTextField.setBounds(177, 249, 106, 19);
		add(zipTextField);
		
		phoneTextField = new JTextField(cashier.getPerson().getPhone());
		phoneTextField.setColumns(10);
		phoneTextField.setBounds(141, 284, 106, 19);
		add(phoneTextField);
		
		pwdTextField = new JTextField(cashier.getPassword());
		pwdTextField.setColumns(10);
		pwdTextField.setBounds(141, 319, 106, 19);
		add(pwdTextField);
		
		ssnTextField = new JTextField(cashier.getPerson().getsSN());
		ssnTextField.setColumns(10);
		ssnTextField.setBounds(389, 74, 106, 19);
		add(ssnTextField);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBackground(new Color(0, 128, 255));
		btnSave.setForeground(new Color(0, 0, 0));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				cashier.setNumber(numberTextField.getText());
				cashier.setPassword(pwdTextField.getText());
				cashier.getPerson().setName(nameTextField.getText());
				cashier.getPerson().setsSN(ssnTextField.getText());
				cashier.getPerson().setAddress(addressTextField.getText());
				cashier.getPerson().setCity(cityTextField.getText());
				cashier.getPerson().setState(stateTextField.getText());
				cashier.getPerson().setZip(zipTextField.getText());
				cashier.getPerson().setPhone(phoneTextField.getText());
				
				if(isAdd) {
					cashier.getPerson().setCashier(cashier);
					myStore.addCashier(cashier);
				}
				
				myFrame.getContentPane().removeAll();
				myFrame.getContentPane().add(new Cashier_ListPanel(myFrame, myStore));
				myFrame.getContentPane().revalidate();
				
			}
		});
		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnSave.setBounds(186, 375, 97, 37);
		add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBackground(new Color(128, 128, 128));
		btnCancel.setForeground(new Color(255, 255, 255));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				myFrame.getContentPane().removeAll();
				myFrame.getContentPane().add(new Cashier_ListPanel(myFrame, myStore));
				myFrame.getContentPane().revalidate();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCancel.setBounds(350, 375, 97, 37);
		add(btnCancel);

	}
}
