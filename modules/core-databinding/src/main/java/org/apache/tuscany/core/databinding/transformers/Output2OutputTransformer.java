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

package org.apache.tuscany.core.databinding.transformers;

import java.util.List;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.interfacedef.util.ElementInfo;
import org.apache.tuscany.interfacedef.util.WrapperInfo;
import org.apache.tuscany.interfacedef.util.XMLType;
import org.apache.tuscany.spi.databinding.DataBinding;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.WrapperHandler;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * This is a special transformer to transform the output from one IDL to the
 * other one
 */
@Service(Transformer.class)
public class Output2OutputTransformer extends TransformerExtension<Object, Object> implements
    PullTransformer<Object, Object> {

    protected Mediator mediator;

    /**
     * @param wrapperHandler
     */
    public Output2OutputTransformer() {
        super();
    }

    /**
     * @param mediator the mediator to set
     */
    @Reference
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public String getSourceDataBinding() {
        return DataBinding.IDL_OUTPUT;
    }

    @Override
    public String getTargetDataBinding() {
        return DataBinding.IDL_OUTPUT;
    }

    /**
     * @see org.apache.tuscany.spi.databinding.extension.TransformerExtension#getSourceType()
     */
    @Override
    protected Class getSourceType() {
        return Object.class;
    }

    /**
     * @see org.apache.tuscany.spi.databinding.extension.TransformerExtension#getTargetType()
     */
    @Override
    protected Class getTargetType() {
        return Object.class;
    }

    /**
     * @see org.apache.tuscany.spi.databinding.Transformer#getWeight()
     */
    public int getWeight() {
        return 10;
    }

    private String getDataBinding(Operation operation) {
        return operation.getDataBinding();
    }

    private WrapperHandler getWrapperHandler(String dataBindingId, boolean required) {
        WrapperHandler wrapperHandler = null;
        if (dataBindingId != null) {
            DataBinding dataBinding = mediator.getDataBindingRegistry().getDataBinding(dataBindingId);
            wrapperHandler = dataBinding == null ? null : dataBinding.getWrapperHandler();
        }
        if (wrapperHandler == null && required) {
            throw new TransformationException("No wrapper handler is provided for databinding: " + dataBindingId);
        }
        return wrapperHandler;
    }

    @SuppressWarnings("unchecked")
    public Object transform(Object response, TransformationContext context) {
        try {
            DataType<DataType> sourceType = context.getSourceDataType();
            Operation sourceOp = context.getSourceOperation();
            boolean sourceWrapped = sourceOp != null && sourceOp.isWrapperStyle();
            WrapperHandler sourceWrapperHandler = null;
            if (sourceWrapped) {
                sourceWrapperHandler = getWrapperHandler(getDataBinding(sourceOp), true);
            }

            DataType<DataType> targetType = context.getTargetDataType();
            Operation targetOp = context.getTargetOperation();
            boolean targetWrapped = targetOp != null && targetOp.isWrapperStyle();
            WrapperHandler targetWrapperHandler = null;
            if (targetWrapped) {
                targetWrapperHandler = getWrapperHandler(getDataBinding(targetOp), true);
            }

            if ((!sourceWrapped) && targetWrapped) {
                // Unwrapped --> Wrapped
                WrapperInfo wrapper = targetOp.getWrapper();
                Object targetWrapper = targetWrapperHandler.create(wrapper.getOutputWrapperElement(), context);

                List<ElementInfo> childElements = wrapper.getOutputChildElements();
                if (childElements.isEmpty()) {
                    // void output
                    return targetWrapper;
                }
                ElementInfo argElement = childElements.get(0);
                DataType<XMLType> argType = wrapper.getUnwrappedOutputType();
                Object child = response;
                child = mediator.mediate(response, sourceType.getLogical(), argType, context.getMetadata());
                targetWrapperHandler.setChild(targetWrapper, 0, argElement, child);
                return targetWrapper;
            } else if (sourceWrapped && (!targetWrapped)) {
                // Wrapped to Unwrapped
                Object sourceWrapper = response;
                List<ElementInfo> childElements = sourceOp.getWrapper().getOutputChildElements();
                if (childElements.isEmpty()) {
                    // The void output
                    return null;
                }
                targetWrapperHandler = getWrapperHandler(getDataBinding(targetOp), false);
                if (targetWrapperHandler != null) {
                    ElementInfo wrapperElement = sourceOp.getWrapper().getInputWrapperElement();
                    // Object targetWrapper =
                    // targetWrapperHandler.create(wrapperElement, context);
                    DataType<XMLType> targetWrapperType = new DataTypeImpl<XMLType>(targetType.getLogical()
                        .getDataBinding(), Object.class, new XMLType(wrapperElement));
                    Object targetWrapper = mediator.mediate(sourceWrapper,
                                                            sourceType.getLogical(),
                                                            targetWrapperType,
                                                            context.getMetadata());
                    return targetWrapperHandler.getChildren(targetWrapper).get(0);
                } else {
                    Object child = sourceWrapperHandler.getChildren(sourceWrapper).get(0);
                    DataType<?> childType = sourceOp.getWrapper().getUnwrappedOutputType();
                    return mediator.mediate(child, childType, targetType.getLogical(), context.getMetadata());
                }
            } else {
                // FIXME: Do we want to handle wrapped to wrapped?
                return mediator.mediate(response, sourceType.getLogical(), targetType.getLogical(), context
                    .getMetadata());
            }
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
