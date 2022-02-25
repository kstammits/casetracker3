package com.kstamm.CT2.client;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;

public class ClientDaemon extends Thread implements WindowListener{

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InterruptedException 
	 */
	private JFrame loginWindow;
	private JFrame entryWindow;
	
	private boolean alive = true;
	private NetClient nc;
	private LoginForm lf;
	private Search_Form sf;
	protected EntryForm ef;
	
	public static void main(String[] args) {

		if(args != null && args.length >1 && args[1] == "test"){
			System.out.println("Start booting Client with test mode.");
			//TestMode();
		}else{ // not test mode, normal start::
			System.out.println("Start booting Client");
			ClientDaemon d;
			try {
				d = new ClientDaemon();
				d.start();
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public ClientDaemon() throws FileNotFoundException, IOException{
		nc = new NetClient("network.ini");
		nc.start();
		
		lf = new LoginForm(nc,this);
		lf.setVisible(true);
		loginWindow = new JFrame("Login");
		   // Add components to
		   // the frame here...
		loginWindow.add(lf);
		loginWindow.addWindowListener(this);
		loginWindow.pack();
		
		
		
		//attempt reposition!
		Toolkit tk = Toolkit.getDefaultToolkit ();
		int scrWidth = (int) tk.getScreenSize ().getWidth ();
		int scrHeigth = (int) tk.getScreenSize ().getHeight ();
		loginWindow.setLocation(scrWidth/2-100, scrHeigth/2-100);
		
		
		loginWindow.setVisible(true);
		alive=true;
		
		// new code: 2016 thanks mate
		//http://stackoverflow.com/questions/12260962/how-to-set-the-java-default-button-to-react-on-enter-key-released
		JRootPane rootPane = loginWindow.getRootPane();
		rootPane.setDefaultButton(lf.button1);	

	}
	
	public void finishLogin(String u,String p){
		loginWindow.setVisible(false);
	//	System.out.println("ClientDaemon has a password and isn't using it.");
		try{
			EntryForm ef = new EntryForm(this);
			JPanel jp = ef.getForm();
			entryWindow = new JFrame("CaseTracker EntryForm");
			entryWindow.add(jp);
			jp.setVisible(true);
			entryWindow.addWindowListener(this);
			entryWindow.pack();
			
			//attempt reposition!
			Toolkit tk = Toolkit.getDefaultToolkit ();
			int scrWidth = (int) tk.getScreenSize ().getWidth ();
			//int scrHeigth = (int) tk.getScreenSize ().getHeight ();
		//	System.out.println(scrWidth);
		//	System.out.println(scrHeigth);
			entryWindow.setLocation(scrWidth/2-300, 100);
			entryWindow.setVisible(true);
			ef.setUser(u);
		}catch(MissingResourceException mre){
			System.out.println("MissingResource Exception");
			mre.printStackTrace();
			alive=false;
		}

	}
	
	public void saveCase(Properties p){
		ByteArrayOutputStream baos;
		baos = new ByteArrayOutputStream(1024);//how many bytes?
		
		try {
			p.store(baos, "");
			String s = baos.toString();
			//System.out.println("ClientDaemon.baos needed "+s.length()+" bytes.");
			nc.attemptSend("CASE\n"); 		
			nc.attemptSend(s);
			nc.attemptSend("\nCASE_DONE_CODE_DELIMITER");
			
		} catch (IOException e) {
			
			System.out.println("Problem saving the current page to a ByteArrayOutputStream");
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		System.out.println("Client Alive.");

		while(alive==true){
			//do something in main loop?
			//System.out.println("Running...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
		}
		
		System.out.println("Done.");
		lf.shutdown();
		loginWindow.removeWindowListener(this);
		loginWindow.dispose();
		if(entryWindow != null){
			
			entryWindow.setVisible(false);
			entryWindow.removeWindowListener(this);
			entryWindow.dispose();
		}
		if(sf!=null){
			sf.setVisible(false);
			sf.dispose();
		}
		
		nc.shutDown();
		System.out.println("Shutdown.");

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
	public Properties loadCase(String casenumber) {
		
		Properties p = nc.loadCase(casenumber);
		return p;
	}
	
	public Properties[] doSearch(String search) {
		return nc.doSearch(search);
	}
	public void registerSF(Search_Form search_Form) {
		sf = search_Form;
	}
	
	protected void registerEF(EntryForm entryForm){
		ef=entryForm;
	}

}
