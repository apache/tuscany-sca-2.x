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

package org.apache.tuscany.sca.implementation.spring.processor;

import java.lang.reflect.Method;

/**
 * This is the Spring runtime side stub for the corresponding Tuscany tie class.
 * It enables the Tuscany code to invoke methods on a Spring context without
 * needing to know about any Spring classes. See the PropertyValueTie class
 * in the implementation-spring module for what the tie does. 
 */
public class PropertyValueStub {

    private Object tie;
    private Method getPropertyObj;
    
    public PropertyValueStub(Object tie) {
        this.tie = tie;
        Class<?> tieClass = tie.getClass();
        try {
            getPropertyObj = tieClass.getMethod("getPropertyObj", new Class<?>[]{Class.class, String.class});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getPropertyObj(Class<?> propertyType, String name) {
        try {

            return getPropertyObj.invoke(tie, propertyType, name);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
