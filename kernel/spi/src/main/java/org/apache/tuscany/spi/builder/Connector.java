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
package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.physical.PhysicalWireDefinition;

/**
 * Implementations are responsible for creating a wires between source and target artifacts
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Connector {

    /**
     * Recursively connects component references and its children. This method will eventially be replaced by one that
     * just takes a WireDefinition
     *
     * @param definition the component definition to connect
     * @throws WiringException
     * @deprecated
     */
    void connect(ComponentDefinition<? extends Implementation<?>> definition) throws WiringException;

    /**
     * Placeholder for the connect operation using federated deployment
     *
     * @param definition metadata describing the wire to create
     * @throws WiringException
     */
    void connect(PhysicalWireDefinition definition) throws BuilderException;

}
