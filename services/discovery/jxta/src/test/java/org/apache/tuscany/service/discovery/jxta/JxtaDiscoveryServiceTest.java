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
package org.apache.tuscany.service.discovery.jxta;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.jxta.platform.NetworkConfigurator;

import org.apache.tuscany.host.RuntimeInfo;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 *
 */
public class JxtaDiscoveryServiceTest extends TestCase {

    public JxtaDiscoveryServiceTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testStartAndStop() {
        
        JxtaDiscoveryService discoveryService = new JxtaDiscoveryService();
        RuntimeInfo runtimeInfo = new RuntimeInfo() {
            public File getApplicationRootDirectory() {
                return null;
            }
            public URL getBaseURL() {
                return null;
            }
            public URI getDomain() {
                try {
                    return new URI("test-domain");
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
            public String getRuntimeId() {
                return "test-runtime";
            }
            public boolean isOnline() {
                return false;
            }
            
        };
        discoveryService.setRuntimeInfo(runtimeInfo);
        
        NetworkConfigurator configurator = new NetworkConfigurator();
        configurator.setPrincipal("test-user");
        configurator.setPassword("test-password");
        
        discoveryService.setConfigurator(configurator);
        discoveryService.start();
        discoveryService.stop();
        
    }

}
