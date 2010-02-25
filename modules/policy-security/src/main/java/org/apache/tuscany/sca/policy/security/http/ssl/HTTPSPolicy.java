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

package org.apache.tuscany.sca.policy.security.http.ssl;

import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.xml.Constants;

/**
 * Models the SCA Implementation Security Policy Assertion for Confidentiality.
 * 
 * This would map to enabling SSL communication and would require 
 * the following configuration items :
 * 
 * - javax.net.ssl.keyStore
 * - javax.net.ssl.keyStorePassword
 * - javax.net.ssl.keyStoreType
 *
 * - javax.net.ssl.trustStoreType
 * - javax.net.ssl.trustStore
 * - javax.net.ssl.trustStorePassword
 * 
 * @version $Rev$ $Date$
 */
public class HTTPSPolicy {
    public static final QName NAME = new QName(Constants.SCA11_TUSCANY_NS, "https");

    private String trustStoreType;
    private String trustStore;
    private String trustStorePassword;
    
    private String keyStoreType;
    private String keyStore;
    private String keyStorePassword;
    
    
    public String getTrustStoreType() {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }
    
    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }
    
    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public QName getSchemaName() {
        return NAME;
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        
    }
    
    public Properties toProperties() {
        Properties properties = new Properties();
        
        properties.put("javax.net.ssl.trustStoreType", trustStoreType);
        properties.put("javax.net.ssl.trustStore", trustStore);
        properties.put("javax.net.ssl.trustStorePassword", trustStorePassword);
        
        properties.put("javax.net.ssl.keyStoreType", keyStoreType);
        properties.put("javax.net.ssl.keyStore", keyStore);
        properties.put("javax.net.ssl.keyStorePassword", keyStorePassword);
        
        return properties;
    }

}
