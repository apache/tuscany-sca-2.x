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

package org.apache.tuscany.core.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.invocation.InvocationChain;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeWireImpl implements RuntimeWire {
    private Source wireSource;
    private Target wireTarget;

    private final List<InvocationChain> chains = new ArrayList<InvocationChain>();
    private final List<InvocationChain> callbackChains = new ArrayList<InvocationChain>();

    /**
     * @param source
     * @param target
     */
    public RuntimeWireImpl(Source source, Target target) {
        super();
        this.wireSource = source;
        this.wireTarget = target;
    }

    public List<InvocationChain> getCallbackInvocationChains() {
        return callbackChains;
    }

    public List<InvocationChain> getInvocationChains() {
        return chains;
    }

    public boolean isOptimizable() {
        return false;
    }

    public static class SourceImpl implements RuntimeWire.Source {
        private RuntimeComponent component;
        private RuntimeComponentReference componentReference;
        private Binding binding;
        private InterfaceContract interfaceContract;

        /**
         * @param component
         * @param componentReference
         * @param binding
         * @param interfaceContract
         */
        public SourceImpl(RuntimeComponent component,
                          RuntimeComponentReference componentReference,
                          Binding binding,
                          InterfaceContract interfaceContract) {
            super();
            this.component = component;
            this.componentReference = componentReference;
            this.binding = binding;
            this.interfaceContract = interfaceContract;
        }

        public Binding getBinding() {
            return binding;
        }

        public RuntimeComponent getComponent() {
            return component;
        }

        public RuntimeComponentReference getComponentReference() {
            return componentReference;
        }

        public InterfaceContract getInterfaceContract() {
            return interfaceContract;
        }
    }

    public static class TargetImpl implements RuntimeWire.Target {
        private RuntimeComponent component;
        private RuntimeComponentService componentService;
        private Binding binding;
        private InterfaceContract interfaceContract;

        /**
         * @param component
         * @param componentService
         * @param binding
         * @param interfaceContract
         */
        public TargetImpl(RuntimeComponent component,
                          RuntimeComponentService componentService,
                          Binding binding,
                          InterfaceContract interfaceContract) {
            super();
            this.component = component;
            this.componentService = componentService;
            this.binding = binding;
            this.interfaceContract = interfaceContract;
        }

        public Binding getBinding() {
            return binding;
        }

        public RuntimeComponent getComponent() {
            return component;
        }

        public RuntimeComponentService getComponentService() {
            return componentService;
        }

        public InterfaceContract getInterfaceContract() {
            return interfaceContract;
        }
    }

    public Source getSource() {
        return wireSource;
    }

    public Target getTarget() {
        return wireTarget;
    }

}
