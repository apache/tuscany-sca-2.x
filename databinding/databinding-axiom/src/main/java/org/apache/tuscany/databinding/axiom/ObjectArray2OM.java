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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.extension.TransformerExtension;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.model.DataType;
import org.osoa.sca.annotations.Service;

/**
 * 
 */
@Service(Transformer.class)
public class ObjectArray2OM extends TransformerExtension<Object[], OMElement> implements
        PullTransformer<Object[], OMElement> {

    private Mediator mediator;

    /**
     * 
     */
    public ObjectArray2OM() {
    }

    /**
     * @see org.apache.tuscany.databinding.extension.TransformerExtension#getSourceType()
     */
    @Override
    protected Class getSourceType() {
        return Object[].class;
    }

    /**
     * @see org.apache.tuscany.databinding.extension.TransformerExtension#getTargetType()
     */
    @Override
    protected Class getTargetType() {
        return OMElement.class;
    }

    /**
     * @see org.apache.tuscany.databinding.Transformer#getWeight()
     */
    public int getWeight() {
        return 10;
    }

    @SuppressWarnings("unchecked")
    public OMElement transform(Object[] source, TransformationContext context) {
        DataType<List<DataType<QName>>> targetType = context.getTargetDataType();
        DataType<List<DataType<Class>>> sourceType = context.getSourceDataType();
        OMFactory factory = OMAbstractFactory.getOMFactory();
        QName elementName = (QName) targetType.getMetadata("element.name");
        OMElement element = factory.createOMElement(elementName, null);
        if (source == null)
            return null;
        for (int i = 0; i < source.length; i++) {
            Object child = mediator.mediate(source[i], sourceType.getLogical().get(i), targetType.getLogical().get(i));
            if (child instanceof OMNode) {
                element.addChild((OMNode) child);
            } else {
                OMElement childElement = factory.createOMElement(targetType.getLogical().get(i).getLogical(), element);
                factory.createOMText(childElement, child.toString());
            }
        }
        return element;
    }

    /**
     * @param mediator the mediator to set
     */
    @Autowire
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

}
