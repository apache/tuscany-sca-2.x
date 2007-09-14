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

package org.apache.tuscany.sca.databinding.jaxb;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;

/**
 * The databinding annotation processor for java interfaces
 * 
 * @version $Rev$ $Date$
 */
public class JAXWSJavaInterfaceProcessor implements JavaInterfaceVisitor {

    public JAXWSJavaInterfaceProcessor() {
        super();
    }

    public void visitInterface(JavaInterface contract) throws InvalidInterfaceException {

        Class<?> clazz = contract.getJavaClass();
        WebService webService = clazz.getAnnotation(WebService.class);
        String tns = "";
        if (webService != null) {
            tns = webService.targetNamespace();
            // Mark SEI as Remotable
            contract.setRemotable(true);
        }
        if (!contract.isRemotable()) {
            return;
        }
        Map<String, Operation> operations = new HashMap<String, Operation>();
        for (Operation op : contract.getOperations()) {
            operations.put(op.getName(), op);
        }
        for (Method method : clazz.getMethods()) {
            Operation operation = operations.get(method.getName());

            WebMethod webMethod = method.getAnnotation(WebMethod.class);
            if (webMethod == null) {
                return;
            }

            /*
            String operationName = getValue(webMethod.operationName(), operation.getName());

            RequestWrapper requestWrapper = method.getAnnotation(RequestWrapper.class);
            ResponseWrapper responseWrapper = method.getAnnotation(ResponseWrapper.class);
            if (requestWrapper == null) {
                return;
            }

            String ns = getValue(requestWrapper.targetNamespace(), tns);
            String name = getValue(requestWrapper.localName(), operationName);
            QName inputWrapper = new QName(ns, name);

            ns = getValue(responseWrapper.targetNamespace(), tns);
            name = getValue(responseWrapper.localName(), operationName + "Response");

            QName outputWrapper = new QName(ns, name);

            List<ElementInfo> inputElements = new ArrayList<ElementInfo>();
            for (Annotation[] annotations : method.getParameterAnnotations()) {
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == WebParam.class) {
                        WebParam param = (WebParam)annotation;
                        inputElements.add(new ElementInfo(new QName(param.targetNamespace(), param.name()), null));
                        break;
                    }
                }
            }

            List<ElementInfo> outputElements = new ArrayList<ElementInfo>();
            WebResult result = method.getAnnotation(WebResult.class);
            outputElements.add(new ElementInfo(new QName(result.targetNamespace(), result.name()), null));

            WrapperInfo wrapperInfo =
                new WrapperInfo(JAXBDataBinding.NAME, new ElementInfo(inputWrapper, null),
                                new ElementInfo(outputWrapper, null), inputElements, outputElements);
            operation.setWrapper(wrapperInfo);
            */
            operation.setWrapperStyle(false);
            operation.setDataBinding(JAXBDataBinding.NAME);
        }
    }

    private static String getValue(String value, String defaultValue) {
        return "".equals(value) ? defaultValue : value;
    }

}
