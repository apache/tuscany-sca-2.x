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
package org.apache.tuscany.sca.core.invocation;

import java.util.List;

import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;

/**
 * Uses a wire to return a CallableReference
 * 
 * @version $Rev: 574648 $ $Date: 2007-09-11 18:45:36 +0100 (Tue, 11 Sep 2007) $
 */
public class CallbackReferenceObjectFactory implements ObjectFactory<CallableReference<?>> {
    private Class<?> businessInterface;
    private ProxyFactory proxyFactory;
    private List<RuntimeWire> wires;

    public CallbackReferenceObjectFactory(Class<?> interfaze, ProxyFactory proxyFactory, List<RuntimeWire> wires) {
        this.businessInterface = interfaze;
        this.proxyFactory = proxyFactory;
        this.wires = wires;
    }

    public CallableReference<?> getInstance() throws ObjectCreationException {
        return CallbackReferenceImpl.newInstance(businessInterface, proxyFactory, wires);
    }

}
