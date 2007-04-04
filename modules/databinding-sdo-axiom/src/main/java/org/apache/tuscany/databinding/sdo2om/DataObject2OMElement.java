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

import static org.apache.tuscany.databinding.sdo.SDODataBinding.ROOT_ELEMENT;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.databinding.sdo.SDOContextHelper;
import org.apache.tuscany.idl.DataType;
import org.apache.tuscany.idl.util.XMLType;
import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;

/**
 * SDO DataObject --> AXIOM OMElement transformer
 * 
 * @version $Rev$ $Date$
 */
public class DataObject2OMElement extends TransformerExtension<DataObject, OMElement> implements
    PullTransformer<DataObject, OMElement> {

    public OMElement transform(DataObject source, TransformationContext context) {
        HelperContext helperContext = SDOContextHelper.getHelperContext(context);
        SDODataSource dataSource = new SDODataSource(source, helperContext);
        OMFactory factory = OMAbstractFactory.getOMFactory();

        OMNamespace namespace = null;
        String localName = ROOT_ELEMENT.getLocalPart();
        if (context != null) {
            DataType dataType = context.getTargetDataType();
            Object logical = dataType == null ? null : dataType.getLogical();
            if (logical instanceof XMLType) {
                XMLType xmlType = (XMLType)logical;
                if (xmlType.isElement()) {
                    namespace =
                        factory.createOMNamespace(xmlType.getElementName().getNamespaceURI(), xmlType.getElementName()
                            .getPrefix());
                    localName = xmlType.getElementName().getLocalPart();
                }
            }
        }
        if (namespace == null) {
            namespace =
                factory.createOMNamespace(ROOT_ELEMENT.getNamespaceURI(), ROOT_ELEMENT.getPrefix());
        }

        OMElement element = factory.createOMElement(dataSource, localName, namespace);
        return element;
    }

    public Class getSourceType() {
        return DataObject.class;
    }

    public Class getTargetType() {
        return OMElement.class;
    }

    public int getWeight() {
        return 10;
    }

}
