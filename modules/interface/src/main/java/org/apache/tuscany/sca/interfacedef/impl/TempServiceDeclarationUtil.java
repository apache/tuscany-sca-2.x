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

package org.apache.tuscany.sca.interfacedef.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class TempServiceDeclarationUtil {

    /**
     * Read the service name from a configuration file
     * 
     * @param classLoader
     * @param name The name of the service class
     * @return A class name which extends/implements the service class
     * @throws IOException
     */
    public static Set<String> getServiceClassNames(ClassLoader classLoader, String name) throws IOException {
        Set<String> set = new HashSet<String>();
        Enumeration<URL> urls = classLoader.getResources("META-INF/services/" + name);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            Set<String> service = getServiceClassNames(url);
            if (service != null) {
                set.addAll(service);
    
            }
        }
        return set;
    }

    private static Set<String> getServiceClassNames(URL url) throws IOException {
        Set<String> names = new HashSet<String>();
        InputStream is = url.openStream();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (!line.startsWith("#") && !"".equals(line)) {
                    names.add(line.trim());
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
    
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    //ignore
                }
            }
        }
        return names;
    }

    /**
     * Parse a service declaration in the form class;attr=value,attr=value and
     * return a map of attributes
     * 
     * @param declaration
     * @return a map of attributes
     */
    public static Map<String, String> parseServiceDeclaration(String declaration) {
        Map<String, String> attributes = new HashMap<String, String>();
        StringTokenizer tokens = new StringTokenizer(declaration);
        String className = tokens.nextToken(";");
        if (className != null) {
            attributes.put("class", className);
        }
        for (;;) {
            if (!tokens.hasMoreTokens())
                break;
            String key = tokens.nextToken("=").substring(1);
            if (key == null)
                break;
            String value = tokens.nextToken(",").substring(1);
            if (value == null)
                break;
            attributes.put(key, value);
        }
        return attributes;
    }

}
