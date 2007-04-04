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
package org.apache.tuscany.spi.databinding.extension;

import javax.xml.namespace.QName;

import org.apache.tuscany.idl.util.XMLType;
import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.SimpleTypeMapper;
import org.apache.tuscany.spi.databinding.TransformationContext;

/**
 * Transformer to convert data from a simple java object to a databinding's representation
 */
public abstract class Java2SimpleTypeTransformer<T> extends TransformerExtension<Object, T> implements
        PullTransformer<Object, T> {

    protected SimpleTypeMapper mapper;

    public Java2SimpleTypeTransformer() {
        this.mapper = new SimpleTypeMapperExtension();
    }
    
    public Java2SimpleTypeTransformer(SimpleTypeMapper mapper) {
        this.mapper = (mapper != null) ? mapper : new SimpleTypeMapperExtension();
    }

    public T transform(Object source, TransformationContext context) {
        XMLType xmlType = (XMLType) context.getTargetDataType().getLogical();
        String text = mapper.toXMLLiteral(xmlType.getTypeName(), source, context);
        return createElement(xmlType.getElementName(), text, context);
    }

    public Class getSourceType() {
        return Object.class;
    }

    public int getWeight() {
        return 10000;
    }

    protected abstract T createElement(QName element, String literal, TransformationContext context);

}
