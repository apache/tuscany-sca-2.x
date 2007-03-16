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

package org.apache.tuscany.core.databinding.impl;

import java.util.List;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.databinding.DataBinding;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.WrapperHandler;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.apache.tuscany.spi.model.ElementInfo;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.WrapperInfo;
import org.apache.tuscany.spi.model.XMLType;

/**
 * This is a special transformer to transform the input from one IDL to the other one
 */
@Service(Transformer.class)
public class Input2InputTransformer extends TransformerExtension<Object[], Object[]> implements
    PullTransformer<Object[], Object[]> {

    private static final String IDL_INPUT = "idl:input";

    protected DataBindingRegistry dataBindingRegistry;

    protected Mediator mediator;

    public Input2InputTransformer() {
        super();
    }

    @Override
    public String getSourceDataBinding() {
        return IDL_INPUT;
    }

    @Override
    public String getTargetDataBinding() {
        return IDL_INPUT;
    }

    /**
     * @param mediator the mediator to set
     */
    @Reference
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    /**
     * @param dataBindingRegistry the dataBindingRegistry to set
     */
    @Reference
    public void setDataBindingRegistry(DataBindingRegistry dataBindingRegistry) {
        this.dataBindingRegistry = dataBindingRegistry;
    }

    /**
     * @see org.apache.tuscany.spi.databinding.extension.TransformerExtension#getSourceType()
     */
    @Override
    protected Class getSourceType() {
        return Object[].class;
    }

    /**
     * @see org.apache.tuscany.spi.databinding.extension.TransformerExtension#getTargetType()
     */
    @Override
    protected Class getTargetType() {
        return Object[].class;
    }

    /**
     * @see org.apache.tuscany.spi.databinding.Transformer#getWeight()
     */
    public int getWeight() {
        return 10000;
    }

    @SuppressWarnings("unchecked")
    public Object[] transform(Object[] source, TransformationContext context) {
        DataType<List<DataType<?>>> sourceType = context.getSourceDataType();
        Operation<?> sourceOp = (Operation<?>) sourceType.getOperation();
        boolean sourceWrapped = sourceOp != null && sourceOp.isWrapperStyle();

        WrapperHandler sourceWrapperHandler = null;
        if (sourceWrapped) {
            sourceWrapperHandler = getWapperHandler(sourceType.getOperation().getDataBinding(), true);
        }

        DataType<List<DataType<QName>>> targetType = context.getTargetDataType();
        Operation<?> targetOp = (Operation<?>) targetType.getOperation();
        boolean targetWrapped = targetOp != null && targetOp.isWrapperStyle();
        WrapperHandler targetWrapperHandler = null;
        if (targetWrapped) {
            targetWrapperHandler = getWapperHandler(targetType.getOperation().getDataBinding(), true);
        }

        if ((!sourceWrapped) && targetWrapped) {
            // Unwrapped --> Wrapped
            WrapperInfo wrapper = targetOp.getWrapper();
            ElementInfo wrapperElement = wrapper.getInputWrapperElement();

            // If the source can be wrapped, wrapped it first
            if (sourceWrapperHandler != null) {
                Object sourceWrapper = sourceWrapperHandler.create(wrapperElement, context);
                for (int i = 0; i < source.length; i++) {
                    ElementInfo argElement = wrapper.getInputChildElements().get(i);
                    sourceWrapperHandler.setChild(sourceWrapper, i, argElement, source[0]);
                }
            }
            Object targetWrapper = targetWrapperHandler.create(wrapperElement, context);
            if (source == null) {
                return new Object[]{targetWrapper};
            }
            List<DataType<XMLType>> argTypes = wrapper.getUnwrappedInputType().getLogical();

            for (int i = 0; i < source.length; i++) {
                ElementInfo argElement = wrapper.getInputChildElements().get(i);
                DataType<XMLType> argType = argTypes.get(i);
                Object child = source[i];
                child =
                    mediator.mediate(source[i], sourceType.getLogical().get(i), argType, context
                        .getMetadata());
                targetWrapperHandler.setChild(targetWrapper, i, argElement, child);
            }
            return new Object[]{targetWrapper};
        } else if (sourceWrapped && (!targetWrapped)) {
            // Wrapped to Unwrapped
            Object sourceWrapper = source[0];
            List<ElementInfo> childElements = sourceOp.getWrapper().getInputChildElements();
            Object[] target = new Object[childElements.size()];

            targetWrapperHandler = getWapperHandler(targetType.getOperation().getDataBinding(), false);
            if (targetWrapperHandler != null) {
                ElementInfo wrapperElement = sourceOp.getWrapper().getInputWrapperElement();
                // Object targetWrapper =
                // targetWrapperHandler.create(wrapperElement, context);
                DataType<QName> targetWrapperType =
                    new DataType<QName>(targetType.getOperation().getDataBinding(), Object.class,
                        wrapperElement.getQName());
                Object targetWrapper =
                    mediator.mediate(sourceWrapper,
                        sourceType.getLogical().get(0),
                        targetWrapperType,
                        context.getMetadata());
                for (int i = 0; i < childElements.size(); i++) {
                    ElementInfo childElement = childElements.get(i);
                    target[i] = targetWrapperHandler.getChild(targetWrapper, i, childElement);
                }
            } else {
                for (int i = 0; i < childElements.size(); i++) {
                    ElementInfo childElement = childElements.get(i);
                    Object child = sourceWrapperHandler.getChild(sourceWrapper, i, childElement);
                    DataType<XMLType> childType =
                        sourceOp.getWrapper().getUnwrappedInputType().getLogical().get(i);
                    target[i] =
                        mediator.mediate(child, childType, targetType.getLogical().get(i), context
                            .getMetadata());
                }
            }
            return target;
        } else {
            // Assuming wrapper to wrapper conversion can be handled here as
            // well
            Object[] newArgs = new Object[source.length];
            for (int i = 0; i < source.length; i++) {
                Object child =
                    mediator.mediate(source[i], sourceType.getLogical().get(i), targetType.getLogical()
                        .get(i), context.getMetadata());
                newArgs[i] = child;
            }
            return newArgs;
        }
    }

    private WrapperHandler getWapperHandler(String dataBindingId, boolean required) {
        DataBinding dataBinding = dataBindingRegistry.getDataBinding(dataBindingId);
        WrapperHandler wrapperHandler = dataBinding == null ? null : dataBinding.getWrapperHandler();
        if (wrapperHandler == null && required) {
            throw new TransformationException(
                "No wrapper handler is provided for databinding: " + dataBindingId);
        }
        return wrapperHandler;
    }

}
