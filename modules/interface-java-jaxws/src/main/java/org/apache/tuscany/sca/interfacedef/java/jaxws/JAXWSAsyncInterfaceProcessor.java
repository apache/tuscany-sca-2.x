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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;

public class JAXWSAsyncInterfaceProcessor implements JavaInterfaceVisitor {
    private static String ASYNC = "Async";

    public JAXWSAsyncInterfaceProcessor(ExtensionPointRegistry registry) {
    }

    public void visitInterface(JavaInterface javaInterface) throws InvalidInterfaceException {
        List<Operation> validOperations = new ArrayList<Operation>();
        List<Operation> asyncOperations = new ArrayList<Operation>();

        validOperations.addAll(javaInterface.getOperations());
        for (Operation o : javaInterface.getOperations()) {
            if (!o.getName().endsWith(ASYNC)) {
                JavaOperation op = (JavaOperation)o;
                if (op.getJavaMethod().getName().endsWith(ASYNC)) {
                    continue;
                }
                for (Operation asyncOp : getAsyncOperations(javaInterface.getOperations(), op)) {
                    if (isJAXWSAsyncPoolingOperation(op, asyncOp) || isJAXWSAsyncCallbackOperation(op, asyncOp)) {
                        validOperations.remove(asyncOp);
                        asyncOperations.add(asyncOp);
                    }
                }
            }
        }

        javaInterface.getOperations().clear();
        javaInterface.getOperations().addAll(validOperations);

        javaInterface.getAttributes().put("JAXWS-ASYNC-OPERATIONS", asyncOperations);
    }

    /**
     * The additional client-side asynchronous polling and callback methods defined by JAX-WS are recognized in a Java interface as follows:
     * For each method M in the interface, if another method P in the interface has
     * 
     * a) a method name that is M's method name with the characters "Async" appended, and
     * b) the same parameter signature as M, and
     * c)a return type of Response<R> where R is the return type of M
     * 
     * @param operation
     * @param asyncOperation
     * @return
     */
    private static boolean isJAXWSAsyncPoolingOperation(Operation operation, Operation asyncOperation) {

        if (asyncOperation.getOutputType().getLogical().size() == 0 || Response.class != asyncOperation.getOutputType().getLogical().get(0).getPhysical()) {
            // The return type is not Response<T>
            return false;
        }

        //the same parameter signature as M
        List<DataType> operationInputType = operation.getInputType().getLogical();
        List<DataType> asyncOperationInputType = asyncOperation.getInputType().getLogical();
        int size = operationInputType.size();
        if (asyncOperationInputType.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!isCompatible(operationInputType.get(i), asyncOperationInputType.get(i))) {
                return false;
            }
        }

        //a return type of Response<R> where R is the return type of M
        DataType<?> operationOutputType = null;
        if (operation.getOutputType()!= null && operation.getOutputType().getLogical() != null && operation.getOutputType().getLogical().size() > 0) {
            operationOutputType = operation.getOutputType().getLogical().get(0);
        }
        DataType<?> asyncOperationOutputType = asyncOperation.getOutputType().getLogical().get(0);

        if (operationOutputType != null && asyncOperationOutputType != null) {
            Class<?> asyncReturnTypeClass = (Class<?>)asyncOperationOutputType.getPhysical();
            if (asyncReturnTypeClass == Response.class) {
                //now check the actual type of the Response<R> with R
                Class<?> returnType = operationOutputType.getPhysical();
                Class<?> asyncActualReturnTypeClass = Object.class;
                if (asyncOperationOutputType.getGenericType() instanceof ParameterizedType) {
                    ParameterizedType asyncReturnType = (ParameterizedType)asyncOperationOutputType.getGenericType();
                    asyncActualReturnTypeClass = (Class<?>)asyncReturnType.getActualTypeArguments()[0];
                }

                if (operation.getOutputWrapper() != null) {
                    // The return type could be the wrapper type per JAX-WS spec 
                    Class<?> wrapperClass = operation.getOutputWrapper().getWrapperClass();
                    if (wrapperClass == asyncActualReturnTypeClass) {
                        return true;
                    }
                }
                if (returnType == asyncActualReturnTypeClass || returnType.isPrimitive()
                    && primitiveAssignable(returnType, asyncActualReturnTypeClass)) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * For each method M in the interface, if another method C in the interface has
     * a) a method name that is M's method name with the characters "Async" appended, and
     * b) a parameter signature that is M's parameter signature with an additional 
     *    final parameter of type AsyncHandler<R> where R is the return type of M, and
     * c) a return type of Future<?>
     * 
     * then C is a JAX-WS callback method that isn't part of the SCA interface contract.
     * 
     * @param operation
     * @param asyncOperation
     * @return
     */
    private static boolean isJAXWSAsyncCallbackOperation(Operation operation, Operation asyncOperation) {

        if (asyncOperation.getOutputType().getLogical().size() == 0 || Future.class != asyncOperation.getOutputType().getLogical().get(0).getPhysical()) {
            // The return type is not Future<?>
            return false;
        }

        //a parameter signature that is M's parameter signature 
        //with an additional final parameter of type AsyncHandler<R> where R is the return type of M, and
        List<DataType> operationInputType = operation.getInputType().getLogical();
        List<DataType> asyncOperationInputType = asyncOperation.getInputType().getLogical();
        int size = operationInputType.size();
        if (asyncOperationInputType.size() != size + 1) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!isCompatible(operationInputType.get(i), asyncOperationInputType.get(i))) {
                return false;
            }
        }

        Type genericParamType = asyncOperationInputType.get(size).getGenericType();

        Class<?> asyncLastParameterTypeClass = asyncOperationInputType.get(size).getPhysical();
        if (asyncLastParameterTypeClass == AsyncHandler.class) {
            //now check the actual type of the AsyncHandler<R> with R
            Class<?> asyncActualLastParameterTypeClass = Object.class;
            if (genericParamType instanceof ParameterizedType) {
                ParameterizedType asyncLastParameterType = (ParameterizedType)genericParamType;
                asyncActualLastParameterTypeClass = (Class<?>)asyncLastParameterType.getActualTypeArguments()[0];
            }

            if (operation.getOutputWrapper() != null) {
                // The return type could be the wrapper type per JAX-WS spec 
                Class<?> wrapperClass = operation.getOutputWrapper().getWrapperClass();
                if (wrapperClass == asyncActualLastParameterTypeClass) {
                    return true;
                }
            }

            Class<?> returnType = null;
            if (operation.getOutputType() != null && operation.getOutputType().getLogical() != null && operation.getOutputType().getLogical().size() > 0) {
                returnType = operation.getOutputType().getLogical().get(0).getPhysical();
            }
            if (returnType != null) {
                if (returnType == asyncActualLastParameterTypeClass || returnType.isPrimitive()
                    && primitiveAssignable(returnType, asyncActualLastParameterTypeClass)) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }

    /**
     * Get operation by name
     * 
     * @param operations
     * @param operationName
     * @return
     */
    private static List<Operation> getAsyncOperations(List<Operation> operations, JavaOperation op) {
        List<Operation> returnOperations = new ArrayList<Operation>();

        for (Operation o : operations) {
            if (o == op) {
                continue;
            }
            String operationName = op.getName();
            if (o.getName().equals(operationName)) {
                // Async operations have the same name when @WebMethod is present 
                /*
                JavaOperation jop = (JavaOperation)o;
                if (op.getJavaMethod().getName().equals(jop.getJavaMethod().getName() + ASYNC)) {
                    returnOperations.add(o);
                }
                */
                returnOperations.add(o);
            } else if (o.getName().equals(operationName + ASYNC)) {
                returnOperations.add(o);
            }
        }

        return returnOperations;
    }

    /**
     * Check if two operation parameters are compatible
     * 
     * @param source
     * @param target
     * @return
     */
    private static boolean isCompatible(DataType<?> source, DataType<?> target) {
        if (source == target) {
            return true;
        }

        return target.getPhysical().isAssignableFrom(source.getPhysical());
    }

    /**
     * Compares a two types, assuming one is a primitive, to determine if the
     * other is its object counterpart
     */
    private static boolean primitiveAssignable(Class<?> memberType, Class<?> param) {
        if (memberType == Integer.class) {
            return param == Integer.TYPE;
        } else if (memberType == Double.class) {
            return param == Double.TYPE;
        } else if (memberType == Float.class) {
            return param == Float.TYPE;
        } else if (memberType == Short.class) {
            return param == Short.TYPE;
        } else if (memberType == Character.class) {
            return param == Character.TYPE;
        } else if (memberType == Boolean.class) {
            return param == Boolean.TYPE;
        } else if (memberType == Byte.class) {
            return param == Byte.TYPE;
        } else if (param == Integer.class) {
            return memberType == Integer.TYPE;
        } else if (param == Double.class) {
            return memberType == Double.TYPE;
        } else if (param == Float.class) {
            return memberType == Float.TYPE;
        } else if (param == Short.class) {
            return memberType == Short.TYPE;
        } else if (param == Character.class) {
            return memberType == Character.TYPE;
        } else if (param == Boolean.class) {
            return memberType == Boolean.TYPE;
        } else if (param == Byte.class) {
            return memberType == Byte.TYPE;
        } else {
            return false;
        }
    }
}
