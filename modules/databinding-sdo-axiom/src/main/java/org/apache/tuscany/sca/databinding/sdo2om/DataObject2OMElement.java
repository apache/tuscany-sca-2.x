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
package org.apache.tuscany.sca.databinding.sdo2om;

import static org.apache.tuscany.sca.databinding.sdo.SDODataBinding.ROOT_ELEMENT;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.sdo.SDOContextHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

/**
 * SDO DataObject --> AXIOM OMElement transformer
 * 
 * @version $Rev$ $Date$
 */
public class DataObject2OMElement extends BaseTransformer<DataObject, OMElement> implements
    PullTransformer<DataObject, OMElement> {

    public OMElement transform(DataObject source, TransformationContext context) {
        HelperContext helperContext = SDOContextHelper.getHelperContext(context);
        OMFactory factory = OMAbstractFactory.getOMFactory();

        QName name  = ROOT_ELEMENT;
        if (context != null) {
            DataType dataType = context.getTargetDataType();
            Object logical = dataType == null ? null : dataType.getLogical();
            if (logical instanceof XMLType) {
                XMLType xmlType = (XMLType)logical;
                if (xmlType.isElement()) {
                    name = xmlType.getElementName();
                }
            }
        }

        XMLDocument document = helperContext.getXMLHelper().createDocument(source,
                                                                           name.getNamespaceURI(),
                                                                           name.getLocalPart());
        SDODataSource dataSource = new SDODataSource(document, helperContext);
        OMElement element = AxiomHelper.createOMElement(factory, name, dataSource);
        return element;
    }

    @Override
    public Class getSourceType() {
        return DataObject.class;
    }

    @Override
    public Class getTargetType() {
        return OMElement.class;
    }

    @Override
    public int getWeight() {
        return 10;
    }

}
