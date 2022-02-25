package com.kstamm.CT2.admin;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import com.enterprisedt.net.ftp.FTPException;


public class AdminDaemon extends Thread implements WindowListener{

	private boolean alive=true;
	private DBManager dbm;
	private FTPManager ftp;
	private AdminForm af;
	public static void main(String[] args) {
		System.out.println("Hi");
		try {
			DBManager dbm = new DBManager("database.ini");
			
			String user = JOptionPane.showInputDialog("Enter an admin username");
			try {
				dbm.connect();
				String pass = JOptionPane.showInputDialog("Enter password");
				if(dbm.checkAdminLogin(user, pass)){
					//JOptionPane.showMessageDialog(null,"Yae.");
					dbm.disconnect();
					AdminDaemon ad;
					try {
						ad = new AdminDaemon();
						ad.start();
					} catch (FTPException e) {
						e.printStackTrace();
					}
					
				}else{
					JOptionPane.showMessageDialog(null,"ACCESS DENIED");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Database installation error.");
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Database connection error.");
			}
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Missing files error.");

		} catch (IOException e) {
			e.printStackTrace();		
			JOptionPane.showMessageDialog(null,"File reading error.");

		}
		
		System.out.println("Bye.");
	}
	public AdminDaemon() throws FileNotFoundException, IOException, FTPException, ClassNotFoundException, SQLException{
		//constructor 
	
		ftp = new FTPManager(false);
		dbm = new DBManager("database.ini");
		dbm.connect();
		af = new AdminForm(this);
		af.pack();
		af.addWindowListener(this);
		//attempt reposition!
		Toolkit tk = Toolkit.getDefaultToolkit ();
		int scrWidth = (int) tk.getScreenSize ().getWidth ();
		int scrHeigth = (int) tk.getScreenSize ().getHeight ();
		af.setLocation(scrWidth/2-100, scrHeigth/2-100);
		
		af.setVisible(true);
		alive=true;
	}
	@Override
	public void run(){
		System.out.println("Hello");
		
		while(alive==true)
		{
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ftp.disconnect();
		try {
			dbm.disconnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		af.setVisible(false);
		af.removeWindowListener(this);
		af.dispose();
		System.out.println("Goodbye.");
	}
	

	public void windowActivated(WindowEvent e) {
		
	}
	public void windowClosed(WindowEvent e) {
	}
	public void windowClosing(WindowEvent e) {
		
		alive=false;
	}
	public void windowDeactivated(WindowEvent e) {

	}
	public void windowDeiconified(WindowEvent e) {
		
	}
	public void windowIconified(WindowEvent e) {
		
	}
	public void windowOpened(WindowEvent e) {
		
	}
	public String[] requestEmployees(){
		return dbm.retrieveEmployees();
	}
	public String[] requestAdmins(){
		return dbm.retrieveAdmins();
	}
	public String[] requestWebUsers(){
		String[] retval = new String[2];
		StringBuffer sbu = new StringBuffer();
		StringBuffer sbp = new StringBuffer();
		
		String fn = "userlist.asp";
		if(ftp.request(fn)){
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(fn)));
				boolean done = false;
				while(!done){
					String s = null;
					try {
						s = br.readLine();
					} catch (IOException e) {
						done=true;
						e.printStackTrace();
						break;
					}
					if(s==null){
						done=true;
						break;
					}else{
						//okay we have a string.
						//System.out.println(s);
						if(s.length()>4){
							if(s.startsWith("user_pass(")){
								int i = s.indexOf("=");
								sbu.append(s.substring(i+2, s.length()-1));
								s = br.readLine();//grab the pass
								sbp.append(s.substring(i+2, s.length()-1));
								s = br.readLine();//throwout the level
								sbu.append("\n");
								sbp.append("\n");
								
							}
						}
					}

				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		retval[0] = sbu.toString();
		retval[1] = sbp.toString();
		
		return retval;
	}
	public void saveWebusers(String[] sa) {
		StringTokenizer toku = new StringTokenizer(sa[0],"\n");
		
		StringTokenizer tokp = new StringTokenizer(sa[1],"\n");
		int countU = toku.countTokens();
		int countP = tokp.countTokens();
		boolean error = false;
		if(countU == countP){
			String[] us = new String[countU];
			String[] ps = new String[countU];
			for (int i=0;i<countU;i++){
				us[i] = toku.nextToken();
				if(us[i].indexOf("\"")>=0){
					error = true;	
				}
				ps[i] = tokp.nextToken();
				if(ps[i].indexOf("\"")>=0){
					error = true;	
				}
				
			}
			if(!error){
				this.makeNewWebUserList(us,ps);
			}else{
				JOptionPane.showMessageDialog(null, "You cannot use quotation marks in the usernames or passwords.");	
			}
		}else{
			JOptionPane.showMessageDialog(null, "The username " +
					"and password lists must have the same number of lines.\n" +
					"I see that the users column has "+countU+ " and the pass column " +
					"has "+countP);			
		}
		
		
	}
	
	
	private void makeNewWebUserList(String[] us, String[] ps) {
		String n = new Integer(us.length).toString();
		//System.out.println(n);
		File f = new File("userlist.asp");
		if(f.exists()){
			f.delete();
		}
		try {
			f.createNewFile();
			PrintWriter pw = new PrintWriter(new FileOutputStream(f));
			pw.append("<% \n");
			pw.append("Dim total_users\n");
			pw.append("total_users=");
			pw.append(n);
			pw.append("\n\n");
			pw.append("Dim user_pass("+n+",3)\n\n");
			for(int i=1;i<=us.length;i++){
				pw.append("user_pass("+i+",1) =\""+us[i-1]+"\"\n");
				pw.append("user_pass("+i+",2) =\""+ps[i-1]+"\"\n");
				pw.append("user_pass("+i+",3) =\"1\"\n");
				pw.append("\n");
			}
			pw.append("%>\n");
			pw.flush();
			pw.close();
				
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ftp.send("userlist.asp");
		JOptionPane.showMessageDialog(null, "Saved.");	
		
	}
	
	
	public void saveEmployees(String[] sa) {
		StringTokenizer toku = new StringTokenizer(sa[0],"\n");
		
		StringTokenizer tokp = new StringTokenizer(sa[1],"\n");
		int countU = toku.countTokens();
		int countP = tokp.countTokens();
		boolean error = false;
		if(countU == countP){
			String[] us = new String[countU];
			String[] ps = new String[countU];
			for (int i=0;i<countU;i++){
				us[i] = toku.nextToken();
				if(us[i].indexOf("\"")>=0){
					error = true;	
				}
				ps[i] = tokp.nextToken();
				if(ps[i].indexOf("\"")>=0){
					error = true;	
				}
				
			}
			if(!error){
				this.makeNewEmployeeList(us,ps);
				JOptionPane.showMessageDialog(null, "Saved.");	
				
			}else{
			JOptionPane.showMessageDialog(null, "You cannot use quotation marks in the usernames or passwords.");	
			}
		}else{
			JOptionPane.showMessageDialog(null, "The username " +
					"and password lists must have the same number of lines.\n" +
					"I see that the users column has "+countU+ " and the pass column " +
					"has "+countP);			
		}
		
	}
	private void makeNewEmployeeList(String[] us, String[] ps) {
		dbm.makeNewEmployeeList(us,ps);
	}
	public void saveAdmins(String[] sa) {
		StringTokenizer toku = new StringTokenizer(sa[0],"\n");
		
		StringTokenizer tokp = new StringTokenizer(sa[1],"\n");
		int countU = toku.countTokens();
		int countP = tokp.countTokens();
		boolean error = false;
		if(countU == countP){
			String[] us = new String[countU];
			String[] ps = new String[countU];
			for (int i=0;i<countU;i++){
				us[i] = toku.nextToken();
				if(us[i].indexOf("\"")>=0){
					error = true;	
				}
				ps[i] = tokp.nextToken();
				if(ps[i].indexOf("\"")>=0){
					error = true;	
				}
				
			}
			if(!error){
				this.makeNewAdminList(us,ps);
				JOptionPane.showMessageDialog(null, "Saved.");	
				
			}else{
			JOptionPane.showMessageDialog(null, "You cannot use quotation marks in the usernames or passwords.");	
			}
		}else{
			JOptionPane.showMessageDialog(null, "The username " +
					"and password lists must have the same number of lines.\n" +
					"I see that the users column has "+countU+ " and the pass column " +
					"has "+countP);			
		}
		
	}
	private void makeNewAdminList(String[] us, String[] ps) {
		dbm.makeNewAdminList(us,ps);
	}
	
	
	
}
