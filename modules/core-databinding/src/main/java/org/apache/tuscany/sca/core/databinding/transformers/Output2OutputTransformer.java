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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.BaseTransformer;
import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import static org.apache.tuscany.sca.interfacedef.Operation.IDL_OUTPUT;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * This is a special transformer to transform the output from one IDL to the
 * other one
 *
 * @version $Rev$ $Date$
 */
public class Output2OutputTransformer extends BaseTransformer<Object, Object> implements
    PullTransformer<Object, Object> {

    protected Mediator mediator;

    /**
     * @param wrapperHandler
     */
    public Output2OutputTransformer(ExtensionPointRegistry registry) {
        super();
        this.mediator = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(Mediator.class);
    }

    @Override
    public String getSourceDataBinding() {
        return IDL_OUTPUT;
    }

    @Override
    public String getTargetDataBinding() {
        return IDL_OUTPUT;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.BaseTransformer#getSourceType()
     */
    @Override
    protected Class<Object> getSourceType() {
        return Object.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.BaseTransformer#getTargetType()
     */
    @Override
    protected Class<Object> getTargetType() {
        return Object.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.Transformer#getWeight()
     */
    @Override
    public int getWeight() {
        return 10;
    }

    private String getDataBinding(Operation operation) {
        WrapperInfo wrapper = operation.getWrapper();
        if (wrapper != null) {
            return wrapper.getDataBinding();
        } else {
            return null;
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
        if (!w1.getOutputWrapperElement().equals(w2.getOutputWrapperElement())) {
            return false;
        }

        // Compare the child elements
        List<ElementInfo> list1 = w1.getOutputChildElements();
        List<ElementInfo> list2 = w2.getOutputChildElements();
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
    public Object transform(Object response, TransformationContext context) {
        try {
            
            DataType<List<DataType>> sourceType = context.getSourceDataType();
            Operation sourceOp = context.getSourceOperation();
            boolean sourceWrapped = sourceOp != null && sourceOp.isWrapperStyle() && sourceOp.getWrapper() != null;
            boolean sourceBare = sourceOp != null && !sourceOp.isWrapperStyle() && sourceOp.getWrapper() == null;

            WrapperHandler sourceWrapperHandler = null;
            String sourceDataBinding = getDataBinding(sourceOp);
            sourceWrapperHandler = getWrapperHandler(sourceDataBinding, sourceWrapped);

            DataType<List<DataType>> targetType = context.getTargetDataType();
            Operation targetOp = (Operation)context.getTargetOperation();
            boolean targetWrapped = targetOp != null && targetOp.isWrapperStyle() && targetOp.getWrapper() != null;
            boolean targetBare = targetOp != null && !targetOp.isWrapperStyle() && targetOp.getWrapper() == null;

            WrapperHandler targetWrapperHandler = null;
            String targetDataBinding = getDataBinding(targetOp);
            targetWrapperHandler = getWrapperHandler(targetDataBinding, targetWrapped);      

            if ((!sourceWrapped &&!sourceBare) && targetWrapped) {
                // Unwrapped --> Wrapped
                WrapperInfo wrapper = targetOp.getWrapper();
                ElementInfo wrapperElement = wrapper.getOutputWrapperElement();
                List<ElementInfo> childElements = wrapper.getOutputChildElements();
                Class<?> targetWrapperClass = wrapper != null ? wrapper.getOutputWrapperClass() : null;

                Object[] outputs = null;            
                if ( !sourceOp.hasArrayWrappedOutput() ) {
                    outputs = new Object[] {response};
                } else {
                    outputs = (Object[])response;
                }      
                
                // If the source can be wrapped, wrapped it first
                if (sourceWrapperHandler != null) {
                    WrapperInfo sourceWrapperInfo = sourceOp.getWrapper();
                    DataType sourceWrapperType =
                        sourceWrapperInfo != null ? sourceWrapperInfo.getOutputWrapperType() : null;

                    if (sourceWrapperType != null && matches(sourceOp.getWrapper(), targetOp.getWrapper())) {
                        Class<?> sourceWrapperClass = sourceWrapperType.getPhysical();

                        Object sourceWrapper = sourceWrapperHandler.create(sourceOp, false);
                        if (sourceWrapper != null) {
                            if (!childElements.isEmpty()) {
                                // Set the return value
                                sourceWrapperHandler.setChildren(sourceWrapper,
                                                                 outputs,
                                                                 sourceOp,
                                                                 false);
                            }
                            Object targetWrapper =
                                mediator.mediate(sourceWrapper, sourceWrapperType, targetType.getLogical().get(0), context
                                    .getMetadata());
                            return targetWrapper;
                        }
                    }
                }
                Object targetWrapper = targetWrapperHandler.create(targetOp, false);

                if (childElements.isEmpty()) {
                    // void output
                    return targetWrapper;
                }

                // No source wrapper, so we want to transform the child and then wrap the child-level target with the
                // target wrapper handler.
                
                Object[] targetChildren = new Object[outputs.length];
                for (int i = 0; i < outputs.length; i++) {
                    DataType<XMLType> targetOutputType = wrapper.getUnwrappedOutputType().getLogical().get(i);
                    targetChildren[i] =
                        mediator.mediate(outputs[i], sourceType.getLogical().get(i), targetOutputType, context.getMetadata());
                }
                targetWrapperHandler.setChildren(targetWrapper,
                                                 targetChildren,
                                                 targetOp,
                                                 false);
                return targetWrapper;                                
                
            } else if (sourceWrapped && (!targetWrapped && !targetBare)) {
                // Wrapped to Unwrapped
                Object sourceWrapper = response;
                List<ElementInfo> childElements = sourceOp.getWrapper().getOutputChildElements();
                if (childElements.isEmpty()) {
                    // The void output
                    return null;
                }
                if (targetWrapperHandler != null) {
                    ElementInfo wrapperElement = sourceOp.getWrapper().getOutputWrapperElement();

                    // FIXME: This is a workaround for the wsdless support as it passes in child elements
                    // under the wrapper that only matches by position
                    if (sourceWrapperHandler.isInstance(sourceWrapper, sourceOp, false)) {

                        WrapperInfo targetWrapperInfo = targetOp.getWrapper();
                        DataType targetWrapperType =
                            targetWrapperInfo != null ? targetWrapperInfo.getOutputWrapperType() : null;

                        if (targetWrapperType != null && matches(sourceOp.getWrapper(), targetOp.getWrapper())) {
                            Object targetWrapper =
                                mediator.mediate(sourceWrapper, sourceType.getLogical().get(0), targetWrapperType, context
                                    .getMetadata());
                            List targetChildren = targetWrapperHandler.getChildren(targetWrapper, targetOp, false);
                            if (targetOp.hasArrayWrappedOutput()) {
                                return targetChildren.toArray();
                            } else {
                                return targetChildren.get(0);
                            }
                        }
                    }
                }
                
                // Otherwise we need to unwrap on the source side, and then transform each child                
                Object[] sourceChildren = sourceWrapperHandler.getChildren(sourceWrapper, sourceOp, false).toArray();
                Object[] target = new Object[sourceChildren.length];
                for (int i = 0; i < sourceChildren.length; i++) {
                    DataType<XMLType> childType = sourceOp.getWrapper().getUnwrappedOutputType().getLogical().get(i);
                    target[i] =
                        mediator.mediate(sourceChildren[i], childType, targetType.getLogical().get(i), context
                            .getMetadata());
                }

                if (targetOp.hasArrayWrappedOutput()) {
                    return target;
                } else {
                    if (target.length > 1 ) {
                        throw new IllegalStateException("Expecting only one output based on Operation model, found: " + 
                                                        target.length + " # of outputs.");
                    }
                    return target[0];
                }
            } else {                
                Object[] outputs = null;            
                if ( !sourceOp.hasArrayWrappedOutput() ) {
                    outputs = new Object[] {response};
                } else {
                    outputs = (Object[])response;
                }      
                Object[] target = new Object[outputs.length];
                for (int i = 0; i < outputs.length; i++) {
                    Object child =
                        mediator.mediate(outputs[i], sourceType.getLogical().get(i), targetType.getLogical().get(i), context
                            .getMetadata());
                    target[i] = child;
                }                
                if (targetOp.hasArrayWrappedOutput()) {
                    return target;
                } else {
                    if (target.length > 1 ) {
                        throw new IllegalStateException("Expecting only one output based on Operation model, found: " + 
                                                        target.length + " # of outputs.");
                    }
                    return target[0];
                }
            }
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
