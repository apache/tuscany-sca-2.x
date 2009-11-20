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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * The service binding provider for the remote sca binding implementation. Relies on the
 * binding-ws-axis implementation for providing a remote message endpoint
 *
 * @version $Rev$ $Date$
 */
public class Axis2SCAServiceBindingProvider implements ServiceBindingProvider {

    private static final Logger logger = Logger.getLogger(Axis2SCAServiceBindingProvider.class.getName());

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private SCABinding binding;

    private ServiceBindingProvider axisProvider;
    private WebServiceBinding wsBinding;

    private boolean started = false;


    public Axis2SCAServiceBindingProvider(Endpoint endpoint,
                                          ExtensionPointRegistry extensionPoints) {

        ServletHostExtensionPoint servletHosts = extensionPoints.getExtensionPoint(ServletHostExtensionPoint.class);
        ServletHost servletHost = servletHosts.getServletHosts().get(0);
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        MessageFactory messageFactory = modelFactories.getFactory(MessageFactory.class);

        this.component = (RuntimeComponent)endpoint.getComponent();
        this.service = (RuntimeComponentService)endpoint.getService();
        this.binding = ((DistributedSCABinding)endpoint.getBinding()).getSCABinding();

        // create a ws binding model
        wsBinding = modelFactories.getFactory(WebServiceBindingFactory.class).createWebServiceBinding();
        wsBinding.setName(this.binding.getName());

        URI uri = URI.create(binding.getURI());
        if (!uri.isAbsolute()) {
            int port = 8085;
            String host;
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                host = "localhost";
                logger.warning("unable to determine host address, using localhost");
            }
            ServerSocket socket;
            try {
                socket = new ServerSocket(0);
                port = socket.getLocalPort();
                // host = socket.getInetAddress().getHostAddress();
                socket.close();
            } catch (IOException e) {
            }
            String bindURI = "http://" + host + ":" + port + binding.getURI();

            // FIXME: We need to pass the full URI to the endpoint registry
            binding.setURI(bindURI);
        }

        wsBinding.setURI(this.binding.getURI());

        // Turn the java interface contract into a WSDL interface contract
        BindingWSDLGenerator.generateWSDL(component, service, wsBinding, extensionPoints, null);

        // Set to use the Axiom data binding
        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        contract.getInterface().resetDataBinding(OMElement.class.getName());

        // create a copy of the endpoint  but with the web service binding in
        RuntimeEndpoint ep = null;
        try {
            ep = (RuntimeEndpoint)endpoint.clone();
        } catch (Exception ex){
            // we know we can clone endpoint references
        }
        ep.setBinding(wsBinding);

        // create the real Axis2 service provider
        ProviderFactoryExtensionPoint providerFactories =
            extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        BindingProviderFactory providerFactory =
            (BindingProviderFactory) providerFactories.getProviderFactory(WebServiceBinding.class);
        axisProvider = providerFactory.createServiceBindingProvider(ep);

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
        if (!started) {
            return;
        } else {
            started = false;
        }

        axisProvider.stop();
    }

}
