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

package org.apache.tuscany.interfacedef.wsdl;

import javax.wsdl.Definition;

import org.apache.ws.commons.schema.XmlSchemaCollection;

/**
 * Represents a WSDL definition.
 * WSDLDefinition
 *
 * @version $Rev$ $Date$
 */
public interface WSDLDefinition {
    
    /**
     * Returns the WSDL definition model
     * @return the WSDL definition model
     */
    Definition getDefinition();
    
    /**
     * Sets the WSDL definition model
     * @param definition the WSDL definition model
     */
    void setDefinition(Definition definition);
    
    /**
     * Returns a list of XML schemas inlined in this WSDL definition.
     * @return
     */
    XmlSchemaCollection getInlinedSchemas();
    
    /**
     * Returns the namespace of this WSDL definition.
     * @return the namespace of this WSDL definition
     */
    String getNamespace();

    /**
     * Sets the namespace of this WSDL definition.
     * @param namespace the namespace of this WSDL definition
     */
    void setNamespace(String namespace);

    /**
     * Returns true if the model element is unresolved.
     * 
     * @return true if the model element is unresolved.
     */
    boolean isUnresolved();

    /**
     * Sets whether the model element is unresolved.
     * 
     * @param unresolved whether the model element is unresolved
     */
    void setUnresolved(boolean unresolved);

}
