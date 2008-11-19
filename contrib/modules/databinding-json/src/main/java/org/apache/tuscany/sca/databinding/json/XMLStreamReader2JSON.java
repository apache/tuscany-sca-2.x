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

import java.io.StringWriter;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.xml.XMLStreamSerializer;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;

/**
 * @version $Rev$ $Date$
 */
public class XMLStreamReader2JSON extends BaseTransformer<XMLStreamReader, Object> implements
    PullTransformer<XMLStreamReader, Object> {
    
    @Override
    protected Class<XMLStreamReader> getSourceType() {
        return XMLStreamReader.class;
    }

    @Override
    protected Class<Object> getTargetType() {
        return Object.class;
    }

    public Object transform(XMLStreamReader source, TransformationContext context) {
        try {
            StringWriter writer = new StringWriter();
            XMLStreamWriter jsonWriter = new BadgerFishXMLStreamWriter(writer);
            XMLStreamSerializer serializer = new XMLStreamSerializer();
            serializer.serialize(source, jsonWriter);
            source.close();
            Class type = null;
            if (context != null && context.getTargetDataType() != null) {
                type = context.getTargetDataType().getPhysical();
            }
            return JSONHelper.toJSON(writer.toString(), type);
        } catch (Exception e) {
            throw new TransformationException(e);
        } 
    }

    @Override
    public int getWeight() {
        return 500;
    }

    @Override
    public String getTargetDataBinding() {
        return JSONDataBinding.NAME;
    }

}
