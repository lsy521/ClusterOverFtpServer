package com.hzgc.ftpserver.local;

import org.apache.ftpserver.filesystem.nativefs.impl.NativeFtpFile;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;


public class LocalFileSystem implements FtpFile{
    private final Logger log = LoggerFactory.getLogger(NativeFtpFile.class);

    // the file name with respect to the user root.
    // The path separator character will be '/' and
    // it will always begin with '/'.
    private String fileName;

    private File file;

    private User user;

    protected LocalFileSystem(final String fileName, final File file, final User user) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName can not be null");
        }
        if (file == null) {
            throw new IllegalArgumentException("file can not be null");
        }

        if (fileName.length() == 0) {
            throw new IllegalArgumentException("fileName can not be empty");
        } else if (fileName.charAt(0) != '/') {
            throw new IllegalArgumentException(
                    "fileName must be an absolut path");
        }

        this.fileName = fileName;
        this.file = file;
        this.user = user;
    }


    public String getAbsolutePath() {
        // strip the last '/' if necessary
        String fullName = fileName;
        int filelen = fullName.length();
        if ((filelen != 1) && (fullName.charAt(filelen - 1) == '/')) {
            fullName = fullName.substring(0, filelen - 1);
        }

        return fullName;
    }

    public String getName() {
        // root - the short name will be '/'
        if (fileName.equals("/")) {
            return "/";
        }

        // strip the last '/'
        String shortName = fileName;
        int filelen = fileName.length();
        if (shortName.charAt(filelen - 1) == '/') {
            shortName = shortName.substring(0, filelen - 1);
        }

        // return from the last '/'
        int slashIndex = shortName.lastIndexOf('/');
        if (slashIndex != -1) {
            shortName = shortName.substring(slashIndex + 1);
        }
        return shortName;
    }

    public boolean isHidden() {
        return file.isHidden();
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public boolean isFile() {
        return file.isFile();
    }

    public boolean doesExist() {
        return file.exists();
    }

    public boolean isReadable() {
        return file.canRead();
    }

    public boolean isWritable() {
        return file.canWrite();
    }

    public boolean isRemovable() {
        // root cannot be deleted
        if ("/".equals(fileName)) {
            return false;
        }

        /* Added 12/08/2008: in the case that the permission is not explicitly denied for this file
         * we will check if the parent file has write permission as most systems consider that a file can
         * be deleted when their parent directory is writable.
        */
        String fullName = getAbsolutePath();

        // we check FTPServer's write permission for this file.
        if (user.authorize(new WriteRequest(fullName)) == null) {
            return false;
        }
        // In order to maintain consistency, when possible we delete the last '/' character in the String
        int indexOfSlash = fullName.lastIndexOf('/');
        String parentFullName;
        if (indexOfSlash == 0) {
            parentFullName = "/";
        } else {
            parentFullName = fullName.substring(0, indexOfSlash);
        }

        // we check if the parent FileObject is writable.
        LocalFileSystem parentObject = new LocalFileSystem(parentFullName, file
                .getAbsoluteFile().getParentFile(), user);
        return parentObject.isWritable();
    }

    public String getOwnerName() {
        return "user";
    }

    public String getGroupName() {
        return "group";
    }

    public int getLinkCount() {
        return file.isDirectory() ? 3 : 1;
    }

    public long getLastModified() {
        return file.lastModified();
    }

    public boolean setLastModified(long time) {
        return file.setLastModified(time);
    }

    public long getSize() {
        return file.length();
    }

    public boolean mkdir() {
        boolean retVal = false;
        if (isWritable()) {
            retVal = file.mkdir();
        }
        return retVal;
    }

    public boolean delete() {
        boolean retVal = false;
        if (isRemovable()) {
            retVal = file.delete();
        }
        return retVal;
    }

    public boolean move(FtpFile destination) {
        boolean retVal = false;
        if (destination.isWritable() && isReadable()) {
            File destFile = ((LocalFileSystem) destination).file;

            if (destFile.exists()) {
                // renameTo behaves differently on different platforms
                // this check verifies that if the destination already exists,
                // we fail
                retVal = false;
            } else {
                retVal = file.renameTo(destFile);
            }
        }
        return retVal;
    }

    public List<FtpFile> listFiles() {
        return null;
    }

    public OutputStream createOutputStream(long offset) throws IOException {
        // permission check
        if (!isWritable()) {
            throw new IOException("No write permission : " + file.getName());
        }

        // create output stream
        final RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(offset);
        raf.seek(offset);

        // The IBM jre needs to have both the stream and the random access file
        // objects closed to actually close the file
        return new FileOutputStream(raf.getFD()) {
            @Override
            public void close() throws IOException {
                super.close();
                raf.close();
            }
        };
    }

    public InputStream createInputStream(long offset) throws IOException {
        // permission check
        if (!isReadable()) {
            throw new IOException("No read permission : " + file.getName());
        }

        // move to the appropriate offset and create input stream
        final RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.seek(offset);

        // The IBM jre needs to have both the stream and the random access file
        // objects closed to actually close the file
        return new FileInputStream(raf.getFD()) {
            @Override
            public void close() throws IOException {
                super.close();
                raf.close();
            }
        };
    }
}
