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
package org.apache.tuscany.sca.binding.ws.jaxws.ri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.jaxws.JAXWSBindingProvider;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

@WebServiceProvider
@ServiceMode(Mode.MESSAGE)
public class JAXWSServiceBindingProvider implements ServiceBindingProvider {
    
    private RuntimeEndpoint endpoint;
    private WebServiceBinding wsBinding;
    
    private JAXWSBindingProvider jaxwsBindingProvider;
    
    private Endpoint wsEndpoint;
    
    public JAXWSServiceBindingProvider(RuntimeEndpoint endpoint,
                                       ServletHost servletHost,
                                       FactoryExtensionPoint modelFactories,
                                       DataBindingExtensionPoint dataBindings, String defaultPort) {
        
        this.endpoint = endpoint;
        this.wsBinding = (WebServiceBinding)endpoint.getBinding();
        
        jaxwsBindingProvider = new JAXWSBindingProvider(endpoint, 
                                                        servletHost,
                                                        modelFactories,
                                                        dataBindings,
                                                        defaultPort);
    }

    public void start() {
        // create the JAXWS endpoint based on the provider
        wsEndpoint = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, jaxwsBindingProvider);
        
        // TODO - There is something odd in the way that service name is calculated in
        //        some circumstances
        //           sometimes getServiceName() returns null
        //           sometimes getService().getQName returns a QName namespace that doesn't match the WSDL
        //           sometimes getNamespace() returns null
        //        So here we delve directly into the WSDL4J model as the Tuscany model isn't up to date
        String targetNamespace = wsBinding.getUserSpecifiedWSDLDefinition().getDefinition().getTargetNamespace();
       
        //set up WSDL for Provider   
        List<Source> metadata = new ArrayList<Source>();
        
        // WSDL DOM seems to be null here so went with writing out
        // string version of WSDL and reading it back in again
        //Node node = wsBinding.getWSDLDefinition().getDefinition().getDocumentationElement();
        //Source source = new DOMSource(node);
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            WSDLWriter writer = WSDLFactory.newInstance().newWSDLWriter();
            writer.writeWSDL(wsBinding.getUserSpecifiedWSDLDefinition().getDefinition(), outStream);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
        //System.out.println(outStream.toString());
        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        Source source = new StreamSource(inStream);
        source.setSystemId(targetNamespace);
             
        metadata.add(source);
        
        Map<String, Object> properties = new HashMap<String, Object>();

        QName portName =  new QName(targetNamespace,
                                    wsBinding.getPort().getName());
        properties.put(Endpoint.WSDL_PORT, portName);
                      
        QName serviceName = new QName(targetNamespace,
                                      wsBinding.getService().getQName().getLocalPart());
        properties.put(Endpoint.WSDL_SERVICE, serviceName);
        
        wsEndpoint.setMetadata(metadata);
        wsEndpoint.setProperties(properties);               
       
        // Start up the endpoint
        wsEndpoint.publish(wsBinding.getURI());
        
        jaxwsBindingProvider.start();        
    }

    public void stop() {
        jaxwsBindingProvider.stop();
        wsEndpoint.stop();
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return true;
    }
}
