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

import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.databinding.impl.BaseDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sdo.api.SDOUtil;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

/**
 * SDO Databinding
 * 
 * @version $Rev$ $Date$
 */
public class SDODataBinding extends BaseDataBinding {
    public static final String NAME = DataObject.class.getName();

    public static final String ROOT_NAMESPACE = "commonj.sdo";
    public static final QName ROOT_ELEMENT = new QName(ROOT_NAMESPACE, "dataObject");

    private WrapperHandler<Object> wrapperHandler;
    private XMLTypeHelper xmlTypeHelper;

    public SDODataBinding() {
        super(NAME, DataObject.class);
        wrapperHandler = new SDOWrapperHandler();
        xmlTypeHelper = new SDOTypeHelper();
    }

    @Override
    public boolean introspect(DataType dataType, final Operation operation) {
        final Class javaType = dataType.getPhysical();
        // Allow privileged access to read system properties. Requires PropertyPermission
        // java.specification.version read in security policy.
        final HelperContext context = AccessController.doPrivileged(new PrivilegedAction<HelperContext>() {
            public HelperContext run() {
                return SDOContextHelper.getHelperContext(operation);
            }
        });

        final Type type = context.getTypeHelper().getType(javaType);
        if (type == null) {
            // FIXME: Need a better to test dynamic SDO
            if (DataObject.class.isAssignableFrom(javaType)) {
                // Dynamic SDO
                dataType.setDataBinding(getName());
                if (dataType.getLogical() == null) {
                    dataType.setLogical(XMLType.UNKNOWN);
                }
                return true;
            }
            return false;
        } 
        if (type.isDataType()) {
            // FIXME: Ignore simple types?
            return false;
        }

        // Found a SDO type, replace the default context with a private one
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                if (context == SDOContextHelper.getDefaultHelperContext()) {
                    HelperContext newContext = SDOUtil.createHelperContext();
                    SDOContextHelper.register(newContext, type);
                    if (operation != null) {
                        operation.getInputType().setMetaData(HelperContext.class, newContext);
                    }
                } else {
                    SDOContextHelper.register(context, type);
                }
                return null;
            }
        });

        String namespace = type.getURI();
        String name = context.getXSDHelper().getLocalName(type);
        QName xmlType = new QName(namespace, name);
        dataType.setDataBinding(getName());
        QName elementName = null;
        Object logical = dataType.getLogical();
        if (logical instanceof XMLType) {
            elementName = ((XMLType)logical).getElementName();
        }
        dataType.setLogical(new XMLType(elementName, xmlType));

        return true;
    }

    @Override
    public WrapperHandler getWrapperHandler() {
        return wrapperHandler;
    }

    @Override
    public XMLTypeHelper getXMLTypeHelper() {
        // return new SDOTypeHelper();
        return xmlTypeHelper;
    }

    @Override
    public Object copy(Object arg, DataType dataType, Operation operation) {
        HelperContext context = SDOContextHelper.getHelperContext(operation);
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
            return super.copy(arg, dataType, operation);
        }
    }

}
