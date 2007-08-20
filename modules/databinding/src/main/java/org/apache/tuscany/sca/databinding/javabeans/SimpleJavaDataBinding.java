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

import java.lang.annotation.Annotation;

import org.apache.tuscany.sca.databinding.impl.BaseDataBinding;
import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * DataBinding for Java simple types
 */
public class SimpleJavaDataBinding extends BaseDataBinding {
    public static final String NAME = "java:simpleType";

    public SimpleJavaDataBinding() {
        super(NAME, Object.class);
    }

    @Override
    public Object copy(Object arg) {
        return arg;
    }

    @Override
    public boolean introspect(DataType type, Annotation[] annotations) {
        Class<?> cls = type.getPhysical();
        if (cls == Object.class) {
            return false;
        }
        if (SimpleTypeMapperImpl.JAVA2XML.keySet().contains(cls)) {
            type.setDataBinding(getName());
            type.setLogical(new XMLType(SimpleTypeMapperImpl.getXMLType(cls)));
            return true;
        } else {
            return false;
        }
    }

}
