package POS_UI;

import javax.swing.JPanel;

import POS_PD.Register;
import POS_PD.Store;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Register_EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;

	/**
	 * Create the panel.
	 * @param isAdd 
	 * @param register 
	 * @param myStore 
	 * @param contentPane 
	 */
	public Register_EditPanel(JPanel contentPane, Store myStore, Register register, boolean isAdd) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Register Number");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(177, 158, 96, 20);
		add(lblNewLabel);
		
		textField = new JTextField(register.getNumber());
		textField.setBounds(300, 160, 90, 20);
		add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Save");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				register.setNumber(textField.getText());
				if(isAdd) {
					myStore.addRegister(register);
				}
				
				contentPane.removeAll();
				contentPane.add(new Register_ListPanel(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewButton.setBounds(162, 273, 85, 27);
		add(btnNewButton);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new Register_ListPanel(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCancel.setBounds(305, 273, 85, 27);
		add(btnCancel);
		

	}

}
