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

package org.apache.tuscany.sca.interfacedef.wsdl;

import java.io.Serializable;

import javax.wsdl.Definition;

/**
 * Model for objects in a WSDL definition
 * @version $Rev$ $Date$
 */
public interface WSDLObject<T extends Serializable> {
    /**
     * Get the owning definition where the element is declared
     * @return The owning definition
     */
    Definition getDefinition();

    /**
     * Set the owning definition
     * @param definition
     */
    void setDefinition(Definition definition);

    /**
     * Get the WSDL element such as Service, PortType, Binding or Message
     * @return The WSDL element
     */
    T getElement();

    /**
     * Set the WSDL element
     * @param element
     */
    void setElement(T element);
}
