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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
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
                                       DataBindingExtensionPoint dataBindings) {
        
        this.endpoint = endpoint;
        this.wsBinding = (WebServiceBinding)endpoint.getBinding();
        
        jaxwsBindingProvider = new JAXWSBindingProvider(endpoint, 
                                                        servletHost,
                                                        modelFactories,
                                                        dataBindings);
    }

    public void start() {
        jaxwsBindingProvider.start();
        wsEndpoint = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, jaxwsBindingProvider);

/* TODO - set up WSDL for Provider   
        List<Source> metadata = new ArrayList<Source>();
        wsEndpoint.setMetadata(metadata);
       
        Map<String, Object> properties = new HashMap<String, Object>();
        wsEndpoint.setProperties(properties);
*/        
        
        wsEndpoint.publish(wsBinding.getURI());
    }

    public void stop() {
        jaxwsBindingProvider.stop();
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return true;
    }
}
