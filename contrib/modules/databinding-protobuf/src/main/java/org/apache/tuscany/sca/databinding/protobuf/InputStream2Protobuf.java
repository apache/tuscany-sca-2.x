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

package org.apache.tuscany.sca.databinding.protobuf;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;

import com.google.protobuf.Message;

/**
 * 
 */
public class InputStream2Protobuf implements PullTransformer<InputStream, Message> {

    @Override
    public String getSourceDataBinding() {
        return "application/x-protobuf" + "#" + InputStream.class.getName();
    }

    @Override
    public String getTargetDataBinding() {
        // TODO Auto-generated method stub
        return ProtobufDatabinding.NAME;
    }

    @Override
    public int getWeight() {
        // TODO Auto-generated method stub
        return 10;
    }

    @Override
    public Message transform(InputStream source, TransformationContext context) {
        try {
            Class<?> type = context.getTargetDataType().getPhysical();
            Method method = type.getMethod("parseFrom", InputStream.class);
            Object result = method.invoke(null, source);
            return (Message)result;
        } catch (Throwable e) {
            throw new TransformationException(e);
        }
    }

}
