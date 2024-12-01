package POS_UI;

import javax.swing.JPanel;

import POS_PD.*;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.Color;

public class Sale_Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField itemField;
	private JTextField quantityField;
	private JTextField subTotalField;
	private JTextField taxField;
	private JTextField totalField;
	private JTextField amtTenderedField;
	private JTextField changeField;
	JButton btnCompleteSale, btnPayment;
	DefaultListModel<SaleLineItem> sliListModel;
	JLabel lblItemNotFound;

	/**
	 * Create the panel.
	 * @param sale 
	 * @param session 
	 * @param myStore 
	 * @param contentPane 
	 */
	public Sale_Panel(JPanel contentPane, Store myStore, Session session, Sale sale) {
		sale.setTaxFree(false);
		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) 
			{
				if(!sliListModel.isEmpty()) {
					
					btnPayment.setEnabled(true);
					subTotalField.setText(sale.calcSubTotal().toString());
					taxField.setText(sale.calcTax().toString());
					totalField.setText(sale.calcTotal().toString());
					amtTenderedField.setText(sale.calcAmtTendered().toString());
					BigDecimal p = new BigDecimal(0);
					if((sale.calcChange().compareTo(p))>=0) {
						changeField.setText(sale.calcChange().toString());
					}
					else {
						changeField.setText("Not yet Paid");
					}
					
					
					if(sale.calcAmtTendered().compareTo(sale.calcTotal())>=0) {
						btnCompleteSale.setEnabled(true);
					}
				}
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});
		setLayout(null);
		JPanel currentPanel=this;
		
		JLabel lblNewLabel = new JLabel("Cashier");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(40, 29, 79, 19);
		add(lblNewLabel);
		
		JLabel lblRegister = new JLabel("Register");
		lblRegister.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRegister.setBounds(40, 58, 79, 19);
		add(lblRegister);
		
		JLabel lblNewLabel_2 = new JLabel(session.getCashier().getPerson().getName());
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(118, 29, 79, 19);
		add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel(session.getRegister().getNumber());
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_3.setBounds(118, 58, 79, 19);
		add(lblNewLabel_3);
		
		JLabel lblNewLabel_1 = new JLabel("Sale");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_1.setBounds(308, 37, 85, 27);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_4 = new JLabel("Item");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_4.setBounds(40, 108, 79, 19);
		add(lblNewLabel_4);
		
		JLabel lblNewLabel_4_1 = new JLabel("Quantity");
		lblNewLabel_4_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_4_1.setBounds(291, 112, 79, 19);
		add(lblNewLabel_4_1);
		
		itemField = new JTextField();
		itemField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				Item item = myStore.findItemForNumber(itemField.getText());
				if(item != null) {
					SaleLineItem sli = new SaleLineItem(sale, item, quantityField.getText());
					sliListModel.addElement(sli);
					subTotalField.setText(sale.calcSubTotal().toString());
					taxField.setText(sale.calcTax().toString());
					totalField.setText(sale.calcTotal().toString());
					BigDecimal x  = new BigDecimal(0);
					lblItemNotFound.setVisible(false);
					if((sale.calcChange().compareTo(x))>=0) {
						changeField.setText(sale.calcChange().toString());
					}
					else {
						changeField.setText("Not yet Paid");
					}
								
					btnPayment.setEnabled(true);
				}
				else {
					lblItemNotFound.setVisible(true);
					//JOptionPane.showMessageDialog(null, "Item not found", "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		itemField.setBounds(77, 109, 96, 19);
		add(itemField);
		itemField.setColumns(10);
		
		quantityField = new JTextField();
		quantityField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		quantityField.setText("1");
		quantityField.setColumns(10);
		quantityField.setBounds(352, 109, 54, 19);
		add(quantityField);
		
		JCheckBox chckbxTaxFree = new JCheckBox("Tax Free");
		chckbxTaxFree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(chckbxTaxFree.isSelected()) {
					sale.setTaxFree(true);
					taxField.setText(sale.calcTax().toString());
					totalField.setText(sale.calcTotal().toString());
					if(!sale.getTotalPayments().equals(0)) {
						changeField.setText(sale.calcChange().toString());
					}
				}
				else {
					sale.setTaxFree(false);
					taxField.setText(sale.calcTax().toString());
					totalField.setText(sale.calcTotal().toString());
					if(!sale.getTotalPayments().equals(0)) {
						changeField.setText(sale.calcChange().toString());
					}
				}
			}
		});
		chckbxTaxFree.setFont(new Font("Tahoma", Font.PLAIN, 12));
		chckbxTaxFree.setBounds(486, 108, 93, 21);
		add(chckbxTaxFree);
		
		sliListModel = new DefaultListModel<>();
		
		JList<SaleLineItem> list = new JList<>(sliListModel);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) 
			{
				// TODO: Action listener for SaleLineItem
//				if(list!=null){
//					btnPayment.setEnabled(true);
//				}
			}
		});
		list.setBounds(77, 168, 293, 114);
		add(list);
		
		JLabel lblSubTotal = new JLabel("Sub Total");
		lblSubTotal.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblSubTotal.setBounds(432, 169, 79, 19);
		add(lblSubTotal);
		
		JLabel lblTax = new JLabel("Tax");
		lblTax.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTax.setBounds(432, 203, 79, 19);
		add(lblTax);
		
		JLabel lblTotal = new JLabel("Total");
		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTotal.setBounds(432, 242, 79, 19);
		add(lblTotal);
		
		JLabel lblAmtTendered = new JLabel("Amt Tendered");
		lblAmtTendered.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblAmtTendered.setBounds(432, 319, 93, 19);
		add(lblAmtTendered);
		
		JLabel lblChange = new JLabel("Change");
		lblChange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblChange.setBounds(432, 361, 79, 19);
		add(lblChange);
		
		subTotalField = new JTextField();
		subTotalField.setBounds(521, 166, 96, 19);
		add(subTotalField);
		subTotalField.setColumns(10);
		
		taxField = new JTextField();
		taxField.setColumns(10);
		taxField.setBounds(521, 204, 96, 19);
		add(taxField);
		
		totalField = new JTextField();
		totalField.setColumns(10);
		totalField.setBounds(521, 243, 96, 19);
		add(totalField);
		
		amtTenderedField = new JTextField();
		amtTenderedField.setColumns(10);
		amtTenderedField.setBounds(521, 320, 96, 19);
		add(amtTenderedField);
		
		changeField = new JTextField();
		changeField.setColumns(10);
		changeField.setBounds(521, 362, 96, 19);
		add(changeField);
		
		
		btnPayment = new JButton("Payment");
		btnPayment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new Payment_Panel(contentPane, currentPanel, myStore, session, sale));
				contentPane.revalidate();
			}
		});
		btnPayment.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnPayment.setBounds(72, 361, 101, 19);
		add(btnPayment);
		btnPayment.setEnabled(false);
		
		JButton btnCancelSale = new JButton("Cancel Sale");
		btnCancelSale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int response = JOptionPane.showConfirmDialog(
			            null,
			            "Do you want to cancel?",
			            "Cancel Confirmation",
			            JOptionPane.YES_NO_OPTION,
			            JOptionPane.QUESTION_MESSAGE
			        );
				
				if(response==JOptionPane.YES_OPTION) {
					contentPane.removeAll();
					contentPane.add(new Sale_Panel(contentPane, myStore, session, new Sale()));
					contentPane.revalidate();
				}
				else {
					contentPane.repaint();
					}
				
				
			}
		});
		btnCancelSale.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCancelSale.setBounds(72, 406, 101, 21);
		add(btnCancelSale);
		
		btnCompleteSale = new JButton("Complete Sale");
		btnCompleteSale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				session.getRegister().getCashDrawer().addCash(sale.getTotalPayments());
				session.getRegister().getCashDrawer().removeCash(sale.calcChange());
				session.setEndDateTime(LocalDateTime.now());
				session.addSale(sale);
				
				contentPane.removeAll();
				contentPane.add(new Sale_Panel(contentPane, myStore, session, new Sale()));
				contentPane.revalidate();
			}
		});
		btnCompleteSale.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCompleteSale.setBounds(216, 360, 107, 21);
		add(btnCompleteSale);
		btnCompleteSale.setEnabled(false);
		
		JButton btnEndSession = new JButton("End Session");
		btnEndSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				myStore.addSession(session);
				contentPane.removeAll();
				contentPane.add( new EndSession_Panel(contentPane, myStore, session));
				contentPane.revalidate();
			}
		});
		btnEndSession.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnEndSession.setBounds(216, 406, 107, 21);
		add(btnEndSession);
		
		lblItemNotFound = new JLabel("ITEM NOT FOUND");
		lblItemNotFound.setForeground(new Color(255, 0, 0));
		lblItemNotFound.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblItemNotFound.setBounds(180, 112, 101, 15);
		add(lblItemNotFound);
		lblItemNotFound.setVisible(false);
		

	}
}
