/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.common.io.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;

/**
 *         <p/>
 *         This class is a workaround for URL stream issue as illustrated below.
 *         <p/>
 *         InputStream is=url.getInputStream();
 *         is.close(); // This line doesn't close the JAR file if the URL is a jar entry like "jar:file:/a.jar!/sca.module"
 *         <p/>
 *         We also need to turn off the JarFile cache.
 */
public class FixedURLInputStream extends InputStream {

    private JarFile jarFile;
    private InputStream is;

    /**
     * Constructor
     *
     * @param url
     * @throws IOException
     */
    public FixedURLInputStream(URL url) throws IOException {
        String protocol = url.getProtocol();
        if (protocol != null && (protocol.equals("jar") || protocol.equals("wsjar"))) {
            String urlStr = url.toString();
            if (urlStr.startsWith("wsjar:")) {
                url = new URL("jar:" + urlStr.substring(6));
            }
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            // We cannot use cache
            connection.setUseCaches(false);
            try {
                is = connection.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jarFile = connection.getJarFile();
        } else {
            is = url.openStream();
        }
    }

    /**
     * Constructor
     *
     * @param connection
     * @throws IOException
     */
    public FixedURLInputStream(JarURLConnection connection) throws IOException {
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
        if (jarFile != null)
            jarFile.close();
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