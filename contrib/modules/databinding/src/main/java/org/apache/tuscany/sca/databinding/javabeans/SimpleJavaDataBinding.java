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

package org.apache.tuscany.sca.databinding.javabeans;


import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.impl.BaseDataBinding;
import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.databinding.xml.XMLStringDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * DataBinding for Java simple types
 *
 * @version $Rev$ $Date$
 */
public class SimpleJavaDataBinding extends BaseDataBinding {
    public static final String NAME = "java:simpleType";

    public SimpleJavaDataBinding() {
        super(NAME, Object.class);
    }

    @Override
    public Object copy(Object arg, DataType dataType, Operation operation) {
        if (arg instanceof byte[]) {
            return ((byte[])arg).clone();
        }
        return arg;
    }

    @Override
    public boolean introspect(DataType type, Operation operation) {
        Class<?> cls = type.getPhysical();
        if (cls == Object.class) {
            return false;
        }
        // HACK: [rfeng] By pass the one know to XMLString
        String db = type.getDataBinding();
        if (db != null && (XMLStringDataBinding.NAME.equals(db))) {
            return false;
        }
        if (SimpleTypeMapperImpl.JAVA2XML.keySet().contains(cls)) {
            type.setDataBinding(NAME);
            QName elementName = null;
            Object logical = type.getLogical();
            if (logical instanceof XMLType) {
                elementName = ((XMLType)logical).getElementName();
            }
            TypeInfo typeInfo = SimpleTypeMapperImpl.getXMLType(cls);
            type.setLogical(new XMLType(elementName, typeInfo == null ? null : typeInfo.getQName()));
            return true;
        } else {
            return false;
        }
    }

}
