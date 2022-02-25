package com.kstamm.CT2.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.enterprisedt.net.ftp.WriteMode;

public class FTPManager {
	private FileTransferClient ftp;

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
		
	}
	private String ftp_dir1="",ftp_dir2="",ftp_dir3="",ftp_dir4="",ftp_dir5="",ftp_dir6="",ftp_dir7="",ftp_dir8 = "";
	
	public FTPManager(boolean offlinemode) throws FileNotFoundException, IOException, FTPException {
		//shutting_down=false;

		ftp  = new FileTransferClient();

			Properties p = new Properties();
		
			p.load(new FileInputStream("server.ini"));
			
			String ftp_host = p.get("ftp_host").toString();
			String ftp_user = p.get("ftp_username").toString();
			String ftp_pass = p.get("ftp_password").toString();
			
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
			
			ftp.setRemoteHost(ftp_host);
            ftp.setUserName(ftp_user);
            ftp.setPassword(ftp_pass);
            ftp.setContentType(FTPTransferType.ASCII);
            if(offlinemode==true){
            	System.out.println("FTP Manager is in offline mode.");
            } else{
	            System.out.println("Attempting connection to ftp server now...");
				ftp.connect();
				System.out.println("Successful ftp connection. We are in directory '"+ftp.getRemoteDirectory()+"'");
				
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
				
				//ftp.changeDirectory("pcclinic.com");
				//ftp.changeDirectory("www");
				//ftp.changeDirectory("tracker");

				System.out.println("We are in directory '"+ftp.getRemoteDirectory()+"'");
            }
	}
	public boolean request(String fileName){
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
	public void send(String fileName){
		
		//System.out.println("warning the single-threaded FTP uploader is in use, please refactor multi.");
		try {
			File target = new File(fileName);
	
		      if (!target.exists()) {
		          System.err.println("File " + fileName
		              + " not present to begin with!");
		          return;
		        }
		      ftp.uploadFile(fileName, fileName,WriteMode.OVERWRITE);
				//if all went well, erase the existing one.
				
		        // Quick, now, delete it immediately:
		        if (target.delete())
		          System.out.println("Deleted local temp copy of " + fileName + " ");
		        else
		          System.out.println("Failed to delete " + fileName);
		      } catch (SecurityException e) {
		        System.err.println("Unable to delete " + fileName + "("
		            + e.getMessage() + ")");
		      }
			
			
		  catch (FTPException e) {
			  System.out.println("FTPManager failing send().");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("FTPManager failing send()");	
			e.printStackTrace();
			
		}
	}
	
	public void disconnect(){
		try {
			ftp.disconnect();
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void reconnect() throws FTPException, IOException{
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
			
			//ftp.changeDirectory("pcclinic.com");
			//ftp.changeDirectory("www");
			//ftp.changeDirectory("tracker");
				
	}

}
