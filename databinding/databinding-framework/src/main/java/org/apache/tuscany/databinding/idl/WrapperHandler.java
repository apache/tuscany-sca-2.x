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

package org.apache.tuscany.databinding.idl;

import org.apache.ws.commons.schema.XmlSchemaElement;

/**
 * A contract for transformers to deal with wrapping/unwrapping for WSDL wrapper style operations
 */
public interface WrapperHandler<T> {
    /**
     * Create a wrapper element
     * 
     * @param element The XSD element
     * @return
     */
    T create(XmlSchemaElement element);

    /**
     * Set child element for the wrapper
     * 
     * @param wrapper The wrapper
     * @param i The index
     * @param childElement The XSD element
     * @param value The value of the child
     */
    void setChild(T wrapper, int i, XmlSchemaElement childElement, Object value);

    /**
     * Get child element from the wrapper
     * 
     * @param wrapper The wrapper
     * @param i The index
     * @param element The XSD element
     * @return The value of the child
     */
    Object getChild(T wrapper, int i, XmlSchemaElement element);
}
