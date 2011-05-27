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

package implementation.lifecycle;

import helloworld.StatusImpl;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Implementation provider for Sample component implementations.
 * 
 * @version $Rev$ $Date$
 */
class LifecycleProvider implements ImplementationProvider {
    final RuntimeComponent comp;
    final LifecycleImplementation impl;
    final ProxyFactory pxf;
    final ExtensionPointRegistry ep;
    Object instance;
    
    // make this static rather than worrying about persistence on the reference side
    static Map<String, Object> asyncMessageMap = new HashMap<String, Object>();

    LifecycleProvider(final RuntimeComponent comp, final LifecycleImplementation impl, ProxyFactory pf, ExtensionPointRegistry ep) {
        this.comp = comp;
        this.impl = impl;
        this.pxf = pf;
        this.ep = ep;
    }

    public void start() {
        StatusImpl.appendStatus("Implementation start", comp.getName());
    }

    public void stop() {
        StatusImpl.appendStatus("Implementation stop", comp.getName());
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public Invoker createInvoker(final RuntimeComponentService s, final Operation op) {
        try {
            return new SampleJavaInvoker((JavaOperation)op, impl.clazz, instance);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
