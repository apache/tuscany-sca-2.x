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
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;

import com.metaparadigm.jsonrpc.JSONSerializer;
import com.metaparadigm.jsonrpc.SerializerState;

/**
 * @version $Rev$ $Date$
 */
public class JSON2JavaBean implements PullTransformer<Object, Object> {
    private JSONSerializer serializer;

    public JSON2JavaBean() {
        super();
        serializer = new JSONSerializer();
        try {
            serializer.registerDefaultSerializers();
        } catch (Exception e) {
            throw new TransformationException(e);
        }
        serializer.setMarshallClassHints(true);
        serializer.setMarshallNullAttributes(true);
    }

    public Object transform(Object source, TransformationContext context) {
        if (source == null) {
            return null;
        }

        try {
            SerializerState state = new SerializerState();
            return serializer.unmarshall(state, context.getTargetDataType().getPhysical(), source);
        } catch (Exception e) {
            throw new TransformationException(e);
        }

    }

    public String getSourceDataBinding() {
        return JSONDataBinding.NAME;
    }

    public String getTargetDataBinding() {
        return JavaBeansDataBinding.NAME;
    }

    public int getWeight() {
        return 5000;
    }
}
