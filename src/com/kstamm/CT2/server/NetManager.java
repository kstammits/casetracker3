package com.kstamm.CT2.server;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

public class NetManager extends Thread{

	/**
	 * @param args
	 */
	private int local_port;
	private boolean alive;
	private boolean release;
	private ServerDaemon serverDaemon;
	private boolean user_auth=false;
	
	public NetManager(String resourcefile,ServerDaemon sdm) throws FileNotFoundException, IOException{
		serverDaemon = sdm;
		Properties p = new Properties();
		p.load(new FileInputStream(resourcefile));
		local_port = (int)Integer.parseInt(p.get("listen_port").toString());
		alive=true;
		
	}
	public void shutdown(){
		alive=false;
		release=true;
	}
	
	@Override
	public void run() {
		String tstamp;
		System.out.println("Server started and listening on port "+local_port);
		ServerSocket ss;
		try {
			ss = new ServerSocket(local_port);
			ss.setSoTimeout(2000);//only look for two seconds at a time.
			System.out.println("Server alive and waiting for a connection.");
		      
			while(alive==true){
				try {
					  Socket s = ss.accept();
					  tstamp = new Date().toString();
				      System.out.println(tstamp+"   Server Got a connection from "+s.getInetAddress().getHostAddress());
				      user_auth=false;
				      PrintWriter pw = new PrintWriter(s.getOutputStream());
				      InputStream is = s.getInputStream();
				      InputStreamReader isr = new InputStreamReader(is);
					  BufferedReader br = new BufferedReader(isr);
					  pw.println("HELLO");
					  pw.flush();
					  release=false;
					  int time=0;
					  while(release==false){
						  time++;
						  if(isr.ready()){
							  String msg="";
							  try{
								  msg = br.readLine();
							  }catch(java.net.SocketException se){
								  release=true;
								  
							  }
							  if(msg.length()>1) {
								  if(msg.startsWith("LOGIN")){
									  System.out.println("recvd: LOGIN ");
								  }else{
									  System.out.println("recvd: "+msg);
								  }
							  }
							  StringTokenizer tok = new StringTokenizer(msg," ");
							  try{
							  String code = tok.nextToken();
							  if(code.equals("LOGIN")){
								  String user=null;
								  if(tok.hasMoreTokens())
									  user = tok.nextToken();
								  else
									  user="";
								  String pass=null;
								  if(tok.hasMoreTokens())
								  pass = tok.nextToken();
								  else
									  pass="";
								  //hit up the db to see if this is a valid user account to send accolades to user
								  
								  if(serverDaemon.checkEmplLogin(user, pass)){
									  System.out.println("Sending WELCOME");
									  pw.println("WELCOME");
									  user_auth=true;
								  }else{
									  System.out.println("Sending ACCESSDENIED");
									  pw.println("ACCESSDENIED");
									  user_auth=false;
								  }
								  pw.flush();
							  }else if(code.equals("CASE")){
								  if(user_auth){
									  //System.out.println("CASE seen, spooling input...");
									  Properties p = new Properties();
									  //how am I gonna read only until the end of the case def?
									  StringBuffer sb = new StringBuffer();
									  boolean end_of_case=false;
									  while(end_of_case==false){
										  String t = br.readLine();
										  if(t==null){//uh oh. socket closed during flight
											  //System.out.println("Socket apparently closed during transmission.");
											  end_of_case=true;
											  p=new Properties();
											  break;
										  }
										  if(t.equals("CASE_DONE_CODE_DELIMITER")){
											  //System.out.println("We're all done, the sb is now:");
											 // System.out.println(sb.toString());
											  StringReader sr = new StringReader(sb.toString());
											  p.load(sr);
											  sr.close();
											  end_of_case=true;
										  }else{
											  //System.out.println("line read: "+t);
											  sb.append(t);
											  sb.append("\n");
										  }
										  
									  }
									 // System.out.println("P loaded:");
									 // p.list(System.out);
									 // System.out.println("that's it");
									  serverDaemon.receiveCase(p);
								  }
							  }else if(code.equals("LOAD")){
								  if(user_auth){
									  String casenumber = tok.nextToken();
									  Properties p = serverDaemon.retrieveCase(casenumber);
									  pw.print("CASE\n");
									  p.store(pw, "");
									  pw.print("\nCASE_DONE_CODE_DELIMITER\n");
									  pw.flush();
								  }
							  }else if(code.equals("SEARCH")){
								  if(user_auth){
									  String member_name = "";
									  if(tok.countTokens()<1){
										  member_name = " ";
									  }else{
										  while(tok.countTokens()>0){
											  member_name = member_name + tok.nextToken();  
											  if(tok.hasMoreTokens()){
												member_name = member_name + " ";  
											  }
										  } 
									  }
									  Properties[] results = serverDaemon.doSearch(member_name);
									  if(results!=null)
									  for(int i=0;i<results.length;i++){
										  Properties p = results[i];
										  if(p!=null){
											  pw.print("CASE\n");//im not sure what this line does
											  p.store(pw, "");
											  pw.print("\nCASE_DONE_CODE_DELIMITER\n");//tell client that props is done
										  }
									  }
									  pw.print("\nRESULTS_DONE_CODE_DELIMITER\n");//tell client we're done sending props
									  pw.flush();
								  }
							  }
							  else if(code.equals("DISCONNECT")){
								  System.out.println("Client is leaving.");
								  release=true;
								  user_auth=false;
							  }
							  }catch(java.util.NoSuchElementException nsee) {
								  System.out.println("That was a bad command.");
							  }
							  time=0;
						  }else{
							  if(time>300){//30 seconds
								  //no message in the last whatever seconds
								  //System.out.println("Havent heard from socket in a while, kicking.");
								  release=true;
							  }
						  }
						  try {
							  Thread.sleep(100);
						  } catch (InterruptedException e) {
						  	  release=true;
							  e.printStackTrace();
						  }
					  }//end while release==false
				      s.close();
				      tstamp = new Date().toString();
				      System.out.println(tstamp+"   Connection closed.");
				  } 
				  catch(SocketTimeoutException ste){
					//do nothing.
				  }
				  catch (IOException ex) {
					  System.out.println("IOException ex: "+ex.getMessage());
					  ex.printStackTrace();
				  }
			}//end while alive==true;
			//clean up any spawned threads
			//I have none.
			
			System.out.println("NetManager:"+this.local_port+" Shutting down.");
		
		} catch (IOException e) {
			
			if(e instanceof java.net.BindException){
				System.out.println("ERROR:");
				System.out.println("   The port this server is trying to use is already bound to another process.  Most likely that means you're running the software twice.  Close one.");
				
			}else{
				e.printStackTrace();	
			}
		}

	}
	
	

}
