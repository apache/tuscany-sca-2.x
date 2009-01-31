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

package org.apache.tuscany.sca.binding.corba.testing.service.mocks;

import java.lang.reflect.Type;

import org.apache.tuscany.sca.interfacedef.DataType;

/**
 * Mock DataType implementation. Only few methods needs to be implemented.
 */
public class TestDataType<L> implements DataType<L> {

    public Class<?> typeClass;
    public L logical;

    public TestDataType(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public TestDataType(Class<?> typeClass, L logical) {
        this.typeClass = typeClass;
        this.logical = logical;
    }

    public String getDataBinding() {
        return null;
    }

    public Type getGenericType() {
        return null;
    }

    public L getLogical() {
        return logical;
    }

    public <T> T getMetaData(Class<T> arg0) {
        return null;
    }

    public Class<?> getPhysical() {
        return typeClass;
    }

    public void setDataBinding(String arg0) {

    }

    public void setGenericType(Type arg0) {

    }

    public void setLogical(L arg0) {

    }

    public <T> void setMetaData(Class<T> arg0, T arg1) {

    }

    public void setPhysical(Class<?> arg0) {

    }

    @Override
    public Object clone() {
        return null;
    }

}
