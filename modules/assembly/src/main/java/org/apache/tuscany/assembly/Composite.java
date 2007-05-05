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

import javax.xml.namespace.QName;


/**
 * Represents a composite.
 * 
 * @version $Rev$ $Date$
 */
public interface Composite extends Implementation, Visitable {

    /**
     * Returns the name of the composite.
     * 
     * @return the name of the composite
     */
    QName getName();

    /**
     * Sets the name of the composite.
     * 
     * @param name the name of the composite
     */
    void setName(QName name);

    /**
     * Returns a list of composites included in this composite.
     * 
     * @return a list of composites included in this composite.
     */
    List<Composite> getIncludes();

    /**
     * Returns a list of components contained in this composite.
     * 
     * @return a list of components contained in this composite
     */
    List<Component> getComponents();

    /**
     * Returns a list of wires contained in this composite.
     * 
     * @return a list of wires contained in this composite
     */
    List<Wire> getWires();

    /**
     * Returns true if all the components within the composite must run in the
     * same process.
     * 
     * @return true if all the components within the composite must run in the
     *         same process
     */
    boolean isLocal();

    /**
     * Sets whether all the components within the composite must run in the same
     * process.
     * 
     * @param local whether all the components within the composite must run in
     *            the same process
     */
    void setLocal(boolean local);

    /**
     * Returns true if autowiring is enabled in the composite.
     * 
     * @return true if autowiring is enabled in the composite
     */
    boolean isAutowire();

    /**
     * Sets whether autowiring is enabled in the composite.
     * 
     * @param autowire whether autowiring is enabled in the composite
     */
    void setAutowire(boolean autowire);

    /**
     * Returns a clone of the component type.
     * 
     * @return a clone of the component type
     * @throws CloneNotSupportedException
     */
    Object clone() throws CloneNotSupportedException;

}
