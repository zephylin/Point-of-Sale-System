package POS_UI;
import POS_PD.*;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class POS_Frame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void run(Store myStore) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					POS_Frame frame = new POS_Frame(myStore);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public POS_Frame(Store myStore) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 600);
		
		JFrame myFrame=this;
		myFrame.setTitle(myStore.getName());
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnMaintain = new JMenu("Maintain");
		menuBar.add(mnMaintain);
		
		JMenuItem mntmStore = new JMenuItem("Store");
		mntmStore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				contentPane.removeAll();
				contentPane.add(new Store_EditPanel(contentPane,myStore));
				contentPane.revalidate();
			}
		});
		mnMaintain.add(mntmStore);
		
		JMenuItem mntmTaxCategory = new JMenuItem("Tax Category");
		mntmTaxCategory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new TaxCategory_ListPanel(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		mnMaintain.add(mntmTaxCategory);
		
		JMenuItem mntmItem = new JMenuItem("Items");
		mntmItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.removeAll();
				contentPane.add(new Item_ListPanel(contentPane, myStore));
				contentPane.revalidate();
				
			}
		});
		mnMaintain.add(mntmItem);
		
		JMenuItem mntmCashier = new JMenuItem("Cashiers");
		mntmCashier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				contentPane.removeAll();
				contentPane.add(new Cashier_ListPanel(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		mnMaintain.add(mntmCashier);
		
		JMenuItem mntmRegister = new JMenuItem("Registers");
		mntmRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				contentPane.removeAll();
				contentPane.add(new Register_ListPanel(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		mnMaintain.add(mntmRegister);
		
		JMenu mnPos = new JMenu("POS");
		menuBar.add(mnPos);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Login");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				contentPane.removeAll();
				contentPane.add(new Login_Panel(contentPane, myStore));
				contentPane.revalidate();
			}
		});
		mnPos.add(mntmNewMenuItem);
		
		JMenu mnReport = new JMenu("Reports");
		menuBar.add(mnReport);
		
		contentPane = new POS_Home(contentPane, myStore);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

}
