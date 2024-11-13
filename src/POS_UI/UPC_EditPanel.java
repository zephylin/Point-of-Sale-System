package POS_UI;

import javax.swing.JPanel;

import POS_PD.*;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class UPC_EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;

	/**
	 * Create the panel.
	 * @param isAdd 
	 * @param upc 
	 * @param item 
	 * @param contentPane 
	 * @param currentPanel 
	 * @param myStore 
	 */
	public UPC_EditPanel(JPanel contentPane, JPanel currentPanel, Store myStore, Item item, UPC upc, boolean isAdd) {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("UPC");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(216, 144, 90, 21);
		add(lblNewLabel);
		
		textField = new JTextField(upc.getuPC());
		textField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField.setBounds(343, 145, 113, 19);
		add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Save");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				upc.setuPC(textField.getText());
				
				if(isAdd) {
					upc.setItem(item);
					item.addUPC(upc);
					myStore.addUPC(upc);
				}
				
				contentPane.removeAll();
				contentPane.add(currentPanel);
				contentPane.repaint();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewButton.setBounds(216, 283, 85, 28);
		add(btnNewButton);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(currentPanel);
				contentPane.repaint();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCancel.setBounds(371, 283, 85, 28);
		add(btnCancel);
		

	}
}
