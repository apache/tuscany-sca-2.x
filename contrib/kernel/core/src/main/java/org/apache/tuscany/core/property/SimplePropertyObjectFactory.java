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

package org.apache.tuscany.core.property;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.TypeInfo;
import org.w3c.dom.Document;

public class SimplePropertyObjectFactory<P> implements ObjectFactory<P> {
    private SimpleTypeMapperExtension typeMapper;
    private Property<P> property;
    private Document value;
    private P instance;

    public SimplePropertyObjectFactory(Property<P> property, Document value) {
        super();
        
        this.property = property;
        this.value = (value == null) ? property.getDefaultValue() : value;
        this.typeMapper = new SimpleTypeMapperExtension();
    }

    @SuppressWarnings("unchecked")
    public P getInstance() throws ObjectCreationException {
        if (value == null) {
            return null;
        }
        if (instance == null) {
            String text = value.getDocumentElement().getTextContent();
            TypeInfo xmlType = null;
            if (property.getJavaType() == null) {
                xmlType = new TypeInfo(property.getXmlType(), true, null);
            } else {
                xmlType = typeMapper.getXMLType(property.getJavaType());
            }
            if (xmlType == null) {
                throw new IllegalArgumentException("Complex property is not supported.");
            }
            instance = (P)typeMapper.toJavaObject(xmlType.getQName(), text, null);
        }
        return instance;
    }

}
