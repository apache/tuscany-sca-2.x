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

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * The sca service binding provider mediates between the twin requirements of
 * local sca bindings and remote sca bindings. In the local case is does
 * very little. When the sca binding model is set as being remote this binding will
 * try and create a remote service endpoint for remote references to connect to
 *
 * @version $Rev$ $Date$
 */
public class RuntimeSCAServiceBindingProvider implements ServiceBindingProvider {

    private ServiceBindingProvider distributedProvider;
    private SCABindingMapper scaBindingMapper;

    public RuntimeSCAServiceBindingProvider(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint) {
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.scaBindingMapper = utilities.getUtility(SCABindingMapper.class);

        this.distributedProvider = new DelegatingSCAServiceBindingProvider(endpoint, scaBindingMapper);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return distributedProvider.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return distributedProvider.supportsOneWayInvocation();
    }

    public void start() {
        distributedProvider.start();
    }

    public void stop() {
        distributedProvider.stop();
    }

}
