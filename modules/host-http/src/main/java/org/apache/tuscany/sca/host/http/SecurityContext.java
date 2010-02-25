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

package org.apache.tuscany.sca.host.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A class to store policy context to enable Security QoS to 
 * HTTP binding  
 */
public class SecurityContext {
    private boolean isSSLEnabled = false;
    private Properties sslProperties;
    
    private boolean isAuthenticationEnabled = false;
    private List<UserContext> users = new ArrayList<UserContext>(); 
    
    public boolean isSSLEnabled() {
        return isSSLEnabled;
    }
    
    public void setSSLEnabled(boolean value) {
        this.isSSLEnabled = value;
    }
    
    public Properties getSSLProperties() {
        return sslProperties;
    }
    
    public void setSSLProperties(Properties sslProperties) {
        this.sslProperties = sslProperties;
    }
    
    public boolean isAuthenticationEnabled() {
        return this.isAuthenticationEnabled;
    }
    
    public void setAuthenticationEnabled(boolean value) {
        this.isAuthenticationEnabled = value;
    }
    
    public List<UserContext> getUsers() {
        return this.users;
    }
}
