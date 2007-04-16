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

package org.apache.tuscany.databinding.impl;

import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.extension.TransformerExtension;
import org.apache.tuscany.interfacedef.DataType;

/**
 * This is a special transformer to transform the output from one IDL to the
 * other one
 */
public class Group2GroupTransformer extends TransformerExtension<Object, Object> implements
    PullTransformer<Object, Object> {

    protected Mediator mediator;

    /**
     * @param wrapperHandler
     */
    public Group2GroupTransformer() {
        super();
    }

    /**
     * @param mediator the mediator to set
     */
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public String getSourceDataBinding() {
        return GroupDataBinding.NAME;
    }

    @Override
    public String getTargetDataBinding() {
        return GroupDataBinding.NAME;
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
    public Object transform(Object source, TransformationContext context) {
        DataType<DataType> sourceType = context.getSourceDataType();
        DataType<DataType> targetType = context.getTargetDataType();

        return mediator.mediate(source, sourceType.getLogical(), targetType.getLogical(), context.getMetadata());
    }

}
