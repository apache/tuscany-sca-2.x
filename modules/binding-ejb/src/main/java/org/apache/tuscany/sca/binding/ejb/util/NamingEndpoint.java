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
package org.apache.tuscany.sca.binding.ejb.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class NamingEndpoint {
    private String jndiName;
    private EJBLocator locator;
    private boolean managed = true;

    public NamingEndpoint(String hostName, int port, String jndiName) {
        this.jndiName = jndiName;
        this.locator = new EJBLocator(hostName, port);
    }

    public NamingEndpoint(String name) {

        /**
         * by default it's a managed environment means SCA composite with ref
         * binding is running on an AppServer. If running on J2SE, pass
         * -Dmanaged=false for the VM
         */
        final String managedEnv = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty("managed");
            }
        });

        if (managedEnv != null) {
            managed = Boolean.valueOf(managedEnv);
        }

        if ((!managed) && name.startsWith("corbaname:iiop:")) {
            /**
             * if (name.startsWith("corbaname:iiop:")) { corbaname:iiop:<hostName>:<port>/<root>#name
             * For exmaple,
             * "corbaname:iiop:localhost:2809/NameServiceServerRoot#ejb/MyEJBHome";
             */

            String[] parts = split(name, '#');
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid corbaname: " + name);
            }    

            this.jndiName = name; // The logical jndi name
            this.locator = new EJBLocator(parts[0], managed);

        } else {
            this.jndiName = name;
            this.locator = new EJBLocator(managed);
        }

    }

    private static String[] split(String str, char ch) {
        int index = str.lastIndexOf(ch);
        if (index == -1) {
            return new String[] {str, ""};
        } else {
            return new String[] {str.substring(0, index), str.substring(index + 1)};
        }
    }

    /**
     * @return Returns the jndiName.
     */
    public String getJndiName() {
        return jndiName;
    }

    public EJBLocator getLocator() {
        return locator;
    }

    public String getCorbaname() {
        return locator.getCorbaname(jndiName);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof NamingEndpoint) {
            NamingEndpoint endpoint = (NamingEndpoint)obj;
            return jndiName.equals(endpoint.jndiName);
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return jndiName.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return jndiName;
    }
}
