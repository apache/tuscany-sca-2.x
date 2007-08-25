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

import java.math.BigDecimal;
import java.math.BigInteger;

import net.sf.saxon.value.DecimalValue;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.FloatValue;
import net.sf.saxon.value.IntegerValue;
import net.sf.saxon.value.ObjectValue;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.javabeans.SimpleJavaDataBinding;

/**
 * Transforms simple types and strings to Value objects needed by Saxon parser
 * @version $Rev$ $Date$
 */
public class SimpleType2ValueTransformer extends BaseTransformer<Object, Value> implements
    PullTransformer<Object, Value> {

    @Override
    public String getSourceDataBinding() {
        return SimpleJavaDataBinding.NAME;
    }

    @Override
    protected Class getSourceType() {
        return Object.class;
    }

    @Override
    protected Class getTargetType() {
        return Value.class;
    }

    @Override
    public int getWeight() {
        return 10000;
    }

    public Value transform(Object source, TransformationContext context) {
        Value result = null;
        if (source instanceof Integer) {
            result = IntegerValue.makeIntegerValue(BigInteger.valueOf(((Integer)source)));
        } else if (source instanceof Long) {
            result = IntegerValue.makeIntegerValue(BigInteger.valueOf(((Long)source)));
        } else if (source instanceof Short) {
            result = IntegerValue.makeIntegerValue(BigInteger.valueOf(((Short)source)));
        } else if (source instanceof Byte) {
            result = IntegerValue.makeIntegerValue(BigInteger.valueOf(((Byte)source)));
        } else if (source instanceof Double) {
            result = new DoubleValue((Double)source);
        } else if (source instanceof Float) {
            result = new FloatValue((Float)source);
        } else if (source instanceof BigDecimal) {
            result = new DecimalValue((BigDecimal)source);
        } else if (source instanceof String) {
            result = new StringValue(((String)source));
        } else {
            result = new ObjectValue(source);
        }

        return result;
    }
}
