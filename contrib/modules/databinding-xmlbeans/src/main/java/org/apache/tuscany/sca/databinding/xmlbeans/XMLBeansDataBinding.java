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

package org.apache.tuscany.sca.databinding.xmlbeans;

import java.lang.reflect.Field;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.databinding.impl.BaseDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.osoa.sca.ServiceRuntimeException;

/**
 * XMLBeans DataBinding
 *
 * @version $Rev$ $Date$
 */
public class XMLBeansDataBinding extends BaseDataBinding {
    public static final String NAME = XmlObject.class.getName();
    public static final String[] ALIASES = {"xmlbeans"};

    public XMLBeansDataBinding() {
        super(NAME, XmlObject.class);
    }

    @Override
    public WrapperHandler getWrapperHandler() {
        return new XMLBeansWrapperHandler();
    }

    @Override
    public boolean introspect(DataType type, Operation operation) {
        if (XmlObject.class.isAssignableFrom(type.getPhysical())) {
            Class<?> cls = type.getPhysical();
            SchemaType schemaType = null;
            try {
                Field f = cls.getField("type");
                schemaType = (SchemaType)f.get(null);
            } catch (Throwable e) {
                throw new ServiceRuntimeException(e);
            }
            QName typeName = schemaType.getName();
            Object logical = type.getLogical();
            QName elementName = null;
            if (logical instanceof XMLType) {
                elementName = ((XMLType)logical).getElementName();
            }
            type.setLogical(new XMLType(elementName, typeName));
            type.setMetaData(SchemaType.class, schemaType);
            return true;
        }
        return false;
    }

    @Override
    public Object copy(Object object, DataType dataType, Operation operation) {
        return ((XmlObject)object).copy();
    }

}
