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

package org.apache.tuscany.binding.jms.databinding;

import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.Transformer;
import org.osoa.sca.annotations.Service;

/**
 * This is a special transformer to transform the output from one IDL to the
 * other one
 */
@Service(Transformer.class)
public class JmsOutput2OutputTransformer extends AbstractJmsTransformer<Object> {

    /**
     * @param wrapperHandler
     */
    public JmsOutput2OutputTransformer() {
        super();
    }

    @Override
    public String getSourceDataBinding() {
        return JMS_OUTPUT;
    }

    @Override
    public String getTargetDataBinding() {
        return IDL_OUTPUT;
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

    @SuppressWarnings("unchecked")
    public Object transform(Object response, TransformationContext context) {
        return read((byte[])response);
    }

}
