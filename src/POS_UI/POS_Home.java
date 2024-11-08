package POS_UI;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;

import javax.swing.SwingConstants;
import java.awt.Color;
import POS_PD.*;

public class POS_Home extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public POS_Home(JFrame currentFrame, Store myStore) {
		setLayout(null);
		
		JLabel lblStoreName = new JLabel(myStore.getName());
		lblStoreName.setBackground(new Color(240, 240, 240));
		lblStoreName.setHorizontalAlignment(SwingConstants.CENTER);
		lblStoreName.setFont(new Font("Segoe UI Black", Font.BOLD | Font.ITALIC, 17));
		lblStoreName.setBounds(146, 39, 254, 52);
		add(lblStoreName);
		
		ImageIcon icon=new ImageIcon("logo.png");
		Image img = icon.getImage();
		Image resizedImg = img.getScaledInstance(116, 52, java.awt.Image.SCALE_SMOOTH);
		ImageIcon resizedIcon = new ImageIcon(resizedImg);
		JLabel lblNewLabel = new JLabel(resizedIcon);
		lblNewLabel.setBounds(442, 39, 116, 52);
		add(lblNewLabel);
		
		ImageIcon icon2=new ImageIcon("food.jpg");
		Image img1 = icon2.getImage();
		Image resizedImg1 = img1.getScaledInstance(400, 300, java.awt.Image.SCALE_SMOOTH);
		ImageIcon resizedIcon1 = new ImageIcon(resizedImg1);
		JLabel lblNewLabel_1 = new JLabel(resizedIcon1);
		lblNewLabel_1.setBounds(146, 183, 412, 212);
		add(lblNewLabel_1);

	}
}
