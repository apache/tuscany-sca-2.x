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
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;

/**
 * @version $Rev$ $Date$
 */
public class CorbaReferenceBindingProvider implements ReferenceBindingProvider {
    private CorbaBinding binding;

    /**
     * @see org.apache.tuscany.sca.provider.ReferenceBindingProvider#createInvoker(org.apache.tuscany.sca.interfacedef.Operation)
     */
    public Invoker createInvoker(Operation operation) {
        return new CorbaInvoker(binding, operation);
    }

    /**
     * @see org.apache.tuscany.sca.provider.ReferenceBindingProvider#getBindingInterfaceContract()
     */
    public InterfaceContract getBindingInterfaceContract() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.provider.ReferenceBindingProvider#start()
     */
    public void start() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.tuscany.sca.provider.ReferenceBindingProvider#stop()
     */
    public void stop() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.tuscany.sca.provider.ReferenceBindingProvider#supportsOneWayInvocation()
     */
    public boolean supportsOneWayInvocation() {
        return false;
    }

}
