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

package org.apache.tuscany.databinding.jaxb;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.apache.tuscany.idl.Operation;
import org.apache.tuscany.idl.util.ElementInfo;
import org.apache.tuscany.idl.util.WrapperInfo;

/**
 * The databinding annotation processor for java interfaces
 * 
 * @version $Rev$ $Date$
 */
public class JAXWSJavaInterfaceProcessor extends JavaInterfaceProcessorExtension {

    public JAXWSJavaInterfaceProcessor() {
        super();
    }

    public void visitInterface(Class<?> clazz, Class<?> callbackClass, Contract contract)
        throws InvalidServiceContractException {
        if (!contract.isRemotable()) {
            return;
        }
        Map<String, Operation> operations = contract.getOperations();
        processInterface(clazz, contract, operations);
        if (callbackClass != null) {
            Map<String, Operation> callbackOperations = contract.getCallbackOperations();
            processInterface(callbackClass, contract, callbackOperations);
        }
    }

    private static String getValue(String value, String defaultValue) {
        return "".equals(value) ? defaultValue : value;
    }

    private void processInterface(Class<?> clazz, Contract contract, Map<String, Operation> operations) {

        for (Method method : clazz.getMethods()) {
            Operation<?> operation = operations.get(method.getName());

            WebMethod webMethod = method.getAnnotation(WebMethod.class);
            if (webMethod == null) {
                return;
            }

            String operationName = getValue(webMethod.operationName(), operation.getName());

            RequestWrapper requestWrapper = method.getAnnotation(RequestWrapper.class);
            ResponseWrapper responseWrapper = method.getAnnotation(ResponseWrapper.class);
            if (requestWrapper == null) {
                return;
            }

            WebService webService = clazz.getAnnotation(WebService.class);
            String tns = "";
            if (webService != null) {
                tns = webService.targetNamespace();
            }

            String ns = getValue(requestWrapper.targetNamespace(), tns);
            String name = getValue(requestWrapper.localName(), operationName);
            QName inputWrapper = new QName(ns, name);

            ns = getValue(responseWrapper.targetNamespace(), tns);
            name = getValue(responseWrapper.localName(), operationName + "Response");

            QName outputWrapper = new QName(ns, name);

            WrapperInfo wrapperInfo =
                new WrapperInfo(JAXBDataBinding.NAME, new ElementInfo(inputWrapper, null),
                                new ElementInfo(outputWrapper, null), null, null);
            operation.setWrapperStyle(true);
            operation.setWrapper(wrapperInfo);
        }
    }
}
