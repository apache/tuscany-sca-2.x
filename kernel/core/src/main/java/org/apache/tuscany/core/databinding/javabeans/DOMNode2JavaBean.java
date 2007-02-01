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
package org.apache.tuscany.core.databinding.javabeans;

import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.apache.tuscany.spi.idl.ElementInfo;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Node;

/**
 * Transformer to convert data from DOM Node to JavaBean
 */
@Service(Transformer.class)
public class DOMNode2JavaBean extends TransformerExtension<Node, Object> implements PullTransformer<Node, Object> {

    protected XMLTypeMapperExtension<Node> mapper;
    
    public DOMNode2JavaBean() {
        this.mapper = new XMLTypeMapperExtension<Node>();
    }
    
    public Object transform(Node source, TransformationContext context) {
        TypeInfo xmlType = (TypeInfo)context.getSourceDataType().getMetadata(TypeInfo.class.getName());
        if (xmlType == null) {
            ElementInfo element =
                (ElementInfo)context.getSourceDataType().getMetadata(ElementInfo.class.getName());
            xmlType = (TypeInfo)element.getType();
        }
        
        return mapper.toJavaObject(xmlType, source, context);
    }
    
    public Class getSourceType() {
        return Node.class;
    }
    
    public Class getTargetType() {
        return Object.class;
    }
}
