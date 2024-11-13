package POS_UI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import POS_PD.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class Store_EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;

	/**
	 * Create the panel.
	 */
	public Store_EditPanel(JPanel contentPane, Store myStore)
	{
		setLayout(null);
		
		JLabel lblStoreName = new JLabel("Store Name");
		lblStoreName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblStoreName.setBounds(146, 229, 92, 25);
		add(lblStoreName);
		
		textField = new JTextField(myStore.getName());
		textField.setBounds(277, 231, 177, 23);
		add(textField);
		textField.setColumns(10);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				
				myStore.setName(textField.getText());
				contentPane.removeAll();
				contentPane.add(new POS_Home(contentPane, myStore));
				contentPane.revalidate();
				
			}
		});
		btnSave.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSave.setBounds(146, 314, 85, 34);
		add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new POS_Home(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCancel.setBounds(369, 314, 85, 34);
		add(btnCancel);
		
		JLabel lblNewLabel = new JLabel("Edit Store");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(98, 135, 150, 34);
		add(lblNewLabel);
		

	}
}
