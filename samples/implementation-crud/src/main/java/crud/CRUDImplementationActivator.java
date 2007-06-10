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

package crud;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.spi.ImplementationActivator;
import org.apache.tuscany.sca.spi.InvokerFactory;

import crud.backend.ResourceManager;

public class CRUDImplementationActivator implements ImplementationActivator<CRUDImplementation> {

    private static final QName IMPLEMENTATION_CRUD = new QName("http://crud", "implementation.crud");
    
    public InvokerFactory createInvokerFactory(RuntimeComponent rc, ComponentType ct, final CRUDImplementation implementation) {
        return new InvokerFactory() {
            public Invoker createInvoker(Operation operation) {
                return new CRUDInvoker(operation, new ResourceManager(implementation.getDirectory()));
            }};
    }

    public Class<CRUDImplementation> getImplementationClass() {
        return CRUDImplementation.class;
    }

    public QName getSCDLQName() {
        return IMPLEMENTATION_CRUD;
    }

}
