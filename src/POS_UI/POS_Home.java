package POS_UI;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
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
		lblStoreName.setFont(new Font("Tahoma", Font.ITALIC, 14));
		lblStoreName.setBounds(72, 122, 311, 52);
		add(lblStoreName);

	}
}
