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

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Implementations are responsible for bridging invocation chains as an assembly is converted to runtime artifacts
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Connector {

    /**
     * Connects the given artifact to a target in its composite
     *
     * @param source the source artifact to context, i.e. a <code>Service</code>, <code>Component</code>, or
     *               <code>Reference</code>
     */
    void connect(SCAObject source);

    /**
     * Bridges the invocation chains associated with an inbound and outbound wire.
     *
     * @param inboundWire  the wire to bridge from
     * @param outboundWire the target wire
     * @param optimizable  if the bridge may be optimized
     * @throws BuilderConfigException
     */
    void connect(InboundWire inboundWire, OutboundWire outboundWire, boolean optimizable)
        throws BuilderConfigException;

}
