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

package org.apache.tuscany.sca.databinding.json.jackson;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;
import org.apache.tuscany.sca.databinding.json.JSONDataBinding;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

/**
 * @version $Rev$ $Date$
 */
public class JSON2Object implements PullTransformer<Object, Object> {
    private ObjectMapper mapper;

    public JSON2Object() {
        super();
        mapper = JacksonHelper.createObjectMapper();
    }

    public Object transform(Object source, TransformationContext context) {
        if (source == null) {
            return null;
        }

        try {
            JavaType javaType = TypeFactory.type(context.getTargetDataType().getGenericType());
            if (source instanceof String) {
                return mapper.readValue((String)source, javaType);
            } else if (source instanceof JsonNode) {
                return mapper.treeToValue((JsonNode)source, context.getTargetDataType().getPhysical());
            } else if (source instanceof JsonParser) {
                return mapper.readValue((JsonParser)source, javaType);
            } else {
                return mapper.readValue(source.toString(), javaType);
            }
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
