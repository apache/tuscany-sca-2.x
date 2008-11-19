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
package org.apache.tuscany.sca.databinding.saxon;

import net.sf.saxon.value.FloatValue;
import net.sf.saxon.value.ObjectValue;
import net.sf.saxon.value.Value;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;

/**
 * Transforms generic java objects to Value objects needed by Saxon parser.
 *
 * @version $Rev$ $Date$
 */
public class Object2ValueTransformer extends BaseTransformer<Object, Value> implements PullTransformer<Object, Value> {

    @Override
    protected Class<Object> getSourceType() {
        return Object.class;
    }

    @Override
    protected Class<Value> getTargetType() {
        return Value.class;
    }

    @Override
    public String getSourceDataBinding() {
        return JavaBeansDataBinding.NAME;
    }

    @Override
    public int getWeight() {
        return 10000;
    }

    public Value transform(Object source, TransformationContext context) {
        // WORKAROUND for ClassCastException in ObjectValue.toJavaObject(float)
        if (source instanceof Float) {
            return new FloatValue(((Float)source).floatValue());
        }
        return new ObjectValue(source);
    }

}
