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

import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.FaultExceptionMapper;

/**
 * This is a special transformer to transform the exception from one IDL to the
 * other one
 *
 * @version $Rev$ $Date$
 */
public class Exception2ExceptionTransformer extends BaseTransformer<Throwable, Throwable> implements
    PullTransformer<Throwable, Throwable> {

    protected Mediator mediator;
    protected FaultExceptionMapper faultExceptionMapper;

    public Exception2ExceptionTransformer(Mediator mediator, FaultExceptionMapper faultExceptionMapper) {
        super();
        this.mediator = mediator;
        this.faultExceptionMapper = faultExceptionMapper;
    }

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
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.impl.BaseTransformer#getSourceType()
     */
    @Override
    protected Class<Throwable> getSourceType() {
        return Throwable.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.impl.BaseTransformer#getTargetType()
     */
    @Override
    protected Class<Throwable> getTargetType() {
        return Throwable.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.Transformer#getWeight()
     */
    @Override
    public int getWeight() {
        return 10000;
    }

    @SuppressWarnings("unchecked")
    public Throwable transform(Throwable source, TransformationContext context) {
        DataType<DataType> sourceType = context.getSourceDataType();

        DataType<DataType> targetType = context.getTargetDataType();

        Object sourceFaultInfo = faultExceptionMapper.getFaultInfo(source, sourceType.getLogical().getPhysical(), context.getSourceOperation());
        Object targetFaultInfo =
            mediator.mediate(sourceFaultInfo, sourceType.getLogical(), targetType.getLogical(), context.getMetadata());

        Throwable targetException =
            faultExceptionMapper.wrapFaultInfo(targetType, source.getMessage(), targetFaultInfo, source.getCause(), context.getTargetOperation());

        // FIXME
        return targetException == null ? source : targetException;

    }

    public void setFaultExceptionMapper(FaultExceptionMapper faultExceptionMapper) {
        this.faultExceptionMapper = faultExceptionMapper;
    }
}
