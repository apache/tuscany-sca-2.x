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

package org.apache.tuscany.sca.binding.sca.rmi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.rmi.RMIBinding;
import org.apache.tuscany.sca.binding.rmi.RMIBindingFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * The service binding provider for the remote sca binding implementation. Relies on the
 * RMI binding for providing a remote message endpoint
 */
public class RMISCAServiceBindingProvider implements ServiceBindingProvider {

    private static final Logger logger = Logger.getLogger(RMISCAServiceBindingProvider.class.getName());

    private SCABinding binding;
    private RMIBinding rmiBinding;
    private ServiceBindingProvider serviceBindingProvider;

    private boolean started = false;

    public RMISCAServiceBindingProvider(Endpoint endpoint, ExtensionPointRegistry extensionPoints) {

        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);

        this.binding = ((DistributedSCABinding)endpoint.getBinding()).getSCABinding();

        rmiBinding = modelFactories.getFactory(RMIBindingFactory.class).createRMIBinding();
        rmiBinding.setName(this.binding.getName());

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

        rmiBinding.setURI(this.binding.getURI());

        // create a copy of the endpoint  but with the web service binding in
        RuntimeEndpoint ep = null;
        try {
            ep = (RuntimeEndpoint)endpoint.clone();
        } catch (Exception ex){
            // we know we can clone endpoint references
        }
        ep.setBinding(rmiBinding);

        ProviderFactoryExtensionPoint providerFactories = extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        BindingProviderFactory<?> providerFactory = (BindingProviderFactory<?>) providerFactories.getProviderFactory(RMIBinding.class);
        serviceBindingProvider = providerFactory.createServiceBindingProvider(ep);
        
        // Set the service binding provider so that it can be used to start/stop
        ((RuntimeEndpoint) endpoint).setBindingProvider(serviceBindingProvider);
        
        logger.info("Service using RMI SCA Binding: " + rmiBinding.getURI());
    }

    public InterfaceContract getBindingInterfaceContract() {
        return null;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        if (!started) {
            started = true;
            serviceBindingProvider.start();
        }
    }

    public void stop() {
        if (started) {
            started = false;
            serviceBindingProvider.stop();
        }
    }
}
