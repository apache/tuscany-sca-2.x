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
import org.codehaus.jettison.json.JSONObject;

/**
 * @version $Rev$ $Date$
 */
public class String2JSON extends BaseTransformer<String, JSONObject> implements
    PullTransformer<String, JSONObject> {
    
    @Override
    protected Class getSourceType() {
        return String.class;
    }

    @Override
    protected Class getTargetType() {
        return JSONObject.class;
    }

    public JSONObject transform(String source, TransformationContext context) {
        try {
            return new JSONObject(source);
        } catch (Exception e) {
            throw new TransformationException(e);
        } 
    }

    public int getWeight() {
        return 500;
    }

}
