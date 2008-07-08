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

package org.apache.tuscany.sca.host.corba;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Various utilities for host-corba
 */
public class CorbaHostUtils {

    public static String DEFAULT_NAME_SERVICE = "NameService";
    public static String DEFAULT_HOST = "localhost";
    public static int DEFAULT_PORT = 900;

    /**
     * Tests if given URI is valid corbaname string
     * @param uri
     * @return
     */
    public static boolean isValidCorbanameURI(String uri) {
        return uri != null && uri.startsWith("corbaname") && uri.contains("#") && uri.indexOf('#') < uri.length() - 1;
    }

    /**
     * Creates corbaname URI basing on given parameters
     * 
     * @param name
     * @param host
     * @param port
     * @return
     */
    public static String createCorbanameURI(String name, String host, int port) {
        String portStr = null;
        if (port > 0) {
            portStr = Integer.toString(port);
        } else {
            portStr = "";
        }
        if (host == null) {
            host = "";
        }
        return "corbaname::" + host + ":" + portStr + "#" + name;
    }

    /**
     * Translates corbaname URI to CorbanameDetails instances
     * 
     * @param uri
     * @return
     */
    public static CorbanameDetails getServiceDetails(String uri) {
        CorbanameDetails details = new CorbanameDetails();
        StringTokenizer hashTokenizer = new StringTokenizer(uri, "#");
        if (hashTokenizer.countTokens() == 2) {
            String serviceLocation = hashTokenizer.nextToken();
            String servicePath = hashTokenizer.nextToken();
            StringTokenizer pathDisc = new StringTokenizer(servicePath, "/");
            List<String> namePath = new ArrayList<String>();
            while (pathDisc.hasMoreTokens()) {
                namePath.add(pathDisc.nextToken());
            }
            details.setNamePath(namePath);
            StringTokenizer slashTokenizer = new StringTokenizer(serviceLocation, "/");
            String serviceHost = slashTokenizer.nextToken();
            String nameService = null;
            if (slashTokenizer.hasMoreTokens()) {
                nameService = slashTokenizer.nextToken();
            } else {
                nameService = DEFAULT_NAME_SERVICE;
            }
            details.setNameService(nameService);
            StringTokenizer colonTokenizer = new StringTokenizer(serviceHost, ":", true);
            if (colonTokenizer.countTokens() > 0 && colonTokenizer.nextToken().equals("corbaname")) {
                String host = null;
                int port = 0;
                try {
                    colonTokenizer.nextToken();
                    String methodPart = colonTokenizer.nextToken();
                    if (!methodPart.equals(":")) {
                        colonTokenizer.nextToken();
                    }
                    host = colonTokenizer.nextToken();
                    if (host.equals(":")) {
                        // no host provided, no need to get another ":"
                        host = DEFAULT_HOST;
                    } else {
                        // host provided, so another ":" should be read
                        colonTokenizer.nextToken();
                    }
                    try {
                        port = Integer.parseInt(colonTokenizer.nextToken());
                    } catch (Exception e) {
                        port = DEFAULT_PORT;
                    }
                } catch (NoSuchElementException e) {
                } finally {
                    if (host == null) {
                        // parsing failed - user didn't provide host
                        host = DEFAULT_HOST;
                    }
                    if (port == 0) {
                        // parsing failed - user didn't provide port
                        port = DEFAULT_PORT;
                    }
                }
                details.setHost(host);
                details.setPort(port);
            } else {
                throw new IllegalArgumentException("Given corbaname URI does not begin with 'corbaname'");
            }
        } else {
            throw new IllegalArgumentException("Given corbaname is missing hash separator");
        }
        return details;
    }

}
