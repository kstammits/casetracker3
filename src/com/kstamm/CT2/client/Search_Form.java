package com.kstamm.CT2.client;

import java.awt.*;
import java.awt.event.*;
import java.util.Properties;

import javax.swing.*;
import javax.swing.table.*;
/*
 * Created by JFormDesigner on Thu Apr 10 16:10:56 CDT 2008
 * and hacked into a jgoodies forms version 2016
 * manually bewcause that author has hidden the software behind paywall
 */



/**
 * @author Karl Stamm
 */
public class Search_Form extends JFrame {
	/**
	 * 
	 */
	private ClientDaemon master;
	private static final long serialVersionUID = -5875993622644987953L;
	public Search_Form(ClientDaemon cd) {
		master = cd;
		master.registerSF(this);
		initComponents();
	}

	private void but_Search_ActionPerformed(ActionEvent e) {
		if(this.textField1.getText().length()>0){
			String search = this.textField1.getText();
			Properties[] results = master.doSearch(search);
			if(results!=null){
				int num = results.length;
				int actually_Found=0;
				if(num>0){
					DefaultTableModel dtm = (DefaultTableModel)table1.getModel();
					dtm.setRowCount(0);
					for(int i=0;i<num;i++){
						if(results[i]!=null)
						{
							dtm.addRow(new Object[]{results[i].get("customer") , results[i].get("casenumber") , results[i].get("request") , results[i].get("model")});
							actually_Found++;
						}
					}
				}
				
				if(actually_Found==0){
					JOptionPane.showMessageDialog(null, "No results found with member name containing '"+search+"'");
				}

			}else{
				System.out.println("Received a null set erroneously.");
			}
		}
	}

	private void table1MouseClicked(MouseEvent e) {
		int rowIndex = table1.getSelectedRow();
		//System.out.println("Selected Row: "+rowIndex);
		//System.out.println("Selected RowCount: "+table1.getSelectedRowCount());
		String casenumber =  table1.getModel().getValueAt(rowIndex, 1).toString();
		//System.out.println("Casenum: "+casenumber);
		master.ef.forcedLoad(master.loadCase(casenumber));
		//master.ef.loadFormFromProperties(master.loadCase(casenumber));
		
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Karl Stamm
		panel1 = new JPanel();
		textField1 = new JTextField();
		button1 = new JButton();
		panel2 = new JPanel();
		scrollPane1 = new JScrollPane();
		table1 = new JTable();

		//======== this ========
		setTitle("Search by Member");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		//======== panel1 ========
		{
			panel1.setMaximumSize(new Dimension(2767, 50));

			// JFormDesigner evaluation mark
			panel1.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), panel1.getBorder()));
			
			panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

			//---- textField1 ----
			textField1.setColumns(20);
			textField1.setMinimumSize(new Dimension(100, 28));
			panel1.add(textField1);

			//---- button1 ----
			button1.setText("Search");
			button1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					but_Search_ActionPerformed(e);
				}
			});
			panel1.add(button1);
		}
		contentPane.add(panel1);

		//======== panel2 ========
		{
			panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

			//======== scrollPane1 ========
			{
				scrollPane1.setPreferredSize(new Dimension(500, 128));
				scrollPane1.setMinimumSize(new Dimension(350, 23));

				//---- table1 ----
				table1.setModel(new DefaultTableModel(
					new Object[][] {
						{null, null, "", null, null},
						{null, null, null, null, null},
						{null, null, null, "", null},
						{null, "", null, null, null},
						{null, null, null, null, null},
					},
					new String[] {
						"CustomerName", "CaseNumber", "RequestDate", "Status", "Model#"
					}
				) {
					/**
					 * 
					 */
					private static final long serialVersionUID = -4383220768494212740L;
					@SuppressWarnings("rawtypes")
					Class[] columnTypes = new Class[] {
						String.class, String.class, String.class, String.class, String.class
					};
					@Override
					public Class<?> getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
				});
				{
					TableColumnModel cm = table1.getColumnModel();
					cm.getColumn(0).setMinWidth(15);
					cm.getColumn(0).setPreferredWidth(95);
					cm.getColumn(1).setMinWidth(15);
					cm.getColumn(1).setPreferredWidth(80);
					cm.getColumn(2).setMinWidth(15);
					cm.getColumn(2).setPreferredWidth(80);
					cm.getColumn(3).setMinWidth(15);
					cm.getColumn(3).setPreferredWidth(75);
					cm.getColumn(4).setMinWidth(15);
					cm.getColumn(4).setPreferredWidth(80);
				}
				table1.setBorder(null);
				table1.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						table1MouseClicked(e);
					}
				});
				scrollPane1.setViewportView(table1);
			}
			panel2.add(scrollPane1);
		}
		contentPane.add(panel2);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Karl Stamm
	private JPanel panel1;
	private JTextField textField1;
	private JButton button1;
	private JPanel panel2;
	private JScrollPane scrollPane1;
	private JTable table1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
