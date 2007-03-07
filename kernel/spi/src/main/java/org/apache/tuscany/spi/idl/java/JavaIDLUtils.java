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
package org.apache.tuscany.spi.idl.java;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;

/**
 * Contains methods for mapping between an operation in a {@link org.apache.tuscany.spi.model.ServiceContract} and a
 * method defined by a Java interface
 *
 * @version $Rev$ $Date$
 */
public final class JavaIDLUtils {

    private JavaIDLUtils() {
    }

    /**
     * Searches an array of methods for a match against the given operation
     *
     * @param operation the operation to match
     * @param methods   the methods to match against
     * @return a matching method or null
     * @deprecated
     */
    public static Method findMethod(Operation<?> operation, Method[] methods) {
        for (Method method : methods) {
            if (match(operation, method)) {
                return method;
            }
        }
        return null;
    }

    /**
     * Searches a collection of operations for a match against the given method
     *
     * @param method     the method to match
     * @param operations the operations to match against
     * @return a matching operation or null
     */
    public static Operation findOperation(Method method, Collection<Operation<?>> operations) {
        for (Operation<?> operation : operations) {
            if (match(operation, method)) {
                return operation;
            }
        }
        return null;
    }

    /**
     * Determines if the given operation matches the given method
     *
     * @return true if the operation matches, false if does not
     */
    private static <T> boolean match(Operation<T> operation, Method method) {
        Class<?>[] params = method.getParameterTypes();
        DataType<List<DataType<T>>> inputType = operation.getInputType();
        List<DataType<T>> types = inputType.getLogical();
        boolean found = true;
        if (types.size() == params.length && method.getName().equals(operation.getName())) {
            for (int i = 0; i < params.length; i++) {
                Class<?> clazz = params[i];
                if (!clazz.equals(operation.getInputType().getLogical().get(i).getPhysical())) {
                    found = false;
                }
            }
        } else {
            found = false;
        }
        return found;

    }


}
