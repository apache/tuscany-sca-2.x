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
package org.apache.tuscany.databinding.sdo2om;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.extension.TransformerExtension;
import org.apache.tuscany.databinding.sdo.SDOContextHelper;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

/**
 * SDO XMLDocument --> AXIOM OMElement transformer
 * @version $Rev$ $Date$
 */
public class XMLDocument2OMElement extends TransformerExtension<XMLDocument, OMElement> implements
    PullTransformer<XMLDocument, OMElement> {

    public OMElement transform(XMLDocument source, TransformationContext context) {
        HelperContext helperContext = SDOContextHelper.getHelperContext(context);
        SDODataSource dataSource = new SDODataSource(source, helperContext);
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace namespace = factory.createOMNamespace(source.getRootElementURI(), source.getRootElementName());
        OMElement element = factory.createOMElement(dataSource, source.getRootElementName(), namespace);
        return element;
    }

    public Class getSourceType() {
        return XMLDocument.class;
    }

    public Class getTargetType() {
        return OMElement.class;
    }

    public int getWeight() {
        return 10;
    }

}
