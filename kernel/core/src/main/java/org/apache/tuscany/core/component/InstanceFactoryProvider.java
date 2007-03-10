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
package org.apache.tuscany.core.component;

import java.util.List;

import org.apache.tuscany.spi.wire.Wire;

/**
 * @version $Rev$ $Date$
 */
public interface InstanceFactoryProvider<T> {
    /**
     * Attach the outbound wire for a single-valued reference.
     *
     * @param wire the outbound wire to attach
     */
    void attachWire(Wire wire);

    /**
     * Attach the outbound wires for a multi-valued reference.
     *
     * @param wires the outbound wires to attach
     */
    void attachWires(List<Wire> wires);

    /**
     * Create an instance factory that can be used to create component instances.
     *
     * @return a new instance factory
     */
    InstanceFactory<T> createFactory();
}
