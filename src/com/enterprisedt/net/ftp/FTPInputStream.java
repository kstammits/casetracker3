/**
 * 
 *  Copyright (C) 2007 Enterprise Distributed Technologies Ltd
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
 *    $Log: FTPInputStream.java,v $
 *    Revision 1.1  2007-12-18 07:52:06  bruceb
 *    2.0 changes
 *
 *
 */
package com.enterprisedt.net.ftp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.enterprisedt.util.debug.Logger;

/**
 *  Represents an input stream of bytes coming from an FTP server, permitting
 *  the user to download a file by reading the stream. It can only be used
 *  for one download, i.e. after the stream is closed it cannot be reopened.
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1 $
 */
public class FTPInputStream extends FileTransferInputStream {
    
    private static Logger log = Logger.getLogger("FTPInputStream");
    
    /**
     * Line separator
     */
    final private static byte[] LINE_SEPARATOR = System.getProperty("line.separator").getBytes();

    /**
     * Interval that we notify the monitor of progress
     */
    private long monitorInterval;
      
    /**
     * The client being used to perform the transfer
     */
    private FTPClient client; 
    
    /**
     * The input stream from the FTP server
     */
    private BufferedInputStream in;
    
    /**
     * Number of bytes downloaded
     */
    private long size = 0;
    
    /**
     * Is this an ASCII transfer or not?
     */
    private boolean isASCII = false;
    
    /**
     * Was a CR found?
     */
    private boolean crFound = false;
    
    /**
     * Buffer that supplies the stream
     */
    private byte [] buffer;
    
    /**
     * Current position to read from in the buffer
     */
    private int bufpos = 0;
    
    /**
     * The length of the buffer
     */
    private int buflen = 0;
    
    /**
     * Buffer read from the FTP server
     */
    private byte[] chunk;
    
    /**
     * Output stream to write to 
     */
    private ByteArrayOutputStream out;
    
    /**
     * Count of byte since last the progress monitor was notified.
     */
    private long monitorCount = 0; 

    /**
     * Progress monitor reference
     */
    private FTPProgressMonitor monitor;
    
    /**
     * Progress monitor reference
     */
    private FTPProgressMonitorEx monitorEx;
    
    /**
     * Flag to indicated we've started downloading
     */
    private boolean started = false;
            
    /**
     * Constructor. A connected FTPClient instance must be supplied. This sets up the
     * download 
     * 
     * @param client            connected FTPClient instance
     * @param remoteFile        remote file
     * @throws IOException
     * @throws FTPException
     */
    public FTPInputStream(FTPClient client, String remoteFile) throws IOException, FTPException {
        this.client = client;
        this.remoteFile = remoteFile;
        try {
            client.initGet(remoteFile);

            // get an input stream to read data from ... AFTER we have
            // the ok to go ahead AND AFTER we've successfully opened a
            // stream for the local file
            in = new BufferedInputStream(new DataInputStream(client.getInputStream()));

        } 
        catch (IOException ex) {
            client.validateTransferOnError(ex);
            throw ex;
        }
        
        this.monitorInterval = client.getMonitorInterval();
        this.monitor = client.getProgressMonitor();
        this.chunk = new byte[client.getTransferBufferSize()];
        this.out = new ByteArrayOutputStream(client.getTransferBufferSize());
        this.isASCII = (client.getType().equals(FTPTransferType.ASCII));
    }
    
    
    /**
     * The input stream uses the progress monitor currently owned by the FTP client.
     * This method allows a different progress monitor to be passed in, or for the
     * monitor interval to be altered.
     * 
     * @param monitor               progress monitor reference
     * @param monitorInterval       
     */
    public void setMonitor(FTPProgressMonitorEx monitor, long monitorInterval) {
        this.monitor = monitor;
        this.monitorEx = monitor;
        this.monitorInterval = monitorInterval;
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is 
     * returned as an int in the range 0 to 255. If no byte is available because 
     * the end of the stream has been reached, the value -1 is returned. 
     * This method blocks until input data is available, the end of the stream 
     * is detected, or an exception is thrown. 
     */
    public int read() throws IOException {
        if (!started) {
            start();
        }
        if (buffer == null)
            return -1;
        if (bufpos == buflen) {
            buffer = refreshBuffer();
            if (buffer == null)
                return -1;
        }
        return buffer[bufpos++];
    }
    
    /**
     * Reads up to len bytes of data from the input stream into an array of bytes. 
     * An attempt is made to read as many as len bytes, but a smaller number may 
     * be read, possibly zero. The number of bytes actually read is returned as an integer. 
     * This method blocks until input data is available, end of file is detected, 
     * or an exception is thrown. 
     *
     * @param b    array to read into
     * @param off  offset into the array to start at
     * @param len  the number of bytes to be read
     * 
     * @return  the number of bytes read, or -1 if the end of the stream has been reached.
     */
    public int read(byte b[], int off, int len) throws IOException {
        if (!started) {
            start();
        }
        if (buffer == null || len == 0)
            return -1;
        
        if (bufpos == buflen) {
            buffer = refreshBuffer();
            if (buffer == null)
                return -1;
        }
        
        int available = 0;
        int remaining = len;
        while ( (available = buflen-bufpos) < remaining ) {
            System.arraycopy(buffer, bufpos, b, off, available);
            remaining -= available;
            off += available;
            buffer = refreshBuffer();
            if (buffer == null)
                return len-remaining;
        }
        System.arraycopy(buffer, bufpos, b, off, remaining);
        bufpos += remaining;
        return len;        
    }
    
    /**
     * Start the transfer
     * 
     * @throws IOException
     */
    private void start() throws IOException {
        if (monitorEx != null) {
            monitorEx.transferStarted(TransferDirection.DOWNLOAD, remoteFile);
        }
        buffer = refreshBuffer();
        started = true;
    }
    
    /**
     * Refresh the buffer by reading the internal FTP input stream 
     * directly from the server
     * 
     * @return  byte array of bytes read, or null if the end of stream is reached
     * @throws IOException
     */
    private byte[] refreshBuffer() throws IOException {
        bufpos = 0;
        if (client.isTransferCancelled())
            return null;
        int count = client.readChunk(in, chunk, chunk.length);
        if (count < 0) {
            if (isASCII && crFound) {               
                size++;
                buflen = 1;
                monitorCount++;
                crFound = false;
                byte[] tmp = new byte[1];
                tmp[0] = FTPClient.CARRIAGE_RETURN;
                return tmp;
            }
            return null;
        }
        try {
            if (!isASCII) {
                size += count;
                monitorCount += count;
                buflen = count;
                return chunk;
            }
            else {
                // transform CRLF
                out.reset();
                boolean lfFound = false;
                for (int i = 0; i < count; i++) {
                    lfFound = chunk[i] == FTPClient.LINE_FEED;
                    // if previous is a CR, write it out if current is LF, otherwise
                    // write out the previous CR
                    if (crFound) {
                        if (lfFound) {
                           out.write(LINE_SEPARATOR, 0, LINE_SEPARATOR.length);
                           size += LINE_SEPARATOR.length;
                           monitorCount += LINE_SEPARATOR.length;
                        }
                        else {
                            // not CR LF so write out previous CR
                            out.write(FTPClient.CARRIAGE_RETURN); 
                            size++;
                            monitorCount++;
                        }                           
                    }
                    
                    // now check if current is CR
                    crFound = chunk[i] == FTPClient.CARRIAGE_RETURN;
                   
                    // if we didn't find a LF this time, write current byte out
                    // unless it is a CR - in that case save it
                    if (!lfFound && !crFound) {
                        out.write(chunk[i]);
                        size++;
                        monitorCount++;
                    }
                }
                byte[] result = out.toByteArray();
                buflen = result.length;
                return result;
            }
        }
        finally {
            if (monitor != null && monitorCount > monitorInterval) {
                monitor.bytesTransferred(size); 
                monitorCount = 0;  
            }    

        }
    }
    

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream. This <b>must</b> be called before any other operations
     * are initiated on the FTPClient.
     *
     * @exception  IOException  if an I/O error occurs.
     */
    public void close() throws IOException {
        
        if (!closed) {
            closed = true;
            
            client.forceResumeOff();
    
            // close streams
            client.closeDataSocket(in);
            
            if (monitor != null)
                monitor.bytesTransferred(size);  
    
            // log bytes transferred
            log.debug("Transferred " + size + " bytes from remote host");
            
            try {
                client.validateTransfer();
            }
            catch (FTPException ex) {
                throw new IOException(ex.getMessage());
            }
            
            if (monitorEx != null)
                monitorEx.transferComplete(TransferDirection.DOWNLOAD, remoteFile);
        }
    }
    
}
