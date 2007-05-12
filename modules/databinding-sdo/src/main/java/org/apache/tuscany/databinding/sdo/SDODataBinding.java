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

import java.lang.annotation.Annotation;

import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.ExceptionHandler;
import org.apache.tuscany.databinding.SimpleTypeMapper;
import org.apache.tuscany.databinding.WrapperHandler;
import org.apache.tuscany.databinding.impl.BaseDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.impl.HelperProvider;

/**
 * SDO Databinding
 * 
 * @version $Reve$ $Date$
 */
public class SDODataBinding extends BaseDataBinding {
    public static final String NAME = DataObject.class.getName();
    public static final String[] ALIASES = new String[] {"sdo"};
    
    public static final String ROOT_NAMESPACE = "commonj.sdo";
    public static final QName ROOT_ELEMENT = new QName(ROOT_NAMESPACE, "dataObject");

    private WrapperHandler<Object> wrapperHandler;

    public SDODataBinding() {
        super(NAME, ALIASES, DataObject.class);
        wrapperHandler = new SDOWrapperHandler();
    }

    @Override
    public boolean introspect(DataType dataType, Annotation[] annotations) {
        Object physical = dataType.getPhysical();
        if (!(physical instanceof Class)) {
            return false;
        }
        Class javaType = (Class)physical;
        HelperContext context = HelperProvider.getDefaultContext();
        // FIXME: Need a better to test dynamic SDO
        if (DataObject.class.isAssignableFrom(javaType)) {
            // Dynamic SDO
            dataType.setDataBinding(getName());
            dataType.setLogical(XMLType.UNKNOWN);
            return true;
        }
        // FIXME: We need to access HelperContext
        Type type = context.getTypeHelper().getType(javaType);
        if (type == null) {
            return false;
        }
        if (type.isDataType()) {
            // FIXME: Ignore simple types?
            return false;
        }
        String namespace = type.getURI();
        String name = context.getXSDHelper().getLocalName(type);
        QName xmlType = new QName(namespace, name);
        dataType.setDataBinding(getName());
        dataType.setLogical(new XMLType(null, xmlType));
        return true;
    }

    @Override
    public WrapperHandler getWrapperHandler() {
        return wrapperHandler;
    }

    public SimpleTypeMapper getSimpleTypeMapper() {
        return new SDOSimpleTypeMapper();
    }

    @Override
    public Object copy(Object arg) {
        HelperContext context = HelperProvider.getDefaultContext();
        CopyHelper copyHelper = context.getCopyHelper();
        if (arg instanceof XMLDocument) {
            XMLDocument document = (XMLDocument)arg;
            DataObject dataObject = copyHelper.copy(document.getRootObject());
            return context.getXMLHelper().createDocument(dataObject,
                                                         document.getRootElementURI(),
                                                         document.getRootElementName());
        } else if (arg instanceof DataObject) {
            return context.getCopyHelper().copy((DataObject)arg);
        } else {
            return super.copy(arg);
        }
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new SDOExceptionHandler();
    }

}
