/**
 *
 *  edtFTPj
 * 
 *  Copyright (C) 2000-2004 Enterprise Distributed Technologies Ltd
 *
 *  www.enterprisedt.com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Bug fixes, suggestions and comments should be should posted on 
 *  http://www.enterprisedt.com/forums/index.php
 *
 *  Change Log:
 *
 *    $Log: FTPFileFactory.java,v $
 *    Revision 1.16  2007-10-12 05:21:44  bruceb
 *    multiple locale stuff
 *
 *    Revision 1.15  2007/02/26 07:15:52  bruceb
 *    Add getVMSParser() method
 *
 *    Revision 1.14  2006/10/27 15:38:16  bruceb
 *    renamed logger
 *
 *    Revision 1.13  2006/10/11 08:54:34  hans
 *    made cvsId final
 *
 *    Revision 1.12  2006/05/24 11:35:54  bruceb
 *    fix VMS problem for listings over 3+ lines
 *
 *    Revision 1.11  2006/01/08 19:10:19  bruceb
 *    better error information
 *
 *    Revision 1.10  2005/06/10 15:43:41  bruceb
 *    more VMS tweaks
 *
 *    Revision 1.9  2005/06/03 11:26:05  bruceb
 *    VMS stuff
 *
 *    Revision 1.8  2005/04/01 13:57:35  bruceb
 *    added some useful debug
 *
 *    Revision 1.7  2004/10/19 16:15:16  bruceb
 *    swap to unix if seems like unix listing
 *
 *    Revision 1.6  2004/10/18 15:57:16  bruceb
 *    set locale
 *
 *    Revision 1.5  2004/08/31 10:45:50  bruceb
 *    removed unused import
 *
 *    Revision 1.4  2004/07/23 08:31:52  bruceb
 *    parser rotation
 *
 *    Revision 1.3  2004/05/01 11:44:21  bruceb
 *    modified for server returning "total 3943" as first line
 *
 *    Revision 1.2  2004/04/17 23:42:07  bruceb
 *    file parsing part II
 *
 *    Revision 1.1  2004/04/17 18:37:23  bruceb
 *    new parse functionality
 *
 */

package com.enterprisedt.net.ftp;

import java.text.ParseException;
import java.util.Locale;

import com.enterprisedt.util.debug.Logger;

/**
 *  Factory for creating FTPFile objects
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.16 $
 */
public class FTPFileFactory {
    
    /**
     *  Revision control id
     */
    public static final String cvsId = "@(#)$Id: FTPFileFactory.java,v 1.16 2007-10-12 05:21:44 bruceb Exp $";
    
    /**
     * Logging object
     */
    private static Logger log = Logger.getLogger("FTPFileFactory");

    /**
     * Windows server comparison string
     */
    final static String WINDOWS_STR = "WINDOWS";
                  
    /**
     * UNIX server comparison string
     */
    final static String UNIX_STR = "UNIX";
    
    /**
     * VMS server comparison string
     */
    final static String VMS_STR = "VMS";
        
    /**
     * SYST string
     */
    private String system;
    
    /**
     * Cached windows parser
     */
    private WindowsFileParser windows = new WindowsFileParser();
    
    /**
     * Cached unix parser
     */
    private UnixFileParser unix = new UnixFileParser();
    
    /**
     * Cached vms parser
     */
    private VMSFileParser vms = new VMSFileParser();
    
    /**
     * Current parser
     */
    private FTPFileParser parser = null;
    
    /**
     * Original parser
     */
    private FTPFileParser origParser = null;
    
    /**
     * True if using VMS parser
     */
    private boolean usingVMS = false;
    
    /**
     * Rotate parsers when a ParseException is thrown?
     */
    private boolean rotateParsers = true;
    
    /**
     * Locales to try out
     */
    private Locale[] localesToTry;
    
    /**
     * Index of locale to try next
     */
    private int localeIndex = 0;
     
    /**
     * Constructor
     * 
     * @param system    SYST string
     */
    public FTPFileFactory(String system) throws FTPException {
        setParser(system);
    }
    
    /**
     * Constructor. User supplied parser. Note that parser
     * rotation (in case of a ParseException) is disabled if
     * a parser is explicitly supplied
     * 
     * @param parser   the parser to use
     */
    public FTPFileFactory(FTPFileParser parser) {
        this.parser = parser;
        origParser = parser;
        rotateParsers = false;
    }   
    
    
    /**
     * Return a reference to the VMS parser being used.
     * This allows the user to set VMS-specific settings on
     * the parser.
     * 
     * @return  VMSFileParser object
     */
    public VMSFileParser getVMSParser() {
        return vms;
    }


    /**
     * Set the locale for date parsing of listings
     * 
     * @param locale    locale to set
     */
    public void setLocale(Locale locale) {
        windows.setLocale(locale);
        unix.setLocale(locale);
        vms.setLocale(locale);
        parser.setLocale(locale); // might be user supplied
    }
    
    /**
     * Set the locales to try for date parsing of listings
     * 
     * @param locales    locales to try
     */
    public void setLocales(Locale[] locales) {
        this.localesToTry = locales;
        setLocale(locales[0]); 
        localeIndex = 1;
    }
    
    /**
     * Set the remote server type
     * 
     * @param system    SYST string
     */
    private void setParser(String system) {
        this.system = system;
        if (system.toUpperCase().startsWith(WINDOWS_STR))
            parser = windows;
        else if (system.toUpperCase().startsWith(UNIX_STR))
            parser = unix;
        else if (system.toUpperCase().startsWith(VMS_STR)) {
            parser = vms;
            usingVMS = true;
        }
        else {
            parser = unix;
            log.warn("Unknown SYST '" + system + "' - defaulting to Unix parsing");
        }
        origParser = parser;
    }
    
    /**
     * Reinitialize the parsers
     */
    private void reinitializeParsers() {
        windows.setIgnoreDateParseErrors(false);
        unix.setIgnoreDateParseErrors(false);
        vms.setIgnoreDateParseErrors(false);
        parser.setIgnoreDateParseErrors(false);
    }
    
    
    /**
     * Parse an array of raw file information returned from the
     * FTP server
     * 
     * @param files     array of strings
     * @return array of FTPFile objects
     */
    public FTPFile[] parse(String[] files) throws ParseException {
               
        reinitializeParsers();
        
        FTPFile[] temp = new FTPFile[files.length];
        
        // quick check if no files returned
        if (files.length == 0)
            return temp;
                
        int count = 0;
        boolean checkedUnix = false;
        boolean reparse = false;
        int reparseCount = 1;
        for (int i = 0; i < files.length; i++) {
            if (reparse) { // rotated parsers, try this line again
                i -= reparseCount;
                reparse = false;
                reparseCount = 1;
            }
            try {
                if (files[i] == null || files[i].trim().length() == 0)
                    continue;
                
                // swap to Unix if looks like Unix listing
                if (!checkedUnix && parser != unix && UnixFileParser.isUnix(files[i])) {
                    parser = unix;
                    checkedUnix = true;
                    log.info("Swapping Windows parser to Unix");
                }
                
                FTPFile file = null;
                if(usingVMS) {
                    // vms uses more than 1 line for some file listings. We must keep going
                	// thru till we've got everything
                	reparseCount = 1;
                	StringBuffer filename = new StringBuffer(files[i]);
                	while (i+1 < files.length && files[i+1].indexOf(';') < 0) {
                		filename.append(" ").append(files[i+1]);
                		i++;
                		reparseCount++;
                	}
                	file = parser.parse(filename.toString());
                }
                else {
                    file = parser.parse(files[i]);
                }
                // we skip null returns - these are duff lines we know about and don't
                // really want to throw an exception
                if (file != null) {
                    temp[count++] = file;
                }
            }
            catch (ParseException ex) {
                // for date exceptions, try going thru the locales
                if (ex.getMessage().toUpperCase().indexOf("UNPARSEABLE DATE") >= 0) {
                    if (localesToTry != null && localesToTry.length > localeIndex) {
                        log.info("Trying " + localesToTry[localeIndex].toString() + " locale");
                        setLocale(localesToTry[localeIndex]);
                        localeIndex++;  
                        count = 0;
                        i = -1; // account for the increment to set i back to 0
                        continue;
                    }  
                    // from this point start again ignoring date errors (we've rotated parsers and
                    // tried all our locales)
                    if (!rotateParsers) {
                        count = 0;
                        i = -1; // account for the increment to set i back to 0
                        parser = origParser;
                        parser.setIgnoreDateParseErrors(true);
                        log.debug("Ignoring date parsing errors");
                        continue;
                    }
                }
                
                StringBuffer msg = new StringBuffer("Failed to parse line '");
                msg.append(files[i]).append("' (").append(ex.getMessage()).append(")");
                log.info(msg.toString());
                if (rotateParsers) { // first error, let's try swapping parsers
                    rotateParsers = false; // only do once
                    rotateParsers();
                    if (localesToTry != null) {
                        setLocale(localesToTry[0]);
                        localeIndex = 1;
                    }               
                    count = 0;
                    i = -1; // account for the increment to set i back to 0
                }
                else {// rethrow
                    throw new ParseException(msg.toString(), ex.getErrorOffset());
                }
            }
        }
        FTPFile[] result = new FTPFile[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }
    
    /**
     * Swap from one parser to the other. We can just check
     * object references
     */
    private void rotateParsers() {
        usingVMS = false;
        if (parser == unix) {
            parser = windows;
            log.info("Rotated parser to Windows");
        }
        else if (parser == windows){
            parser = unix;
            log.info("Rotated parser to Unix");
        }
    }

    /**
     * Get the SYST string
     * 
     * @return the system string.
     */
    public String getSystem() {
        return system;
    }


}
