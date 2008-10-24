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

package org.apache.tuscany.sca.binding.gdata.provider;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.tuscany.sca.binding.gdata.GdataBinding;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

import com.google.gdata.client.GoogleService;
import com.google.gdata.util.AuthenticationException;

/**
 * Implementation of the Atom binding provider.
 * 
 * @version $Rev$ $Date$
 */
class GdataReferenceBindingProvider implements ReferenceBindingProvider {

    private RuntimeComponentReference reference;
    private GdataBinding binding;
    private String authorizationHeader;
    private GoogleService googleService;
    private HttpClient httpClient;
    private Mediator mediator;
    private DataType<?> itemClassType;
    private DataType<?> itemXMLType;
    private boolean supportsFeedEntries;

    /**
     * Constructs a new AtomReferenceBindingProvider
     * 
     * @param component
     * @param reference
     * @param binding
     * @param mediator
     * @throws AuthenticationException 
     */
    GdataReferenceBindingProvider(RuntimeComponent component,
                                  RuntimeComponentReference reference,
                                  GdataBinding binding,
                                  Mediator mediator) throws AuthenticationException {
        this.reference = reference;
        this.binding = binding;
        this.mediator = mediator;

        // Prepare authorization header
        String authorization = "admin" + ":" + "admin";
        authorizationHeader = "Basic " + new String(Base64.encodeBase64(authorization.getBytes()));
        
        // Prepare gdata header
        String serviceType = binding.getServiceType();
        String usernane = binding.getUsername();
        String password = binding.getPassword();
        
        System.out.println("[Debug Info] binding.getServiceType()" + serviceType);
        System.out.println("[Debug Info] binding.getName()" +usernane);
        System.out.println("[Debug Info] binding.getPassword()" + password);    
        
        googleService = new GoogleService(serviceType, "");
        if(binding.getUsername().equals("admin") == false && binding.getPassword().equals("admin")==false){
            googleService.setUserCredentials(binding.getUsername(),binding.getPassword());     
        }

        // Create an HTTP client
        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(10);
        connectionManager.getParams().setConnectionTimeout(60000);
        httpClient = new HttpClient(connectionManager);
    }

    public Invoker createInvoker(Operation operation) {

        String operationName = operation.getName();

        if (operationName.equals("get")) {

            // Determine the collection item type
            itemXMLType = new DataTypeImpl<Class<?>>(String.class.getName(), String.class, String.class);
            Class<?> itemClass = operation.getOutputType().getPhysical();
            DataType<XMLType> outputType = operation.getOutputType();
            QName qname = outputType.getLogical().getElementName();
            qname = new QName(qname.getNamespaceURI(), itemClass.getSimpleName());
            itemClassType = new DataTypeImpl<XMLType>("java:complexType", itemClass, new XMLType(qname, null));

            if (itemClassType.getPhysical() == com.google.gdata.data.Entry.class) {
                supportsFeedEntries = true;
            }

            String entryURI = this.binding.getURI();
            if(entryURI.contains("picasaweb.google.com/data/feed/api/")){
            	entryURI = entryURI.replace("picasaweb.google.com/data/feed/api/", "picasaweb.google.com/data/entry/api/");
            }

            return new GdataBindingInvoker.GetInvoker(operation, entryURI, googleService, httpClient, authorizationHeader, this);

        } else if (operationName.equals("post")) {

            return new GdataBindingInvoker.PostInvoker(operation, binding.getURI(), googleService, httpClient, authorizationHeader, this);

        } else if (operationName.equals("put")) {

            String entryURI = this.binding.getURI();
            if(entryURI.contains("picasaweb.google.com/data/feed/api/")){
            	entryURI = entryURI.replace("picasaweb.google.com/data/feed/api/", "picasaweb.google.com/data/entry/api/");
            }

            return new GdataBindingInvoker.PutInvoker(operation, entryURI,  googleService, httpClient, authorizationHeader, this);
            
        } else if (operationName.equals("delete")) {

        	String entryURI = this.binding.getURI();
            if(entryURI.contains("picasaweb.google.com/data/feed/api/")){
            	entryURI = entryURI.replace("picasaweb.google.com/data/feed/api/", "picasaweb.google.com/data/entry/api/");
            }
            return new GdataBindingInvoker.DeleteInvoker(operation, entryURI,  googleService, httpClient, authorizationHeader, this);
            
        } else if (operationName.equals("getFeed") || operationName.equals("getAll")) {

            return new GdataBindingInvoker.GetAllInvoker(operation, binding.getURI(),  googleService, httpClient, authorizationHeader, this);

        } else if (operationName.equals("postMedia")) {
            
        	return new GdataBindingInvoker.PostMediaInvoker(operation, binding.getURI(),  googleService, httpClient, authorizationHeader, this);
        	
        } else if (operationName.equals("putMedia")) {
        	
            return new GdataBindingInvoker.PutMediaInvoker(operation, binding.getURI(),  googleService, httpClient, authorizationHeader, this);
            
        } else if (operationName.equals("query")) {
        	
            return new GdataBindingInvoker.QueryInvoker(operation, binding.getURI(),  googleService, httpClient, authorizationHeader, this);
        }

        return new GdataBindingInvoker(operation, binding.getURI(),  googleService, httpClient, authorizationHeader, this);
    }
    
    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public void start() {
        // Configure the HTTP client credentials
        Credentials credentials = new UsernamePasswordCredentials("admin", "admin");
        httpClient.getParams().setAuthenticationPreemptive(true);
        URI uri = URI.create(binding.getURI());
        httpClient.getState().setCredentials(new AuthScope(uri.getHost(), uri.getPort()), credentials);
    }

    public void stop() {

    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    /**
     * Returns the mediator.
     * 
     * @return
     */
    Mediator getMediator() {
        return mediator;
    }

    /**
     * Returns the item class type.
     * 
     * @return
     */
    DataType<?> getItemClassType() {
        return itemClassType;
    }

    /**
     * Returns the item XML type.
     * 
     * @return
     */
    DataType<?> getItemXMLType() {
        return itemXMLType;
    }

    /**
     * Returns true if the invoker should work with Atom feed entries.
     * 
     * @return
     */
    boolean supportsFeedEntries() {
        return supportsFeedEntries;
    }

}
