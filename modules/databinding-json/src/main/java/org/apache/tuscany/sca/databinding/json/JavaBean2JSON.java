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

package org.apache.tuscany.sca.databinding.json;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;

import com.metaparadigm.jsonrpc.JSONSerializer;
import com.metaparadigm.jsonrpc.SerializerState;

public class JavaBean2JSON extends BaseTransformer<Object, Object> implements PullTransformer<Object, Object> {
    private JSONSerializer serializer;
    
    public JavaBean2JSON() {
        serializer = new JSONSerializer();
        try {
            serializer.registerDefaultSerializers();
        } catch (Exception e) {
            throw new TransformationException(e);
        }
        serializer.setMarshallClassHints(true);
        serializer.setMarshallNullAttributes(true);
    }

    @Override
    public String getSourceDataBinding() {
        return JavaBeansDataBinding.NAME;
    }

    @Override
    protected Class<Object> getSourceType() {
        return Object.class;
    }

    @Override
    public String getTargetDataBinding() {
        return JSONDataBinding.NAME;
    }

    @Override
    protected Class<Object> getTargetType() {
        return Object.class;
    }

    public Object toJSON(Object source) throws Exception {
        if (source == null) {
            return org.json.JSONObject.NULL;
        }

        SerializerState state = new SerializerState();
        return serializer.marshall(state, source);
    }

    public Object transform(Object source, TransformationContext context) {
        try {
            return toJSON(source);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
