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

import org.apache.tuscany.databinding.DataBinding;
import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.WrapperHandler;
import org.apache.tuscany.databinding.impl.BaseTransformer;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.interfacedef.util.ElementInfo;
import org.apache.tuscany.interfacedef.util.WrapperInfo;
import org.apache.tuscany.interfacedef.util.XMLType;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * This is a special transformer to transform the input from one IDL to the
 * other one
 */
@Service(Transformer.class)
public class Input2InputTransformer extends BaseTransformer<Object[], Object[]> implements
    PullTransformer<Object[], Object[]> {

    protected Mediator mediator;

    public Input2InputTransformer() {
        super();
    }

    @Override
    public String getSourceDataBinding() {
        return DataBinding.IDL_INPUT;
    }

    @Override
    public String getTargetDataBinding() {
        return DataBinding.IDL_INPUT;
    }

    /**
     * @param mediator the mediator to set
     */
    @Reference
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    /**
     * @see org.apache.tuscany.databinding.impl.BaseTransformer#getSourceType()
     */
    @Override
    protected Class getSourceType() {
        return Object[].class;
    }

    /**
     * @see org.apache.tuscany.databinding.impl.BaseTransformer#getTargetType()
     */
    @Override
    protected Class getTargetType() {
        return Object[].class;
    }

    /**
     * @see org.apache.tuscany.databinding.Transformer#getWeight()
     */
    public int getWeight() {
        return 10000;
    }

    @SuppressWarnings("unchecked")
    public Object[] transform(Object[] source, TransformationContext context) {
        DataType<List<DataType>> sourceType = context.getSourceDataType();
        Operation sourceOp = context.getSourceOperation();
        boolean sourceWrapped = sourceOp != null && sourceOp.isWrapperStyle();

        WrapperHandler sourceWrapperHandler = null;
        String sourceDataBinding = getDataBinding(sourceOp);
        if (sourceWrapped) {
            sourceWrapperHandler = getWrapperHandler(sourceDataBinding, true);
        }

        DataType<List<DataType>> targetType = context.getTargetDataType();
        Operation targetOp = (Operation)context.getTargetOperation();
        boolean targetWrapped = targetOp != null && targetOp.isWrapperStyle();
        WrapperHandler targetWrapperHandler = null;
        String targetDataBinding = null;
        if (targetWrapped) {
            targetDataBinding = getDataBinding(targetOp);
            targetWrapperHandler = getWrapperHandler(targetDataBinding, true);
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
                return new Object[] {targetWrapper};
            }
            List<DataType> argTypes = wrapper.getUnwrappedInputType().getLogical();

            for (int i = 0; i < source.length; i++) {
                ElementInfo argElement = wrapper.getInputChildElements().get(i);
                DataType<XMLType> argType = argTypes.get(i);
                Object child = source[i];
                child = mediator.mediate(source[i], sourceType.getLogical().get(i), argType, context.getMetadata());
                targetWrapperHandler.setChild(targetWrapper, i, argElement, child);
            }
            return new Object[] {targetWrapper};
        } else if (sourceWrapped && (!targetWrapped)) {
            // Wrapped to Unwrapped
            Object sourceWrapper = source[0];
            // List<ElementInfo> childElements =
            // sourceOp.getWrapper().getInputChildElements();
            Object[] target = null;

            targetWrapperHandler = getWrapperHandler(targetDataBinding, false);
            if (targetWrapperHandler != null) {
                ElementInfo wrapperElement = sourceOp.getWrapper().getInputWrapperElement();
                // Object targetWrapper =
                // targetWrapperHandler.create(wrapperElement, context);
                DataType<XMLType> targetWrapperType = new DataTypeImpl<XMLType>(targetDataBinding, Object.class,
                                                                                new XMLType(wrapperElement));
                Object targetWrapper = mediator.mediate(sourceWrapper,
                                                        sourceType.getLogical().get(0),
                                                        targetWrapperType,
                                                        context.getMetadata());
                target = targetWrapperHandler.getChildren(targetWrapper).toArray();
            } else {
                Object[] sourceChildren = sourceWrapperHandler.getChildren(sourceWrapper).toArray();
                target = new Object[sourceChildren.length];
                for (int i = 0; i < sourceChildren.length; i++) {
                    DataType<XMLType> childType = sourceOp.getWrapper().getUnwrappedInputType().getLogical().get(i);
                    target[i] = mediator.mediate(sourceChildren[i], childType, targetType.getLogical().get(i), context
                        .getMetadata());
                }
            }
            return target;
        } else {
            // Assuming wrapper to wrapper conversion can be handled here as
            // well
            Object[] newArgs = new Object[source.length];
            for (int i = 0; i < source.length; i++) {
                Object child = mediator.mediate(source[i], sourceType.getLogical().get(i), targetType.getLogical()
                    .get(i), context.getMetadata());
                newArgs[i] = child;
            }
            return newArgs;
        }
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

    private String getDataBinding(Operation operation) {
        return operation.getDataBinding();
    }

}
