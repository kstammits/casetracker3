package com.kstamm.CT2.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class DBManager {

	// The JDBC Connector Class.
	private final String dbClassName = "com.mysql.jdbc.Driver";
	
	private String dbname;
	private String dbuser;
	private String dbpass;
	private String db_address;
	
	private Connection c;
	
	private Properties properties;
	
	public DBManager(String infile) throws FileNotFoundException, IOException{
		readProperties(infile);
	}

	public void connect() throws ClassNotFoundException, SQLException{
		///see if we have a connection already?
		if(c != null){
			if(c.isValid(1)){
				if(!c.isClosed()){
					return;	
				}
			}
		}
		String CONNECTION =
			"jdbc:mysql://"+db_address+"/"+dbname;
		Class.forName(dbClassName);
		// Properties for user and password
		Properties p = new Properties();
		p.put("user",dbuser);
		p.put("password",dbpass);

		// Now try to connect
		c = DriverManager.getConnection(CONNECTION,p);	
		//System.out.println("db connected: "+c.getSchema());
	}
	
	public Properties[] getCasesToUpload() throws SQLException{
		//System.out.println("DBManager.getCasesToUpload is not implemented.");
		//check how many in db
		Statement s = c.createStatement();
		int count=0;
		s.execute("SELECT COUNT(casenumber) FROM cases WHERE onweb=\"N\"");
		//should be a one by one table here.
		ResultSet r = s.getResultSet();
		r.beforeFirst();
		while(r.next()){
			count=r.getInt(1);
		}
		
		Properties[] cs = new Properties[count];
		if(count>0){
		//fill them in
			s.execute("SELECT * FROM cases WHERE onweb=\"N\"");
			r = s.getResultSet();
			ResultSetMetaData rsmd = r.getMetaData();
			int cols = rsmd.getColumnCount();
			String[] names = new String[cols+1];
			for(int i=1;i<=cols;i++){
				names[i] = rsmd.getColumnName(i);
				//System.out.println("name"+i+" is "+names[i]);

			}
			r.beforeFirst();
			int j=0;
			while(r.next()){
				cs[j] = new Properties();
				for(int i=1;i<=cols;i++){
					if(r.getString(i)==null){
						//dont add it, leave blank
					} else{
						cs[j].setProperty(names[i],r.getString(i));
					}
				}//end for cols
				j=j+1;
			}//end while rows
		}//end if count>0
		//now strip out nondisplaying fields!
		for(int j=0;j<cs.length;j++){
			cs[j].remove("user");
			cs[j].remove("onweb");

		}
		return cs;
	}
	
	public void markOnWeb(String casenum){
		//System.out.println("DBManager.markOnWeb is not implemented.");
		Statement s;
		try {
			s = c.createStatement();
			String t = "UPDATE cases SET onweb=\"Y\" WHERE casenumber=\""+casenum+"\"";
			s.execute(t);
			//System.out.println(t);
			s.close();
		} catch (SQLException e) {
			System.out.println("ERROR dbm.markOnWeb failed for casenumber="+casenum);
			e.printStackTrace();
		}
		
	}
	
	public void disconnect() throws SQLException {
		//c.commit();
		c.close(); 
	}
	
	
	public  void testDump() throws
	ClassNotFoundException,SQLException
	{

	Statement s = c.createStatement();
	
	s.setQueryTimeout(3);
	s.execute("select * from cases");
	ResultSet r = s.getResultSet();
	
	ResultSetMetaData rsmd = r.getMetaData();
	int cols = rsmd.getColumnCount();
	String[] names = new String[cols];
	String[] values = new String[cols];
	for (int i=1;i<=cols;i++){
	     names[i-1] = rsmd.getColumnName(i);
	    System.out.print(names[i-1] + " | ");
	}
	System.out.println("\n---=-----=-=--=-===");
	r.beforeFirst();
	while(r.next()){
		for(int i=1;i<=cols;i++){
			//warning check for empty resultset
			values[i-1] = r.getString(i);
			System.out.print(values[i-1]+ " | ");
		}
		System.out.println();
	}
	System.out.println("\nDone.");

	r.close();
	s.close();

    }
	public boolean checkCaseExists(String casenumber){
		Statement s;
		boolean retval=false;
		try {
			s = c.createStatement();
			s.setQueryTimeout(3);
			s.execute("SELECT casenumber FROM cases WHERE casenumber=\""+casenumber+"\"");
			ResultSet r = s.getResultSet();
			retval=false;
			r.beforeFirst();
			
			while(r.next()){
				retval=true;
			}
			
			r.close();
			s.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retval;
		
	}
	public boolean checkEmplLogin(String user,String pass){
		Statement s;
		boolean retval=false;
		try {
			s = c.createStatement();
			s.setQueryTimeout(3);
			s.execute("select login,pass from employees");
			ResultSet r = s.getResultSet();
			while(r.next()){
				//System.out.println("("+r.getString(1) + ","+r.getString(2)+")");
				//if(r.wasNull())
				//	System.out.println("returned nulls");
				if(user.equals(r.getString(1))) {
					if(pass.equals(r.getString(2))){
						retval =  true;
					}else{
						retval =  false; // bad pass
					}
				}
			}
			r.close();
			s.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retval;
	}
	
	private void readProperties(String infile) throws FileNotFoundException, IOException{
		properties = new Properties();
	    properties.load(new FileInputStream(infile));
	    dbuser = properties.get("dbuser").toString();
	    dbpass = properties.get("dbpass").toString();
	    dbname = properties.get("dbname").toString();
	    db_address = properties.get("db_address").toString();
	}

	public void receiveCase(Properties p) throws SQLException {
		String casenumber = p.getProperty("casenumber");
		if(this.checkCaseExists(casenumber)){//update existing one
			p.remove("casenumber");
			Enumeration<Object> e = p.keys();
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE cases SET onweb=\"N\", ");
			
			while(e.hasMoreElements()){
				String key = e.nextElement().toString();
				String value = p.getProperty(key);
				sb.append(key);
				
				sb.append("=\"");
				value = this.SQL_Escape(value);
				sb.append(value);
				if(e.hasMoreElements())
					sb.append("\", ");
				else
					sb.append("\" ");	
			}
			
			sb.append(" WHERE casenumber=\""+casenumber+"\"");
			Statement s = c.createStatement();
	//		System.out.println(sb.toString());
			s.execute(sb.toString());
		}else{//make a new one
			Enumeration<Object> e = p.keys();
			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			
			sb1.append("INSERT INTO cases (");
			sb2.append("VALUES (");
			
			while(e.hasMoreElements()){
				String key = e.nextElement().toString();
				String value = p.getProperty(key);
				sb1.append(key);
				if(e.hasMoreElements())
					sb1.append(", ");
				else
					sb1.append(") ");
				
				sb2.append("\"");
				value = this.SQL_Escape(value);
				sb2.append(value);
				if(e.hasMoreElements())
					sb2.append("\", ");
				else
					sb2.append("\") ");	
			}
			sb1.append(sb2);
			Statement s = c.createStatement();
	//		System.out.println(sb1.toString());
			s.execute(sb1.toString());
		}//end make new one

	}

	private String SQL_Escape(String value) {
	//	System.out.println("SQL_Escape");
	//	System.out.println(value);
		value = value.replaceAll("[\\\\]", "\\\\\\\\");//double all slashies.
		value = value.replaceAll("\"", "\\\\\"");//escape quotation marks.
	//	System.out.println(value);
		return value;
	}

	public Properties retrieveCase(String casenumber) {
		Statement s;
		Properties p = new Properties();
		try {
			s = c.createStatement();
			s.execute("SELECT * FROM cases WHERE casenumber=\""+casenumber+"\"");
			ResultSet r = s.getResultSet();

			ResultSetMetaData rsmd = r.getMetaData();
			int cols = rsmd.getColumnCount();
			String[] names = new String[cols+1];
			for(int i=1;i<=cols;i++){
				names[i] = rsmd.getColumnName(i);
			}
			r.beforeFirst();
			while(r.next()){
				p = new Properties();
				for(int i=1;i<=cols;i++){
					if(r.getString(i)==null){
						//dont add it, leave blank
					} else{
						p.setProperty(names[i],r.getString(i));
					}
				}//end for cols
			}//end while rows
			
			r.close();
			s.close();
		} catch (SQLException e) {
			System.out.println("DBM has a problem retreiving a case#"+casenumber);
			e.printStackTrace();
		}

		return p;
	}

	public Properties[] doSearch(String member_name) {
		Statement s;
		Properties p = new Properties();
		Properties[] results=new Properties[0];
		Vector<Properties> vector=new Vector<Properties>();
		
		try {
			s = c.createStatement();
			s.execute("SELECT customer,casenumber,request,model FROM cases WHERE customer LIKE \"%"+member_name+"%\"");
			ResultSet r = s.getResultSet();

			ResultSetMetaData rsmd = r.getMetaData();
			int cols = rsmd.getColumnCount();
			String[] names = new String[cols+1];
			for(int i=1;i<=cols;i++){
				names[i] = rsmd.getColumnName(i);
			}
			r.beforeFirst();
			while(r.next()){
				p = new Properties();
				for(int i=1;i<=cols;i++){
					if(r.getString(i)==null){
						//dont add it, leave blank
					} else{
						p.setProperty(names[i],r.getString(i));
					}
				}//end for cols
				vector.add(p);
			}//end while rows
			results = vector.toArray(results);
			
			r.close();
			s.close();
		} catch (SQLException e) {
			System.out.println("DBM has a problem querying a customer name: "+member_name);
			e.printStackTrace();
		}

		return results;
	}
}
