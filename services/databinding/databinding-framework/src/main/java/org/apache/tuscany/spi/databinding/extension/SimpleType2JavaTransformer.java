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

import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.SimpleTypeMapper;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.model.XMLType;

/**
 * Transformer to convert data from a databinding's representation of simple
 * types to Java Objects
 */
public abstract class SimpleType2JavaTransformer<T> extends TransformerExtension<T, Object> implements
    PullTransformer<T, Object> {

    protected SimpleTypeMapper mapper;

    public SimpleType2JavaTransformer() {
        this.mapper = new SimpleTypeMapperExtension();
    }

    public SimpleType2JavaTransformer(SimpleTypeMapper mapper) {
        this.mapper = (mapper != null) ? mapper : new SimpleTypeMapperExtension();
    }

    public Object transform(T source, TransformationContext context) {
        XMLType xmlType = (XMLType) context.getSourceDataType().getLogical();
        return mapper.toJavaObject(xmlType.getTypeName(), getText(source), context);
    }

    public Class getTargetType() {
        return Object.class;
    }

    public int getWeight() {
        // Cannot be used for imtermediate
        return 10000;
    }

    /**
     * Get the string value from the source
     * @param source
     * @return A string
     */
    protected abstract String getText(T source);
}
