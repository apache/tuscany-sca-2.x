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

package org.apache.tuscany.container.crud;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.TargetInvokerExtension;
import org.apache.tuscany.spi.model.Operation;

/**
 * @version $Rev$ $Date$
 */
public class CRUDTargetInvoker extends TargetInvokerExtension {
    private static final Map<String, Method> METHODS = new HashMap<String, Method>();
    static {
        for (Method m : CRUD.class.getMethods()) {
            METHODS.put(m.getName(), m);
        }
    }
    private Method operation;
    private CRUD instance;

    public CRUDTargetInvoker(Operation operation, String directory) {
        this.operation = METHODS.get(operation.getName());
        this.instance = new CRUDImpl(directory);

    }

    public Object invokeTarget(Object body, short sequence, WorkContext context) throws InvocationTargetException {
        try {
            return operation.invoke(instance, (Object[])body);
        } catch (IllegalArgumentException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

}
