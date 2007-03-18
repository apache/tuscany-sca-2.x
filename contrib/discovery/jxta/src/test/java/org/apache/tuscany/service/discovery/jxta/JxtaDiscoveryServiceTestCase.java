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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.jxta.platform.NetworkConfigurator;

import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.spi.services.discovery.RequestListener;
import org.apache.tuscany.spi.services.discovery.ResponseListener;
import org.apache.tuscany.spi.services.work.NotificationListener;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.util.stax.StaxUtil;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 *
 */
public class JxtaDiscoveryServiceTestCase extends TestCase {

    public JxtaDiscoveryServiceTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testStartAndStop() throws Exception {
        
        JxtaDiscoveryService discoveryService = getDiscoveryService("runtime-1", "domain");
        
        discoveryService.start();
        while(!discoveryService.isStarted()) {
        }
        
        RequestListener requestListener = new RequestListener() {
            public XMLStreamReader onRequest(XMLStreamReader content) {
                try {
                    return StaxUtil.createReader("<response/>");
                } catch(XMLStreamException ex) {
                    throw new JxtaException(ex);
                }
            }            
        };
        
        ResponseListener responseListener = new ResponseListener() {
            public void onResponse(XMLStreamReader content, int messageId) {
            }
            
        };
        
        discoveryService.registerRequestListener(new QName("request"), requestListener);
        discoveryService.registerResponseListener(new QName("response"), responseListener);
        
        XMLStreamReader reader = StaxUtil.createReader("<request/>");
        discoveryService.sendMessage(null, reader);
        reader.close();
        
    }
    
    private JxtaDiscoveryService getDiscoveryService(final String runtimeId, final String domain) {
        
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
                    return new URI(domain);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
            public String getRuntimeId() {
                return runtimeId;
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
        discoveryService.setWorkScheduler(new WorkScheduler() {
            public <T extends Runnable> void scheduleWork(final T work, final NotificationListener<T> listener) {
                new Thread() {
                    public void run() {
                        try {
                            work.run();
                        } catch(Exception ex) {
                            listener.workFailed(work, ex);
                        }
                    }
                }.start();
            }
            public <T extends Runnable> void scheduleWork(final T work) {
                new Thread() {
                    public void run() {
                        work.run();
                    }
                }.start();
            }            
        });
        return discoveryService;
        
    }

}
