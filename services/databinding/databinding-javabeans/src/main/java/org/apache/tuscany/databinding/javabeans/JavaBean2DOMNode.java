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
package org.apache.tuscany.databinding.javabeans;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.DOMHelper;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.apache.tuscany.spi.idl.ElementInfo;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Transformer to convert data from a JavaBean object to DOM Node
 */
@Service(Transformer.class)
public class JavaBean2DOMNode extends TransformerExtension<Object, Node> implements PullTransformer<Object, Node> {

    protected XMLTypeMapperExtension<Node> mapper;
    private Document factory;
    
    public JavaBean2DOMNode() {
        this.mapper = new XMLTypeMapperExtension<Node>();
        try {
            factory = DOMHelper.newDocument();
        } catch (ParserConfigurationException e) {
            throw new TransformationException(e);
        }
    }
    
    public Node transform(Object source, TransformationContext context) {
       return mapper.toDOMNode(source, context);
    }
    
    public Class getTargetType() {
        return Node.class;
    }
    
    public Class getSourceType() {
        return Object.class;
    }
}
