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
package org.apache.tuscany.sca.assembly;

import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * Represents a wire.
 * 
 * @version $Rev$ $Date$
 */
public interface Wire extends Base, Extensible, PolicySubject, Cloneable {

    /**
     * Returns the source of the wire.
     * 
     * @return the source of the wire
     */
    ComponentReference getSource();

    /**
     * Sets the source of the wire.
     * 
     * @param source the source of the wire
     */
    void setSource(ComponentReference source);

    /**
     * Returns the target of the wire.
     * 
     * @return the target of the wire
     */
    ComponentService getTarget();

    /**
     * Sets the target of the wire.
     * 
     * @param target the target of the wire
     */
    void setTarget(ComponentService target);

    /**
     * A boolean value, with the default of "false". When a wire element has
     * @replace="false", the wire is added to the set of wires which apply to 
     * the reference identified by the @source attribute. When a wire element 
     * has @replace="true", the wire is added to the set of wires which apply to 
     * the reference identified by the @source attribute - but any wires for that 
     * reference specified by means of the @target attribute of the reference 
     * are removed from the set of wires which apply to the reference.
     * 
     * @return
     */
    boolean isReplace();

    /**
     * Set the replace flag for the wire 
     * @param replace
     */
    void setReplace(boolean replace);

    /**
     * Returns a clone of the wire.
     * 
     * @return a clone of the wire
     * @throws CloneNotSupportedException
     */
    Object clone() throws CloneNotSupportedException;

}
