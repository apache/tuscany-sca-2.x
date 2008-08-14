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


/**
 * @version $Rev$ $Date$
 * Various utilities for host-corba
 */
public class CorbaHostUtils {

    /**
     * Tests if given URI is valid corbaname string
     * @param uri
     * @return
     */
    public static boolean isValidCorbanameURI(String uri) {
        return uri != null && uri.startsWith("corbaname:") && uri.contains("#") && uri.indexOf('#') < uri.length() - 1;
    }

    /**
     * Creates corbaname URI basing on given parameters
     * @param host
     * @param port
     * @param name
     * 
     * @return
     */
    public static String createCorbanameURI(String host, int port, String name) {
        return new CorbanameURL(host, port, name).toString();
    }

    /**
     * Translates corbaname URI to CorbanameDetails instances
     * 
     * @param uri
     * @return
     */
    public static CorbanameURL getServiceDetails(String uri) {
        CorbanameURL details = new CorbanameURL(uri);
        return details;
    }

}
