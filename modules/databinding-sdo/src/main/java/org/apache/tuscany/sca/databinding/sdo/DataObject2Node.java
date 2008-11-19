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
import javax.xml.transform.dom.DOMResult;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.impl.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class DataObject2Node extends BaseTransformer<DataObject, Node> implements
    PullTransformer<DataObject, Node> {

    public Node transform(DataObject source, TransformationContext context) {
        if (source == null) {
            return null;
        }
        try {
            HelperContext helperContext = SDOContextHelper.getHelperContext(context, true);
            XMLHelper xmlHelper = helperContext.getXMLHelper();
            QName elementName = SDOContextHelper.getElement(context);
            Document doc = DOMHelper.newDocument();
            DOMResult result = new DOMResult(doc);
            XMLDocument xmlDoc = xmlHelper.createDocument(source, elementName.getNamespaceURI(), elementName.getLocalPart());
            xmlHelper.save(xmlDoc, result, null);
            return doc.getDocumentElement();
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    @Override
    protected Class<DataObject> getSourceType() {
        return DataObject.class;
    }

    @Override
    protected Class<Node> getTargetType() {
        return Node.class;
    }

    @Override
    public int getWeight() {
        return 40;
    }

}
