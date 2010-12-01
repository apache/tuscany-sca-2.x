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

package sampleasync.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLOperation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationAsyncProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Implementation provider for Sample component implementations.
 * 
 * @version $Rev$ $Date$
 */
class SampleAsyncProvider implements ImplementationAsyncProvider {
    final RuntimeComponent comp;
    final SampleAsyncImplementation impl;
    final ProxyFactory pxf;
    final ExtensionPointRegistry ep;
    Object instance;
    Map<String, Object> asyncMessageMap = new HashMap<String, Object>();

    SampleAsyncProvider(final RuntimeComponent comp, final SampleAsyncImplementation impl, ProxyFactory pf, ExtensionPointRegistry ep) {
        this.comp = comp;
        this.impl = impl;
        this.pxf = pf;
        this.ep = ep;
    }

    public void start() {
        // Construct implementation instance and inject reference proxies
        try {
            instance = impl.clazz.newInstance();

            for(ComponentReference r: comp.getReferences()) {
                final Field f = impl.clazz.getDeclaredField(r.getName());
                f.setAccessible(true);
                // Inject a Java or WSDLReference proxy
                final Interface i = r.getInterfaceContract().getInterface();
                if(i instanceof JavaInterface)
                    f.set(instance, pxf.createProxy(comp.getComponentContext().getServiceReference(f.getType(), r.getName())));
                else
                    f.set(instance, new SampleWSDLProxy(asyncMessageMap, r.getEndpointReferences().get(0), i, ep));
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        instance = null;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public Invoker createInvoker(final RuntimeComponentService s, final Operation op) {
        try {
            // Creating an invoker for a Java or WSDL-typed implementation
            if(op instanceof JavaOperation)
                return new SampleJavaInvoker((JavaOperation)op, impl.clazz, instance);
            return new SampleWSDLInvoker((WSDLOperation)op, impl.clazz, instance);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public Invoker createAsyncResponseInvoker(RuntimeComponentService service, Operation operation) {
        return new SampleAsyncResponseInvoker(asyncMessageMap, operation, impl.clazz, instance);
    }
}
