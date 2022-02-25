package com.kstamm.CT2.admin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;

import com.jgoodies.forms.layout.*;
/*
 * Created by JFormDesigner on Wed Apr 16 14:52:49 CDT 2008
 */


public class AdminForm extends JFrame {
	private AdminDaemon master;

	private static final long serialVersionUID = 4275623263746514303L;
	public AdminForm(AdminDaemon ad) {
		master = ad;
		initComponents();
	}
	
	private void but_save_emplActionPerformed(ActionEvent e) {
		String[] sa = new String[2];
		sa[0] = this.editorPane1.getText();
		sa[1] = this.editorPane2.getText();
		
		int r = JOptionPane.showConfirmDialog(null, "Press OK to save the new data to the database.\nAll employee logons not visible here will be discarded!\nPress Cancel to change nothing.", "Are you Sure?", JOptionPane.OK_CANCEL_OPTION);
		if(r==0){
			//okay
			master.saveEmployees(sa);
		}else if(r==2){
			//cancel
			//do nothing I guess.
			System.out.println("Save canceled.");
		}
		
		
	}

	private void but_load_emplActionPerformed(ActionEvent e) {
		String[] sa = master.requestEmployees();
		this.editorPane1.setText(sa[0]);
		this.editorPane2.setText(sa[1]);
		
	}

	private void but_save_adminActionPerformed(ActionEvent e) {
		String[] sa = new String[2];
		sa[0] = this.editorPane5.getText();
		sa[1] = this.editorPane6.getText();
		
		int r = JOptionPane.showConfirmDialog(null, "Press OK to save the new data to the database.\nAll admin logons not written here will be discarded!\nPress Cancel to change nothing.", "Are you Sure?", JOptionPane.OK_CANCEL_OPTION);
		if(r==0){
			//okay
			master.saveAdmins(sa);
		}else if(r==2){
			//cancel
			//do nothing I guess.
			System.out.println("Save canceled.");
		}
		
	}

	private void but_load_adminActionPerformed(ActionEvent e) {
		String[] sa = master.requestAdmins();
		this.editorPane5.setText(sa[0]);
		this.editorPane6.setText(sa[1]);
		
	}

	private void but_save_webActionPerformed(ActionEvent e) {
		String[] sa = new String[2];
		sa[0] = this.editorPane3.getText();
		sa[1] = this.editorPane4.getText();
		int r = JOptionPane.showConfirmDialog(null, "Press OK to save the new data to the web server.\nAll user logons not written here will be discarded!\nPress Cancel to change nothing.", "Are you Sure?", JOptionPane.OK_CANCEL_OPTION);
		if(r==0){
			//okay
			master.saveWebusers(sa);
		}else if(r==2){
			//cancel
			//do nothing I guess.
			System.out.println("Save canceled.");
		}
		
		
	}

	private void but_load_webActionPerformed(ActionEvent e) {
		String[] sa = master.requestWebUsers();
		this.editorPane3.setText(sa[0]);
		this.editorPane4.setText(sa[1]);
		
	}
	
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Karl Stamm
		ResourceBundle bundle = ResourceBundle.getBundle("adminString");
		tabbedPane1 = new JTabbedPane();
		panel1 = new JPanel();
		scrollPane1 = new JScrollPane();
		editorPane1 = new JEditorPane();
		scrollPane2 = new JScrollPane();
		editorPane2 = new JEditorPane();
		panel4 = new JPanel();
		but_save_empl = new JButton();
		but_load_empl = new JButton();
		panel2 = new JPanel();
		scrollPane5 = new JScrollPane();
		editorPane5 = new JEditorPane();
		scrollPane6 = new JScrollPane();
		editorPane6 = new JEditorPane();
		panel7 = new JPanel();
		but_save_admin = new JButton();
		but_load_admin = new JButton();
		panel3 = new JPanel();
		scrollPane3 = new JScrollPane();
		editorPane3 = new JEditorPane();
		scrollPane4 = new JScrollPane();
		editorPane4 = new JEditorPane();
		panel6 = new JPanel();
		but_save_web = new JButton();
		but_load_web = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"198dlu:grow",
			"fill:default:grow"));

		//======== tabbedPane1 ========
		{
			tabbedPane1.setPreferredSize(new Dimension(950, 260));

			//======== panel1 ========
			{

				// JFormDesigner evaluation mark
				panel1.setBorder(new javax.swing.border.CompoundBorder(
					new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
						" ", javax.swing.border.TitledBorder.CENTER,
						javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
						java.awt.Color.red), panel1.getBorder()));
						
				panel1.setLayout(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(35), FormSpec.DEFAULT_GROW),
						new ColumnSpec(Sizes.DLUY4),
						new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(35), FormSpec.DEFAULT_GROW)
					},
					new RowSpec[] {
						new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						new RowSpec(Sizes.DLUY3),
						new RowSpec(Sizes.DEFAULT)
					}));

				//======== scrollPane1 ========
				{
					scrollPane1.setPreferredSize(new Dimension(500, 500));

					//---- editorPane1 ----
					editorPane1.setMinimumSize(new Dimension(64, 16));
					editorPane1.setPreferredSize(new Dimension(500, 500));
					scrollPane1.setViewportView(editorPane1);
				}
				panel1.add(scrollPane1, cc.xy(1, 1));

				//======== scrollPane2 ========
				{
					scrollPane2.setPreferredSize(new Dimension(500, 500));

					//---- editorPane2 ----
					editorPane2.setPreferredSize(new Dimension(500, 500));
					editorPane2.setMinimumSize(new Dimension(64, 16));
					scrollPane2.setViewportView(editorPane2);
				}
				panel1.add(scrollPane2, cc.xy(3, 1));

				//======== panel4 ========
				{
					panel4.setLayout(new FlowLayout());

					//---- but_save_empl ----
					but_save_empl.setText(bundle.getString("admin.but_save_empl.text"));
					but_save_empl.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							but_save_emplActionPerformed(e);
						}
					});
					panel4.add(but_save_empl);

					//---- but_load_empl ----
					but_load_empl.setText(bundle.getString("admin.but_load_empl.text"));
					but_load_empl.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							but_load_emplActionPerformed(e);
						}
					});
					panel4.add(but_load_empl);
				}
				panel1.add(panel4, cc.xywh(1, 3, 3, 1));
			}
			tabbedPane1.addTab(bundle.getString("admin.panel1.tab.title"), panel1);


			//======== panel2 ========
			{
				panel2.setLayout(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(35), FormSpec.DEFAULT_GROW),
						new ColumnSpec(Sizes.DLUX2),
						new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(35), FormSpec.DEFAULT_GROW)
					},
					new RowSpec[] {
						new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						new RowSpec(Sizes.DLUY4),
						new RowSpec(Sizes.DEFAULT)
					}));

				//======== scrollPane5 ========
				{
					scrollPane5.setPreferredSize(new Dimension(500, 500));

					//---- editorPane5 ----
					editorPane5.setMinimumSize(new Dimension(64, 16));
					editorPane5.setPreferredSize(new Dimension(500, 500));
					scrollPane5.setViewportView(editorPane5);
				}
				panel2.add(scrollPane5, cc.xy(1, 1));

				//======== scrollPane6 ========
				{
					scrollPane6.setPreferredSize(new Dimension(500, 500));

					//---- editorPane6 ----
					editorPane6.setPreferredSize(new Dimension(500, 500));
					editorPane6.setMinimumSize(new Dimension(64, 16));
					scrollPane6.setViewportView(editorPane6);
				}
				panel2.add(scrollPane6, cc.xy(3, 1));

				//======== panel7 ========
				{
					panel7.setLayout(new FlowLayout());

					//---- but_save_admin ----
					but_save_admin.setText(bundle.getString("admin.but_save_admin.text"));
					but_save_admin.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							but_save_adminActionPerformed(e);
						}
					});
					panel7.add(but_save_admin);

					//---- but_load_admin ----
					but_load_admin.setText(bundle.getString("admin.but_load_admin.text"));
					but_load_admin.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							but_load_adminActionPerformed(e);
						}
					});
					panel7.add(but_load_admin);

				}
				panel2.add(panel7, cc.xywh(1, 3, 3, 1));
			}
			tabbedPane1.addTab(bundle.getString("admin.panel2.tab.title"), panel2);


			//======== panel3 ========
			{
				panel3.setLayout(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(35), FormSpec.DEFAULT_GROW),
						new ColumnSpec(Sizes.DLUX2),
						new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(35), FormSpec.DEFAULT_GROW)
					},
					new RowSpec[] {
						new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						new RowSpec(Sizes.DLUY4),
						new RowSpec(Sizes.DEFAULT)
					}));

				//======== scrollPane3 ========
				{
					scrollPane3.setPreferredSize(new Dimension(500, 500));

					//---- editorPane3 ----
					editorPane3.setMinimumSize(new Dimension(64, 16));
					editorPane3.setPreferredSize(new Dimension(500, 500));
					scrollPane3.setViewportView(editorPane3);
				}
				panel3.add(scrollPane3, cc.xy(1, 1));

				//======== scrollPane4 ========
				{
					scrollPane4.setPreferredSize(new Dimension(500, 500));

					//---- editorPane4 ----
					editorPane4.setPreferredSize(new Dimension(500, 500));
					editorPane4.setMinimumSize(new Dimension(64, 16));
					scrollPane4.setViewportView(editorPane4);
				}
				panel3.add(scrollPane4, cc.xy(3, 1));

				//======== panel6 ========
				{
					panel6.setLayout(new FlowLayout());

					//---- but_save_web ----
					but_save_web.setText(bundle.getString("admin.but_save_web.text"));
					but_save_web.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							but_save_webActionPerformed(e);
						}
					});
					panel6.add(but_save_web);

					//---- but_load_web ----
					but_load_web.setText(bundle.getString("admin.but_load_web.text"));
					but_load_web.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							but_load_webActionPerformed(e);
						}
					});
					panel6.add(but_load_web);

				}
				panel3.add(panel6, cc.xywh(1, 3, 3, 1));
			}
			tabbedPane1.addTab(bundle.getString("admin.panel3.tab.title"), panel3);

		}
		contentPane.add(tabbedPane1, cc.xy(1, 1));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Karl Stamm
	private JTabbedPane tabbedPane1;
	private JPanel panel1;
	private JScrollPane scrollPane1;
	private JEditorPane editorPane1;
	private JScrollPane scrollPane2;
	private JEditorPane editorPane2;
	private JPanel panel4;
	private JButton but_save_empl;
	private JButton but_load_empl;
	private JPanel panel2;
	private JScrollPane scrollPane5;
	private JEditorPane editorPane5;
	private JScrollPane scrollPane6;
	private JEditorPane editorPane6;
	private JPanel panel7;
	private JButton but_save_admin;
	private JButton but_load_admin;
	private JPanel panel3;
	private JScrollPane scrollPane3;
	private JEditorPane editorPane3;
	private JScrollPane scrollPane4;
	private JEditorPane editorPane4;
	private JPanel panel6;
	private JButton but_save_web;
	private JButton but_load_web;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
