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

package org.apache.tuscany.sca.binding.corba.impl;

import org.apache.tuscany.sca.binding.corba.CorbaBinding;
import org.apache.tuscany.sca.binding.corba.impl.service.ComponentInvocationProxy;
import org.apache.tuscany.sca.binding.corba.impl.service.DynaCorbaServant;
import org.apache.tuscany.sca.binding.corba.impl.service.InvocationProxy;
import org.apache.tuscany.sca.binding.corba.impl.types.util.Utils;
import org.apache.tuscany.sca.host.corba.CorbaHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class CorbaServiceBindingProvider implements ServiceBindingProvider {

    private CorbaBinding binding;
    private CorbaHost host;
    private RuntimeComponentService service;
    private DynaCorbaServant servant;

    public CorbaServiceBindingProvider(CorbaBinding binding, CorbaHost host, RuntimeComponentService service) {
        this.binding = binding;
        this.host = host;
        this.service = service;
    }

    /**
     * @see org.apache.tuscany.sca.provider.ServiceBindingProvider#getBindingInterfaceContract()
     */
    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    /**
     * @see org.apache.tuscany.sca.provider.ServiceBindingProvider#start()
     */
    public void start() {
        try {
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(binding), javaClass);
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
