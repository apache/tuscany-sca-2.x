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

import org.apache.tuscany.spi.databinding.WrapperHandler;
import org.apache.tuscany.spi.databinding.extension.DataBindingExtension;
import org.apache.tuscany.spi.model.DataType;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * 
 */
public class SDODataBinding extends DataBindingExtension {
    private WrapperHandler<Object> wrapperHandler;
    
    @Override
    public DataType introspect(Class<?> javaType) {
        if (javaType == DataObject.class) {
            return new DataType<QName>(getName(), javaType, null);
        }
        Type type = TypeHelper.INSTANCE.getType(javaType);
        if (type == null || type.isDataType()) {
            return null;
        }
        String namespace = type.getURI();
        String name = XSDHelper.INSTANCE.getLocalName(type);
        QName xmlType = new QName(namespace, name);
        DataType<QName> dataType = new DataType<QName>(getName(), javaType, xmlType);
        return dataType;
    }

    public SDODataBinding() {
        super(DataObject.class);
        wrapperHandler = new SDOWrapperHandler();
    }

    @Override
    public WrapperHandler getWrapperHandler() {
        return wrapperHandler;
    }

}
