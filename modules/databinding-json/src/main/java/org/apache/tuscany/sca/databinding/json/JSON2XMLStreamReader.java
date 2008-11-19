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

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader;
import org.codehaus.jettison.json.JSONObject;

/**
 * @version $Rev$ $Date$
 */
public class JSON2XMLStreamReader extends BaseTransformer<Object, XMLStreamReader> implements
    PullTransformer<Object, XMLStreamReader> {
    
    @Override
    protected Class<Object> getSourceType() {
        return Object.class;
    }

    @Override
    protected Class<XMLStreamReader> getTargetType() {
        return XMLStreamReader.class;
    }

    public XMLStreamReader transform(Object source, TransformationContext context) {
        try {
            JSONObject json = JSONHelper.toJettison(source);
            return new BadgerFishXMLStreamReader(json);
        } catch (Exception e) {
            throw new TransformationException(e);
        } 
    }

    @Override
    public int getWeight() {
        return 500;
    }
    @Override
    public String getSourceDataBinding() {
        return JSONDataBinding.NAME;
    }

}
