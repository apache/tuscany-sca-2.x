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
package org.apache.tuscany.sca.binding.gdata.provider;



import com.google.gdata.client.GoogleService;
import org.apache.tuscany.sca.binding.atom.AtomBinding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * Implementation of the Atom binding provider.
 *
 * @version $Rev$ $Date$
 */
class AtomReferenceBindingProvider implements ReferenceBindingProvider {

    private RuntimeComponentReference reference;
    private AtomBinding binding;
    private GoogleService myService;

    /**
     * Constructs a new AtomReferenceBindingProvider
     * @param component
     * @param reference
     * @param binding
     * @param mediator
     */
    AtomReferenceBindingProvider(RuntimeComponent component,
            RuntimeComponentReference reference,
            AtomBinding binding) {
        this.reference = reference;
        this.binding = binding;
        
        //FIXME - Handling only calendar
        this.myService = new GoogleService("cl", "");
        this.myService.setConnectTimeout(60000);
    }

    public Invoker createInvoker(Operation operation) {

        String operationName = operation.getName();
        if (operationName.equals("get")) {
            return new AtomBindingInvoker.GetInvoker(operation, binding.getURI(), myService);
        } else if (operationName.equals("post")) {
            return new AtomBindingInvoker.PostInvoker(operation, binding.getURI(), myService);
        } else if (operationName.equals("put")) {
            return new AtomBindingInvoker.PutInvoker(operation, binding.getURI(), myService);
        } else if (operationName.equals("delete")) {
            return new AtomBindingInvoker.DeleteInvoker(operation, binding.getURI(), myService);
        } else if (operationName.equals("getFeed") || operationName.equals("getAll")) {
            return new AtomBindingInvoker.GetAllInvoker(operation, binding.getURI(), myService);
        } else if (operationName.equals("postMedia")) {
            return new AtomBindingInvoker.PostMediaInvoker(operation, binding.getURI(), myService);
        } else if (operationName.equals("putMedia")) {
            return new AtomBindingInvoker.PutMediaInvoker(operation, binding.getURI(), myService);
        } else if (operationName.equals("query")) {
            return new AtomBindingInvoker.QueryInvoker(operation, binding.getURI(), myService);
        }

        return new AtomBindingInvoker(operation, binding.getURI(), myService);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public void start() {
    }

    public void stop() {
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }
}
