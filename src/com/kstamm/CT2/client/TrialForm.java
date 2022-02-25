package com.kstamm.CT2.client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.jgoodies.forms.layout.*;




/**
 * @author Karl Stamm
 */
public class TrialForm extends JPanel {
	

	private static final long serialVersionUID = 12352334523L;
	private NetClient netClient;
	private int checkCount = 0;
	private boolean shutting_down=false;
	private ClientDaemon master;

	public TrialForm(NetClient nc,ClientDaemon cd) {
		initComponents();
		netClient = nc;
		master=cd;
		shutting_down = false;
	}
	public void shutdown(){
		shutting_down=true;
	}
	private void button1ActionPerformed(ActionEvent e) {
		//this is when the login button is pressed.
		if(validateUserInputs()){
			progressBar1.setValue(1);
			lbl_status.setText("Attempting to connect.");
			netClient.attemptConnect();
			progressBar1.setValue(2);
			//spawn a timer to check on the netclient and see if it has connected yet
			Timer nt = new Timer();
			nt.scheduleAtFixedRate(new LoginTask(nt), 250, 300);
		} else{
			JOptionPane.showMessageDialog(this, "Your Username/Password is invalid.",
                    "Input problem", JOptionPane.PLAIN_MESSAGE);
		}
	}

	private boolean validateUserInputs() {
		String user = this.textField1.getText();
		String pass = this.passwordField1.getPassword().toString();
		if(pass == null || user == null){
			return false;
		}
		if(pass.length() > 2){
			if(user.length() >2){
				if(user.indexOf((int)'\n') > 0){
					return false;
				}
				if(pass.indexOf((int)'\n') > 0){
					return false;
				}
				return true;
			}
		}
		return false;
	}

	class LoginTask extends TimerTask {
		Timer mytimer;
		public LoginTask(Timer mt){
			mytimer = mt;
		}
        public void run() {
        	if(shutting_down==true) return;
        	int sc = netClient.getStateCode();
        	switch(sc) {
        	case NetClient.STATE_CONNECTED:
        		//win!
                progressBar1.setValue(10);
            	lbl_status.setText("Connected!");
            	checkCount=0;
            	mytimer.cancel();
            	//we are connected! do something with it!!
            	try {
            			System.out.println("Sending Login info...");
            			String p = new String(passwordField1.getPassword());
						netClient.attemptSend("LOGIN "+textField1.getText()+ " "+p);
						//with that send going on, quit this task and start up some kind of new watcher.
						Timer nt = new Timer("Timer-ClientLoginMonitor");
						nt.scheduleAtFixedRate(new WatcherTask(nt), 250, 300);
            	  } catch (SocketException e) {
						System.out.println("ERROR sending login information.");
						netClient.attemptDisconnect();
						e.printStackTrace();
					}
        	break;
        	case NetClient.STATE_ATTEMPTING:
                progressBar1.setValue(5);
	            lbl_status.setText("Attempting Connection");
	            checkCount=0;
        	break;
        	case NetClient.STATE_DISCONNECTED:
        		lbl_status.setText("Disconnected.");
        		if(checkCount++>10){
        			mytimer.cancel();
        			checkCount=0;
        		}
        	break;
        	default:
        		lbl_status.setText("Connect Failed.");
        		checkCount=0;
        		mytimer.cancel();
        	}
        }
    }
	class WatcherTask extends TimerTask {
		Timer mytimer;
		public WatcherTask(Timer mt){
			mytimer = mt;
		}
		 public void run() {
        	int sc = netClient.getStateCode();
        	switch(sc) {
        	case NetClient.STATE_CONNECTED:
        		if(netClient.hasData())
        		{
            		//label3.setText("Receiving.");
        			String d = netClient.getData();
            		System.out.println("Loginform recv'd data : "+d);
            		checkCount=0;
            		
            		if(d.equals("WELCOME")){
                		lbl_status.setText("Welcome, user!");
                		master.finishLogin(textField1.getText(), new String(passwordField1.getPassword()));
                		mytimer.cancel();
                		
            		}else{
            			if(d.equals("ACCESSDENIED")){
                    		lbl_status.setText("Access Denied.");
                    		netClient.attemptDisconnect();
                    		//dont watch for incoming messages anymore.
                    		mytimer.cancel();
            			}
            		}
            		
        		}else{//if no data advance checkCount
        			if(shutting_down==true){
        				checkCount=900;
        			}
            		if(checkCount++ > 50){
                		lbl_status.setText("Idle Timeout.");
                		checkCount=0;
            			netClient.attemptDisconnect();
            			mytimer.cancel();
            		}else{
                		lbl_status.setText("Idle Connection.");
                		
            		}
        		}
        		break;
        	case NetClient.STATE_DISCONNECTED:
        		lbl_status.setText("Disconnected.");
        		mytimer.cancel();
        		break;
        	default:
        		lbl_status.setText("State: "+sc);
        		mytimer.cancel();
        	}

        }
	}
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Karl Stamm
		ResourceBundle bundle = ResourceBundle.getBundle("clientStrings");
		label1 = new JLabel();
		textField1 = new JTextField();
		label2 = new JLabel();
		passwordField1 = new JPasswordField();
		button1 = new JButton();
		lbl_status = new JLabel();
		progressBar1 = new JProgressBar();
		CellConstraints cc = new CellConstraints();
	   // this.getRootPane().setDefaultButton(button1);
		//======== this ========
		setMaximumSize(new Dimension(1620, 1400));

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"  ", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

		setLayout(new FormLayout(
			new ColumnSpec[] {
				new ColumnSpec(Sizes.DEFAULT),
				new ColumnSpec(Sizes.dluX(15)),
				new ColumnSpec(Sizes.DEFAULT),
				new ColumnSpec(Sizes.DLUX2),
				new ColumnSpec(Sizes.DLUX2),
				new ColumnSpec(Sizes.DEFAULT),
			},
			new RowSpec[] {
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.pixel(16)),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1), // progress bar
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1), // last entry is a datePicker prototype
					new RowSpec(Sizes.DEFAULT),
					
				}));

		//---- label1 ----
		label1.setText(bundle.getString("login.label1.text"));
		add(label1, cc.xy(1, 5));

		//---- textField1 ----
		textField1.setColumns(12);
		add(textField1, cc.xy(3, 5));

		//---- label2 ----
		label2.setText(bundle.getString("login.label2.text"));
		add(label2, cc.xy(1, 7));

		//---- passwordField1 ----
		passwordField1.setColumns(12);
		add(passwordField1, cc.xy(3, 7));

		//---- button1 ----
		button1.setText(bundle.getString("login.button1.text"));
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button1ActionPerformed(e);
			}
		});
		add(button1, cc.xy(3, 11));
		
		//---- label3 ----
		lbl_status.setMinimumSize(new Dimension(0, 16));
		add(lbl_status, cc.xywh(2, 13, 5, 1));
		add(progressBar1, cc.xywh(1, 17, 6, 1));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents		
	}
	
	private JLabel label1;
	private JTextField textField1;
	private JLabel label2;
	private JPasswordField passwordField1;
	protected JButton button1;
	private JLabel lbl_status;
	private JProgressBar progressBar1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	
	
}
