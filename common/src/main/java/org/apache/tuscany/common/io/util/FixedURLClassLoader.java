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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.Permission;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
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
public class FixedURLClassLoader extends URLClassLoader {

    /**
     * @param urls
     * @param parent
     */
    public FixedURLClassLoader(URL[] urls, ClassLoader parent) {
        super(normalizeURLs(urls), parent, new FixedURLStreamHandlerFactory());
    }

    private static URL[] normalizeURLs(URL[] urls) {
        URL[] newURLs = new URL[urls.length];
        for (int i = 0; i < urls.length; i++)
            try {
                /**
                 * Any URL that ends with a '/' is assumed to refer to a directory. Otherwise, the URL is assumed to
                 * refer to a JAR file which will be downloaded and opened as needed.
                 */
                String spec = urls[i].toString();
                if (!(urls[i].getProtocol().equals("jar") || urls[i].getFile().endsWith("/"))) {
                    spec = "jar:" + spec + "!/";
                }
                newURLs[i] = new URL(null, spec, FixedURLStreamHandler.instance);
            } catch (MalformedURLException e) {
            }
        return newURLs;
    }

    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public static class FixedURLStreamHandlerFactory implements URLStreamHandlerFactory {
        public URLStreamHandler createURLStreamHandler(String protocol) {
            return FixedURLStreamHandler.instance;
        }
    }

    public static class FixedURLStreamHandler extends URLStreamHandler {
        public static final URLStreamHandler instance = new FixedURLStreamHandler();

        public FixedURLStreamHandler() {
            super();
        }

        protected URLConnection openConnection(URL url) throws IOException {
            URLConnection connection = new URL(url.toString()).openConnection();
            connection.setUseCaches(false);
            return new FixedURLConnection(connection, url);
        }
    }

    public static class FixedURLConnection extends URLConnection {
        private URLConnection connection;

        public FixedURLConnection(URLConnection connection, URL url) {
            super(url);
            this.connection = connection;
        }

        public void addRequestProperty(String key, String value) {
            connection.addRequestProperty(key, value);
        }

        public void connect() throws IOException {
            connection.connect();
        }

        public boolean getAllowUserInteraction() {
            return connection.getAllowUserInteraction();
        }

        public Object getContent() throws IOException {
            return connection.getContent();
        }

        public Object getContent(Class[] classes) throws IOException {
            return connection.getContent(classes);
        }

        public String getContentEncoding() {
            return connection.getContentEncoding();
        }

        public int getContentLength() {
            return connection.getContentLength();
        }

        public String getContentType() {
            return connection.getContentType();
        }

        public long getDate() {
            return connection.getDate();
        }

        public boolean getDefaultUseCaches() {
            return connection.getDefaultUseCaches();
        }

        public boolean getDoInput() {
            return connection.getDoInput();
        }

        public boolean getDoOutput() {
            return connection.getDoOutput();
        }

        public long getExpiration() {
            return connection.getExpiration();
        }

        public String getHeaderField(int n) {
            return connection.getHeaderField(n);
        }

        public String getHeaderField(String name) {
            return connection.getHeaderField(name);
        }

        public long getHeaderFieldDate(String name, long Default) {
            return connection.getHeaderFieldDate(name, Default);
        }

        public int getHeaderFieldInt(String name, int Default) {
            return connection.getHeaderFieldInt(name, Default);
        }

        public String getHeaderFieldKey(int n) {
            return connection.getHeaderFieldKey(n);
        }

        public Map<String, List<String>> getHeaderFields() {
            return connection.getHeaderFields();
        }

        public long getIfModifiedSince() {
            return connection.getIfModifiedSince();
        }

        public InputStream getInputStream() throws IOException {
            if (connection instanceof JarURLConnection && url.toString().startsWith("jar:file:")) {
                return getByteArrayInputStream();
                // return new FixedURLInputStream((JarURLConnection) connection);
            } else {
                return connection.getInputStream();
            }
        }

        private InputStream getByteArrayInputStream() throws IOException {
            JarFile jFile = null;
            try {
                String spec = url.toString();
                spec = spec.substring("jar:".length());
                int index = spec.lastIndexOf("!/");
                String file = new URL(spec.substring(0, index)).getFile();
                jFile = new JarFile(file);
                String entryName = spec.substring(index + 2);
                JarEntry jarEntry = jFile.getJarEntry(entryName);
                if (jarEntry != null) {
                    InputStream jarStream = null;
                    try {
                        jarStream = jFile.getInputStream(jarEntry);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte buf[] = new byte[4096];
                        int length;
                        length = jarStream.read(buf);
                        while (length > 0) {
                            out.write(buf, 0, length);
                            length = jarStream.read(buf);
                        }
                        // out.flush();
                        jarStream.close();
                        // out.close();
                        return new ByteArrayInputStream(out.toByteArray());
                    } catch (IOException e) {
                        if (jarStream != null)
                            jarStream.close();
                        throw e;
                    }
                } else {
                    throw new IOException("Entry " + entryName + " is not found in " + file);
                }
            } catch (IOException ex) {
                throw ex;
            } finally {
                if (jFile != null) {
                    try {
                        jFile.close();
                    } catch (IOException e) {
                        // Ignore it
                    }
                }
            }
        }

        public long getLastModified() {
            return connection.getLastModified();
        }

        public OutputStream getOutputStream() throws IOException {
            return connection.getOutputStream();
        }

        public Permission getPermission() throws IOException {
            return connection.getPermission();
        }

        public Map<String, List<String>> getRequestProperties() {
            return connection.getRequestProperties();
        }

        public String getRequestProperty(String key) {
            return connection.getRequestProperty(key);
        }

        public URL getURL() {
            return url;
        }

        public boolean getUseCaches() {
            return connection.getUseCaches();
        }

        public int hashCode() {
            return connection.hashCode();
        }

        public void setAllowUserInteraction(boolean allowuserinteraction) {
            connection.setAllowUserInteraction(allowuserinteraction);
        }

        public void setDefaultUseCaches(boolean defaultusecaches) {
            connection.setDefaultUseCaches(defaultusecaches);
        }

        public void setDoInput(boolean doinput) {
            connection.setDoInput(doinput);
        }

        public void setDoOutput(boolean dooutput) {
            connection.setDoOutput(dooutput);
        }

        public void setIfModifiedSince(long ifmodifiedsince) {
            connection.setIfModifiedSince(ifmodifiedsince);
        }

        public void setRequestProperty(String key, String value) {
            connection.setRequestProperty(key, value);
        }

        public void setUseCaches(boolean usecaches) {
            connection.setUseCaches(usecaches);
        }

        public String toString() {
            return connection.toString();
        }

    }

}