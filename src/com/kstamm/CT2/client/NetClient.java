package com.kstamm.CT2.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.Vector;

public class NetClient extends Thread{
	
	public static final int STATE_CONNECTED = 0;
	public static final int STATE_ATTEMPTING = 2;
	public static final int STATE_DISCONNECTED = 1;
	public static final int STATE_SENDING = 3;
	
	private int state = NetClient.STATE_DISCONNECTED;
	public int getStateCode() {
		return state;
	}
	
	public boolean hasData(){
		try {
			return (reader.ready());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getData(){
		String ret;
		try {
			ret = reader.readLine();
		} catch (IOException e) {
			ret = "";
			e.printStackTrace();
		}
	
		return ret;
	}
	
	private static final int TASK_NOTHING = 0;
	private static final int TASK_CONNECT = 1;
	private static final int TASK_DISCONNECT = 2;
	private static final int TASK_SEND = 3;
	private static final int TASK_SHUTDOWN = 4;
	
	private int task = NetClient.TASK_NOTHING;
	

	private String server_ip;
	private int server_port;
	private InetAddress ia;
	private Socket sock;
	private PrintWriter writer;
	private StringBuffer buffer;
	private BufferedReader reader;
	private boolean alive;
	
	public NetClient(String resourcefile) throws FileNotFoundException, IOException {
		
		state = NetClient.STATE_DISCONNECTED; 
		
		Properties p = new Properties();
		p.load(new FileInputStream(resourcefile));
		server_ip = p.get("server_ip").toString();
		server_port = (int)Integer.parseInt(p.get("server_port").toString());

		ia = InetAddress.getByName(server_ip);
		buffer = new StringBuffer();
		alive=true;
	}//end constructor
	
	public void attemptConnect() {
		task = TASK_CONNECT;
	}
	public void attemptDisconnect() {
		task = TASK_DISCONNECT;
	}
	public void shutDown() {
		task = TASK_SHUTDOWN;
	}
	public void attemptSend(String data) throws SocketException{
		if(state == STATE_CONNECTED){
			buffer.append(data);
			task = TASK_SEND;
		}
		else
		{
			throw new SocketException("Cannot send data when not connected.");
		}
	}
	
	private boolean connect() throws IOException{
		if(sock != null){
			if(sock.isConnected()){
				state = STATE_CONNECTED;
				return true;
			}
		}
		state=STATE_ATTEMPTING;
		//going to try to access the server for 2 seconds.
		if(ia.isReachable(2000) || true){
			sock = new Socket(ia,server_port);
			sock.setKeepAlive(true);
			
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new PrintWriter (sock.getOutputStream(), true);
			state = STATE_CONNECTED;
			return true;
		}else{
			System.out.println("ERROR - The server at "+ia.getHostAddress()+" could not be contacted, it may be down or firewalled, or we may be down.");
			return false;
		}
	}
	
	
	private void disconnect() {
		if(writer != null){
			writer.flush();
			writer.close();
		}
		
		try {
			sock.close();
			state = STATE_DISCONNECTED;
		} catch (IOException e) {
			System.out.println("ERROR - Socket refuses to close!?");
			e.printStackTrace();
		} catch (NullPointerException npe){
			//socket not exist?
			//so we are already disconnected
			state = STATE_DISCONNECTED;
		//	System.out.println("WARNING - Socket disconnect attempted when no socket exists.");
		}
		
	}
	
	
	private void send(String data) throws IOException{
		if(sock != null){
			if(!sock.isConnected()){
				connect();
			}
		}else{
			throw new IOException("Socket was uninitialized at time of data transmission attempt.");
		}
		
		if(data.length()>1){// ECHO for fun:
			System.out.print("NetClient:"+sock.getRemoteSocketAddress());
			if(data.startsWith("LOGIN")){// censor it:
				System.out.println(" LOGIN");
			}else{
				System.out.println(" "+data);
			}	
		}
		writer.println(data);
		writer.flush();
	}

	@Override
	public void run() {
		while(alive==true){
			updateState();
			switch(task){
			case NetClient.TASK_CONNECT:
				try {
						if(connect())
						{
							task = TASK_NOTHING;
							state = STATE_CONNECTED;
						}else{
							System.out.println("Connect failed");
							task=TASK_NOTHING;
							state=STATE_DISCONNECTED;
						}
					} catch (IOException e1) {
						if(task != TASK_SHUTDOWN){
							task = TASK_NOTHING;
							System.out.println("IOException on connect()");
						}
					}
				break;
			case NetClient.TASK_DISCONNECT:
				if(state==STATE_CONNECTED)
				{
					try {
						send("DISCONNECT");
						System.out.println("Sending Disconnect message");

					} catch (IOException e) {
						System.out.println("NetClient says it didn't like to take my DISCONNECT message.");
						e.printStackTrace();
					}
				}
				disconnect();
				task=TASK_NOTHING;
				System.out.println("NetClient Disconnected.");
				state=STATE_DISCONNECTED;
				break;
			case NetClient.TASK_SEND:
				//System.out.println("NetClient - Processing Send Task.");
				if(buffer.length()>0) {
					if(state == STATE_CONNECTED){
						state = STATE_SENDING;
						try {
							send(buffer.toString());
							buffer.setLength(0);
							//if the sending is complete...
							task = TASK_NOTHING;
							state=STATE_CONNECTED;
						} catch (IOException e) {
							//problem with the send, eh?
							System.out.println("Error with the sending...");
							e.printStackTrace();
						}
					}else{
						//send attempted when not connected....
						System.out.println("Send attempt when not connected.");
					}
				}else {
					//done sending that empty buffer....
					task = TASK_NOTHING;
				}
				
				break;
			case NetClient.TASK_NOTHING:
				if(state==STATE_CONNECTED)
				try {
						send(".");//this is just a keepalive.
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				break;
			case NetClient.TASK_SHUTDOWN:
				if(state==STATE_CONNECTED)
				{
					try {
						send("DISCONNECT");
						System.out.println("Sending Disconnect message");

					} catch (IOException e) {
						System.out.println("NetClient says it didn't like to take my DISCONNECT message.");
						e.printStackTrace();
					}
				}
				disconnect();
				alive=false;
				break;
				default:
					System.out.println("NetClient has unhandled task issued.");
			}
			try {
				Thread.sleep(900);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateState() {
		switch(state){
		case NetClient.STATE_CONNECTED:
			//doublecheck...
			if(sock == null){
				state = NetClient.STATE_DISCONNECTED;
			}else{
				if(sock.isConnected()) {//all good...
					if(sock.isInputShutdown()) {
						System.out.println("Input is shutdown?");
						
					}
					if(sock.isOutputShutdown()) {
						System.out.println("Output is shutdown?");
						
					}
				}else{
					state = NetClient.STATE_DISCONNECTED;
				}
			}
			break;
		case NetClient.STATE_DISCONNECTED:
			
			break;
		case NetClient.STATE_ATTEMPTING:
			if(sock == null){
				state = NetClient.STATE_DISCONNECTED;
			}else{
				if(sock.isConnected()) {//all good...
					state = NetClient.STATE_CONNECTED;
				}else{
					//I guess there's nothing
				}
			}
			break;
		case NetClient.STATE_SENDING:
			
			break;
			
		}
	}

	public Properties loadCase(String casenumber) {
		Properties p = new Properties();
		try {
			send("LOAD "+casenumber);
			//now we need to wait a minute and receive some lines
			//let's just block! youre not supposd to block ui thread on network actions...
			StringBuffer sb = new StringBuffer();
			  boolean end_of_case=false;
			  while(end_of_case==false){
				  String t = reader.readLine();
				  if(t==null){//uh oh. socket closed during flight
					  //System.out.println("Socket apparently closed during transmission.");
					  end_of_case=true;
					  p = new Properties();
					  break;
				  }
				  if(t.equals("CASE_DONE_CODE_DELIMITER")){
					  //System.out.println("We're all done, the sb is now:");
					 // System.out.println(sb.toString());
					  ByteArrayInputStream sr = new ByteArrayInputStream(sb.toString().getBytes());
					  p.load(sr);
					  sr.close();
					  end_of_case=true;
				  }else{
					  //System.out.println("line read: "+t);
					  sb.append(t);
					  sb.append("\n");
				  }		  
			  }//end while end_of_case
			System.out.println("NetClient.loadCase has gotten:");
			p.store(System.out, "Diagnostic:");
		} catch (IOException e) {
			System.out.println("NetClient.loadCase has a problem sending the LOAD #### command.");
			e.printStackTrace();
		}
		return p;
	}
	

	public Properties[] doSearch(String member_name) {
		Properties p;
		Properties[] results = new Properties[1];
		
		Vector<Properties> vector = new Vector<Properties>();
		try {
			send("SEARCH "+member_name);
			//now we need to wait a minute and receive some lines
			//let's just block! youre not supposd to block ui thread on network actions...
			boolean end_of_results=false;
			while(end_of_results==false){

			p = new Properties();
			StringBuffer sb = new StringBuffer();
			  boolean end_of_case=false;
			  while(end_of_case==false){
				  String t=null;
				  try{
				  t = reader.readLine();
				  }catch(java.net.SocketException se){
					  t=null;
					  end_of_results=true;
				  }
				  if(t==null){//uh oh. socket closed during flight
					  //System.out.println("Socket apparently closed during transmission.");
					  end_of_case=true;
					  p = new Properties();
					  break;
				  }
				  if(t.equals("CASE_DONE_CODE_DELIMITER")){
					  //System.out.println("We're all done, the sb is now:");
					 // System.out.println(sb.toString());
					  ByteArrayInputStream sr = new ByteArrayInputStream(sb.toString().getBytes());
					  p.load(sr);
					  sr.close();
					  end_of_case=true;
					  vector.add(p);
					  p=new Properties();
					  sb=new StringBuffer();
				  }else if(t.equals("RESULTS_DONE_CODE_DELIMITER")){
					  if(sb.length()>2){
						  ByteArrayInputStream sr = new ByteArrayInputStream(sb.toString().getBytes());
						  p.load(sr);
						  sr.close();
						  vector.add(p);
					  }
					  end_of_case=true;
					  end_of_results=true;
				  }else{
					  //System.out.println("line read: "+t);
					  sb.append(t);
					  sb.append("\n");
				  }		  
			  }//end while end_of_case
			  
			}//end of results seen.
			results = vector.toArray(results);
			  
			System.out.println("NetClient.doSearch has gotten "+results.length+" results.");
			for(int i=0;i<results.length;i++){
				if(results[i]!=null)
					System.out.println(results[i].getProperty("customer"));
				else
					System.out.println("null");
			}
		} catch (IOException e) {
			System.out.println("NetClient.doSearch has a problem sending the SEARCH #### command.");
			e.printStackTrace();
		}
		
		return results;
	}
	
}
