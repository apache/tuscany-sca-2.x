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

package org.apache.tuscany.databinding.json;

import java.io.StringWriter;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;
import org.json.JSONObject;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
@Service(Transformer.class)
public class XMLStreamReader2JSON extends TransformerExtension<XMLStreamReader, JSONObject> implements
    PullTransformer<XMLStreamReader, JSONObject> {
    
    @Override
    protected Class getSourceType() {
        return XMLStreamReader.class;
    }

    @Override
    protected Class getTargetType() {
        return JSONObject.class;
    }

    public JSONObject transform(XMLStreamReader source, TransformationContext context) {
        try {
            StringWriter writer = new StringWriter();
            XMLStreamWriter jsonWriter = new BadgerFishXMLStreamWriter(writer);
            XMLStreamSerializer serializer = new XMLStreamSerializer();
            serializer.serialize(source, jsonWriter);
            return new JSONObject(writer.toString());
        } catch (Exception e) {
            throw new TransformationException(e);
        } 
    }

    public int getWeight() {
        return 10;
    }

}
