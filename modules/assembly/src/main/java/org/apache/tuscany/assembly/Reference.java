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
package org.apache.tuscany.assembly;

import java.util.List;

/**
 * Represents a reference. References within an implementation represent links
 * to services that the implementation uses that must be provided by other
 * components.
 * 
 * @version $Rev$ $Date$
 */
public interface Reference extends AbstractReference, Contract {

    /**
     * Returns a boolean value, "false" by default, which indicates that the
     * implementation wires this reference dynamically.
     * 
     * @return true if the implementation wires this reference dynamically
     */
    boolean isWiredByImpl();

    /**
     * Sets a boolean value, "false" by default, which indicates that the
     * implementation wires this reference dynamically.
     * 
     * @param wiredByImpl whether the implementation wires this reference
     *            dynamically
     */
    void setWiredByImpl(boolean wiredByImpl);

    /**
     * Returns the targets of this reference.
     * 
     * @return the targets of this reference.
     */
    List<ComponentService> getTargets();
}
