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

package org.apache.tuscany.sca.interfacedef.wsdl.impl;

import java.io.Serializable;

import javax.wsdl.Definition;

import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;

/**
 * @version $Rev$ $Date$
 */
public class WSDLObjectImpl<T extends Serializable> implements WSDLObject<T> {
    private Definition definition;
    private T element;

    public WSDLObjectImpl() {
        super();
    }

    public WSDLObjectImpl(Definition definition, T element) {
        super();
        this.definition = definition;
        this.element = element;
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

}
