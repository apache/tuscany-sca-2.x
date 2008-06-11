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

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sdo.api.SDOUtil;
import org.apache.tuscany.sdo.api.XMLStreamHelper;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class DataObject2XMLStreamReader extends BaseTransformer<DataObject, XMLStreamReader> implements
        PullTransformer<DataObject, XMLStreamReader> {

    public XMLStreamReader transform(final DataObject source, TransformationContext context) {
        if (source == null) {
            return null;
        }            
        try {
            HelperContext helperContext = SDOContextHelper.getHelperContext(context, true);
            XMLStreamHelper streamHelper = SDOUtil.createXMLStreamHelper(helperContext);
            final QName elementName = SDOContextHelper.getElement(context);
            final XMLHelper xmlHelper = helperContext.getXMLHelper();
            // Allow privileged access to read properties. REquires java.util.PropertyPermission
            // XML.load.form.lax read in security policy.
            XMLDocument document = AccessController.doPrivileged(new PrivilegedAction<XMLDocument>() {
                public XMLDocument run() {
                    return xmlHelper.createDocument(source, elementName.getNamespaceURI(), elementName.getLocalPart());
                }
            });
                    
            return streamHelper.createXMLStreamReader(document);
        } catch (XMLStreamException e) {
            // TODO: Add context to the exception
            throw new TransformationException(e);
        }
    }

    @Override
    protected Class<DataObject> getSourceType() {
        return DataObject.class;
    }

    @Override
    protected Class<XMLStreamReader> getTargetType() {
        return XMLStreamReader.class;
    }

    @Override
    public int getWeight() {
        return 10;
    }

}
