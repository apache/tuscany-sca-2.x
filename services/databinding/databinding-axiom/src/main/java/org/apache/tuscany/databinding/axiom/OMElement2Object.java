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
package org.apache.tuscany.databinding.axiom;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.databinding.extension.TransformerExtension;
import org.apache.tuscany.databinding.idl.SimpleTypeMapper;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.osoa.sca.annotations.Service;

/**
 * Transformer to convert data from a simple java bject to OMElement
 */
@Service(Transformer.class)
public class OMElement2Object extends TransformerExtension<Object, OMElement> implements
        PullTransformer<Object, OMElement> {
    
    private SimpleTypeMapper mapper = new SimpleTypeMapperExtension();
    private OMFactory factory = OMAbstractFactory.getOMFactory();

    public OMElement transform(Object source, TransformationContext context) {
        XmlSchemaElement element =
                (XmlSchemaElement) context.getTargetDataType().getMetadata(XmlSchemaElement.class.getName());
        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) element.getSchemaType();
        String text = mapper.toString(simpleType, source);
        OMElement omElement = factory.createOMElement(element.getQName(), null);
        factory.createOMText(omElement, text);
        return omElement;
    }

    public Class getSourceType() {
        return Object.class;
    }

    public Class getTargetType() {
        return OMElement.class;
    }

    public int getWeight() {
        return 10;
    }

}
