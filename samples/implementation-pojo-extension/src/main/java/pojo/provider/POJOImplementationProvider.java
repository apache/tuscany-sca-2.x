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
package pojo.provider;

import java.lang.reflect.Method;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.osoa.sca.ServiceRuntimeException;

import pojo.POJOImplementation;

/**
 * An implementation provider for sample CRUD implementations.
 * 
 * The implementation provider is responsible for handling the lifecycle of a component
 * implementation and creating operation invokers for the service operations provided
 * by the implementation.
 * 
 * The start() and stop() methods are called when a component is started
 * and stopped. In this example we are using that opportunity to call init and destroy methods
 * on the POJO instance if these methods exist.
 *
 * The createInvoker method is called for each operation provided by the component
 * implementation. The implementation provider can create an invoker and initialize it
 * at that time to minimize the amount of work to be performed on each invocation.
 * 
 * For example here we are looking up the Java method corresponding to the service operation
 * at passing it to the invoker constructor. This way the invoker won't have to lookup the Java
 * method on each invocation.  
 */
class POJOImplementationProvider implements ImplementationProvider {
    
    private POJOImplementation implementation;
    private Object pojoInstance;

    /**
     * Constructs a new CRUD implementation.
     */
    POJOImplementationProvider(RuntimeComponent component, POJOImplementation implementation) {
        this.implementation = implementation;
        
        // Create a new instance of the POJO
        try {
            pojoInstance = implementation.getPOJOClass().newInstance();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public void start() {
        try {
            // Invoke the POJO's init method
            Method initMethod = implementation.getMethods().get("init");
            if (initMethod != null) {
                initMethod.invoke(pojoInstance);
            }
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public void stop() {
        try {
            // Invoke the POJO's destroy method
            Method destroyMethod = implementation.getMethods().get("destroy");
            if (destroyMethod != null) {
                destroyMethod.invoke(pojoInstance);
            }
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        } finally {
            pojoInstance = null;
        }
    }
    
    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        Method method = implementation.getMethods().get(operation.getName()); 
        POJOImplementationInvoker invoker = new POJOImplementationInvoker(pojoInstance, operation, method);
        return invoker;
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }

}
