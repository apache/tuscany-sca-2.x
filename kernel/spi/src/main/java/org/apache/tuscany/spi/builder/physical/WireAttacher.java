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

package org.apache.tuscany.spi.builder.physical;

import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;

/**
 * Abstraction for the wire attacher.
 *
 * @version $Date$ $Revision$
 */
public interface WireAttacher<C extends Component,
    PWSD extends PhysicalWireSourceDefinition,
    PWTD extends PhysicalWireTargetDefinition> {

    /**
     * Attaches the source to the component.
     *
     * @param component Component.
     * @param source    Source.
     */
    void attach(C component, PWSD source) throws WiringException;

    /**
     * Attaches the target to the component.
     *
     * @param component Component.
     * @param target    Target.
     */
    void attach(C component, PWTD target) throws WiringException;

}
