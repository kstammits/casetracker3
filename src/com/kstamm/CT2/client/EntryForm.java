package com.kstamm.CT2.client;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.JTextComponent;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.jgoodies.forms.layout.*;



/**
 * @author Karl Stamm
 * TODO ServerDaemon crashing on FTP idle disconnection.
 * TODO ServerDaemon crashing on DB reconnection attempts.
 * not sure why second reconnected Client fails to login.
 */


public class EntryForm {
	
	private final boolean HAS_FIRST_PANEL = true;
	private final boolean HAS_SECOND_PANEL = false;
	private final boolean HAS_THIRD_PANEL = false;
	
	private ClientDaemon master;
	private Properties contextMenuItems;
	
	public EntryForm(ClientDaemon cd) {
		master = cd;
		master.registerEF(this);
		initComponents();

		try {
			// and load the context menu items too
			this.contextMenuItems = new Properties();
			contextMenuItems.load(new FileInputStream("context_menu.ini"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to load context_menu.ini");
			contextMenuItems=null; // delete the object;
		}
		
	}
	
	public JPanel getForm(){
		return EntryForm;
	}

	private void but_SaveActionPerformed(ActionEvent e) {
		if(this.isOkayCasenum(this.txt_casenumber.getText())){
			Properties p = saveAllProperties();
			p.setProperty("user", this.txt_User.getText());
			p.setProperty("lastedit",formatDate(new Date()));
				int r = JOptionPane.showConfirmDialog(null, "Press OK to save the new data to the database.\nAll information in the 'Previous' column will be overwritten!\nPress Cancel to abort.", "Are you Sure?", JOptionPane.OK_CANCEL_OPTION);
				if(r==0){//okay
					master.saveCase(p);
				}else if(r==2){//cancel
					System.out.println("Save canceled.");
				}
		} else{
			JOptionPane.showMessageDialog(null, "That casenumber is invalid, you must use a different one.");
		}
		
	}
	
	private void but_SearchActionPerformed(ActionEvent e) {
		if(search_Form==null){
			search_Form = new Search_Form(master);
		}
		search_Form.pack();
		search_Form.setVisible(true);
	}
	
	private boolean isOkayCasenum(String cn){
		boolean retval = true;
		
		if(cn.length() < 2)
			return(false);
		
		String[] bad_names = {"tracker",
							  "webpages",
							  "index",
							  ".."};
		for(int i=0;i<bad_names.length;i++)
		if(cn.startsWith(bad_names[i])){
			retval = false;
		}
		
		return retval;
	}
	
	private boolean toSave(JTextField live, JTextField prev){
		String l = live.getText();
		String p = prev.getText();
		if(l.equals(p)){
			return false;
		}else
			return true;
	}
	
	private boolean toSave(JEditorPane live, JEditorPane prev){
		String l = live.getText();
		String p = prev.getText();
		if(l.equals(p)){
			return false;
		}else
			return true;
	}
	
	private Properties saveAllProperties() throws NumberFormatException{
		Properties p = new Properties();
		String t;
		if(txt_casenumber.getText().length() > 0){
			p.setProperty("casenumber", txt_casenumber.getText());
			//everything else is inside this IF
			
		if(txt_casestatus.getText().length() > 0){
				p.setProperty("phone", txt_casestatus.getText());
		}
		
		if(toSave(txt_arrival1,txt_arrival1_prev)){
			t= this.txt_arrival1.getText();
			p.setProperty("arrival", t);
		}
		if(toSave(txt_arrival2,txt_arrival2_prev)){
			t= this.txt_arrival2.getText();
			p.setProperty("arrival2", t);
		}
		if(toSave(txt_arrival3,txt_arrival3_prev)){
			t= this.txt_arrival3.getText();
			p.setProperty("arrival3", t);
		}
		
		if(toSave(txt_request1,txt_request1_prev)){
			t= this.txt_request1.getText();
			p.setProperty("request", t);
		}
		if(toSave(txt_request2,txt_request2_prev)){
			t= this.txt_request2.getText();
			p.setProperty("request2", t);
		}
		if(toSave(txt_request3,txt_request3_prev)){
			t= this.txt_request3.getText();
			p.setProperty("request3", t);
		}
		
		if(toSave(txt_sendbox1,txt_sendbox1_prev)){
			t= this.txt_sendbox1.getText();
			p.setProperty("sendbox", t);
		}		
		if(toSave(txt_sendbox2,txt_sendbox2_prev)){
			t= this.txt_sendbox2.getText();
			p.setProperty("sendbox2", t);
		}	
		if(toSave(txt_sendbox3,txt_sendbox3_prev)){
			t= this.txt_sendbox3.getText();
			p.setProperty("sendbox3", t);
		}	

		if(toSave(txt_shipdate1,txt_shipdate1_prev)){
			t= this.txt_shipdate1.getText();
			p.setProperty("shipdate", t);
		}	
		if(toSave(txt_shipdate2,txt_shipdate2_prev)){
			t= this.txt_shipdate2.getText();
			p.setProperty("shipdate2", t);
		}	
		if(toSave(txt_shipdate3,txt_shipdate3_prev)){
			t= this.txt_shipdate3.getText();
			p.setProperty("shipdate3", t);
		}	
		/////////////////////////////////////////////////////////////
		if(toSave(txt_customer,txt_customer_prev)){
			p.setProperty("customer", txt_customer.getText());
		}

		if(toSave(txt_desc1,txt_desc1_prev)){
			p.setProperty("nature", txt_desc1.getText());
		}
		if(toSave(txt_desc2,txt_desc2_prev)){
			p.setProperty("nature2", txt_desc2.getText());
		}
		if(toSave(txt_desc3,txt_desc3_prev)){
			p.setProperty("nature3", txt_desc3.getText());
		}

		
		if(toSave(txt_price1,txt_price1_prev)){
			p.setProperty("price1", txt_price1.getText());
		}
		if(toSave(txt_price2,txt_price2_prev)){
			p.setProperty("price2", txt_price2.getText());
		}
		if(toSave(txt_price3,txt_price3_prev)){
			p.setProperty("price3", txt_price3.getText());
		}
		
		if(toSave(txt_returnlabel,txt_returnlabel_prev)){
			p.setProperty("returnlabel", txt_returnlabel.getText());
		}
		if(toSave(txt_returnlabel2,txt_returnlabel2_prev)){
			p.setProperty("returnlabel2", txt_returnlabel2.getText());
		}
		if(toSave(txt_returnlabel3,txt_returnlabel3_prev)){
			p.setProperty("returnlabel3", txt_returnlabel3.getText());
		}
		
		if(toSave(txt_sendboxtrack,txt_sendboxtrack_prev)){
			p.setProperty("sendboxtrack", txt_sendboxtrack.getText());
		}
		if(toSave(txt_sendboxtrack2,txt_sendboxtrack2_prev)){
			p.setProperty("sendboxtrack2", txt_sendboxtrack2.getText());
		}
		if(toSave(txt_sendboxtrack3,txt_sendboxtrack3_prev)){
			p.setProperty("sendboxtrack3", txt_sendboxtrack3.getText());
		}
		
		if(toSave(txt_ship,txt_ship_prev)){
			p.setProperty("ship", txt_ship.getText());
		}
		if(toSave(txt_ship2,txt_ship2_prev)){
			p.setProperty("ship2", txt_ship2.getText());
		}
		if(toSave(txt_ship3,txt_ship3_prev)){
			p.setProperty("ship3", txt_ship3.getText());
		}
		
		if(toSave(txt_tech,txt_tech_prev)){
			p.setProperty("tech", txt_tech.getText());
		}
		if(toSave(txt_tech2,txt_tech2_prev)){
			p.setProperty("tech2", txt_tech2.getText());
		}
		if(toSave(txt_tech3,txt_tech3_prev)){
			p.setProperty("tech3", txt_tech3.getText());
		}
		
		if(toSave(edt_accessory,edt_accessory_prev)){
			p.setProperty("accessory", edt_accessory.getText());
		}
		if(toSave(edt_accessory2,edt_accessory2_prev)){
			p.setProperty("accessory2", edt_accessory2.getText());
		}
		if(toSave(edt_accessory3,edt_accessory3_prev)){
			p.setProperty("accessory3", edt_accessory3.getText());
		}

		if(toSave(edt_technote1,edt_technote1_prev)){
			p.setProperty("desc1", edt_technote1.getText());
		}
		if(toSave(edt_technote2,edt_technote2_prev)){
			p.setProperty("desc2", edt_technote2.getText());
		}
		if(toSave(edt_technote3,edt_technote3_prev)){
			p.setProperty("desc3", edt_technote3.getText());
		}
		
		
	
		if(toSave(edt_diags,edt_diags_prev)){
			p.setProperty("diags", edt_diags.getText());
		}
		if(toSave(edt_diags2,edt_diags2_prev)){
			p.setProperty("diags2", edt_diags2.getText());
		}
		if(toSave(edt_diags3,edt_diags3_prev)){
			p.setProperty("diags3", edt_diags3.getText());
		}
		
		if(toSave(edt_message,edt_message_prev)){
			p.setProperty("message", edt_message.getText());
		}
		if(toSave(edt_message2,edt_message2_prev)){
			p.setProperty("message2", edt_message2.getText());
		}
		if(toSave(edt_message3,edt_message3_prev)){
			p.setProperty("message3", edt_message3.getText());
		}
		
		if(toSave(edt_parts,edt_parts_prev)){
			p.setProperty("parts", edt_parts.getText());
		}
		if(toSave(edt_parts2,edt_parts2_prev)){
			p.setProperty("parts2", edt_parts2.getText());
		}
		if(toSave(edt_parts3,edt_parts3_prev)){
			p.setProperty("parts3", edt_parts3.getText());
		}
		
		if(toSave(edt_services,edt_services_prev)){
			p.setProperty("services", edt_services.getText());
		}
		if(toSave(edt_services2,edt_services2_prev)){
			p.setProperty("services2", edt_services2.getText());
		}
		if(toSave(edt_services3,edt_services3_prev)){
			p.setProperty("services3", edt_services3.getText());
		}
		
		if(toSave(txt_model,txt_model_prev)){
			p.setProperty("model", txt_model.getText());
		}
		
		}//end if casenumber is longer than zero characters.
		
		return p;
	}
	
	private String formatDate(Date d){

		StringBuffer sb = new StringBuffer(19);
		
		int year = d.getYear()+1900;
		int month = d.getMonth()+1;//buffer to two spaces
		int day = d.getDate(); // buffer to two spaces
		int hour = d.getHours(); // buffer to two spaces
		int min = d.getMinutes(); //buffer to two spaces
		
		sb.append(year);
		sb.append("-");
		if(month<10){
			sb.append("0");
		}
		sb.append(month);
		sb.append("-");
		if(day<10){
			sb.append("0");
		}
		sb.append(day);
		sb.append(" ");
		if(hour<10){
			sb.append("0");
		}
		sb.append(hour);
		sb.append(":");
		if(min<10){
			sb.append("0");
		}
		sb.append(min);
		sb.append(":00");
		
		return sb.toString();
		
	}
	
	
	
	
	
	@SuppressWarnings("deprecation")
	private Date unFormatDate(String s){
		Date d = new Date();
		//"2008-04-13 22:18:00.0"
		//"012345678901234567890"
		d.setYear(Integer.parseInt(s.substring(0, 4)));
		d.setMonth(Integer.parseInt(s.substring(5, 7)));
		d.setDate(Integer.parseInt(s.substring(8, 10)));
		d.setHours(Integer.parseInt(s.substring(11, 13)));
		d.setMinutes(Integer.parseInt(s.substring(14, 16)));
		d.setSeconds(0);
		//smiles 1-5 returns mile
		return d;
	}
	
	private void but_LoadActionPerformed(ActionEvent e) {
		//so the load button should do something huh?
		String i = this.txt_casenumber.getText();
		if(i != null && i.length()>=1){
			int r = JOptionPane.showConfirmDialog(null, "Press OK to load case "+i+" from the database.\nAll information in the 'New' column will be overwritten!\nPress Cancel to abort.", "LOAD: Are you Sure?", JOptionPane.OK_CANCEL_OPTION);
			if(r==0){
				//okay
				Properties p = master.loadCase(txt_casenumber.getText());
				if(p.containsKey("casenumber")){
					// then we got one
					this.clearAllFields();
					this.loadFormFromProperties(p);
				}else{
					// case# not found.
					JOptionPane.showMessageDialog(null, "Case "+i+" not found in database.");
				}

			}else if(r==2){
				//cancel
				//do nothing I guess.
				System.out.println("Load canceled.");
			}
			
		}else {
			JOptionPane.showMessageDialog(null, "ERROR - You must enter a case number in the topmost field to load a case.");
		}
	}

	/*
	// JDatePicker nonsense::: version 1.3.4  downloaded 2016 from sourceforge
	class DateLabelFormatter extends AbstractFormatter {

	    private String datePattern = "yyyy-MM-dd";
	    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	    @Override
	    public Object stringToValue(String text) throws ParseException {
	        return dateFormatter.parseObject(text);
	    }

	    @Override
	    public String valueToString(Object value) throws ParseException {
	        if (value != null) {
	            Calendar cal = (Calendar) value;
	            return dateFormatter.format(cal.getTime());
	        }

	        return "";
	    }

	}
	private JDatePicker setupDatePicker(Component jdp) {
		UtilDateModel model = new UtilDateModel();
		//model.setDate(20,04,2014);
		// Need this...
		Properties p = new Properties();
		p.put("text.today", "Today");// these are English spellings of the words. 
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		// Don't know about the formatter, but there it is...
		jdp = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		return (JDatePicker) (jdp);
	}

	*/
	
	private void clearAllFields() {
	
		this.txt_casenumber.setText("");
		this.txt_casestatus.setText("");
		this.txt_customer.setText("");
		this.txt_lastedit.setText("");
		this.txt_desc1.setText("");
		this.txt_desc2.setText("");
		this.txt_desc3.setText("");
		this.txt_returnlabel.setText("");
		this.txt_returnlabel2.setText("");
		this.txt_returnlabel3.setText("");
		this.txt_sendboxtrack.setText("");
		this.txt_sendboxtrack2.setText("");
		this.txt_sendboxtrack3.setText("");
		this.txt_ship.setText("");
		this.txt_ship2.setText("");
		this.txt_ship3.setText("");
		this.txt_tech.setText("");
		this.txt_tech2.setText("");
		this.txt_tech3.setText("");
		this.txt_price1.setText("");
		this.txt_price2.setText("");
		this.txt_price3.setText("");
		
		this.txt_model.setText("");
	
		this.txt_casenumber_prev.setText("");
		this.txt_casestatus_prev.setText("");
		this.txt_customer_prev.setText("");
		this.txt_desc1_prev.setText("");
		this.txt_desc2_prev.setText("");
		this.txt_desc3_prev.setText("");
		this.txt_returnlabel_prev.setText("");
		this.txt_returnlabel2_prev.setText("");
		this.txt_returnlabel3_prev.setText("");
		this.txt_sendboxtrack_prev.setText("");
		this.txt_sendboxtrack2_prev.setText("");
		this.txt_sendboxtrack3_prev.setText("");
		this.txt_ship_prev.setText("");
		this.txt_ship2_prev.setText("");
		this.txt_ship3_prev.setText("");
		this.txt_tech_prev.setText("");
		this.txt_tech2_prev.setText("");
		this.txt_tech3_prev.setText("");
		this.txt_price1_prev.setText("");
		this.txt_price2_prev.setText("");
		this.txt_price3_prev.setText("");
		
		Date d = new Date();
		this.txt_arrival1.setText("");
		this.txt_arrival2.setText("");
		this.txt_arrival3.setText("");
		this.txt_request1.setText("");
		this.txt_request2.setText("");
		this.txt_request3.setText("");
		this.spn_lastedit.setValue(d);
		this.txt_sendbox1.setText("");
		this.txt_sendbox2.setText("");
		this.txt_sendbox3.setText("");
		this.txt_shipdate1.setText("");
		this.txt_shipdate2.setText("");
		this.txt_shipdate3.setText("");
		
		this.txt_arrival1_prev.setText("");
		this.txt_arrival2_prev.setText("");
		this.txt_arrival3_prev.setText("");
		this.txt_request1_prev.setText("");
		this.txt_request2_prev.setText("");
		this.txt_request3_prev.setText("");
		this.txt_sendbox1_prev.setText("");
		this.txt_sendbox2_prev.setText("");
		this.txt_sendbox3_prev.setText("");
		this.txt_shipdate1_prev.setText("");
		this.txt_shipdate2_prev.setText("");
		this.txt_shipdate3_prev.setText("");
		this.edt_technote1.setText("");
		this.edt_technote2.setText("");
		this.edt_technote3.setText("");
		this.edt_parts.setText("");
		this.edt_parts2.setText("");
		this.edt_parts3.setText("");		
		this.edt_services.setText("");
		this.edt_services2.setText("");
		this.edt_services3.setText("");
		this.edt_message.setText("");
		this.edt_message2.setText("");
		this.edt_message3.setText("");
		this.edt_accessory.setText("");
		this.edt_accessory2.setText("");
		this.edt_accessory3.setText("");
		this.edt_diags.setText("");
		this.edt_diags2.setText("");
		this.edt_diags3.setText("");
		this.edt_technote1_prev.setText("");
		this.edt_technote2_prev.setText("");
		this.edt_technote3_prev.setText("");
		this.edt_parts_prev.setText("");
		this.edt_parts2_prev.setText("");
		this.edt_parts3_prev.setText("");		
		this.edt_services_prev.setText("");
		this.edt_services2_prev.setText("");
		this.edt_services3_prev.setText("");
		this.edt_message_prev.setText("");
		this.edt_message2_prev.setText("");
		this.edt_message3_prev.setText("");
		this.edt_accessory_prev.setText("");
		this.edt_accessory2_prev.setText("");
		this.edt_accessory3_prev.setText("");
		this.edt_diags_prev.setText("");
		this.edt_diags2_prev.setText("");
		this.edt_diags3_prev.setText("");
		this.txt_model_prev.setText("");
		
	}

	public void loadFormFromProperties(Properties p) {
		if(p.containsKey("casenumber")){
			txt_casenumber.setText(p.getProperty("casenumber"));
			txt_casenumber_prev.setText(p.getProperty("casenumber"));
			
			if(p.containsKey("customer")){
				txt_customer.setText(p.getProperty("customer"));
				txt_customer_prev.setText(p.getProperty("customer"));
			}
			if(p.containsKey("phone")){
				txt_casestatus.setText(p.getProperty("phone"));
				txt_casestatus_prev.setText(p.getProperty("phone"));
			}
			if(p.containsKey("price1")){
				txt_price1.setText(p.getProperty("price1"));
				txt_price1_prev.setText(p.getProperty("price1"));
			}
			if(p.containsKey("price2")){
				txt_price2.setText(p.getProperty("price2"));
				txt_price2_prev.setText(p.getProperty("price2"));
			}
			if(p.containsKey("price3")){
				txt_price3.setText(p.getProperty("price3"));
				txt_price3_prev.setText(p.getProperty("price3"));
			}

			if(p.containsKey("arrival")){
				String d = p.getProperty("arrival");
				txt_arrival1.setText(d);
				txt_arrival1_prev.setText(d);
			}
			if(p.containsKey("arrival2")){
				String d = p.getProperty("arrival2");
				txt_arrival2.setText(d);
				txt_arrival2_prev.setText(d);
			}
			if(p.containsKey("arrival3")){
				String d = p.getProperty("arrival3");
				txt_arrival3.setText(d);
				txt_arrival3_prev.setText(d);
			}

			if(p.containsKey("request")){
				String d = p.getProperty("request");
				txt_request1.setText(d);
				txt_request1_prev.setText(d);
			}
			if(p.containsKey("request2")){
				String d = p.getProperty("request2");
				txt_request2.setText(d);
				txt_request2_prev.setText(d);
			}
			if(p.containsKey("request3")){
				String d = p.getProperty("request3");
				txt_request3.setText(d);
				txt_request3_prev.setText(d);
			}
			if(p.containsKey("sendbox")){
				String d = p.getProperty("sendbox");
				txt_sendbox1.setText(d);
				txt_sendbox1_prev.setText(d);
			}
			if(p.containsKey("sendbox2")){
				String d = p.getProperty("sendbox2");
				txt_sendbox2.setText(d);
				txt_sendbox2_prev.setText(d);
			}
			if(p.containsKey("sendbox3")){
				String d = p.getProperty("sendbox3");
				txt_sendbox3.setText(d);
				txt_sendbox3_prev.setText(d);
			}
			
			if(p.containsKey("shipdate")){
				String d = p.getProperty("shipdate");
				txt_shipdate1.setText(d);
				txt_shipdate1_prev.setText(d);
			}
			if(p.containsKey("shipdate2")){
				String d = p.getProperty("shipdate2");
				txt_shipdate2.setText(d);
				txt_shipdate2_prev.setText(d);
			}
			if(p.containsKey("shipdate3")){
				String d = p.getProperty("shipdate3");
				txt_shipdate3.setText(d);
				txt_shipdate3_prev.setText(d);
			}
			
			
			if(p.containsKey("nature")){
				txt_desc1.setText(p.getProperty("nature"));
				txt_desc1_prev.setText(p.getProperty("nature"));
			}
			if(p.containsKey("nature2")){
				txt_desc2.setText(p.getProperty("nature2"));
				txt_desc2_prev.setText(p.getProperty("nature2"));
			}
			if(p.containsKey("nature3")){
				txt_desc3.setText(p.getProperty("nature3"));
				txt_desc3_prev.setText(p.getProperty("nature3"));
			}
			
			if(p.containsKey("returnlabel")){
				txt_returnlabel.setText(p.getProperty("returnlabel"));
				txt_returnlabel_prev.setText(p.getProperty("returnlabel"));
			}
			if(p.containsKey("returnlabel2")){
				txt_returnlabel2.setText(p.getProperty("returnlabel2"));
				txt_returnlabel2_prev.setText(p.getProperty("returnlabel2"));
			}
			if(p.containsKey("returnlabel3")){
				txt_returnlabel3.setText(p.getProperty("returnlabel3"));
				txt_returnlabel3_prev.setText(p.getProperty("returnlabel3"));
			}
			
			if(p.containsKey("sendboxtrack")){
				txt_sendboxtrack.setText(p.getProperty("sendboxtrack"));
				txt_sendboxtrack_prev.setText(p.getProperty("sendboxtrack"));
			}
			if(p.containsKey("sendboxtrack2")){
				txt_sendboxtrack2.setText(p.getProperty("sendboxtrack2"));
				txt_sendboxtrack2_prev.setText(p.getProperty("sendboxtrack2"));
			}
			if(p.containsKey("sendboxtrack3")){
				txt_sendboxtrack3.setText(p.getProperty("sendboxtrack3"));
				txt_sendboxtrack3_prev.setText(p.getProperty("sendboxtrack3"));
			}
			
			if(p.containsKey("ship")){
				txt_ship.setText(p.getProperty("ship"));
				txt_ship_prev.setText(p.getProperty("ship"));
			}
			if(p.containsKey("ship2")){
				txt_ship2.setText(p.getProperty("ship2"));
				txt_ship2_prev.setText(p.getProperty("ship2"));
			}
			if(p.containsKey("ship3")){
				txt_ship3.setText(p.getProperty("ship3"));
				txt_ship3_prev.setText(p.getProperty("ship3"));
			}
			
			if(p.containsKey("tech")){
				txt_tech.setText(p.getProperty("tech"));
				txt_tech_prev.setText(p.getProperty("tech"));
			}
			if(p.containsKey("tech2")){
				txt_tech2.setText(p.getProperty("tech2"));
				txt_tech2_prev.setText(p.getProperty("tech2"));
			}
			if(p.containsKey("tech3")){
				txt_tech3.setText(p.getProperty("tech3"));
				txt_tech3_prev.setText(p.getProperty("tech3"));
			}
			
			
			if(p.containsKey("accessory")){
				edt_accessory.setText(p.getProperty("accessory"));
				edt_accessory_prev.setText(p.getProperty("accessory"));
			}
			if(p.containsKey("accessory2")){
				edt_accessory2.setText(p.getProperty("accessory2"));
				edt_accessory2_prev.setText(p.getProperty("accessory2"));
			}
			if(p.containsKey("accessory3")){
				edt_accessory3.setText(p.getProperty("accessory3"));
				edt_accessory3_prev.setText(p.getProperty("accessory3"));
			}
			//desc
			if(p.containsKey("desc1")){
				edt_technote1.setText(p.getProperty("desc1"));
				edt_technote1_prev.setText(p.getProperty("desc1"));
			}
			if(p.containsKey("desc2")){
				edt_technote2.setText(p.getProperty("desc2"));
				edt_technote2_prev.setText(p.getProperty("desc2"));
			}
			if(p.containsKey("desc3")){
				edt_technote3.setText(p.getProperty("desc3"));
				edt_technote3_prev.setText(p.getProperty("desc3"));
			}
			//diags
			if(p.containsKey("diags")){
				edt_diags.setText(p.getProperty("diags"));
				edt_diags_prev.setText(p.getProperty("diags"));
			}
			if(p.containsKey("diags2")){
				edt_diags2.setText(p.getProperty("diags2"));
				edt_diags2_prev.setText(p.getProperty("diags2"));
			}
			if(p.containsKey("diags3")){
				edt_diags3.setText(p.getProperty("diags3"));
				edt_diags3_prev.setText(p.getProperty("diags3"));
			}
			//message
			if(p.containsKey("message")){
				edt_message.setText(p.getProperty("message"));
				edt_message_prev.setText(p.getProperty("message"));
			}
			if(p.containsKey("message2")){
				edt_message2.setText(p.getProperty("message2"));
				edt_message2_prev.setText(p.getProperty("message2"));
			}
			if(p.containsKey("message3")){
				edt_message3.setText(p.getProperty("message3"));
				edt_message3_prev.setText(p.getProperty("message3"));
			}
			//parts
			if(p.containsKey("parts")){
				edt_parts.setText(p.getProperty("parts"));
				edt_parts_prev.setText(p.getProperty("parts"));
			}
			if(p.containsKey("parts2")){
				edt_parts2.setText(p.getProperty("parts2"));
				edt_parts2_prev.setText(p.getProperty("parts2"));
			}
			if(p.containsKey("parts3")){
				edt_parts3.setText(p.getProperty("parts3"));
				edt_parts3_prev.setText(p.getProperty("parts3"));
			}
			//services
			if(p.containsKey("services")){
				edt_services.setText(p.getProperty("services"));
				edt_services_prev.setText(p.getProperty("services"));
			}
			if(p.containsKey("services2")){
				edt_services2.setText(p.getProperty("services2"));
				edt_services2_prev.setText(p.getProperty("services2"));
			}
			if(p.containsKey("services3")){
				edt_services3.setText(p.getProperty("services3"));
				edt_services3_prev.setText(p.getProperty("services3"));
			}
			
			if(p.containsKey("user")){
				txt_lastedit.setText(p.getProperty("user"));
			}
			if(p.containsKey("lastedit")){
				Date d = this.unFormatDate(p.getProperty("lastedit"));
				spn_lastedit.setValue(d);
			}
			
			if(p.containsKey("model")){
				txt_model.setText(p.getProperty("model"));
				txt_model_prev.setText(p.getProperty("model"));
			}
			System.out.println("Loaded case: "+p.getProperty("casenumber"));
		}
		else{
			System.out.println("Case has no C#");
		}
	}

	private void but_NewActionPerformed(ActionEvent e) {
		int r = JOptionPane.showConfirmDialog(null, "Press OK to reset this form.\nAll information in the 'New' column will be lost!\nPress Cancel to abort.", "NEW: Are you Sure?", JOptionPane.OK_CANCEL_OPTION);
		if(r==0){
			//okay
			this.clearAllFields();	
		}else if(r==2){
			//cancel
			//do nothing I guess.
			System.out.println("New canceled.");
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Karl Stamm
		ResourceBundle bundle = ResourceBundle.getBundle("clientStrings");
		EntryForm = new JPanel();
		panel3 = new JPanel();
		panel_header = new JPanel();
		label7 = new JLabel();
		label8 = new JLabel();
		lbl_casenumber = new JLabel();
		txt_casenumber = new JTextField();
		txt_casenumber_prev = new JTextField();
		
		lbl_casestatus = new JLabel();
		txt_casestatus = new JTextField();
		txt_casestatus_prev = new JTextField();
		
		scrollPane_main = new JScrollPane();
		panel_datafields = new JPanel();
		label9 = new JLabel();
		txt_customer = new JTextField();
		txt_customer_prev = new JTextField();
		label2 = new JLabel();
		txt_request1 = new JTextField();
		txt_request1_prev = new JTextField();
		label5 = new JLabel();
		txt_sendbox1 = new JTextField();
		txt_sendbox1_prev = new JTextField();
		label3 = new JLabel();
		txt_sendboxtrack = new JTextField();
		txt_sendboxtrack_prev = new JTextField();
		label4 = new JLabel();
		txt_returnlabel = new JTextField();
		txt_returnlabel_prev = new JTextField();
		lbl_arrival1 = new JLabel();
		txt_arrival1 = new JTextField();
		txt_arrival1_prev = new JTextField();
		lbl_model = new JLabel();
		txt_model = new JTextField();
		txt_model_prev = new JTextField();
		label16 = new JLabel();
		scrollPane4 = new JScrollPane();
		edt_accessory = new JEditorPane();
		scrollPane7 = new JScrollPane();
		edt_accessory_prev = new JEditorPane();
		label14 = new JLabel();
		txt_desc1 = new JTextField();
		txt_desc1_prev = new JTextField();
		label17 = new JLabel();
		scrollPane8 = new JScrollPane();
		edt_technote1 = new JEditorPane();
		scrollPane9 = new JScrollPane();
		edt_technote1_prev = new JEditorPane();
		label19 = new JLabel();
		scrollPane10 = new JScrollPane();
		edt_diags = new JEditorPane();
		scrollPane11 = new JScrollPane();
		edt_diags_prev = new JEditorPane();
		label20 = new JLabel();
		scrollPane12 = new JScrollPane();
		edt_services = new JEditorPane();
		scrollPane13 = new JScrollPane();
		edt_services_prev = new JEditorPane();
		label21 = new JLabel();
		scrollPane15 = new JScrollPane();
		edt_parts = new JEditorPane();
		scrollPane14 = new JScrollPane();
		edt_parts_prev = new JEditorPane();
		label18 = new JLabel();
		txt_tech = new JTextField();
		txt_tech_prev = new JTextField();
		label22 = new JLabel();
		scrollPane16 = new JScrollPane();
		edt_message_prev = new JEditorPane();
		scrollPane17 = new JScrollPane();
		edt_message = new JEditorPane();
		label23 = new JLabel();
		txt_shipdate1 = new JTextField();
		txt_shipdate1_prev = new JTextField();
		label24 = new JLabel();
		txt_ship = new JTextField();
		txt_ship_prev = new JTextField();
		lbl_price1 = new JLabel();
		txt_price1 = new JTextField();
		txt_price1_prev = new JTextField();
		lbl_SecondArrival = new JLabel();
		label26 = new JLabel();
		label27 = new JLabel();
		scrollPane18 = new JScrollPane();
		edt_accessory2_prev = new JEditorPane();
		txt_desc2_prev = new JTextField();
		txt_returnlabel2 = new JTextField();
		lbl_accessory2 = new JLabel();
		lbl_accessory2p = new JLabel();
		scrollPane19 = new JScrollPane();
		edt_accessory2 = new JEditorPane();
		scrollPane20 = new JScrollPane();
		edt_parts2_prev = new JEditorPane();
		txt_tech2 = new JTextField();
		scrollPane21 = new JScrollPane();
		edt_services2_prev = new JEditorPane();
		label30 = new JLabel();
		scrollPane22 = new JScrollPane();
		edt_diags2 = new JEditorPane();
		label31 = new JLabel();
		txt_sendboxtrack2 = new JTextField();
		txt_returnlabel2_prev = new JTextField();
		label32 = new JLabel();
		lbl_arrival2 = new JLabel();
		scrollPane23 = new JScrollPane();
		edt_parts2 = new JEditorPane();
		txt_desc2 = new JTextField();
		label34 = new JLabel();
		scrollPane24 = new JScrollPane();
		edt_message2 = new JEditorPane();
		txt_sendboxtrack2_prev = new JTextField();
		scrollPane25 = new JScrollPane();
		edt_technote2_prev = new JEditorPane();
		scrollPane26 = new JScrollPane();
		edt_message2_prev = new JEditorPane();
		label35 = new JLabel();
		txt_request2 = new JTextField();
		txt_request2_prev = new JTextField();
		txt_sendbox2 = new JTextField();
		txt_sendbox2_prev = new JTextField();
		txt_ship2_prev = new JTextField();
		label36 = new JLabel();
		label37 = new JLabel();
		txt_arrival2 = new JTextField();
		txt_arrival2_prev = new JTextField();
		scrollPane27 = new JScrollPane();
		edt_technote2 = new JEditorPane();
		label38 = new JLabel();
		txt_tech2_prev = new JTextField();
		label39 = new JLabel();
		scrollPane28 = new JScrollPane();
		edt_services2 = new JEditorPane();
		scrollPane29 = new JScrollPane();
		edt_diags2_prev = new JEditorPane();
		txt_shipdate2 = new JTextField();
		txt_shipdate2_prev = new JTextField();
		txt_ship2 = new JTextField();
		label40 = new JLabel();
		lbl_price2 = new JLabel();
		txt_price2 = new JTextField();
		txt_price2_prev = new JTextField();
		lbl_ThirdArrival = new JLabel();
		label42 = new JLabel();
		txt_request3 = new JTextField();
		txt_request3_prev = new JTextField();
		label43 = new JLabel();
		txt_sendbox3 = new JTextField();
		txt_sendbox3_prev = new JTextField();
		label44 = new JLabel();
		txt_sendboxtrack3 = new JTextField();
		txt_sendboxtrack3_prev = new JTextField();
		label45 = new JLabel();
		txt_returnlabel3 = new JTextField();
		txt_returnlabel3_prev = new JTextField();
		label46 = new JLabel();
		txt_arrival3 = new JTextField();
		txt_arrival3_prev = new JTextField();
		label47 = new JLabel();
		scrollPane30 = new JScrollPane();
		edt_accessory3_prev = new JEditorPane();
		txt_desc3_prev = new JTextField();
		label48 = new JLabel();
		scrollPane31 = new JScrollPane();
		edt_accessory3 = new JEditorPane();
		label49 = new JLabel();
		lbl_desc3 = new JLabel();
		txt_desc3 = new JTextField();
		label51 = new JLabel();
		label52 = new JLabel();
		label53 = new JLabel();
		label54 = new JLabel();
		txt_tech3_prev = new JTextField();
		txt_tech3 = new JTextField();
		label55 = new JLabel();
		scrollPane32 = new JScrollPane();
		edt_parts3_prev = new JEditorPane();
		scrollPane33 = new JScrollPane();
		edt_services3_prev = new JEditorPane();
		scrollPane34 = new JScrollPane();
		edt_diags3 = new JEditorPane();
		scrollPane35 = new JScrollPane();
		edt_parts3 = new JEditorPane();
		scrollPane36 = new JScrollPane();
		edt_message3 = new JEditorPane();
		scrollPane37 = new JScrollPane();
		edt_technote3_prev = new JEditorPane();
		scrollPane38 = new JScrollPane();
		edt_message3_prev = new JEditorPane();
		scrollPane39 = new JScrollPane();
		edt_technote3 = new JEditorPane();
		scrollPane40 = new JScrollPane();
		edt_services3 = new JEditorPane();
		scrollPane41 = new JScrollPane();
		edt_diags3_prev = new JEditorPane();
		txt_shipdate3 = new JTextField();
		txt_shipdate3_prev = new JTextField();
		lbl_ship3 = new JLabel();
		txt_ship3 = new JTextField();
		txt_ship3_prev = new JTextField();
		lbl_price3 = new JLabel();
		txt_price3 = new JTextField();
		txt_price3_prev = new JTextField();
		panel_metadata = new JPanel();
		lbl_User = new JLabel();
		txt_User = new JTextField();
		lbl_lastedit = new JLabel();
		spn_lastedit = new JSpinner();
		lbl_lasteditor = new JLabel();
		txt_lastedit = new JTextField();
		panel_buttons = new JPanel();
		but_Save = new JButton();
		but_Load = new JButton();
		but_Clear = new JButton();
		but_Search = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== EntryForm ========
		{
			// try to make the data entry field very tall:
			Toolkit tk = Toolkit.getDefaultToolkit ();
			int scrHeight = (int) tk.getScreenSize ().getHeight ();
			int WindowHeight = (int) ((0.80*scrHeight)-64);	
			if(WindowHeight < 500)
				WindowHeight = 500;
			EntryForm.setPreferredSize(new Dimension(580, WindowHeight));
			EntryForm.setMinimumSize(new Dimension(500, 500));

			////////////////
			
			EntryForm.setLayout(new FormLayout(
				ColumnSpec.decodeSpecs("290dlu:grow"),
				new RowSpec[] {
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(RowSpec.CENTER, Sizes.dluY(135), FormSpec.DEFAULT_GROW),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.dluY(19)),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.dluY(21))
				}));

			//======== panel3 ========
			{
				panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));

				//======== panel_header ========
				{
					panel_header.setLayout(new FormLayout(
						new ColumnSpec[] {
							new ColumnSpec(Sizes.dluX(80)),
							ColumnSpec.createGap(Sizes.pixel(9)),
							new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(30), FormSpec.DEFAULT_GROW),
							ColumnSpec.createGap(Sizes.pixel(9)),
							new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(30), FormSpec.DEFAULT_GROW),
							ColumnSpec.createGap(Sizes.pixel(9)),
							new ColumnSpec(Sizes.DLUX8)
						},
						RowSpec.decodeSpecs("default, default, default, default")));

					//---- label7 ----
					label7.setText(bundle.getString("EntryForm.label7.text"));
					label7.setAlignmentX(0.5F);
					label7.setHorizontalAlignment(SwingConstants.CENTER);
					panel_header.add(label7, cc.xy(3, 1));

					//---- label8 ----
					label8.setText(bundle.getString("EntryForm.label8.text"));
					label8.setHorizontalAlignment(SwingConstants.CENTER);
					panel_header.add(label8, cc.xy(5, 1));

					//---- label1 ----
					lbl_casenumber.setText(bundle.getString("EntryForm.label1.text"));
					lbl_casenumber.setLabelFor(txt_casenumber);
					lbl_casenumber.setAlignmentX(0.5F);
					lbl_casenumber.setHorizontalAlignment(SwingConstants.RIGHT);
					panel_header.add(lbl_casenumber, cc.xy(1, 2));
					panel_header.add(txt_casenumber, cc.xy(3, 2));

					//---- txt_casenumber_prev ----
					txt_casenumber_prev.setEditable(false);
					txt_casenumber_prev.setEnabled(false);
					panel_header.add(txt_casenumber_prev, cc.xy(5, 2));
				}
				panel3.add(panel_header);

				panel_datafields.setLayout(new FormLayout(
				new ColumnSpec[] {
					new ColumnSpec(Sizes.dluX(79)),
					ColumnSpec.createGap(Sizes.pixel(9)),
					new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(30), FormSpec.DEFAULT_GROW),
					ColumnSpec.createGap(Sizes.pixel(9)),
					new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(30), FormSpec.DEFAULT_GROW)
				},
				new RowSpec[] {
					new RowSpec(Sizes.PREFERRED),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
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
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
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
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.DEFAULT),
					new RowSpec(Sizes.DLUY1),
					new RowSpec(Sizes.dluY(20)),
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
					new RowSpec(Sizes.dluY(22)),
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
					new RowSpec(Sizes.DEFAULT)
				}));
						
						
				//---- label9 ----
				label9.setText(bundle.getString("EntryForm.label9.text"));
				label9.setLabelFor(txt_customer);
				label9.setAlignmentX(0.5F);
				label9.setHorizontalAlignment(SwingConstants.RIGHT);
				panel_header.add(label9, cc.xy(1, 3));
				panel_header.add(txt_customer, cc.xy(3, 3));

				//---- txt_customer_prev ----
				txt_customer_prev.setEditable(false);
				txt_customer_prev.setEnabled(false);
				panel_header.add(txt_customer_prev, cc.xy(5, 3));

				//---- lbl_casestatus ----
				lbl_casestatus.setText(bundle.getString("EntryForm.lbl_casestatus.text"));
				lbl_casestatus.setLabelFor(txt_casestatus);
				lbl_casestatus.setAlignmentX(0.5F);
				lbl_casestatus.setHorizontalAlignment(SwingConstants.RIGHT);
				panel_header.add(lbl_casestatus, cc.xy(1, 4));
				panel_header.add(txt_casestatus, cc.xy(3, 4));
				txt_casestatus_prev.setEditable(false);
				txt_casestatus_prev.setEnabled(false);
				panel_header.add(txt_casestatus_prev, cc.xy(5, 4));
			
						if(this.HAS_FIRST_PANEL)
							initFirstPanel(bundle, cc);
						if(this.HAS_SECOND_PANEL)
							initSecondPanel(bundle, cc);
						
						if(this.HAS_THIRD_PANEL)
							initThirdPanel(bundle, cc);// end if panel3
					
					scrollPane_main.setViewportView(panel_datafields);
					scrollPane_main.getVerticalScrollBar().setBlockIncrement(50);
					scrollPane_main.getVerticalScrollBar().setUnitIncrement(15);
				}
				panel3.add(scrollPane_main);
			
			EntryForm.add(panel3, cc.xy(1, 3));

			//======== panel_metadata ========
			{
				panel_metadata.setLayout(new FlowLayout());

				//---- lbl_User ----
				lbl_User.setText(bundle.getString("EntryForm.lbl_User.text"));
				lbl_User.setLabelFor(txt_User);
				lbl_User.setHorizontalAlignment(SwingConstants.RIGHT);
				panel_metadata.add(lbl_User);

				//---- txt_User ----
				txt_User.setMinimumSize(new Dimension(100, 22));
				txt_User.setPreferredSize(new Dimension(100, 22));
				txt_User.setEditable(false);
				panel_metadata.add(txt_User);

				//---- lbl_lastedit ----
				lbl_lastedit.setText(bundle.getString("EntryForm.lbl_lastedit.text"));
				lbl_lastedit.setLabelFor(spn_lastedit);
				lbl_lastedit.setHorizontalAlignment(SwingConstants.RIGHT);
				panel_metadata.add(lbl_lastedit);

				//---- spn_lastedit ----
				spn_lastedit.setModel(new SpinnerDateModel());
				spn_lastedit.setEnabled(false);
				panel_metadata.add(spn_lastedit);

				//---- lbl_lasteditor ----
				lbl_lasteditor.setText(bundle.getString("EntryForm.lbl_lasteditor.text"));
				lbl_lasteditor.setLabelFor(txt_lastedit);
				lbl_lasteditor.setHorizontalAlignment(SwingConstants.RIGHT);
				panel_metadata.add(lbl_lasteditor);

				//---- txt_lastedit ----
				txt_lastedit.setPreferredSize(new Dimension(100, 22));
				txt_lastedit.setMinimumSize(new Dimension(100, 22));
				txt_lastedit.setEditable(false);
				panel_metadata.add(txt_lastedit);
			}
			EntryForm.add(panel_metadata, cc.xy(1, 7));

			//======== panel_buttons ========
			{
				panel_buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 2));

				//---- but_Save ----
				but_Save.setText(bundle.getString("EntryForm.but_Save.text"));
				but_Save.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						but_SaveActionPerformed(e);
					}
				});
				panel_buttons.add(but_Save);

				//---- but_Load ----
				but_Load.setText(bundle.getString("EntryForm.but_Load.text"));
				but_Load.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						but_LoadActionPerformed(e);
					}
				});
				panel_buttons.add(but_Load);

				//---- but_Clear ----
				but_Clear.setText(bundle.getString("EntryForm.but_Clear.text"));
				but_Clear.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						but_NewActionPerformed(e);
					}
				});
				panel_buttons.add(but_Clear);
				
				//---- but_Search ----
				but_Search.setText(bundle.getString("EntryForm.but_Search.text"));
				but_Search.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						but_SearchActionPerformed(e);
					}
				});
				panel_buttons.add(but_Search);
				
			}
			EntryForm.add(panel_buttons, cc.xy(1, 5));
		}
		
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
		
		txt_casestatus.addMouseListener(new PopClickListener("casestatus", txt_casestatus));

		this.txt_model.addMouseListener(new PopClickListener("modelnumber", this.txt_model));
		
		edt_accessory.addMouseListener(new PopClickListener("accessory", edt_accessory));
		edt_accessory2.addMouseListener(new PopClickListener("accessory",edt_accessory2));
		edt_accessory3.addMouseListener(new PopClickListener("accessory",edt_accessory3));
		
		edt_technote1.addMouseListener(new PopClickListener("technote", edt_technote1));
		edt_technote2.addMouseListener(new PopClickListener("technote", edt_technote2));
		edt_technote3.addMouseListener(new PopClickListener("technote", edt_technote3));
		
		txt_desc1.addMouseListener(new PopClickListener("desc", txt_desc1));
		txt_desc2.addMouseListener(new PopClickListener("desc", txt_desc2));
		txt_desc3.addMouseListener(new PopClickListener("desc", txt_desc3));
		
		edt_diags.addMouseListener(new PopClickListener("diags", edt_diags));
		edt_diags2.addMouseListener(new PopClickListener("diags", edt_diags2));
		edt_diags3.addMouseListener(new PopClickListener("diags", edt_diags3));
						
		edt_message.addMouseListener(new PopClickListener("message", edt_message));
		edt_message2.addMouseListener(new PopClickListener("message", edt_message2));
		edt_message3.addMouseListener(new PopClickListener("message", edt_message3));
		
		edt_parts.addMouseListener(new PopClickListener("parts", edt_parts));
		edt_parts2.addMouseListener(new PopClickListener("parts", edt_parts2));
		edt_parts3.addMouseListener(new PopClickListener("parts", edt_parts3));
				
		edt_services.addMouseListener(new PopClickListener("services", edt_services));
		edt_services2.addMouseListener(new PopClickListener("services", edt_services2));
		edt_services3.addMouseListener(new PopClickListener("services", edt_services3));
		
		txt_sendboxtrack.addMouseListener(new PopClickListener("tracker", txt_sendboxtrack));
		txt_sendboxtrack2.addMouseListener(new PopClickListener("tracker", txt_sendboxtrack2));
		txt_sendboxtrack3.addMouseListener(new PopClickListener("tracker", txt_sendboxtrack3));
		txt_returnlabel.addMouseListener(new PopClickListener("tracker", txt_returnlabel));
		txt_ship.addMouseListener(new PopClickListener("tracker", txt_ship));
		
		txt_returnlabel2.addMouseListener(new PopClickListener("tracker", txt_returnlabel2));
		txt_ship2.addMouseListener(new PopClickListener("tracker", txt_ship2));
		txt_returnlabel3.addMouseListener(new PopClickListener("tracker", txt_returnlabel3));
		txt_ship3.addMouseListener(new PopClickListener("tracker", txt_ship3));
		
	}

	private void initFirstPanel(ResourceBundle bundle, CellConstraints cc) {

		label2.setText(bundle.getString("EntryForm.label2.text"));
		label2.setAlignmentX(0.5F);
		label2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label2, cc.xy(1, 7));
		panel_datafields.add(txt_request1, cc.xy(3, 7));

		//---- txt_request1_prev ----
		txt_request1_prev.setEditable(false);
		txt_request1_prev.setEnabled(false);
		panel_datafields.add(txt_request1_prev, cc.xy(5, 7));

		//---- label5 ----
		label5.setText(bundle.getString("EntryForm.label5.text"));
		label5.setAlignmentX(0.5F);
		label5.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label5, cc.xy(1, 9));
		panel_datafields.add(txt_sendbox1, cc.xy(3, 9));

		//---- txt_sendbox1_prev ----
		txt_sendbox1_prev.setEditable(false);
		txt_sendbox1_prev.setEnabled(false);
		panel_datafields.add(txt_sendbox1_prev, cc.xy(5, 9));

		//---- label3 ----
		label3.setText(bundle.getString("EntryForm.label3.text"));
		label3.setLabelFor(txt_sendboxtrack);
		label3.setAlignmentX(0.5F);
		label3.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label3, cc.xy(1, 11));
		panel_datafields.add(txt_sendboxtrack, cc.xy(3, 11));

		//---- txt_sendboxtrack_prev ----
		txt_sendboxtrack_prev.setEditable(false);
		txt_sendboxtrack_prev.setEnabled(false);
		panel_datafields.add(txt_sendboxtrack_prev, cc.xy(5, 11));

		//---- label4 ----
		label4.setText(bundle.getString("EntryForm.label4.text"));
		label4.setLabelFor(txt_returnlabel);
		label4.setAlignmentX(0.5F);
		label4.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label4, cc.xy(1, 13));
		panel_datafields.add(txt_returnlabel, cc.xy(3, 13));

		//---- txt_returnlabel_prev ----
		txt_returnlabel_prev.setEditable(false);
		txt_returnlabel_prev.setEnabled(false);
		panel_datafields.add(txt_returnlabel_prev, cc.xy(5, 13));

		//---- lbl_arrival1 ----
		lbl_arrival1.setText(bundle.getString("EntryForm.lbl_arrival1.text"));
		lbl_arrival1.setAlignmentX(0.5F);
		lbl_arrival1.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(lbl_arrival1, cc.xy(1, 15));
		panel_datafields.add(txt_arrival1, cc.xy(3, 15));

		//---- txt_arrival1_prev ----
		txt_arrival1_prev.setEditable(false);
		txt_arrival1_prev.setEnabled(false);
		panel_datafields.add(txt_arrival1_prev, cc.xy(5, 15));

		//---- lbl_model ----
		lbl_model.setText(bundle.getString("EntryForm.lbl_model.text"));
		lbl_model.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(lbl_model, cc.xy(1, 17));
		panel_datafields.add(txt_model, cc.xy(3, 17));

		//---- txt_model_prev ----
		txt_model_prev.setEnabled(false);
		txt_model_prev.setEditable(false);
		panel_datafields.add(txt_model_prev, cc.xy(5, 17));

		//---- label16 ----
		label16.setText(bundle.getString("EntryForm.label16.text"));
		label16.setAlignmentX(0.5F);
		label16.setHorizontalAlignment(SwingConstants.RIGHT);
		label16.setLabelFor(edt_accessory);
		panel_datafields.add(label16, cc.xy(1, 19));

		//======== scrollPane4 ========
		{
			scrollPane4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_accessory ----
			edt_accessory.setPreferredSize(new Dimension(144, 48));
			edt_accessory.setMinimumSize(new Dimension(144, 48));
			scrollPane4.setViewportView(edt_accessory);
		}
		panel_datafields.add(scrollPane4, cc.xy(3, 19));

		//======== scrollPane7 ========
		{
			scrollPane7.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane7.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_accessory_prev ----
			edt_accessory_prev.setPreferredSize(new Dimension(144, 48));
			edt_accessory_prev.setMinimumSize(new Dimension(144, 48));
			edt_accessory_prev.setEditable(false);
			edt_accessory_prev.setEnabled(false);
			scrollPane7.setViewportView(edt_accessory_prev);
		}
		panel_datafields.add(scrollPane7, cc.xy(5, 19));

		//---- label14 ----
		label14.setText(bundle.getString("EntryForm.label14.text"));
		label14.setLabelFor(txt_desc1);
		label14.setAlignmentX(0.5F);
		label14.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label14, cc.xy(1, 21));
		panel_datafields.add(txt_desc1, cc.xy(3, 21));

		txt_desc1_prev.setEditable(false);
		txt_desc1_prev.setEnabled(false);
		panel_datafields.add(txt_desc1_prev, cc.xy(5, 21));

		//---- label17 ----
		label17.setText(bundle.getString("EntryForm.label17.text"));
		label17.setAlignmentX(0.5F);
		label17.setHorizontalAlignment(SwingConstants.RIGHT);
		label17.setLabelFor(edt_accessory_prev);
		panel_datafields.add(label17, cc.xy(1, 23));

		//======== scrollPane8 ========
		{
			scrollPane8.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane8.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			edt_technote1.setPreferredSize(new Dimension(144, 64));
			edt_technote1.setMinimumSize(new Dimension(144, 48));
			scrollPane8.setViewportView(edt_technote1);
		}
		panel_datafields.add(scrollPane8, cc.xy(3, 23));

		//======== scrollPane9 ========
		{
			scrollPane9.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane9.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			edt_technote1_prev.setPreferredSize(new Dimension(144, 64));
			edt_technote1_prev.setMinimumSize(new Dimension(144, 48));
			edt_technote1_prev.setEditable(false);
			edt_technote1_prev.setEnabled(false);
			scrollPane9.setViewportView(edt_technote1_prev);
		}
		panel_datafields.add(scrollPane9, cc.xy(5, 23));

		//---- label19 ----
		label19.setText(bundle.getString("EntryForm.label19.text"));
		label19.setAlignmentX(0.5F);
		label19.setHorizontalAlignment(SwingConstants.RIGHT);
		label19.setLabelFor(edt_accessory_prev);
		panel_datafields.add(label19, cc.xy(1, 25));

		//======== scrollPane10 ========
		{
			scrollPane10.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane10.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_diags ----
			edt_diags.setPreferredSize(new Dimension(144, 64));
			edt_diags.setMinimumSize(new Dimension(144, 48));
			scrollPane10.setViewportView(edt_diags);

		}
		panel_datafields.add(scrollPane10, cc.xy(3, 25));

		//======== scrollPane11 ========
		{
			scrollPane11.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane11.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_diags_prev ----
			edt_diags_prev.setPreferredSize(new Dimension(144, 64));
			edt_diags_prev.setMinimumSize(new Dimension(144, 48));
			edt_diags_prev.setEditable(false);
			edt_diags_prev.setEnabled(false);
			scrollPane11.setViewportView(edt_diags_prev);
			
		}
		panel_datafields.add(scrollPane11, cc.xy(5, 25));

		//---- label20 ----
		label20.setText(bundle.getString("EntryForm.label20.text"));
		label20.setAlignmentX(0.5F);
		label20.setHorizontalAlignment(SwingConstants.RIGHT);
		label20.setLabelFor(edt_accessory_prev);
		panel_datafields.add(label20, cc.xy(1, 27));

		//======== scrollPane12 ========
		{
			scrollPane12.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane12.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_services ----
			edt_services.setPreferredSize(new Dimension(144, 64));
			edt_services.setMinimumSize(new Dimension(144, 48));
			scrollPane12.setViewportView(edt_services);

		}
		panel_datafields.add(scrollPane12, cc.xy(3, 27));

		//======== scrollPane13 ========
		{
			scrollPane13.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane13.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_services_prev ----
			edt_services_prev.setPreferredSize(new Dimension(144, 64));
			edt_services_prev.setMinimumSize(new Dimension(144, 48));
			edt_services_prev.setEditable(false);
			edt_services_prev.setEnabled(false);
			scrollPane13.setViewportView(edt_services_prev);
		}
		panel_datafields.add(scrollPane13, cc.xy(5, 27));

		//---- label21 ----
		label21.setText(bundle.getString("EntryForm.label21.text"));
		label21.setAlignmentX(0.5F);
		label21.setHorizontalAlignment(SwingConstants.RIGHT);
		label21.setLabelFor(edt_parts);
		panel_datafields.add(label21, cc.xy(1, 29));

		//======== scrollPane15 ========
		{
			scrollPane15.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane15.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_parts ----
			edt_parts.setPreferredSize(new Dimension(144, 48));
			edt_parts.setMinimumSize(new Dimension(144, 48));
			scrollPane15.setViewportView(edt_parts);
		}
		panel_datafields.add(scrollPane15, cc.xy(3, 29));

		//======== scrollPane14 ========
		{
			scrollPane14.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane14.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_parts_prev ----
			edt_parts_prev.setPreferredSize(new Dimension(144, 48));
			edt_parts_prev.setMinimumSize(new Dimension(144, 48));
			edt_parts_prev.setEditable(false);
			edt_parts_prev.setEnabled(false);
			scrollPane14.setViewportView(edt_parts_prev);
		}
		panel_datafields.add(scrollPane14, cc.xy(5, 29));

		label18.setText(bundle.getString("EntryForm.label18.text"));
		label18.setLabelFor(txt_tech);
		label18.setAlignmentX(0.5F);
		label18.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label18, cc.xy(1, 31));
		panel_datafields.add(txt_tech, cc.xy(3, 31));

		txt_tech_prev.setEditable(false);
		txt_tech_prev.setEnabled(false);
		panel_datafields.add(txt_tech_prev, cc.xy(5, 31));

		label22.setText(bundle.getString("EntryForm.label22.text"));
		label22.setAlignmentX(0.5F);
		label22.setHorizontalAlignment(SwingConstants.RIGHT);
		label22.setLabelFor(edt_accessory_prev);
		panel_datafields.add(label22, cc.xy(1, 33));

		scrollPane16.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane16.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		edt_message_prev.setPreferredSize(new Dimension(144, 64));
		edt_message_prev.setMinimumSize(new Dimension(144, 48));
		edt_message_prev.setEditable(false);
		edt_message_prev.setEnabled(false);
		scrollPane16.setViewportView(edt_message_prev);
		panel_datafields.add(scrollPane16, cc.xy(5, 33));


		scrollPane17.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane17.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		edt_message.setPreferredSize(new Dimension(144, 64));
		edt_message.setMinimumSize(new Dimension(144, 48));
		scrollPane17.setViewportView(edt_message);
		panel_datafields.add(scrollPane17, cc.xy(3, 33));

		//---- label23 ----
		label23.setText(bundle.getString("EntryForm.label23.text"));
		label23.setAlignmentX(0.5F);
		label23.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label23, cc.xy(1, 35));
		panel_datafields.add(txt_shipdate1, cc.xy(3, 35));

		//---- txt_shipdate1_prev ----
		txt_shipdate1_prev.setEditable(false);
		txt_shipdate1_prev.setEnabled(false);
		panel_datafields.add(txt_shipdate1_prev, cc.xy(5, 35));

		//---- label24 ----
		label24.setText(bundle.getString("EntryForm.label24.text"));
		label24.setLabelFor(txt_ship);
		label24.setAlignmentX(0.5F);
		label24.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label24, cc.xy(1, 37));
		panel_datafields.add(txt_ship, cc.xy(3, 37));

		//---- txt_ship_prev ----
		txt_ship_prev.setEditable(false);
		txt_ship_prev.setEnabled(false);
		panel_datafields.add(txt_ship_prev, cc.xy(5, 37));

		//---- lbl_price1 ----
		lbl_price1.setText(bundle.getString("EntryForm.lbl_price1.text"));
		lbl_price1.setLabelFor(txt_price1);
		lbl_price1.setAlignmentX(0.5F);
		lbl_price1.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(lbl_price1, cc.xy(1, 39));
		panel_datafields.add(txt_price1, cc.xy(3, 39));

		//---- txt_price1_prev ----
		txt_price1_prev.setEditable(false);
		txt_price1_prev.setEnabled(false);
		panel_datafields.add(txt_price1_prev, cc.xy(5, 39));
	}

	private void initSecondPanel(ResourceBundle bundle, CellConstraints cc) {

		lbl_SecondArrival.setText(bundle.getString("EntryForm.lbl_SecondArrival.text"));
		lbl_SecondArrival.setHorizontalAlignment(SwingConstants.CENTER);
		panel_datafields.add(lbl_SecondArrival, cc.xywh(1, 41, 5, 1));

		label26.setText(bundle.getString("EntryForm.label26.text"));
		label26.setAlignmentX(0.5F);
		label26.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label26, cc.xy(1, 45));

		//---- label27 ----
		label27.setText(bundle.getString("EntryForm.label27.text"));
		label27.setLabelFor(txt_tech2);
		label27.setAlignmentX(0.5F);
		label27.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label27, cc.xy(1, 65));


		scrollPane18.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane18.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		edt_accessory2_prev.setPreferredSize(new Dimension(144, 48));
		edt_accessory2_prev.setMinimumSize(new Dimension(144, 48));
		edt_accessory2_prev.setEditable(false);
		edt_accessory2_prev.setEnabled(false);
		scrollPane18.setViewportView(edt_accessory2_prev);
		panel_datafields.add(scrollPane18, cc.xy(5, 53));


		txt_desc2_prev.setEditable(false);
		txt_desc2_prev.setEnabled(false);
		panel_datafields.add(txt_desc2_prev, cc.xy(5, 55));
		panel_datafields.add(txt_returnlabel2, cc.xy(3, 49));

		lbl_accessory2.setText(bundle.getString("EntryForm.label28.text"));
		lbl_accessory2.setAlignmentX(0.5F);
		lbl_accessory2.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_accessory2.setLabelFor(edt_accessory2);
		panel_datafields.add(lbl_accessory2, cc.xy(1, 53));

		lbl_accessory2p.setText(bundle.getString("EntryForm.label29.text"));
		lbl_accessory2p.setAlignmentX(0.5F);
		lbl_accessory2p.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_accessory2p.setLabelFor(edt_accessory2_prev);
		panel_datafields.add(lbl_accessory2p, cc.xy(1, 57));

		scrollPane19.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane19.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		edt_accessory2.setPreferredSize(new Dimension(144, 48));
		edt_accessory2.setMinimumSize(new Dimension(144, 48));
		scrollPane19.setViewportView(edt_accessory2);
	
		panel_datafields.add(scrollPane19, cc.xy(3, 53));
		
		//======== scrollPane20 ========
		{
			scrollPane20.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane20.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_parts2_prev ----
			edt_parts2_prev.setPreferredSize(new Dimension(144, 48));
			edt_parts2_prev.setMinimumSize(new Dimension(144, 48));
			edt_parts2_prev.setEditable(false);
			edt_parts2_prev.setEnabled(false);
			scrollPane20.setViewportView(edt_parts2_prev);
		}
		panel_datafields.add(scrollPane20, cc.xy(5, 63));
		
		panel_datafields.add(txt_tech2, cc.xy(3, 65));

		
		scrollPane21.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane21.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		edt_services2_prev.setPreferredSize(new Dimension(144, 64));
		edt_services2_prev.setMinimumSize(new Dimension(144, 48));
		edt_services2_prev.setEditable(false);
		edt_services2_prev.setEnabled(false);
		scrollPane21.setViewportView(edt_services2_prev);
		panel_datafields.add(scrollPane21, cc.xy(5, 61));

		label30.setText(bundle.getString("EntryForm.label30.text"));
		label30.setAlignmentX(0.5F);
		label30.setHorizontalAlignment(SwingConstants.RIGHT);
		label30.setLabelFor(edt_accessory2_prev);
		panel_datafields.add(label30, cc.xy(1, 59));
		
			scrollPane22.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane22.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			edt_diags2.setPreferredSize(new Dimension(144, 64));
			edt_diags2.setMinimumSize(new Dimension(144, 48));
			scrollPane22.setViewportView(edt_diags2);
		panel_datafields.add(scrollPane22, cc.xy(3, 59));

		label31.setText(bundle.getString("EntryForm.label31.text"));
		label31.setAlignmentX(0.5F);
		label31.setHorizontalAlignment(SwingConstants.RIGHT);
		label31.setLabelFor(edt_accessory2_prev);
		panel_datafields.add(label31, cc.xy(1, 67));
		panel_datafields.add(txt_sendboxtrack2, cc.xy(3, 47));

		//---- txt_returnlabel2_prev ----
		txt_returnlabel2_prev.setEditable(false);
		txt_returnlabel2_prev.setEnabled(false);
		panel_datafields.add(txt_returnlabel2_prev, cc.xy(5, 49));

		//---- label32 ----
		label32.setText(bundle.getString("EntryForm.label32.text"));
		label32.setLabelFor(txt_returnlabel2);
		label32.setAlignmentX(0.5F);
		label32.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label32, cc.xy(1, 49));

		//---- lbl_arrival2 ----
		lbl_arrival2.setText(bundle.getString("EntryForm.lbl_arrival2.text"));
		lbl_arrival2.setAlignmentX(0.5F);
		lbl_arrival2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(lbl_arrival2, cc.xy(1, 51));

		//======== scrollPane23 ========
		{
			scrollPane23.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane23.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_parts2 ----
			edt_parts2.setPreferredSize(new Dimension(144, 48));
			edt_parts2.setMinimumSize(new Dimension(144, 48));
			scrollPane23.setViewportView(edt_parts2);
		}
		panel_datafields.add(scrollPane23, cc.xy(3, 63));
		panel_datafields.add(txt_desc2, cc.xy(3, 55));

		//---- label34 ----
		label34.setText(bundle.getString("EntryForm.label34.text"));
		label34.setAlignmentX(0.5F);
		label34.setHorizontalAlignment(SwingConstants.RIGHT);
		label34.setLabelFor(edt_parts2);
		panel_datafields.add(label34, cc.xy(1, 63));

		//======== scrollPane24 ========
		{
			scrollPane24.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane24.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_message2 ----
			edt_message2.setPreferredSize(new Dimension(144, 64));
			edt_message2.setMinimumSize(new Dimension(144, 48));
			scrollPane24.setViewportView(edt_message2);
		}
		panel_datafields.add(scrollPane24, cc.xy(3, 67));

		//---- txt_sendboxtrack2_prev ----
		txt_sendboxtrack2_prev.setEditable(false);
		txt_sendboxtrack2_prev.setEnabled(false);
		panel_datafields.add(txt_sendboxtrack2_prev, cc.xy(5, 47));

		//======== scrollPane25 ========
		{
			scrollPane25.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane25.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			edt_technote2_prev.setPreferredSize(new Dimension(144, 64));
			edt_technote2_prev.setMinimumSize(new Dimension(144, 48));
			edt_technote2_prev.setEditable(false);
			edt_technote2_prev.setEnabled(false);
			scrollPane25.setViewportView(edt_technote2_prev);
		}
		panel_datafields.add(scrollPane25, cc.xy(5, 57));

		//======== scrollPane26 ========
		{
			scrollPane26.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane26.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_message2_prev ----
			edt_message2_prev.setPreferredSize(new Dimension(144, 64));
			edt_message2_prev.setMinimumSize(new Dimension(144, 48));
			edt_message2_prev.setEditable(false);
			edt_message2_prev.setEnabled(false);
			scrollPane26.setViewportView(edt_message2_prev);
		}
		panel_datafields.add(scrollPane26, cc.xy(5, 67));

		//---- label35 ----
		label35.setText(bundle.getString("EntryForm.label35.text"));
		label35.setAlignmentX(0.5F);
		label35.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label35, cc.xy(1, 43));
		panel_datafields.add(txt_request2, cc.xy(3, 43));

		//---- txt_request2_prev ----
		txt_request2_prev.setEditable(false);
		txt_request2_prev.setEnabled(false);
		panel_datafields.add(txt_request2_prev, cc.xy(5, 43));
		panel_datafields.add(txt_sendbox2, cc.xy(3, 45));

		//---- txt_sendbox2_prev ----
		txt_sendbox2_prev.setEnabled(false);
		txt_sendbox2_prev.setEditable(false);
		panel_datafields.add(txt_sendbox2_prev, cc.xy(5, 45));

		//---- txt_ship2_prev ----
		txt_ship2_prev.setEditable(false);
		txt_ship2_prev.setEnabled(false);
		panel_datafields.add(txt_ship2_prev, cc.xy(5, 71));

		//---- label36 ----
		label36.setText(bundle.getString("EntryForm.label36.text"));
		label36.setAlignmentX(0.5F);
		label36.setHorizontalAlignment(SwingConstants.RIGHT);
		label36.setLabelFor(edt_accessory2_prev);
		panel_datafields.add(label36, cc.xy(1, 61));

		//---- label37 ----
		label37.setText(bundle.getString("EntryForm.label37.text"));
		label37.setLabelFor(txt_sendboxtrack2);
		label37.setAlignmentX(0.5F);
		label37.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label37, cc.xy(1, 47));
		panel_datafields.add(txt_arrival2, cc.xy(3, 51));

		//---- txt_arrival2_prev ----
		txt_arrival2_prev.setEditable(false);
		txt_arrival2_prev.setEnabled(false);
		panel_datafields.add(txt_arrival2_prev, cc.xy(5, 51));

		//======== scrollPane27 ========
		{
			scrollPane27.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane27.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			edt_technote2.setPreferredSize(new Dimension(144, 64));
			edt_technote2.setMinimumSize(new Dimension(144, 48));
			scrollPane27.setViewportView(edt_technote2);
		}
		panel_datafields.add(scrollPane27, cc.xy(3, 57));

		//---- label38 ----
		label38.setText(bundle.getString("EntryForm.label38.text"));
		label38.setAlignmentX(0.5F);
		label38.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label38, cc.xy(1, 69));

		//---- txt_tech2_prev ----
		txt_tech2_prev.setEditable(false);
		txt_tech2_prev.setEnabled(false);
		panel_datafields.add(txt_tech2_prev, cc.xy(5, 65));

		//---- label39 ----
		label39.setText(bundle.getString("EntryForm.label39.text"));
		label39.setLabelFor(txt_desc2);
		label39.setAlignmentX(0.5F);
		label39.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label39, cc.xy(1, 55));

		//======== scrollPane28 ========
		{
			scrollPane28.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane28.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_services2 ----
			edt_services2.setPreferredSize(new Dimension(144, 64));
			edt_services2.setMinimumSize(new Dimension(144, 48));
			scrollPane28.setViewportView(edt_services2);
		}
		panel_datafields.add(scrollPane28, cc.xy(3, 61));

		//======== scrollPane29 ========
		{
			scrollPane29.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane29.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_diags2_prev ----
			edt_diags2_prev.setPreferredSize(new Dimension(144, 64));
			edt_diags2_prev.setMinimumSize(new Dimension(144, 48));
			edt_diags2_prev.setEditable(false);
			edt_diags2_prev.setEnabled(false);
			scrollPane29.setViewportView(edt_diags2_prev);
		}
		panel_datafields.add(scrollPane29, cc.xy(5, 59));
		panel_datafields.add(txt_shipdate2, cc.xy(3, 69));

		//---- txt_shipdate2_prev ----
		txt_shipdate2_prev.setEditable(false);
		txt_shipdate2_prev.setEnabled(false);
		panel_datafields.add(txt_shipdate2_prev, cc.xy(5, 69));
		panel_datafields.add(txt_ship2, cc.xy(3, 71));

		//---- label40 ----
		label40.setText(bundle.getString("EntryForm.label40.text"));
		label40.setLabelFor(txt_ship2);
		label40.setAlignmentX(0.5F);
		label40.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label40, cc.xy(1, 71));

		//---- lbl_price2 ----
		lbl_price2.setText(bundle.getString("EntryForm.lbl_price2.text"));
		lbl_price2.setLabelFor(txt_price2);
		lbl_price2.setAlignmentX(0.5F);
		lbl_price2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(lbl_price2, cc.xy(1, 73));
		panel_datafields.add(txt_price2, cc.xy(3, 73));

		//---- txt_price2_prev ----
		txt_price2_prev.setEditable(false);
		txt_price2_prev.setEnabled(false);
		panel_datafields.add(txt_price2_prev, cc.xy(5, 73));
		
	}

	private void initThirdPanel(ResourceBundle bundle, CellConstraints cc) {
		//---- lbl_ThirdArrival ----
		lbl_ThirdArrival.setText(bundle.getString("EntryForm.lbl_ThirdArrival.text"));
		lbl_ThirdArrival.setHorizontalAlignment(SwingConstants.CENTER);
		panel_datafields.add(lbl_ThirdArrival, cc.xywh(1, 75, 5, 1));

		//---- label42 ----
		label42.setText(bundle.getString("EntryForm.label42.text"));
		label42.setAlignmentX(0.5F);
		label42.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label42, cc.xy(1, 77));
		panel_datafields.add(txt_request3, cc.xy(3, 77));

		//---- txt_request3_prev ----
		txt_request3_prev.setEditable(false);
		txt_request3_prev.setEnabled(false);
		panel_datafields.add(txt_request3_prev, cc.xy(5, 77));

		//---- label43 ----
		label43.setText(bundle.getString("EntryForm.label43.text"));
		label43.setAlignmentX(0.5F);
		label43.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label43, cc.xy(1, 79));
		panel_datafields.add(txt_sendbox3, cc.xy(3, 79));

		//---- txt_sendbox3_prev ----
		txt_sendbox3_prev.setEditable(false);
		txt_sendbox3_prev.setEnabled(false);
		panel_datafields.add(txt_sendbox3_prev, cc.xy(5, 79));

		//---- label44 ----
		label44.setText(bundle.getString("EntryForm.label44.text"));
		label44.setLabelFor(txt_sendboxtrack3);
		label44.setAlignmentX(0.5F);
		label44.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label44, cc.xy(1, 81));
		panel_datafields.add(txt_sendboxtrack3, cc.xy(3, 81));

		//---- txt_sendboxtrack3_prev ----
		txt_sendboxtrack3_prev.setEditable(false);
		txt_sendboxtrack3_prev.setEnabled(false);
		panel_datafields.add(txt_sendboxtrack3_prev, cc.xy(5, 81));

		//---- label45 ----
		label45.setText(bundle.getString("EntryForm.label45.text"));
		label45.setLabelFor(txt_returnlabel3);
		label45.setAlignmentX(0.5F);
		label45.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label45, cc.xy(1, 83));
		panel_datafields.add(txt_returnlabel3, cc.xy(3, 83));

		//---- txt_returnlabel3_prev ----
		txt_returnlabel3_prev.setEditable(false);
		txt_returnlabel3_prev.setEnabled(false);
		panel_datafields.add(txt_returnlabel3_prev, cc.xy(5, 83));

		//---- label46 ----
		label46.setText(bundle.getString("EntryForm.label46.text"));
		label46.setAlignmentX(0.5F);
		label46.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label46, cc.xy(1, 85));
		panel_datafields.add(txt_arrival3, cc.xy(3, 85));

		//---- txt_arrival3_prev ----
		txt_arrival3_prev.setEditable(false);
		txt_arrival3_prev.setEnabled(false);
		panel_datafields.add(txt_arrival3_prev, cc.xy(5, 85));

		//---- label47 ----
		label47.setText(bundle.getString("EntryForm.label47.text"));
		label47.setAlignmentX(0.5F);
		label47.setHorizontalAlignment(SwingConstants.RIGHT);
		label47.setLabelFor(edt_accessory3);
		panel_datafields.add(label47, cc.xy(1, 87));

		//======== scrollPane30 ========
		{
			scrollPane30.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane30.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_accessory3_prev ----
			edt_accessory3_prev.setPreferredSize(new Dimension(144, 48));
			edt_accessory3_prev.setMinimumSize(new Dimension(144, 48));
			edt_accessory3_prev.setEditable(false);
			edt_accessory3_prev.setEnabled(false);
			scrollPane30.setViewportView(edt_accessory3_prev);
		}
		panel_datafields.add(scrollPane30, cc.xy(5, 87));

		txt_desc3_prev.setEditable(false);
		txt_desc3_prev.setEnabled(false);
		panel_datafields.add(txt_desc3_prev, cc.xy(5, 89));

		//---- label48 ----
		label48.setText(bundle.getString("EntryForm.label48.text"));
		label48.setAlignmentX(0.5F);
		label48.setHorizontalAlignment(SwingConstants.RIGHT);
		label48.setLabelFor(edt_accessory3_prev);
		panel_datafields.add(label48, cc.xy(1, 91));

		//======== scrollPane31 ========
		{
			scrollPane31.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane31.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_accessory3 ----
			edt_accessory3.setPreferredSize(new Dimension(144, 48));
			edt_accessory3.setMinimumSize(new Dimension(144, 48));
			scrollPane31.setViewportView(edt_accessory3);
		}
		panel_datafields.add(scrollPane31, cc.xy(3, 87));

		//---- label49 ----
		label49.setText(bundle.getString("EntryForm.label49.text"));
		label49.setLabelFor(txt_tech3);
		label49.setAlignmentX(0.5F);
		label49.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label49, cc.xy(1, 99));

		lbl_desc3.setText(bundle.getString("EntryForm.lbl_nature3.text"));
		lbl_desc3.setLabelFor(txt_desc3);
		lbl_desc3.setAlignmentX(0.5F);
		lbl_desc3.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(lbl_desc3, cc.xy(1, 89));
		panel_datafields.add(txt_desc3, cc.xy(3, 89));

		//---- label51 ----
		label51.setText(bundle.getString("EntryForm.label51.text"));
		label51.setAlignmentX(0.5F);
		label51.setHorizontalAlignment(SwingConstants.RIGHT);
		label51.setLabelFor(edt_accessory3_prev);
		panel_datafields.add(label51, cc.xy(1, 93));

		//---- label52 ----
		label52.setText(bundle.getString("EntryForm.label52.text"));
		label52.setAlignmentX(0.5F);
		label52.setHorizontalAlignment(SwingConstants.RIGHT);
		label52.setLabelFor(edt_accessory3_prev);
		panel_datafields.add(label52, cc.xy(1, 95));

		//---- label53 ----
		label53.setText(bundle.getString("EntryForm.label53.text"));
		label53.setAlignmentX(0.5F);
		label53.setHorizontalAlignment(SwingConstants.RIGHT);
		label53.setLabelFor(edt_parts3);
		panel_datafields.add(label53, cc.xy(1, 97));

		//---- label54 ----
		label54.setText(bundle.getString("EntryForm.label54.text"));
		label54.setAlignmentX(0.5F);
		label54.setHorizontalAlignment(SwingConstants.RIGHT);
		label54.setLabelFor(edt_accessory3_prev);
		panel_datafields.add(label54, cc.xy(1, 101));

		//---- txt_tech3_prev ----
		txt_tech3_prev.setEditable(false);
		txt_tech3_prev.setEnabled(false);
		panel_datafields.add(txt_tech3_prev, cc.xy(5, 99));
		panel_datafields.add(txt_tech3, cc.xy(3, 99));

		//---- label55 ----
		label55.setText(bundle.getString("EntryForm.label55.text"));
		label55.setAlignmentX(0.5F);
		label55.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(label55, cc.xy(1, 103));

		//======== scrollPane32 ========
		{
			scrollPane32.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane32.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_parts3_prev ----
			edt_parts3_prev.setPreferredSize(new Dimension(144, 48));
			edt_parts3_prev.setMinimumSize(new Dimension(144, 48));
			edt_parts3_prev.setEditable(false);
			edt_parts3_prev.setEnabled(false);
			scrollPane32.setViewportView(edt_parts3_prev);
		}
		panel_datafields.add(scrollPane32, cc.xy(5, 97));

		//======== scrollPane33 ========
		{
			scrollPane33.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane33.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_services3_prev ----
			edt_services3_prev.setPreferredSize(new Dimension(144, 64));
			edt_services3_prev.setMinimumSize(new Dimension(144, 48));
			edt_services3_prev.setEditable(false);
			edt_services3_prev.setEnabled(false);
			scrollPane33.setViewportView(edt_services3_prev);
		}
		panel_datafields.add(scrollPane33, cc.xy(5, 95));

		//======== scrollPane34 ========
		{
			scrollPane34.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane34.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_diags3 ----
			edt_diags3.setPreferredSize(new Dimension(144, 64));
			edt_diags3.setMinimumSize(new Dimension(144, 48));
			scrollPane34.setViewportView(edt_diags3);
		}
		panel_datafields.add(scrollPane34, cc.xy(3, 93));

		//======== scrollPane35 ========
		{
			scrollPane35.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane35.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_parts3 ----
			edt_parts3.setPreferredSize(new Dimension(144, 48));
			edt_parts3.setMinimumSize(new Dimension(144, 48));
			scrollPane35.setViewportView(edt_parts3);
		}
		panel_datafields.add(scrollPane35, cc.xy(3, 97));

		//======== scrollPane36 ========
		{
			scrollPane36.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane36.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//---- edt_message3 ----
			edt_message3.setPreferredSize(new Dimension(144, 64));
			edt_message3.setMinimumSize(new Dimension(144, 48));
			scrollPane36.setViewportView(edt_message3);
		}
		panel_datafields.add(scrollPane36, cc.xy(3, 101));


		scrollPane37.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane37.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		edt_technote3_prev.setPreferredSize(new Dimension(144, 64));
		edt_technote3_prev.setMinimumSize(new Dimension(144, 48));
		edt_technote3_prev.setEditable(false);
		edt_technote3_prev.setEnabled(false);
		scrollPane37.setViewportView(edt_technote3_prev);
		panel_datafields.add(scrollPane37, cc.xy(5, 91));

		
		scrollPane38.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane38.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		edt_message3_prev.setPreferredSize(new Dimension(144, 64));
		edt_message3_prev.setMinimumSize(new Dimension(144, 48));
		edt_message3_prev.setEditable(false);
		edt_message3_prev.setEnabled(false);
		scrollPane38.setViewportView(edt_message3_prev);
		panel_datafields.add(scrollPane38, cc.xy(5, 101));


		scrollPane39.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane39.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		edt_technote3.setPreferredSize(new Dimension(144, 64));
		edt_technote3.setMinimumSize(new Dimension(144, 48));
		scrollPane39.setViewportView(edt_technote3);
		panel_datafields.add(scrollPane39, cc.xy(3, 91));

		scrollPane40.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane40.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		edt_services3.setPreferredSize(new Dimension(144, 64));
		edt_services3.setMinimumSize(new Dimension(144, 48));
		scrollPane40.setViewportView(edt_services3);
		panel_datafields.add(scrollPane40, cc.xy(3, 95));

		scrollPane41.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane41.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		edt_diags3_prev.setPreferredSize(new Dimension(144, 64));
		edt_diags3_prev.setMinimumSize(new Dimension(144, 48));
		edt_diags3_prev.setEditable(false);
		edt_diags3_prev.setEnabled(false);
		scrollPane41.setViewportView(edt_diags3_prev);
		panel_datafields.add(scrollPane41, cc.xy(5, 93));

		panel_datafields.add(txt_shipdate3, cc.xy(3, 103));
		txt_shipdate3_prev.setEditable(false);
		txt_shipdate3_prev.setEnabled(false);
		panel_datafields.add(txt_shipdate3_prev, cc.xy(5, 103));

		lbl_ship3.setText(bundle.getString("EntryForm.lbl_ship3.text"));
		lbl_ship3.setLabelFor(txt_ship3);
		lbl_ship3.setAlignmentX(0.5F);
		lbl_ship3.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(lbl_ship3, cc.xy(1, 105));
		panel_datafields.add(txt_ship3, cc.xy(3, 105));

		txt_ship3_prev.setEditable(false);
		txt_ship3_prev.setEnabled(false);
		panel_datafields.add(txt_ship3_prev, cc.xy(5, 105));

		
		//---- lbl_price3 ----
		lbl_price3.setText(bundle.getString("EntryForm.lbl_price3.text"));
		lbl_price3.setLabelFor(txt_price3);
		lbl_price3.setAlignmentX(0.5F);
		lbl_price3.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_datafields.add(lbl_price3, cc.xy(1, 107));
		panel_datafields.add(txt_price3, cc.xy(3, 107));

		//---- txt_price3_prev ----
		txt_price3_prev.setEditable(false);
		txt_price3_prev.setEnabled(false);
		panel_datafields.add(txt_price3_prev, cc.xy(5, 107));
				
	}

	
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Karl Stamm
	private JPanel EntryForm;
	private JPanel panel3;
	private JPanel panel_header;
	private JLabel label7;
	private JLabel label8;
	private JLabel lbl_casenumber;
	private JTextField txt_casenumber;
	private JTextField txt_casenumber_prev;
	private JScrollPane scrollPane_main;
	private JPanel panel_datafields;
	private JLabel label9;
	private JTextField txt_customer;
	private JTextField txt_customer_prev;
	private JLabel label2;
	private JTextField txt_request1;
	private JTextField txt_request1_prev;
	private JLabel label5;
	private JTextField txt_sendbox1;
	private JTextField txt_sendbox1_prev;
	
	private JLabel lbl_casestatus;
	private JTextField txt_casestatus;
	private JTextField txt_casestatus_prev;
	
	
	private JLabel label3;
	private JTextField txt_sendboxtrack;
	private JTextField txt_sendboxtrack_prev;
	private JLabel label4;
	private JTextField txt_returnlabel;
	private JTextField txt_returnlabel_prev;
	private JLabel lbl_arrival1;
	private JTextField txt_arrival1;
	private JTextField txt_arrival1_prev;
	private JLabel lbl_model;
	private JTextField txt_model;
	private JTextField txt_model_prev;
	private JLabel label16;
	private JScrollPane scrollPane4;
	private JEditorPane edt_accessory;
	private JScrollPane scrollPane7;
	private JEditorPane edt_accessory_prev;
	private JLabel label14;
	private JTextField txt_desc1;
	private JTextField txt_desc1_prev;
	private JLabel label17;
	private JScrollPane scrollPane8;
	private JEditorPane edt_technote1;
	private JScrollPane scrollPane9;
	private JEditorPane edt_technote1_prev;
	private JLabel label19;
	private JScrollPane scrollPane10;
	private JEditorPane edt_diags;
	private JScrollPane scrollPane11;
	private JEditorPane edt_diags_prev;
	private JLabel label20;
	private JScrollPane scrollPane12;
	private JEditorPane edt_services;
	private JScrollPane scrollPane13;
	private JEditorPane edt_services_prev;
	private JLabel label21;
	private JScrollPane scrollPane15;
	private JEditorPane edt_parts;
	private JScrollPane scrollPane14;
	private JEditorPane edt_parts_prev;
	private JLabel label18;
	private JTextField txt_tech;
	private JTextField txt_tech_prev;
	private JLabel label22;
	private JScrollPane scrollPane16;
	private JEditorPane edt_message_prev;
	private JScrollPane scrollPane17;
	private JEditorPane edt_message;
	private JLabel label23;
	private JTextField txt_shipdate1;
	private JTextField txt_shipdate1_prev;
	private JLabel label24;
	private JTextField txt_ship;
	private JTextField txt_ship_prev;
	private JLabel lbl_price1;
	private JTextField txt_price1;
	private JTextField txt_price1_prev;
	private JLabel lbl_SecondArrival;
	private JLabel label26;
	private JLabel label27;
	private JScrollPane scrollPane18;
	private JEditorPane edt_accessory2_prev;
	private JTextField txt_desc2_prev;
	private JTextField txt_returnlabel2;
	private JLabel lbl_accessory2;
	private JLabel lbl_accessory2p;
	private JScrollPane scrollPane19;
	private JEditorPane edt_accessory2;
	private JScrollPane scrollPane20;
	private JEditorPane edt_parts2_prev;
	private JTextField txt_tech2;
	private JScrollPane scrollPane21;
	private JEditorPane edt_services2_prev;
	private JLabel label30;
	private JScrollPane scrollPane22;
	private JEditorPane edt_diags2;
	private JLabel label31;
	private JTextField txt_sendboxtrack2;
	private JTextField txt_returnlabel2_prev;
	private JLabel label32;
	private JLabel lbl_arrival2;
	private JScrollPane scrollPane23;
	private JEditorPane edt_parts2;
	private JTextField txt_desc2;
	private JLabel label34;
	private JScrollPane scrollPane24;
	private JEditorPane edt_message2;
	private JTextField txt_sendboxtrack2_prev;
	private JScrollPane scrollPane25;
	private JEditorPane edt_technote2_prev;
	private JScrollPane scrollPane26;
	private JEditorPane edt_message2_prev;
	private JLabel label35;
	private JTextField txt_request2;
	private JTextField txt_request2_prev;
	private JTextField txt_sendbox2;
	private JTextField txt_sendbox2_prev;
	private JTextField txt_ship2_prev;
	private JLabel label36;
	private JLabel label37;
	private JTextField txt_arrival2;
	private JTextField txt_arrival2_prev;
	private JScrollPane scrollPane27;
	private JEditorPane edt_technote2;
	private JLabel label38;
	private JTextField txt_tech2_prev;
	private JLabel label39;
	private JScrollPane scrollPane28;
	private JEditorPane edt_services2;
	private JScrollPane scrollPane29;
	private JEditorPane edt_diags2_prev;
	private JTextField txt_shipdate2;
	private JTextField txt_shipdate2_prev;
	private JTextField txt_ship2;
	private JLabel label40;
	private JLabel lbl_price2;
	private JTextField txt_price2;
	private JTextField txt_price2_prev;
	private JLabel lbl_ThirdArrival;
	private JLabel label42;
	private JTextField txt_request3;
	private JTextField txt_request3_prev;
	private JLabel label43;
	private JTextField txt_sendbox3;
	private JTextField txt_sendbox3_prev;
	private JLabel label44;
	private JTextField txt_sendboxtrack3;
	private JTextField txt_sendboxtrack3_prev;
	private JLabel label45;
	private JTextField txt_returnlabel3;
	private JTextField txt_returnlabel3_prev;
	private JLabel label46;
	private JTextField txt_arrival3;
	private JTextField txt_arrival3_prev;
	private JLabel label47;
	private JScrollPane scrollPane30;
	private JEditorPane edt_accessory3_prev;
	private JTextField txt_desc3_prev;
	private JLabel label48;
	private JScrollPane scrollPane31;
	private JEditorPane edt_accessory3;
	private JLabel label49;
	private JLabel lbl_desc3;
	private JTextField txt_desc3;
	private JLabel label51;
	private JLabel label52;
	private JLabel label53;
	private JLabel label54;
	private JTextField txt_tech3_prev;
	private JTextField txt_tech3;
	private JLabel label55;
	private JScrollPane scrollPane32;
	private JEditorPane edt_parts3_prev;
	private JScrollPane scrollPane33;
	private JEditorPane edt_services3_prev;
	private JScrollPane scrollPane34;
	private JEditorPane edt_diags3;
	private JScrollPane scrollPane35;
	private JEditorPane edt_parts3;
	private JScrollPane scrollPane36;
	private JEditorPane edt_message3;
	private JScrollPane scrollPane37;
	private JEditorPane edt_technote3_prev;
	private JScrollPane scrollPane38;
	private JEditorPane edt_message3_prev;
	private JScrollPane scrollPane39;
	private JEditorPane edt_technote3;
	private JScrollPane scrollPane40;
	private JEditorPane edt_services3;
	private JScrollPane scrollPane41;
	private JEditorPane edt_diags3_prev;
	private JTextField txt_shipdate3;
	private JTextField txt_shipdate3_prev;
	private JLabel lbl_ship3;
	private JTextField txt_ship3;
	private JTextField txt_ship3_prev;
	private JLabel lbl_price3;
	private JTextField txt_price3;
	private JTextField txt_price3_prev;
	private JPanel panel_metadata;
	private JLabel lbl_User;
	private JTextField txt_User;
	private JLabel lbl_lastedit;
	private JSpinner spn_lastedit;
	private JLabel lbl_lasteditor;
	private JTextField txt_lastedit;
	private JPanel panel_buttons;
	private JButton but_Save;
	private JButton but_Load;
	private JButton but_Clear;
	private JButton but_Search;
	private Search_Form search_Form;
	
	
	public void setUser(String u) {
		txt_User.setText(u);
	}

	public void forcedLoad(Properties loadCase) {
		this.clearAllFields();
		this.loadFormFromProperties(loadCase);
	}
	
	

	class ContextPopUp extends JPopupMenu {
		/**
		 *  helper text for several text inputs boxes.
		 */
		private static final long serialVersionUID = 1926131974588968202L;
		JMenuItem anItem;
	    public ContextPopUp(String fi, JTextComponent textbox){
	    	if(contextMenuItems != null) {
	    	for(int i=1;i<20;i++){
	    		String key = contextMenuItems.getProperty(fi + ".item"+i);
	    		if(key != null){
	    			int endpoint = 24; if(endpoint > key.length()) endpoint = key.length();
	    	        anItem = new JMenuItem(new CMenuAction(key.substring(0, endpoint),textbox,key,null));
	    	        add(anItem);
	    		}
	    	}
	    	}else{
	    		// context_menu not loaded.
	    		String key="context menu not loaded";
	    		anItem = new JMenuItem(new CMenuAction(key.substring(0, 16),textbox,key,null));
    	        add(anItem);
	    	}
	    }
	}
	
	class PopClickListener extends MouseAdapter {
		private String field;
		private JTextComponent textbox;
		
		public PopClickListener(String fi, JTextComponent edt){
			super();
			this.field = fi;
			this.textbox = edt;
		}
		public void mousePressed(MouseEvent e){
	        if (e.isPopupTrigger())
	            doPop(e);
	    }
	    public void mouseReleased(MouseEvent e){
	        if (e.isPopupTrigger())
	            doPop(e);
	    }
	    private void doPop(MouseEvent e){
	    	ContextPopUp menu = new ContextPopUp(this.field, this.textbox);
	        menu.show(e.getComponent(), e.getX(), e.getY());
	    }
	}

	class CMenuAction extends AbstractAction {
		private static final long serialVersionUID = 6889316313193856300L;
		private JTextComponent myTextBox;
		private String myResult="UNDEF";
	    public CMenuAction(String text, JTextComponent txtbox,
	                      String desc, Integer mnemonic) {
	        super(text, null);
	        putValue(SHORT_DESCRIPTION, desc);
	        putValue(MNEMONIC_KEY, mnemonic);
	        this.myResult=desc;
	        this.myTextBox=txtbox;
	    }
	    public void actionPerformed(ActionEvent e) {
	        myTextBox.setText(this.myResult);
	    }
	}
	
	
}
