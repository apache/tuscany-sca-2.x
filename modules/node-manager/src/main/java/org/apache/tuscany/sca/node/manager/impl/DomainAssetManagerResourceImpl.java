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

package org.apache.tuscany.sca.node.manager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.host.http.client.HttpClientFactory;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.extensibility.NodeActivator;
import org.apache.tuscany.sca.node.extensibility.NodeExtension;
import org.apache.tuscany.sca.node.manager.DomainAssetManagerResource;
import org.apache.tuscany.sca.node.manager.ManageableResource;
import org.apache.tuscany.sca.node.manager.ManageableService;
import org.apache.tuscany.sca.node.manager.Status;
import org.oasisopen.sca.annotation.Init;

public class DomainAssetManagerResourceImpl implements NodeActivator, DomainAssetManagerResource {
    private static Map<String, NodeExtension> nodeMap = new ConcurrentHashMap<String,NodeExtension>();
    
    private HttpClientFactory httpClientFactory;

    public void nodeStarted(Node node) {
        NodeExtension nodeExtension = (NodeExtension) node;
        nodeMap.put(nodeExtension.getDomainURI(), nodeExtension);     
    }

    public void nodeStopped(Node node) {
        NodeExtension nodeExtension = (NodeExtension) node;
        nodeMap.remove(nodeExtension.getDomainURI());
    }
    
    @Init
    public void init() {
        NodeExtension node = (NodeExtension) nodeMap.values().iterator().next();
        if(node != null) {
            ExtensionPointRegistry extensionPoints =  node.getExtensionPointRegistry();
            FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
            this.httpClientFactory = HttpClientFactory.getInstance(extensionPoints);
        }
    }
    
    @Override
    public List<Status> getResourceStatus(@PathParam("domainURI") @DefaultValue("default") String domainURI) {
        if( ! nodeMap.containsKey(domainURI)) {
            throw new WebApplicationException(404);
        }
        
        NodeExtension node = nodeMap.get(domainURI);
        Composite domainComposite = node.getDomainComposite();
        
        List<Status> statuses = new ArrayList<Status>();
        for(Component component : domainComposite.getComponents()) {
            for(Service service : component.getServices()) {
                Interface interfaceContract = service.getInterfaceContract().getInterface();
                if(ManageableResource.class.getName().equals(interfaceContract.toString())) {
                    Binding binding = service.getBinding(RESTBinding.class);
                    if(binding != null) {
                        
                        Status status = new Status();
                        status.setName(component.getName());
                        status.setUri(binding.getURI());
                        
                        try {
                            Timer t = new Timer();
                            status = testResource(status);
                            status.setExecution(t.elapsed(TimeUnit.MILLISECONDS));
                            status.setStatus(Status.SUCCESS);
                        } catch (Exception e) {
                            status.setStatus(Status.FAILURE);
                            status.setStatusMessage(e.getMessage());
                        }
                        
                        statuses.add(status);
                    }
                }
            }
            
        }
        return statuses;
    }

    @Override
    public List<Status> getServiceStatus(@PathParam("domainURI") @DefaultValue("default") String domainURI) {
        if( ! nodeMap.containsKey(domainURI)) {
            throw new WebApplicationException(404);
        }

        NodeExtension node = nodeMap.get(domainURI);
        Composite domainComposite = node.getDomainComposite();

        List<Status> statuses = new ArrayList<Status>();
        for(Component component : domainComposite.getComponents()) {
            for(Service service : component.getServices()) {
                Interface interfaceContract = service.getInterfaceContract().getInterface();
                if(ManageableService.class.getName().equals(interfaceContract.toString())) {

                    Status status = new Status();
                    status.setName(component.getName());
                    status.setUri(service.getBindings().get(0).getURI());
                    
                    try {
                        String serviceName = component.getName() +"/" + service.getName();
                        ManageableService serviceInstance = node.getService(ManageableService.class, component.getName());
                        Timer t = new Timer();
                        serviceInstance.isAlive();
                        status.setExecution(t.elapsed(TimeUnit.MILLISECONDS));
                        status.setStatus(Status.SUCCESS);
                    } catch (Exception e) {
                        status.setStatus(Status.FAILURE);
                        status.setStatusMessage(e.getMessage());
                    }
                    
                    statuses.add(status);
                }
            }
        }
        
        return statuses;
    }
    
    
    private Status testResource(Status status) throws IOException {
        
        // Create an HTTP client
        HttpClient httpClient = httpClientFactory.createHttpClient();
        
        HttpGet request = new HttpGet(status.getUri() + "ping");
        //request.addHeader("Accept","application/json");
        HttpResponse response = httpClient.execute(request);
        
        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("Error invoking service at '" + request.getURI() +"' => " + response.getStatusLine().getReasonPhrase());
        }
        
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
        
        return status;
    }
    
    
    class Timer {
        Date time;
        long t;

        public Timer() {
            reset();
        }

        public void reset() {
            time = new Date();
            t = System.nanoTime();
        }

        public Date time() {
            return time; 
        }
        
        public long elapsed(TimeUnit timeUnit) {
            long elapsedTime = elapsed();
            return timeUnit.convert(elapsedTime, TimeUnit.NANOSECONDS);
        }

        public void print(String s, TimeUnit timeUnit) {
            long elapsedTime = elapsed();

            System.out.println(s + ": " + timeUnit.convert(elapsedTime, TimeUnit.NANOSECONDS)  + "ms");
        }
        
        private long elapsed() {
            return System.nanoTime() - t;
        }
    }
    
}
