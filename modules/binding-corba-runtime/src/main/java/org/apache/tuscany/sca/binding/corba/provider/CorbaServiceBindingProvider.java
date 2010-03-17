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

package org.apache.tuscany.sca.binding.corba.provider;

import org.apache.tuscany.sca.binding.corba.CorbaBinding;
import org.apache.tuscany.sca.binding.corba.provider.service.ComponentInvocationProxy;
import org.apache.tuscany.sca.binding.corba.provider.service.DynaCorbaServant;
import org.apache.tuscany.sca.binding.corba.provider.service.InvocationProxy;
import org.apache.tuscany.sca.binding.corba.provider.types.util.Utils;
import org.apache.tuscany.sca.host.corba.CorbaHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class CorbaServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeEndpoint endpoint;
    private CorbaBinding binding;
    private CorbaHost host;
    // private RuntimeComponentService service;
    private DynaCorbaServant servant;

    public CorbaServiceBindingProvider(RuntimeEndpoint ep, CorbaHost host) {
        this.endpoint = ep;
        // this.service = (RuntimeComponentService) ep.getService();
        this.binding = (CorbaBinding) ep.getBinding();
        this.host = host;
    }

    /**
     * @see org.apache.tuscany.sca.provider.ServiceBindingProvider#getBindingInterfaceContract()
     */
    public InterfaceContract getBindingInterfaceContract() {
        return endpoint.getBindingInterfaceContract();
    }

    /**
     * @see org.apache.tuscany.sca.provider.ServiceBindingProvider#start()
     */
    public void start() {
        try {
            Class<?> javaClass = ((JavaInterface)endpoint.getComponentTypeServiceInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(endpoint, javaClass);
            servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            servant.setIds(new String[] {binding.getId()});
            host.registerServant(binding.getCorbaname(), servant);
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }

    }

    /**
     * @see org.apache.tuscany.sca.provider.ServiceBindingProvider#stop()
     */
    public void stop() {
        try {
            host.unregisterServant(binding.getCorbaname());
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }

    }

    /**
     * @see org.apache.tuscany.sca.provider.ServiceBindingProvider#supportsOneWayInvocation()
     */
    public boolean supportsOneWayInvocation() {
        return false;
    }

}
