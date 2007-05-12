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

import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.impl.BaseTransformer;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLHelper;

public class DataObject2String extends BaseTransformer<DataObject, String> implements
    PullTransformer<DataObject, String> {

    public String transform(DataObject source, TransformationContext context) {
        try {
            HelperContext helperContext = SDOContextHelper.getHelperContext(context);
            XMLHelper xmlHelper = helperContext.getXMLHelper();
            QName elementName = SDOContextHelper.getElement(context.getSourceDataType());
            return xmlHelper.save(source, elementName.getNamespaceURI(), elementName.getLocalPart());
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class getSourceType() {
        return DataObject.class;
    }

    public Class getTargetType() {
        return String.class;
    }

    public int getWeight() {
        return 40;
    }

}
