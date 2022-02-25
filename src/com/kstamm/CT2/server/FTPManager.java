package com.kstamm.CT2.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;
import java.util.Properties;

import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileStatistics;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.enterprisedt.net.ftp.WriteMode;

public class FTPManager {
	private Boolean ENABLED;
	private Boolean PERMIT_FAILURE=false; // do we let the user continue with a failed FTP connection?
	
	private FileTransferClient ftp;
	//private boolean shutting_down;
	String ftp_host,ftp_pass,ftp_user;
	public void shutdown(){
		System.out.println("FTPManager begin shutdown attempt.");
		//shutting_down=true;
		nonthreaded_shutdown();
	}
	
	private void nonthreaded_shutdown(){
		
		try {
			ftp.disconnect();
			System.out.println("FTPManager successful shutdown.");
		} catch (FTPException e) {
			System.out.println("FTPManager FAILING shutdown.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("FTPManager FAILING shutdown.");
			e.printStackTrace();
		}
		ENABLED = false;
	}
	private String ftp_dir1="",ftp_dir2="",ftp_dir3="",ftp_dir4="",ftp_dir5="",ftp_dir6="",ftp_dir7="",ftp_dir8 = "";
	
	public FTPManager(boolean offlinemode) throws FileNotFoundException, IOException, FTPException {
		//shutting_down=false;

		ftp  = new FileTransferClient();

			Properties p = new Properties();
		
			p.load(new FileInputStream("server.ini"));
			
			ftp_host = p.get("ftp_host").toString();
			ftp_user = p.get("ftp_username").toString();
			ftp_pass = p.get("ftp_password").toString();
			try{
			 ftp_dir1 = p.get("ftp_dir1").toString();
			}catch(NullPointerException npe){
				System.out.println("ftp_dir1 not set.");
			}
			try{
			 ftp_dir2 = p.get("ftp_dir2").toString();
			}catch(NullPointerException npe){
				System.out.println("ftp_dir2 not set.");
			}try{
			 ftp_dir3 = p.get("ftp_dir3").toString();
			}catch(NullPointerException npe){
				System.out.println("ftp_dir3 not set.");
			}try{
			 ftp_dir4 = p.get("ftp_dir4").toString();
			}catch(NullPointerException npe){
				System.out.println("ftp_dir4 not set.");
			}try{
				 ftp_dir5 = p.get("ftp_dir5").toString();
			}catch(NullPointerException npe){
				System.out.println("ftp_dir5 not set.");
			}try{
			 ftp_dir6 = p.get("ftp_dir6").toString();
			}catch(NullPointerException npe){
				System.out.println("ftp_dir6 not set.");
			}try{
			 ftp_dir7 = p.get("ftp_dir7").toString();
			}catch(NullPointerException npe){
				System.out.println("ftp_dir7 not set.");
			}try{
			 ftp_dir8 = p.get("ftp_dir8").toString();
			}catch(NullPointerException npe){
				System.out.println("ftp_dir8 not set.");
			}
			this.reconnect(0);
			if(ENABLED) {
				System.out.println("We are in directory '"+ftp.getRemoteDirectory()+"'");
			}else {
				System.out.println("WARN: FTP did not connect.");
			}
            
	}
	
	public void disconnect(){
		System.out.println(new Date().toString() + "   FTP Disconnect");
		if(!ENABLED) {
			return; // disconnect is nonsense
		}
		try {
			ftp.disconnect();
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			if(e instanceof java.net.SocketException){
				if(e.getMessage().contains("Connection reset by peer")){
					// then it's still disconnected success.
				}
			}else{
				e.printStackTrace();
			}
		}
		
	}
	
	public void reconnect( int tries) throws FTPException, IOException{
		try{
			ftp.setUserName(this.ftp_user);
			ftp.setRemoteHost(this.ftp_host);
			ftp.setPassword(this.ftp_pass);
			ftp.setContentType(FTPTransferType.ASCII);
			ENABLED = true;
		}catch(FTPException ftpe){
			if(ftpe.getMessage().contains("action must be performed before a connection")){
				//no problem, the data is already set.
				
			}else{
				ftpe.printStackTrace();
			}
		}
		try{
		ftp.connect();
		  if(ftp_dir1.length()>0)
			{
				ftp.changeDirectory(ftp_dir1);
				if(ftp_dir2.length()>0)
				{
					ftp.changeDirectory(ftp_dir2);
					if(ftp_dir3.length()>0)
					{
						ftp.changeDirectory(ftp_dir3);
						if(ftp_dir4.length()>0)
						{
							ftp.changeDirectory(ftp_dir4);
							if(ftp_dir5.length()>0)
							{
								ftp.changeDirectory(ftp_dir5);
								if(ftp_dir6.length()>0)
								{
									ftp.changeDirectory(ftp_dir6);
									if(ftp_dir7.length()>0)
									{
										ftp.changeDirectory(ftp_dir7);
										if(ftp_dir8.length()>0)
										{
											ftp.changeDirectory(ftp_dir8);
										}
									}
								}
							}
						}
					}
				}
			}
			System.out.println("FTP Manager initialized successfully.");
			ENABLED = true;
		}catch(FTPException ftpe){
			if(ftpe.getMessage().contains("Login incorrect")){
				//just an error about login incorrect bullshit
				if(tries<6){
					System.out.print("FTP Login Incorrect...");
					try {
						Thread.sleep(1500+500*tries);
						System.out.println(" retrying...");
						this.reconnect(tries+1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					System.out.println("FTP Login failure... giving up");
					ENABLED = false;
				}
			}else{
				//different error:
				ftpe.printStackTrace();
				ENABLED = false;
			}
		}catch(ConnectException ce) {
			ENABLED = false;
			ce.printStackTrace();
			if(PERMIT_FAILURE) {
				System.out.println("FTP Connect failure.. disabling for now WARN WARN");
			}else {
				System.out.println("\nFTP Connect failure.\n");
				System.exit(1);
			}
		}
	}
	
	public int send(String fileName){
		if(!ENABLED) {
			System.out.println("send failed: FTP not enabled.");
			return(0);
		}
		//System.out.println("warning the single-threaded FTP uploader is in use, please refactor multi.");
		int nowSent=0;int alreadySent=0; // track sending statistics
		try {
			File target = new File(fileName);
		      if (!target.exists()) {
		          System.err.println("Bug: File " + fileName
		              + " not present to send.");
		          return(0);
		        }
		      if(!ftp.isConnected()){
		    	  this.reconnect(0);
		      }
		      if(ftp.isConnected()){
		    	  System.out.println("Connected to  '"+ftp.getRemoteDirectory()+"'");
		    	  FileStatistics beforeSend = ftp.getStatistics();
		    	  alreadySent = beforeSend.getUploadCount();
		    	  // the NTD version needed this stuff
		    	  // the goDaddy didnt ever say Illegal PORT Command
		    	  
		    	//  try{
		    	//	  ftp.uploadFile(fileName, fileName,WriteMode.OVERWRITE);
		    	//  }catch(com.enterprisedt.net.ftp.FTPException fe){
		    //		  if(fe.getMessage().contains("Illegal PORT command")){
		    			  //try passive mode.
		    			  ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);
				    	  ftp.uploadFile(fileName, fileName,WriteMode.OVERWRITE);
		    	//	  }
		    	//  }
				    	  
		    	  FileStatistics afterSend = ftp.getStatistics();
		    	  nowSent = afterSend.getUploadCount();
		    	  if(nowSent == alreadySent){
		    		  // failure to increment getUploadCount, or failure to send.
		    		  System.out.println("Failure to send files, still "+nowSent + " sent.");
		    	  }
		      }
		      else{
		    	  System.out.println("Error: cannot connect to FTP server.");
		      }
		      if(nowSent > alreadySent){
				  //if all went well, erase the existing one.
		    	  target.delete();
		      }
	     } catch (SecurityException e) {
	        System.err.println("Unable to delete " + fileName + "("
	            + e.getMessage() + ")");
	     }
		catch (FTPException e) {
			  System.out.println("FTPManager failing send()");
			  e.printStackTrace();
		} catch (IOException e) {
			System.out.println("FTPManager failing send()");	
			e.printStackTrace();
		}
		return(nowSent - alreadySent);
	}

	
	public boolean request(String fileName){
		if(!ENABLED) {
			System.out.println("request failed: FTP not enabled.");
			return(false);
		}
		try {
			ftp.downloadFile(fileName, fileName);
			File f = new File(fileName);
			return f.exists();
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
