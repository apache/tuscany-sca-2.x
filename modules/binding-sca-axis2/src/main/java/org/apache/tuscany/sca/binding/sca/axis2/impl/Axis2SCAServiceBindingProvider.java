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

package org.apache.tuscany.sca.binding.sca.axis2.impl;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.Axis2ServiceProvider;
import org.apache.tuscany.sca.binding.ws.axis2.Java2WSDLHelper;
import org.apache.tuscany.sca.domain.SCADomainService;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * The service binding provider for the remote sca binding implementation. Relies on the 
 * binding-ws-axis implementation for providing a remote message endpoint for this service
 * 
 * @version $Rev: 563772 $ $Date: 2007-08-08 07:50:49 +0100 (Wed, 08 Aug 2007) $
 */
public class Axis2SCAServiceBindingProvider implements ServiceBindingProvider {
    
    private final static Logger logger = Logger.getLogger(Axis2SCAServiceBindingProvider.class.getName());

    private SCANode node;
    private SCABinding binding;
    private Axis2ServiceProvider axisProvider;
    private WebServiceBinding wsBinding;
    
    private boolean started = false;


    public Axis2SCAServiceBindingProvider(SCANode node,
    		                              RuntimeComponent component,
                                          RuntimeComponentService service,
                                          DistributedSCABinding binding,
                                          ServletHost servletHost,
                                          MessageFactory messageFactory) {
    	this.node = node;
        this.binding = binding.getSCABinding();
        wsBinding = (new DefaultWebServiceBindingFactory()).createWebServiceBinding();
        
        // Turn the java interface contract into a wsdl interface contract
        InterfaceContract contract = service.getInterfaceContract();
        if ((contract instanceof JavaInterfaceContract)) {
            contract = Java2WSDLHelper.createWSDLInterfaceContract((JavaInterfaceContract)contract, null);
        }
        
        // Set to use the Axiom data binding
        contract.getInterface().setDefaultDataBinding(OMElement.class.getName());
        
        wsBinding.setBindingInterfaceContract(contract);
        wsBinding.setName(this.binding.getName()); 
        wsBinding.setURI(this.binding.getURI());
        
        axisProvider = new Axis2SCAServiceProvider(component, 
                                                   service, 
                                                   this.binding,
                                                   wsBinding,
                                                   servletHost,
                                                   messageFactory);
        

        if (node != null){
	        // get the url out of the binding and send it to the registry if
	        // a distributed domain is configured
	        SCADomainService domainService = node.getDomainService();
	        
	        if (domainService != null) {
		        // work out what the component service name is that will be registered
	            // it should be the path element of the binding uri
		        String componentServiceName = this.binding.getURI();
		        
		        try {
		            URI servicePath = new URI(this.binding.getURI());
		            componentServiceName = servicePath.getPath();
		            
		            // strip any leading slash
		            if (componentServiceName.charAt(0) == '/'){
		                componentServiceName = componentServiceName.substring(1, componentServiceName.length());
		            }
		        } catch(Exception ex) {
		            // do nothing, the binding uri string will be used
		        }
		        
		        // work out what the endpoint address is that the component service name will be registered
		        // against. Be default this is the url calculated by the web services binding but
		        // we have to adjust that to:
		        // 1. correct the host and port in the case that this is a web app as the container controlls the port
                // 2. correct the host name in the case that it's localhost		        
		        String componentServiceUrlString = wsBinding.getURI();
		        URL componentServiceUrl;
		        
		        try {
		            componentServiceUrl = new URL(componentServiceUrlString);
		        } catch (MalformedURLException ex) {
		            throw new IllegalStateException("Unable to conver url " + 
		                                            componentServiceUrlString + 
		                                            " as generated by the web service binding into a URL");
		        }
		        
	            String originalHost = componentServiceUrl.getHost();
	            String newHost = originalHost;
	            int originalPort = componentServiceUrl.getPort();
	            int newPort = originalPort;
		        
		        // TODO - could do with a change to the ServletHost API so that we can just ask the servlet
		        //        host if it is controlling the URL
		        if (servletHost.getClass().getName().equals("WebbAppServletHost")){
		            // the service URL will likely be completely different to that 
		            // calculated by the ws binding so replace it with the node url
		            // The node url will have been set via init parameters in the web app
                    URL nodeUrl = node.getNodeURL();
                    
                    if (nodeUrl != null){
                        newHost = nodeUrl.getHost();
                        newPort = nodeUrl.getPort();
                    } else {
                        throw new IllegalStateException("Node running inside a webapp and node was not created with a valid node url");
                    }
		        }
		        
		        // no good registering localhost as a host name when nodes are spread across 
		        // machines
                if ( newHost.equals("localhost")){
                    try {
                        newHost = InetAddress.getLocalHost().getHostName();
                    } catch(UnknownHostException ex) {
                        throw new IllegalStateException("Got unknown host while trying to get the local host name in order to regsiter service with the domain");
                    }		        
                }
                
		        // replace the old with the new
                componentServiceUrlString = componentServiceUrlString.replace(String.valueOf(originalPort), String.valueOf(newPort));          
                componentServiceUrlString = componentServiceUrlString.replace(originalHost, newHost);		        
		        		
		        try {
		            domainService.registerServiceEndpoint(node.getDomainURI(), 
    		                                              node.getNodeURI(), 
    		                                              componentServiceName, 
    		                                              SCABinding.class.getName(), 
    		                                              componentServiceUrlString);
                } catch(Exception ex) {
                    logger.log(Level.WARNING, 
                               "Unable to  register service: "  +
                               node.getDomainURI() + " " +
                               node.getNodeURI() + " " +
                               componentServiceName + " " +
                               SCABinding.class.getName() + " " +
                               componentServiceUrlString);
                }
	        } else {
	          /* don't think we should thrown an exception here as it
	           * may be a stand alone node
	            throw new IllegalStateException("No service manager available for component: "+
                                                component.getName() +
                                                " and service: " + 
                                                service.getName());
                  */	        	
	        }
        } else {
            throw new IllegalStateException("No distributed domain available for component: "+
                                            component.getName() +
                                            " and service: " + 
                                            service.getName());        	
        }

    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        if (started) {
            return;
        } else {
            started = true;
        }
        
        axisProvider.start();
    }

    public void stop() {
        axisProvider.stop();
    }

}
