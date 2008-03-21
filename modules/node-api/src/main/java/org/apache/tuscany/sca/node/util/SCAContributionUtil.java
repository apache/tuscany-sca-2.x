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

package org.apache.tuscany.sca.node.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Utility methods to find an SCA contribution from a class or a resource.
 * 
 * @version $Rev$ $Date$
 */
public class SCAContributionUtil {
    
    /**
     * Given a class this method finds the contribution that it belongs to.
     * This could be either a local directory of a jar file.
     * 
     * @param clazz
     * @return the contribution URL
     * @throws MalformedURLException
     */  
    public static URL findContributionFromClass(Class<?> clazz) throws MalformedURLException {
        return findContributionFromResource(clazz.getClassLoader(), clazz.getName().replace('.', '/') + ".class");
    }
              
    /**
     * Given the path to a resource this method finds the contribution that it belongs to
     * this could be either a local directory of a jar file.
     * 
     * @param classLoader
     * @param compositeString
     * @return the contribution URL
     * @throws MalformedURLException
     */  
    public static URL findContributionFromResource(ClassLoader classLoader, String compositeString) throws MalformedURLException {
    	   	
        URL contributionURL = classLoader.getResource(compositeString);
        
        if ( contributionURL != null ){ 
            String contributionURLString = contributionURL.toExternalForm();
            String protocol = contributionURL.getProtocol();
            
            if ("file".equals(protocol)) {
                // directory contribution
                if (contributionURLString.endsWith(compositeString)) {
                    String location = contributionURLString.substring(0, contributionURLString.lastIndexOf(compositeString));
                    // workaround from evil URL/URI form Maven
                    contributionURL = toFile(new URL(location)).toURI().toURL();
                }
    
            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = contributionURLString.substring(4, contributionURLString.lastIndexOf("!/"));
                // workaround for evil URL/URI from Maven
                contributionURL = toFile(new URL(location)).toURI().toURL();
            }
        } 
        
    	return contributionURL;
    } 

    /**
     * Convert from a <code>URL</code> to a <code>File</code>.
     * <p>
     * From version 1.1 this method will decode the URL. Syntax such as
     * <code>file:///my%20docs/file.txt</code> will be correctly decoded to
     * <code>/my docs/file.txt</code>.
     * 
     * @param url the file URL to convert, null returns null
     * @return the equivalent <code>File</code> object, or <code>null</code>
     *         if the URL's protocol is not <code>file</code>
     * @throws IllegalArgumentException if the file is incorrectly encoded
     */
    private static File toFile(URL url) {
        if (url == null || !url.getProtocol().equals("file")) {
            return null;
        } else {
            String filename = url.getFile().replace('/', File.separatorChar);
            int pos = 0;
            while ((pos = filename.indexOf('%', pos)) >= 0) { // NOPMD
                if (pos + 2 < filename.length()) {
                    String hexStr = filename.substring(pos + 1, pos + 3);
                    char ch = (char)Integer.parseInt(hexStr, 16);
                    filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
                }
            }
            return new File(filename);
        }
    }

}