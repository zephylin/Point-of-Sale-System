package POS_UI;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;

import POS_PD.*;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JCheckBox;

public class Login_Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JPasswordField passwordField;

	/**
	 * Create the panel.
	 * @param myStore 
	 * @param contentPane 
	 */
	public Login_Panel(JPanel contentPane, Store myStore) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Login");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.ITALIC, 14));
		lblNewLabel.setBounds(279, 40, 74, 28);
		add(lblNewLabel);
		
		JLabel lblMessage = new JLabel("INVALID PASSWORD");
		lblMessage.setForeground(new Color(255, 0, 0));
		lblMessage.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblMessage.setBounds(251, 302, 150, 17);
		add(lblMessage);
		lblMessage.setVisible(false);
		
		JLabel lblNewLabel_1 = new JLabel("Cashier Number/Name");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(155, 139, 132, 22);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Register Number");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1_1.setBounds(155, 180, 132, 22);
		add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_1_2 = new JLabel("Starting Cash");
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1_2.setBounds(155, 223, 132, 22);
		add(lblNewLabel_1_2);
		
		JLabel lblNewLabel_1_3 = new JLabel("Password");
		lblNewLabel_1_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1_3.setBounds(155, 270, 132, 22);
		add(lblNewLabel_1_3);
		
		DefaultComboBoxModel<Cashier> cashierListModel = new DefaultComboBoxModel<>();
		for(Cashier cashier : myStore.getCashiers().values()) {
			cashierListModel.addElement(cashier);
		}
		JComboBox<Cashier> comboBox = new JComboBox<>(cashierListModel);
		comboBox.setBounds(332, 141, 100, 21);
		add(comboBox);
		comboBox.setSelectedItem(null);
		
		DefaultComboBoxModel<Register> registerListModel = new DefaultComboBoxModel<>();
		for(Register register : myStore.getRegisters().values()) {
			registerListModel.addElement(register);
		}
		JComboBox<Register> comboBox_1 = new JComboBox<>(registerListModel);
		comboBox_1.setBounds(332, 182, 100, 21);
		add(comboBox_1);
		comboBox_1.setSelectedItem(null);
		
		textField = new JTextField("0.00");
		textField.setBounds(332, 226, 100, 19);
		add(textField);
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(332, 273, 100, 19);
		add(passwordField);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				Cashier cashier = (Cashier)(comboBox.getSelectedItem());
				Register register = (Register)(comboBox_1.getSelectedItem());
				String password = new String(passwordField.getText());
				if(cashier==null || register==null) {
					JOptionPane.showMessageDialog(null, "Please select both Cashier and Register", "Error", JOptionPane.ERROR_MESSAGE);
				}
				else 
				{
					if(cashier.isAuthorized(password)) {
						register.getCashDrawer().addCash(new BigDecimal(textField.getText()));
						Session session = new Session(cashier, register, myStore);
						contentPane.removeAll();
						contentPane.add(new Sale_Panel(contentPane, myStore, session, new Sale()));
						contentPane.revalidate();					
					
					}
					else {
					//JOptionPane.showMessageDialog(null, "Password is incorrect, Please provide correct Passsword", "Error", JOptionPane.ERROR_MESSAGE);
					lblMessage.setVisible(true);
				}
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewButton.setBounds(210, 359, 85, 28);
		add(btnNewButton);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				contentPane.removeAll();
				contentPane.add(new POS_Home(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCancel.setBounds(339, 359, 85, 28);
		add(btnCancel);
		
		JCheckBox checkBox = new JCheckBox("View Password");
		checkBox.setFont(new Font("Tahoma", Font.PLAIN, 10));
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(checkBox.isSelected()) {
					passwordField.setEchoChar((char) 0); // Show password
				}
				else {
					passwordField.setEchoChar('*'); // Hide password
                    
				}
			}
		});
		checkBox.setBounds(438, 272, 128, 21);
		add(checkBox);
		


	}
}
