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

package org.apache.tuscany.sca.assembly.impl;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AbstractProperty;
import org.apache.tuscany.sca.interfacedef.DataType;

/**
 * Represents an abstract property.
 * 
 * @version $Rev$ $Date$
 */
public class AbstractPropertyImpl extends ExtensibleImpl implements AbstractProperty {
    protected Object value;
    protected String name;
    protected QName xsdType;
    protected QName xsdElement;
    protected boolean many;
    protected boolean mustSupply;
    protected DataType dataType;

    /**
     * Constructs a new abstract property.
     */
    protected AbstractPropertyImpl() {
    }

    public Object getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public QName getXSDElement() {
        return xsdElement;
    }

    public QName getXSDType() {
        return xsdType;
    }

    public boolean isMany() {
        return many;
    }

    public boolean isMustSupply() {
        return mustSupply;
    }

    public void setValue(Object defaultValue) {
        this.value = defaultValue;
    }

    public void setMany(boolean many) {
        this.many = many;
    }

    public void setMustSupply(boolean mustSupply) {
        this.mustSupply = mustSupply;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setXSDElement(QName element) {
        this.xsdElement = element;
    }

    public void setXSDType(QName type) {
        this.xsdType = type;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

}
