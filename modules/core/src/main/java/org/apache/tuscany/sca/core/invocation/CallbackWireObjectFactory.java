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

import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.spi.ObjectCreationException;
import org.apache.tuscany.sca.spi.ObjectFactory;

/**
 * Returns proxy instance for a wire callback
 *
 * @version $Rev$ $Date$
 */
public class CallbackWireObjectFactory implements ObjectFactory {
    private ProxyFactory proxyFactory;
    private Class<?> interfaze;
    private List<RuntimeWire> wires;

    public CallbackWireObjectFactory(Class<?> interfaze, ProxyFactory proxyService, List<RuntimeWire> wires) {
        this.interfaze = interfaze;
        this.proxyFactory = proxyService;
        this.wires = wires;
    }

    public Object getInstance() throws ObjectCreationException {
        return proxyFactory.createCallbackProxy(interfaze, wires);
    }

}
