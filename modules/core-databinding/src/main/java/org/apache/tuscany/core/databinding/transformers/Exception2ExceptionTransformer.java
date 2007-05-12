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

import org.apache.tuscany.databinding.DataBinding;
import org.apache.tuscany.databinding.ExceptionHandler;
import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * This is a special transformer to transform the exception from one IDL to the
 * other one
 */
@Service(Transformer.class)
public class Exception2ExceptionTransformer extends BaseTransformer<Object[], Object[]> implements
    PullTransformer<Exception, Exception> {

    protected Mediator mediator;

    public Exception2ExceptionTransformer() {
        super();
    }

    @Override
    public String getSourceDataBinding() {
        return DataBinding.IDL_FAULT;
    }

    @Override
    public String getTargetDataBinding() {
        return DataBinding.IDL_FAULT;
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
        return Exception.class;
    }

    /**
     * @see org.apache.tuscany.databinding.impl.BaseTransformer#getTargetType()
     */
    @Override
    protected Class getTargetType() {
        return Exception.class;
    }

    /**
     * @see org.apache.tuscany.databinding.Transformer#getWeight()
     */
    public int getWeight() {
        return 10000;
    }

    @SuppressWarnings("unchecked")
    public Exception transform(Exception source, TransformationContext context) {
        DataType<DataType> sourceType = context.getSourceDataType();

        DataType<DataType> targetType = context.getTargetDataType();

        ExceptionHandler exceptionHandler = getExceptionHandler(sourceType);
        if (exceptionHandler == null) {
            return source;
        }

        Object sourceFaultInfo = exceptionHandler.getFaultInfo(source);
        Object targetFaultInfo =
            mediator.mediate(sourceFaultInfo, sourceType.getLogical(), targetType.getLogical(), context.getMetadata());

        ExceptionHandler targetHandler = getExceptionHandler(targetType);

        if (targetHandler != null) {
            Exception targetException =
                targetHandler.createException(targetType, source.getMessage(), targetFaultInfo, source.getCause());
            return targetException;
        }

        // FIXME
        return source;

    }

    private ExceptionHandler getExceptionHandler(DataType<DataType> targetType) {
        DataType targetFaultType = (DataType)targetType.getLogical();
        DataBinding targetDataBinding =
            mediator.getDataBindings().getDataBinding(targetFaultType.getDataBinding());
        if (targetDataBinding == null) {
            return null;
        }
        ExceptionHandler targetHandler = targetDataBinding.getExceptionHandler();
        return targetHandler;
    }
}
