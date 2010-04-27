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

package org.apache.tuscany.sca.interfacedef.java.jaxrs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;

public class JAXRSJavaInterfaceProcessor implements JavaInterfaceVisitor {

    public void visitInterface(JavaInterface contract) throws InvalidInterfaceException {
        
        final Class<?> clazz = contract.getJavaClass();

        List<Operation> getOperations = new ArrayList<Operation>();
        List<Operation> putOperations = new ArrayList<Operation>();
        List<Operation> postOperations = new ArrayList<Operation>();
        List<Operation> deleteOperations = new ArrayList<Operation>();
        
        boolean hasJAXRSAnnotarions = false;
        
        for (Iterator<Operation> it = contract.getOperations().iterator(); it.hasNext();) {
            final JavaOperation operation = (JavaOperation)it.next();
            final Method method = operation.getJavaMethod();
         
            GET get = method.getAnnotation(GET.class);
            if(get != null) {
                hasJAXRSAnnotarions = true;
                operation.getAttributes().put(GET.class.getClass(), true);
                getOperations.add(operation);
            }
            
            if(! getOperations.isEmpty()) {
                contract.getAttributes().put(GET.class.getClass(), getOperations);
            }
            
            PUT put = method.getAnnotation(PUT.class);
            if(put != null) {
                hasJAXRSAnnotarions = true;
                operation.getAttributes().put(PUT.class.getClass(), true);
                putOperations.add(operation);
            }

            if(! putOperations.isEmpty()) {
                contract.getAttributes().put(PUT.class.getClass(), putOperations);
            }

            
            POST post = method.getAnnotation(POST.class);
            if(post != null) {
                hasJAXRSAnnotarions = true;
                operation.getAttributes().put(POST.class.getClass(), true);
                postOperations.add(operation);
            }
            
            if(! postOperations.isEmpty()) {
                contract.getAttributes().put(POST.class.getClass(), postOperations);
            }

            DELETE delete = method.getAnnotation(DELETE.class);
            if(delete != null) {
                hasJAXRSAnnotarions = true;
                operation.getAttributes().put(DELETE.class.getClass(), true);
                deleteOperations.add(operation);
            }

            if(! deleteOperations.isEmpty()) {
                contract.getAttributes().put(GET.class.getClass(), deleteOperations);
            }
            
        }
        
        // Always set JAX-RS annotated interfaces as remotables
        if (hasJAXRSAnnotarions) {
            contract.setRemotable(true);
        }
    }
}
