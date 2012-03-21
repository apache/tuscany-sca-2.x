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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.tuscany.sca.databinding.PushTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.json.JSONDataBinding;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 */
public class JSON2OutputStream implements PushTransformer<Object, OutputStream> {

    public String getSourceDataBinding() {
        return JSONDataBinding.NAME;
    }

    public String getTargetDataBinding() {
        return "application/json" + "#" + OutputStream.class.getName();
    }

    public void transform(Object source, OutputStream sink, TransformationContext context) {
        if (source == null) {
            return;
        }
        if (source instanceof InputStream) {
            try {
                InputStream input = (InputStream) source;
                byte[] buffer = new byte[4096];
                int n = 0;
                while (-1 != (n = input.read(buffer))) {
                    sink.write(buffer, 0, n);
                }
                input.close();
            } catch(IOException e) {
                throw new TransformationException(e);
            }
        } else if (source instanceof JsonNode) {
            JacksonHelper.write((JsonNode)source, sink);
        } else if (source instanceof JsonParser) {
            JacksonHelper.write((JsonParser)source, sink);
        } else {
            if (source instanceof String || source instanceof JSONObject
                || source instanceof JSONArray
                || source instanceof org.codehaus.jettison.json.JSONObject
                || source instanceof org.codehaus.jettison.json.JSONArray) {
                try {
                    sink.write(source.toString().getBytes("UTF-8"));
                } catch (Exception e) {
                    throw new TransformationException(e);
                }
            } else {
                ObjectMapper mapper = JacksonHelper.createObjectMapper(source.getClass());
                try {
                    mapper.writeValue(sink, source);
                } catch (Throwable e) {
                    throw new TransformationException(e);
                }
            }
        }
    }

    public int getWeight() {
        return 50;
    }

}
