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
package org.apache.tuscany.sca.databinding.sdo;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sdo.api.XMLStreamHelper;
import org.apache.tuscany.sdo.api.SDOUtil;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;

public class XMLStreamReader2DataObject extends BaseTransformer<XMLStreamReader, DataObject> implements
    PullTransformer<XMLStreamReader, DataObject> {

    public DataObject transform(XMLStreamReader source, TransformationContext context) {
        try {
            HelperContext helperContext = SDOContextHelper.getHelperContext(context);
            XMLStreamHelper streamHelper = SDOUtil.createXMLStreamHelper(helperContext);
            // The XMLStreamHelper requires that the reader is posistioned at
            // START_ELEMENT
            while (source.getEventType() != XMLStreamConstants.START_ELEMENT && source.hasNext()) {
                source.next();
            }
            DataObject target = streamHelper.loadObject(source);
            source.close();
            return target;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class getTargetType() {
        return DataObject.class;
    }

    public Class getSourceType() {
        return XMLStreamReader.class;
    }

    public int getWeight() {
        return 15;
    }

}
