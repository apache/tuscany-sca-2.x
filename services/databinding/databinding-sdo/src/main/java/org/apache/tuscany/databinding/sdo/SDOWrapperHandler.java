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

package org.apache.tuscany.databinding.sdo;

import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.idl.WrapperHandler;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.spi.model.DataType;
import org.apache.ws.commons.schema.XmlSchemaElement;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 * SDO Wrapper Handler
 */
public class SDOWrapperHandler implements WrapperHandler<Object> {

    /**
     * @see org.apache.tuscany.databinding.idl.WrapperHandler#create(org.apache.ws.commons.schema.XmlSchemaElement,
     *      TransformationContext)
     */
    public Object create(XmlSchemaElement element, TransformationContext context) {
        TypeHelper typeHelper = TypeHelper.INSTANCE;
        if (context != null) {
            DataType targetType = context.getTargetDataType();
            if (targetType != null) {
                typeHelper = (TypeHelper) targetType.getMetadata(TypeHelper.class.getName());
                if (typeHelper == null) {
                    typeHelper = TypeHelper.INSTANCE;
                }
            }
        }
        QName typeName = element.getSchemaTypeName();
        DataFactory dataFactory = SDOUtil.createDataFactory(typeHelper);
        DataObject root = dataFactory.create(typeName.getNamespaceURI(), typeName.getLocalPart());
        XMLHelper xmlHelper = SDOUtil.createXMLHelper(typeHelper);
        return xmlHelper.createDocument(root, element.getQName().getNamespaceURI(), element.getQName().getLocalPart());
    }

    /**
     * @see org.apache.tuscany.databinding.idl.WrapperHandler#getChild(java.lang.Object, int,
     *      org.apache.ws.commons.schema.XmlSchemaElement)
     */
    public Object getChild(Object wrapper, int i, XmlSchemaElement element) {
        DataObject wrapperDO =
                (wrapper instanceof XMLDocument) ? ((XMLDocument) wrapper).getRootObject() : (DataObject) wrapper;
        return wrapperDO.get(element.getName());
    }

    /**
     * @see org.apache.tuscany.databinding.idl.WrapperHandler#setChild(java.lang.Object, int,
     *      org.apache.ws.commons.schema.XmlSchemaElement, java.lang.Object)
     */
    public void setChild(Object wrapper, int i, XmlSchemaElement childElement, Object value) {
        DataObject wrapperDO =
                (wrapper instanceof XMLDocument) ? ((XMLDocument) wrapper).getRootObject() : (DataObject) wrapper;
        wrapperDO.set(childElement.getName(), value);
    }

}
