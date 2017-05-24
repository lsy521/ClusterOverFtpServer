package com.hzgc.ftpserver.local;

import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.DataType;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.impl.DefaultFtpSession;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.IODataConnection;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;
import org.apache.ftpserver.usermanager.impl.TransferRateRequest;
import org.apache.ftpserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;


public class LocalIODataConnection implements DataConnection{
    private final Logger LOG = LoggerFactory
            .getLogger(LocalIODataConnection.class);


    private static final byte[] EOL = System.getProperty("line.separator").getBytes();

    private FtpIoSession session;

    private Socket socket;

    private ServerDataConnectionFactory factory;

    public LocalIODataConnection(final Socket socket, final FtpIoSession session,
                            final ServerDataConnectionFactory factory) {
        this.session = session;
        this.socket = socket;
        this.factory = factory;
    }

    /**
     * Get data input stream. The return value will never be null.
     */
    public InputStream getDataInputStream() throws IOException {
        try {

            // get data socket
            Socket dataSoc = socket;
            if (dataSoc == null) {
                throw new IOException("Cannot open data connection.");
            }

            // create input stream
            InputStream is = dataSoc.getInputStream();
            if (factory.isZipMode()) {
                is = new InflaterInputStream(is);
            }
            return is;
        } catch (IOException ex) {
            factory.closeDataConnection();
            throw ex;
        }
    }

    /**
     * Get data output stream. The return value will never be null.
     */
    protected OutputStream getDataOutputStream() throws IOException {
        try {

            // get data socket
            Socket dataSoc = socket;
            if (dataSoc == null) {
                throw new IOException("Cannot open data connection.");
            }

            // create output stream
            OutputStream os = dataSoc.getOutputStream();
            if (factory.isZipMode()) {
                os = new DeflaterOutputStream(os);
            }
            return os;
        } catch (IOException ex) {
            factory.closeDataConnection();
            throw ex;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.apache.ftpserver.FtpDataConnection2#transferFromClient(java.io.
     * OutputStream)
     */
    public final long transferFromClient(FtpSession session,
                                         final OutputStream out) throws IOException {
        TransferRateRequest transferRateRequest = new TransferRateRequest();
        transferRateRequest = (TransferRateRequest) session.getUser()
                .authorize(transferRateRequest);
        int maxRate = 0;
        if (transferRateRequest != null) {
            maxRate = transferRateRequest.getMaxUploadRate();
        }

        InputStream is = getDataInputStream();
        try {
            return transfer(session, false, is, out, maxRate);
        } finally {
            IoUtils.close(is);
        }
    }

    public final long transferFromClient(FtpSession session, final InputStream is
            ,final OutputStream out) throws IOException {
        TransferRateRequest transferRateRequest = new TransferRateRequest();
        transferRateRequest = (TransferRateRequest) session.getUser()
                .authorize(transferRateRequest);
        int maxRate = 0;
        if (transferRateRequest != null) {
            maxRate = transferRateRequest.getMaxUploadRate();
        }

        try {
            return transfer(session, false, is, out, maxRate);
        } finally {
            IoUtils.close(is);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.ftpserver.FtpDataConnection2#transferToClient(java.io.InputStream
     * )
     */
    public final long transferToClient(FtpSession session, final InputStream in)
            throws IOException {
        TransferRateRequest transferRateRequest = new TransferRateRequest();
        transferRateRequest = (TransferRateRequest) session.getUser()
                .authorize(transferRateRequest);
        int maxRate = 0;
        if (transferRateRequest != null) {
            maxRate = transferRateRequest.getMaxDownloadRate();
        }

        OutputStream out = getDataOutputStream();
        try {
            return transfer(session, true, in, out, maxRate);
        } finally {
            IoUtils.close(out);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.ftpserver.FtpDataConnection2#transferToClient(java.lang.String
     * )
     */
    public final void transferToClient(FtpSession session, final String str)
            throws IOException {
        OutputStream out = getDataOutputStream();
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(out, "UTF-8");
            writer.write(str);

            // update session
            if (session instanceof DefaultFtpSession) {
                ((DefaultFtpSession) session).increaseWrittenDataBytes(str
                        .getBytes("UTF-8").length);
            }
        } finally {
            if (writer != null) {
                writer.flush();
            }
            IoUtils.close(writer);
        }

    }

    private final long transfer(FtpSession session, boolean isWrite,
                                final InputStream in, final OutputStream out, final int maxRate)
            throws IOException {
        long transferredSize = 0L;

        boolean isAscii = session.getDataType() == DataType.ASCII;
        long startTime = System.currentTimeMillis();
        byte[] buff = new byte[4096];

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = IoUtils.getBufferedInputStream(in);

            bos = IoUtils.getBufferedOutputStream(out);

            DefaultFtpSession defaultFtpSession = null;
            if (session instanceof DefaultFtpSession) {
                defaultFtpSession = (DefaultFtpSession) session;
            }

            byte lastByte = 0;
            while (true) {
                // if current rate exceeds the max rate, sleep for 50ms
                // and again check the current transfer rate
                if (maxRate > 0) {

                    // prevent "divide by zero" exception
                    long interval = System.currentTimeMillis() - startTime;
                    if (interval == 0) {
                        interval = 1;
                    }

                    // check current rate
                    long currRate = (transferredSize * 1000L) / interval;
                    if (currRate > maxRate) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            break;
                        }
                        continue;
                    }
                }

                // read data
                int count = bis.read(buff);

                if (count == -1) {
                    break;
                }

                // update MINA session
                if (defaultFtpSession != null) {
                    if (isWrite) {
                        defaultFtpSession.increaseWrittenDataBytes(count);
                    } else {
                        defaultFtpSession.increaseReadDataBytes(count);
                    }
                }

                // write data
                // if ascii, replace \n by \r\n
                if (isAscii) {
                    for (int i = 0; i < count; ++i) {
                        byte b = buff[i];
                        if(isWrite) {
                            if (b == '\n' && lastByte != '\r') {
                                bos.write('\r');
                            }

                            bos.write(b);
                        } else {
                            if(b == '\n') {
                                // for reads, we should always get \r\n
                                // so what we do here is to ignore \n bytes
                                // and on \r dump the system local line ending
                                // Some clients won't transform new lines into \r\n so we make sure we don't delete new lines
                                if (lastByte != '\r'){
                                    bos.write(EOL);
                                }
                            } else if(b == '\r') {
                                bos.write(EOL);
                            } else {
                                // not a line ending, just output
                                bos.write(b);
                            }
                        }
                        // store this byte so that we can compare it for line endings
                        lastByte = b;
                    }
                } else {
                    bos.write(buff, 0, count);
                }

                transferredSize += count;

                notifyObserver();
            }
        } catch(IOException e) {
            LOG.warn("Exception during data transfer, closing data connection socket", e);
            factory.closeDataConnection();
            throw e;
        } catch(RuntimeException e) {
            LOG.warn("Exception during data transfer, closing data connection socket", e);
            factory.closeDataConnection();
            throw e;
        } finally {
            if (bos != null) {
                bos.flush();
            }
        }

        return transferredSize;
    }

    /**
     * Notify connection manager observer.
     */
    protected void notifyObserver() {
        session.updateLastAccessTime();

        // TODO this has been moved from AbstractConnection, do we need to keep
        // it?
        // serverContext.getConnectionManager().updateConnection(this);
    }
}
