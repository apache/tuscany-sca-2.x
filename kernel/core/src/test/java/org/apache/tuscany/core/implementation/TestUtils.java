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
package org.apache.tuscany.core.implementation;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;

import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;

/**
 * @version $Rev$ $Date$
 */
public final class TestUtils {
    private static final JavaInterfaceProcessorRegistry REGISTRY = new JavaInterfaceProcessorRegistryImpl();

    private TestUtils() {
    }

    public static List<InboundWire> createInboundWires(List<Class<?>> interfazes)
        throws InvalidServiceContractException {
        List<InboundWire> wires = new ArrayList<InboundWire>(interfazes.size());
        for (Class<?> interfaze : interfazes) {
            InboundWire wire = createInboundWire(interfaze);
            wires.add(wire);
        }
        return wires;
    }

    public static InboundWire createInboundWire(Class<?> interfaze) throws InvalidServiceContractException {
        InboundWire wire = new InboundWireImpl();
        JavaServiceContract contract = REGISTRY.introspect(interfaze);
        wire.setServiceContract(contract);
        return wire;
    }

    public static InboundWire createInboundWire(Class<?> interfaze, CompositeComponent parent)
        throws InvalidServiceContractException {
        InboundWire wire = createInboundWire(interfaze);
        return wire;
    }
}
