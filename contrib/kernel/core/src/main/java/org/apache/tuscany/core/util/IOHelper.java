/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;

public class IOHelper {
    /**
     * The default buffer size to use.
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    
    protected IOHelper() {
        
    }

    /**
     * Unconditionally close an <code>InputStream</code>.
     * <p>
     * Equivalent to {@link InputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param input  the InputStream to close, may be null or already closed
     */
    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
    
    /**
     * Unconditionally close an <code>OutputStream</code>.
     * <p>
     * Equivalent to {@link OutputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param output  the OutputStream to close, may be null or already closed
     */
    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
    
    /**
     * Copy bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) { // NOPMD
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    public static InputStream getInputStream(URL url) throws IOException {
        return new SafeURLInputStream(url);
    }
    
    /**
     * This class is a workaround for URL stream issue as illustrated below.
     * InputStream is=url.getInputStream(); is.close(); // This line doesn't close
     * the JAR file if the URL is a jar entry like "jar:file:/a.jar!/my.composite" We
     * also need to turn off the JarFile cache.
     * 
     * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4950148
     * 
     * @version $Rev$ $Date$
     */
    public static class SafeURLInputStream extends InputStream {
        private JarFile jarFile;
        private InputStream is;

        public SafeURLInputStream(URL url) throws IOException {
            String protocol = url.getProtocol();
            if (protocol != null && (protocol.equals("jar"))) {
                JarURLConnection connection = (JarURLConnection)url.openConnection();
                // We cannot use cache
                connection.setUseCaches(false);
                try {
                    is = connection.getInputStream();
                } catch (IOException e) {
                    throw e;
                }
                jarFile = connection.getJarFile();
            } else {
                is = url.openStream();
            }
        }

        public SafeURLInputStream(JarURLConnection connection) throws IOException {
            // We cannot use cache
            connection.setUseCaches(false);
            is = connection.getInputStream();
            jarFile = connection.getJarFile();
        }

        public int available() throws IOException {
            return is.available();
        }

        public void close() throws IOException {
            is.close();
            // We need to close the JAR file
            if (jarFile != null) {
                jarFile.close();
            }
        }

        public synchronized void mark(int readlimit) {
            is.mark(readlimit);
        }

        public boolean markSupported() {
            return is.markSupported();
        }

        public int read() throws IOException {
            return is.read();
        }

        public int read(byte[] b, int off, int len) throws IOException {
            return is.read(b, off, len);
        }

        public int read(byte[] b) throws IOException {
            return is.read(b);
        }

        public synchronized void reset() throws IOException {
            is.reset();
        }

        public long skip(long n) throws IOException {
            return is.skip(n);
        }
    }   
}