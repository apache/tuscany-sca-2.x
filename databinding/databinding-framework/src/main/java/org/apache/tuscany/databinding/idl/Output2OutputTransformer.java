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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.extension.TransformerExtension;
import org.apache.tuscany.idl.wsdl.WSDLOperation;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.model.DataType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.osoa.sca.annotations.Service;

/**
 * This is a special transformer to transform the output from one IDL to the other one
 */
@Service(Transformer.class)
public abstract class Output2OutputTransformer<T> extends TransformerExtension<Object, Object> implements
        PullTransformer<Object, Object> {

    protected WrapperHandler<T> wrapperHandler;
    protected Mediator mediator;

    /**
     * @param wrapperHandler
     */
    protected Output2OutputTransformer(WrapperHandler<T> wrapperHandler) {
        super();
        this.wrapperHandler = wrapperHandler;
    }

    /**
     * @param mediator the mediator to set
     */
    @Autowire
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    /**
     * @see org.apache.tuscany.databinding.extension.TransformerExtension#getSourceType()
     */
    @Override
    protected Class getSourceType() {
        return Object.class;
    }

    /**
     * @see org.apache.tuscany.databinding.extension.TransformerExtension#getTargetType()
     */
    @Override
    protected Class getTargetType() {
        return Object.class;
    }

    /**
     * @see org.apache.tuscany.databinding.Transformer#getWeight()
     */
    public int getWeight() {
        return 10;
    }

    @SuppressWarnings("unchecked")
    public Object transform(Object response, TransformationContext context) {
        try {
            DataType<?> sourceType = context.getSourceDataType();
            WSDLOperation sourceOp = (WSDLOperation) sourceType.getMetadata(WSDLOperation.class.getName());
            boolean sourceWrapped = (sourceOp != null && sourceOp.isWrapperStyle());

            DataType<?> targetType = context.getTargetDataType();
            WSDLOperation targetOp = (WSDLOperation) targetType.getMetadata(WSDLOperation.class.getName());
            boolean targetWrapped = (targetOp != null && targetOp.isWrapperStyle());

            if ((!sourceWrapped) && targetWrapped) {
                // Unwrapped --> Wrapped
                WSDLOperation.Wrapper wrapper = targetOp.getWrapper();
                T targetWrapper = wrapperHandler.create(wrapper.getOutputWrapperElement());
                if (response == null) {
                    return targetWrapper;
                }

                XmlSchemaElement argElement = wrapper.getOutputChildElements().get(0);
                DataType<QName> argType = wrapper.getUnwrappedOutputType();
                XmlSchemaType argXSDType = argElement.getSchemaType();
                boolean isSimpleType = (argXSDType instanceof XmlSchemaSimpleType);
                Object child = response;
                if (!isSimpleType) {
                    child = mediator.mediate(response, sourceType, argType);
                    wrapperHandler.setChild(targetWrapper, 0, argElement, child);
                } else {
                    wrapperHandler.setChild(targetWrapper, 0, argElement, child);
                }
                return targetWrapper;
            } else if (sourceWrapped && (!targetWrapped)) {
                // Wrapped to Unwrapped
                T sourceWrapper = (T) response;
                List<XmlSchemaElement> childElements = sourceOp.getWrapper().getOutputChildElements();
                XmlSchemaElement childElement = childElements.get(0);
                if (childElement.getSchemaType() instanceof XmlSchemaSimpleType) {
                    return wrapperHandler.getChild(sourceWrapper, 0, childElement);
                } else {
                    Object child = wrapperHandler.getChild(sourceWrapper, 0, childElement);
                    DataType<?> childType = sourceOp.getWrapper().getUnwrappedOutputType();
                    return mediator.mediate(child, childType, targetType);
                }
            } else {
                return mediator.mediate(response, sourceType, targetType);
            }
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
