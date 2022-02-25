package com.kstamm.CT2.server;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.*;

import biz.source_code.miniTemplator.MiniTemplator;
import biz.source_code.miniTemplator.MiniTemplator.BlockNotDefinedException;
import biz.source_code.miniTemplator.MiniTemplator.TemplateSyntaxException;
import biz.source_code.miniTemplator.MiniTemplator.VariableNotDefinedException;

import com.enterprisedt.net.ftp.FTPException;

public class ServerDaemon extends Thread implements WindowListener{

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InterruptedException 
	 */

	private boolean alive = true;
	private boolean cases_pending = true;
	
	private boolean db_connected = false;
	private int db_timeout = 0;
	
	
	private DBManager dbm;
	private NetManager nm;
	private FTPManager ftp;
	
	public static void main(String[] args) {

		System.out.println("Start booting Server...");
		ServerDaemon sd;
		try {
			sd = new ServerDaemon();
			sd.start();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find a configuration file!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Problem reading from a configuration file! Be sure it is well formed by following the example files.");
			System.out.println("Also, perhaps the internet connection is down.");
			e.printStackTrace();
		} catch (FTPException e) {
			System.out.println("Problem connecting with the ftp server!");
			e.printStackTrace();
			System.exit(0);
		} catch (ClassNotFoundException e) {
			System.out.println("Database connection failure, this installation is missing the proper Connector/J libraries");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Database connection failure, is it down or are we not installed properly?");
			e.printStackTrace();
		}
	}
	
	public ServerDaemon() throws FileNotFoundException, IOException, FTPException, ClassNotFoundException, SQLException{
		dbm = new DBManager("database.ini");

		dbm.connect();
		db_connected = true;
		db_timeout = 0;
		nm = new NetManager("server.ini",this);
		nm.start();
			
		ftp = new FTPManager(false);

		alive=true;
		cases_pending=true;//just check on startup.
	}
	
	
	@Override
	public void run() {
		String tstamp = new Date().toString();
		System.out.println(tstamp + " Server Alive.");

		while(alive==true){
			//do something in main loop?
			//System.out.println("Running...");
			if(cases_pending==true){
				if(!db_connected){//if we have gone to sleep...
					try {
						dbm.connect();
						ftp.reconnect(0);
						db_connected=true;
						db_timeout=0;
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (FTPException e) {
						System.out.println("Message: " + e.getMessage());
						System.out.println("Cause: " + e.getCause());
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}//end reconnection code.
				
				cases_pending=false;
			Properties[] cs;
			try {
				cs = dbm.getCasesToUpload();
				if(cs.length >0)
					System.out.println("I think I have some cases that need uploading.");
			} catch (SQLException e1) {
				System.out.println("ServerDaemon calling dbm.getCasesToUpload ran into an issue:");
				cs=null;
				e1.printStackTrace();
			}
			if(cs != null)
				if(cs.length > 0){
					int sent=0;
					for(int i=0;i<cs.length;i++){
						File f;
						try {
							f = generateASP(cs[i]);
							//System.out.println("ftp send is commented off");
							int apparentlySent = ftp.send(f.getPath());
							sent=sent+apparentlySent;
							String casenum = cs[i].getProperty("casenumber");
							dbm.markOnWeb(casenum);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (TemplateSyntaxException e) {
							e.printStackTrace();
						}
					}//end for cases
					tstamp = new Date().toString();
					System.out.println(tstamp +"  Uploads successful: "+sent);
					}//end if cs.length>0
				else{
					System.out.println("No cases needed uploading.");
				}
			}
			//check on db timeout...
			if(db_connected)
				if(db_timeout++ > 50)
				{  // System.out.println("Releasing DB and FTP connections.");
					try {
						dbm.disconnect();
						ftp.disconnect();
						db_connected=false;
					} catch (SQLException e) {
						System.out.println("SQLException.getMessage()"+e.getMessage());
						//fail to dc ???
					}
				}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("ServerDaemon timer thread interruption.");
				e.printStackTrace();
			}	
		}
		
		System.out.println("Done.");
		try {
			dbm.disconnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		nm.shutdown();
		ftp.shutdown();
		System.out.println("Shutdown happened.");

	}

	private File generateASP(Properties p) throws IOException, TemplateSyntaxException {
		if(p==null)throw new IllegalArgumentException();
		
		String outfile = p.getProperty("casenumber") + ".asp";
		File templatefile = new File("template.html");
		MiniTemplator mt = new MiniTemplator(templatefile);
		File newFile = new File(outfile);
		
		Enumeration<Object> e = p.keys();
		Pattern pat = Pattern.compile("[12][0-9]{3}[-][0-9]{2}[-][0-9]{2}[ ][0-9]{2}[:][0-9]{2}[:][0-9]{2}");
		Matcher mat;
		while(e.hasMoreElements()){
			String key = e.nextElement().toString();
			String value = p.getProperty(key);
			if(value.length()>=19){
				mat = pat.matcher(value.substring(0, 19));
				if(mat.matches()){
					//System.out.println("Date found: "+value);
					//"2008-04-13 22:18:00.0"
					//"012345678901234567890"
					StringBuffer sb = new StringBuffer();
					sb.append(Integer.parseInt(value.substring(5, 7)));
					sb.append("/");
					sb.append(Integer.parseInt(value.substring(8, 10)));
					sb.append("/");
					sb.append(Integer.parseInt(value.substring(0, 4)));
					value = sb.toString();
					//System.out.println(sb.toString());
				}	
			}
			value = value.replaceAll("\\n", "<br>");
			if(value.length() > 0){ //don't publish fields with zero length strings.
				try {
					mt.setVariable(key, value);
					mt.addBlock(key);
	
				} catch (VariableNotDefinedException vnde) {
					// some variables are intentionally not on the page
				} catch (BlockNotDefinedException bnde) {
					// some variables are intentionally not on the page
				}
			}
		}
		mt.generateOutput(newFile);
		return newFile;
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
	public boolean checkEmplLogin(String user, String pass) {
		if(!db_connected){
			try {
				dbm.connect();
				ftp.reconnect(0);
				db_connected=true;
				db_timeout=0;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (FTPException e) {
				System.out.println("Problem connecting to the FTP server.  Maybe the password is changed or maybe you don't have an internet connection.");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(dbm.checkEmplLogin(user, pass)){
			return true;
		}else
		return false;
	}

	public void receiveCase(Properties p) {
		if(p.containsKey("casenumber"))
		try {
			if(!db_connected){
				dbm.connect();
				ftp.reconnect(0);
				db_connected=true;
			}
			db_timeout=0;
			dbm.receiveCase(p);
			cases_pending=true;
		} catch (SQLException e) {
			System.out.println("Problem writing a case to the database!");
			//I should probably tell the client.
			if(e instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException){
				//it was a quote or semicolon issue...
				System.out.println("It was a sql syntax error, you're trying to use symbols that won't go into the database.");
				e.printStackTrace();
			}else
			if(e instanceof com.mysql.jdbc.MysqlDataTruncation){
				System.out.println("There was a problem parsing data.");
				if(e.getMessage().contains("datetime value: ")){
					String msg = e.getMessage();
					System.out.println(msg);
					String prekey = "for column '";
					int pre = msg.indexOf(prekey);
					msg = msg.substring(pre+prekey.length(), msg.length());
					msg = msg.substring(0, msg.indexOf('\''));
					//now msg is just the column that had the problem.
					System.out.println("The field with the bad date is "+msg);
				}
			}else
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FTPException e) {
			System.out.println("Message: " + e.getMessage());
			System.out.println("Cause: " + e.getCause());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public Properties retrieveCase(String casenumber) {
		try {
			if(!db_connected){
				dbm.connect();
				ftp.reconnect(0);
				db_connected=true;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		db_timeout=0;
		
		return dbm.retrieveCase(casenumber);
	
	}

	public Properties[] doSearch(String member_name) {
		try {
			if(!db_connected){
				dbm.connect();
				db_connected=true;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db_timeout=0;
		

		member_name = member_name.replace("--","");
		member_name = member_name.replace("%","");
		member_name = member_name.replace(";","");
		member_name = member_name.replace(')','\0');
		member_name = member_name.replace('(','\0');
		
		Properties[] results = dbm.doSearch(member_name);
		return results;
		
	}
	
	

}
