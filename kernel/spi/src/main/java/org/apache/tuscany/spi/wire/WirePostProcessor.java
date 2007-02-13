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
package org.apache.tuscany.spi.wire;

import org.apache.tuscany.spi.component.SCAObject;

/**
 * Implementations are called after inbound and outbound wires are decorated with policy and before they are connected.
 *
 * @version $Rev$ $Date$
 */
public interface WirePostProcessor {

    /**
     * @param source
     * @param target
     */
    void process(SCAObject source, OutboundWire sourceWire, SCAObject target, InboundWire targetWire);

    /**
     * @param source
     * @param target
     */
    void process(SCAObject source, InboundWire sourceWire, SCAObject target, OutboundWire targetWire);

}
