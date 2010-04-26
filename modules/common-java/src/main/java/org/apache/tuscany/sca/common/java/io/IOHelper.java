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

package org.apache.tuscany.sca.common.java.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Helper class for I/O operations
 * @tuscany.spi.extension.asclient
 */
public class IOHelper {
    
    public static InputStream openStream(URL url) throws IOException {
        // Handle file:<relative path> which strictly speaking is not a valid file URL  
        File file = toFile(url);
        if (file != null) {
            return new FileInputStream(file);
        }
        URLConnection connection = url.openConnection();
        if (connection instanceof JarURLConnection) {
            // See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5041014
            connection.setUseCaches(false);
        }
        InputStream is = connection.getInputStream();
        return is;
    }
    
    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }
    
    /**
     * Escape the space in URL string
     * @param uri
     * @return
     */
    public static URI createURI(String uri) {
        if (uri == null) {
            return null;
        }
        if (uri.indexOf('%') != -1) {
            // Avoid double-escaping
            return URI.create(uri);
        }
        int index = uri.indexOf(':');
        String scheme = null;
        String ssp = uri;
        if (index != -1) {
            scheme = uri.substring(0, index);
            ssp = uri.substring(index + 1);
        }
        try {
            return new URI(scheme, ssp, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static URI toURI(URL url) {
        if (url == null) {
            return null;
        }
        return createURI(url.toString());
    }
    
    public static URL normalize(URL url) {
        // Make sure the trailing / is added to the file directory URL so that
        // URLClassLoader can load classes from that
        try {
            File file = toFile(url);
            if (file != null) {
                return file.toURI().toURL();
            } else {
                return toURI(url).toURL();
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Returns the File object representing  the given URL.
     *
     * @param url
     * @return
     */
    public static File toFile(URL url) {
        if (url == null || !url.getProtocol().equals("file")) {
            return null;
        } else {
            String filename = url.getFile().replace('/', File.separatorChar);
            int pos = 0;
            while ((pos = filename.indexOf('%', pos)) >= 0) {
                if (pos + 2 < filename.length()) {
                    String hexStr = filename.substring(pos + 1, pos + 3);
                    char ch = (char)Integer.parseInt(hexStr, 16);
                    filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
                }
            }
            return new File(filename);
        }
    }

    /**
     * Returns the location of the classpath entry, JAR, WAR etc. containing the given class.
     *
     * @param clazz
     * @return
     */
    public static URL codeLocation(Class<?> clazz) {
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        if (url == null) {
            url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class");
        }
        return url;
    }

}
