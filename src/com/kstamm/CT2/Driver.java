package com.kstamm.CT2;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		
		System.out.println("Version 1.2.3.2020-06-06");
		
		// we can use this class to launch each of the 3 parts.
		/*
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<args.length;i++){
			sb.append(args[i]).append(" ");
		}
		System.out.println(sb.toString());
		*/
		
		if(args[0].equals("client")){
			com.kstamm.CT2.client.ClientDaemon.main(args);
		}

		if(args[0].equals("server")){
			com.kstamm.CT2.server.ServerDaemon.main(args);
		}
		
		if(args[0].equals("admin")){
			com.kstamm.CT2.admin.AdminDaemon.main(args);
		}


		
	}

}
