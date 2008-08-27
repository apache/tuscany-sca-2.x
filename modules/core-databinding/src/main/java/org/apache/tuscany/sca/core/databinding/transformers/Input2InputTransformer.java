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

package org.apache.tuscany.sca.core.databinding.transformers;

import java.util.List;
import java.util.logging.Logger;

import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.osoa.sca.annotations.Reference;

/**
 * This is a special transformer to transform the input from one IDL to the
 * other one
 *
 * @version $Rev$ $Date$
 */
public class Input2InputTransformer extends BaseTransformer<Object[], Object[]> implements
    PullTransformer<Object[], Object[]> {
    private static final Logger logger = Logger.getLogger(Input2InputTransformer.class.getName());

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
     * @see org.apache.tuscany.sca.databinding.impl.BaseTransformer#getSourceType()
     */
    @Override
    protected Class<Object[]> getSourceType() {
        return Object[].class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.impl.BaseTransformer#getTargetType()
     */
    @Override
    protected Class<Object[]> getTargetType() {
        return Object[].class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.Transformer#getWeight()
     */
    @Override
    public int getWeight() {
        return 10000;
    }

    /**
     * Match the structure of the wrapper element. If it matches, then we can do
     * wrapper to wrapper transformation. Otherwise, we do child to child.
     * @param w1
     * @param w2
     * @return
     */
    private boolean matches(WrapperInfo w1, WrapperInfo w2) {
        if (w1 == null || w2 == null) {
            return false;
        }
        if (!w1.getInputWrapperElement().equals(w2.getInputWrapperElement())) {
            return false;
        }

        // Compare the child elements
        List<ElementInfo> list1 = w1.getInputChildElements();
        List<ElementInfo> list2 = w2.getInputChildElements();
        if (list1.size() != list2.size()) {
            return false;
        }
        // FXIME: [rfeng] At this point, the J2W generates local elments under the namespace
        // of the interface instead of "". We only compare the local parts only to work around
        // the namespace mismatch
        for (int i = 0; i < list1.size(); i++) {
            String n1 = list1.get(i).getQName().getLocalPart();
            String n2 = list2.get(i).getQName().getLocalPart();
            if (!n1.equals(n2)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public Object[] transform(Object[] source, TransformationContext context) {
        // Check if the source operation is wrapped
        DataType<List<DataType>> sourceType = context.getSourceDataType();
        Operation sourceOp = context.getSourceOperation();
        boolean sourceWrapped = sourceOp != null && sourceOp.isWrapperStyle() && sourceOp.getWrapper() != null;
        boolean sourceBare = sourceOp != null && !sourceOp.isWrapperStyle() && sourceOp.getWrapper() == null;

        // Find the wrapper handler for source data
        WrapperHandler sourceWrapperHandler = null;
        String sourceDataBinding = getDataBinding(sourceOp);
        sourceWrapperHandler = getWrapperHandler(sourceDataBinding, sourceWrapped);

        // Check if the target operation is wrapped
        DataType<List<DataType>> targetType = context.getTargetDataType();
        Operation targetOp = (Operation)context.getTargetOperation();
        boolean targetWrapped = targetOp != null && targetOp.isWrapperStyle() && targetOp.getWrapper() != null;
        boolean targetBare = targetOp != null && !targetOp.isWrapperStyle() && targetOp.getWrapper() == null;

        // Find the wrapper handler for target data
        WrapperHandler targetWrapperHandler = null;
        String targetDataBinding = getDataBinding(targetOp);
        targetWrapperHandler = getWrapperHandler(targetDataBinding, targetWrapped);

        if ((!sourceWrapped && !sourceBare) && targetWrapped) {
            // Unwrapped --> Wrapped
            WrapperInfo wrapper = targetOp.getWrapper();
            // ElementInfo wrapperElement = wrapper.getInputWrapperElement();

            // Class<?> targetWrapperClass = wrapper != null ? wrapper.getInputWrapperClass() : null;

            if (source == null) {
                // Empty child elements
                Object targetWrapper = targetWrapperHandler.create(targetOp, true);
                return new Object[] {targetWrapper};
            }

            // If the source can be wrapped, wrapped it first
            if (sourceWrapperHandler != null) {
                WrapperInfo sourceWrapperInfo = sourceOp.getWrapper();
                DataType sourceWrapperType = sourceWrapperInfo != null ? sourceWrapperInfo.getInputWrapperType() : null;

                // We only do wrapper to wrapper transformation if the source has a wrapper and both sides
                // match by XML structure
                if (sourceWrapperType != null && matches(sourceOp.getWrapper(), targetOp.getWrapper())) {
                    Class<?> sourceWrapperClass = sourceWrapperType.getPhysical();

                    // Create the source wrapper
                    Object sourceWrapper = sourceWrapperHandler.create(sourceOp, true);

                    // Populate the source wrapper
                    if (sourceWrapper != null) {
                        sourceWrapperHandler.setChildren(sourceWrapper,
                                                         source,
                                                         sourceOp,
                                                         true);

                        // Transform the data from source wrapper to target wrapper
                        Object targetWrapper =
                            mediator.mediate(sourceWrapper, sourceWrapperType, targetType.getLogical().get(0), context
                                .getMetadata());
                        return new Object[] {targetWrapper};
                    }
                }
            }
            // Fall back to child by child transformation
            Object targetWrapper = targetWrapperHandler.create(targetOp, true);
            List<DataType> argTypes = wrapper.getUnwrappedInputType().getLogical();
            Object[] targetChildren = new Object[source.length];
            for (int i = 0; i < source.length; i++) {
                // ElementInfo argElement = wrapper.getInputChildElements().get(i);
                DataType<XMLType> argType = argTypes.get(i);
                targetChildren[i] =
                    mediator.mediate(source[i], sourceType.getLogical().get(i), argType, context.getMetadata());
            }
            targetWrapperHandler.setChildren(targetWrapper,
                                             targetChildren,
                                             targetOp,
                                             true);
            return new Object[] {targetWrapper};

        } else if (sourceWrapped && (!targetWrapped && !targetBare)) {
            // Wrapped to Unwrapped
            Object sourceWrapper = source[0];
            Object[] target = null;

            // List<ElementInfo> childElements = sourceOp.getWrapper().getInputChildElements();
            if (targetWrapperHandler != null) {
                // ElementInfo wrapperElement = sourceOp.getWrapper().getInputWrapperElement();
                // FIXME: This is a workaround for the wsdless support as it passes in child elements
                // under the wrapper that only matches by position
                if (sourceWrapperHandler.isInstance(sourceWrapper, sourceOp, true)) {

                    WrapperInfo targetWrapperInfo = targetOp.getWrapper();
                    DataType targetWrapperType =
                        targetWrapperInfo != null ? targetWrapperInfo.getInputWrapperType() : null;
                    if (targetWrapperType != null && matches(sourceOp.getWrapper(), targetOp.getWrapper())) {
                        Object targetWrapper =
                            mediator.mediate(sourceWrapper, sourceType.getLogical().get(0), targetWrapperType, context
                                .getMetadata());
                        target = targetWrapperHandler.getChildren(targetWrapper, targetOp, true).toArray();
                        return target;
                    }
                }
            }
            Object[] sourceChildren = sourceWrapperHandler.getChildren(sourceWrapper, sourceOp, true).toArray();
            target = new Object[sourceChildren.length];
            for (int i = 0; i < sourceChildren.length; i++) {
                DataType<XMLType> childType = sourceOp.getWrapper().getUnwrappedInputType().getLogical().get(i);
                target[i] =
                    mediator.mediate(sourceChildren[i], childType, targetType.getLogical().get(i), context
                        .getMetadata());
            }
            return target;
        } else {
            // Assuming wrapper to wrapper conversion can be handled here as well
            Object[] newArgs = new Object[source.length];
            for (int i = 0; i < source.length; i++) {
                Object child =
                    mediator.mediate(source[i], sourceType.getLogical().get(i), targetType.getLogical().get(i), context
                        .getMetadata());
                newArgs[i] = child;
            }
            return newArgs;
        }
    }

    private WrapperHandler getWrapperHandler(String dataBindingId, boolean required) {
        WrapperHandler wrapperHandler = null;
        if (dataBindingId != null) {
            DataBinding dataBinding = mediator.getDataBindings().getDataBinding(dataBindingId);
            wrapperHandler = dataBinding == null ? null : dataBinding.getWrapperHandler();
        }
        if (wrapperHandler == null && required) {
            throw new TransformationException("No wrapper handler is provided for databinding: " + dataBindingId);
        }
        return wrapperHandler;
    }

    private String getDataBinding(Operation operation) {
        WrapperInfo wrapper = operation.getWrapper();
        if (wrapper != null) {
            return wrapper.getDataBinding();
        } else {
            return null;
        }
    }

}
