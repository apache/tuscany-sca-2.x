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

package org.apache.tuscany.sca.binding.sca.provider;

import java.util.logging.Logger;

import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * The service binding provider for the remote sca binding implementation. Relies on the
 * binding-ws-axis implementation for providing a remote message endpoint
 *
 * @version $Rev$ $Date$
 */
public class DelegatingSCAServiceBindingProvider implements ServiceBindingProvider {

    private static final Logger logger = Logger.getLogger(DelegatingSCAServiceBindingProvider.class.getName());

    private ServiceBindingProvider provider;
    private RuntimeEndpoint endpoint;
    private RuntimeEndpoint mappedEndpoint;
    private boolean started = false;

    public DelegatingSCAServiceBindingProvider(RuntimeEndpoint endpoint, SCABindingMapper mapper) {
        this.endpoint = endpoint;
        this.mappedEndpoint = mapper.map(endpoint);
        if (mappedEndpoint != null) {
            provider = mappedEndpoint.getBindingProvider();
        }

    }

    public InterfaceContract getBindingInterfaceContract() {
        return provider.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return provider.supportsOneWayInvocation();
    }

    public void start() {
        if (started) {
            return;
        } else {
            provider.start();
            // Set the resolved binding URI back to the binding.sca
            endpoint.getBinding().setURI(mappedEndpoint.getBinding().getURI());
            started = true;
        }
    }

    public void stop() {
        if (!started) {
            return;
        }
        try {
            provider.stop();
        } finally {
            started = false;
        }
    }

}
