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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.namespace.QName;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.FaultExceptionMapper;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * Introspect the java class/interface with JSR-181 and JAXWS annotations
 * 
 * @version $Rev$ $Date$
 */
public class JAXWSJavaInterfaceProcessor implements JavaInterfaceVisitor {
    private static final String JAXB_DATABINDING = "javax.xml.bind.JAXBElement";
    private FaultExceptionMapper faultExceptionMapper;

    public JAXWSJavaInterfaceProcessor(FaultExceptionMapper faultExceptionMapper) {
        super();
        this.faultExceptionMapper = faultExceptionMapper;
    }

    public JAXWSJavaInterfaceProcessor() {
        super();
    }

    public void visitInterface(JavaInterface contract) throws InvalidInterfaceException {

        Class<?> clazz = contract.getJavaClass();
        WebService webService = clazz.getAnnotation(WebService.class);
        String tns = JavaInterfaceUtil.getNamespace(clazz);
        if (webService != null) {
            tns = getValue(webService.targetNamespace(), tns);
            // Mark SEI as Remotable
            contract.setRemotable(true);
        }
        if (!contract.isRemotable()) {
            return;
        }

        // SOAP binding (doc/lit/wrapped|bare or rpc/lit)
        SOAPBinding soapBinding = clazz.getAnnotation(SOAPBinding.class);

        for (Iterator<Operation> it = contract.getOperations().iterator(); it.hasNext();) {
            JavaOperation operation = (JavaOperation)it.next();
            Method method = operation.getJavaMethod();
            introspectFaultTypes(operation);

            // SOAP binding (doc/lit/wrapped|bare or rpc/lit)
            SOAPBinding methodSOAPBinding = method.getAnnotation(SOAPBinding.class);
            if (methodSOAPBinding == null) {
                methodSOAPBinding = soapBinding;
            }

            boolean documentStyle = true;
            boolean bare = false;
            if (methodSOAPBinding != null) {
                bare = methodSOAPBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE;
                // For BARE parameter style, the data is in the wrapped format already
                operation.setWrapperStyle(bare);
                documentStyle = methodSOAPBinding.style() == Style.DOCUMENT;
            }

            String operationName = operation.getName();
            // WebMethod
            WebMethod webMethod = method.getAnnotation(WebMethod.class);
            if (webMethod != null) {
                if (webMethod.exclude()) {
                    // Exclude the method
                    it.remove();
                    continue;
                }
                operationName = getValue(webMethod.operationName(), operationName);
                operation.setName(operationName);
            }

            // Is one way?
            Oneway oneway = method.getAnnotation(Oneway.class);
            if (oneway != null) {
                // JSR 181
                assert method.getReturnType() == void.class;
                operation.setNonBlocking(true);
            }

            // Handle BARE mapping
            if (bare) {
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    WebParam param = getAnnotation(method, i, WebParam.class);
                    if (param != null) {
                        String ns = getValue(param.targetNamespace(), tns);
                        // Default to <operationName> for doc-bare
                        String name = getValue(param.name(), documentStyle ? operationName : "arg" + i);
                        QName element = new QName(ns, name);
                        Object logical = operation.getInputType().getLogical().get(i).getLogical();
                        if (logical instanceof XMLType) {
                            ((XMLType)logical).setElementName(element);
                        }
                    }
                }
                WebResult result = method.getAnnotation(WebResult.class);
                if (result != null) {
                    String ns = getValue(result.targetNamespace(), tns);
                    // Default to <operationName>Response for doc-bare
                    String name = getValue(result.name(), documentStyle ? operationName + "Response" : "return");
                    QName element = new QName(ns, name);
                    Object logical = operation.getOutputType().getLogical();
                    if (logical instanceof XMLType) {
                        ((XMLType)logical).setElementName(element);
                    }
                }
                // FIXME: [rfeng] For the BARE mapping, do we need to create a Wrapper?
                // it's null at this point
            } else {

                RequestWrapper requestWrapper = method.getAnnotation(RequestWrapper.class);
                String ns = requestWrapper == null ? tns : getValue(requestWrapper.targetNamespace(), tns);
                String name =
                    requestWrapper == null ? operationName : getValue(requestWrapper.localName(), operationName);
                QName inputWrapper = new QName(ns, name);

                ResponseWrapper responseWrapper = method.getAnnotation(ResponseWrapper.class);
                ns = responseWrapper == null ? tns : getValue(responseWrapper.targetNamespace(), tns);
                name =
                    responseWrapper == null ? operationName + "Response" : getValue(responseWrapper.localName(),
                                                                                    operationName + "Response");
                QName outputWrapper = new QName(ns, name);

                List<ElementInfo> inputElements = new ArrayList<ElementInfo>();
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    WebParam param = getAnnotation(method, i, WebParam.class);
                    ns = param != null ? param.targetNamespace() : "";
                    // Default to "" for doc-lit-wrapped && non-header
                    ns = getValue(ns, documentStyle && (param == null || !param.header()) ? "" : tns);
                    name = param != null ? param.name() : "";
                    name = getValue(name, "arg" + i);
                    QName element = new QName(ns, name);
                    Object logical = operation.getInputType().getLogical().get(i).getLogical();
                    if (logical instanceof XMLType) {
                        ((XMLType)logical).setElementName(element);
                    }
                    inputElements.add(new ElementInfo(element, null));
                }

                List<ElementInfo> outputElements = new ArrayList<ElementInfo>();
                WebResult result = method.getAnnotation(WebResult.class);
                // Default to "" for doc-lit-wrapped && non-header
                ns = result != null ? result.targetNamespace() : "";
                ns = getValue(ns, documentStyle && (result == null || !result.header()) ? "" : tns);
                name = result != null ? result.name() : "";
                name = getValue(name, "return");
                QName element = new QName(ns, name);

                if (operation.getOutputType() != null) {
                    Object logical = operation.getOutputType().getLogical();
                    if (logical instanceof XMLType) {
                        ((XMLType)logical).setElementName(element);
                    }
                }
                outputElements.add(new ElementInfo(element, null));

                WrapperInfo wrapperInfo =
                    new WrapperInfo(JAXB_DATABINDING, new ElementInfo(inputWrapper, null),
                                    new ElementInfo(outputWrapper, null), inputElements, outputElements);
                operation.setWrapper(wrapperInfo);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void introspectFaultTypes(Operation operation) {
        if (operation!= null && operation.getFaultTypes() != null) {
            for (DataType exceptionType : operation.getFaultTypes()) {
                faultExceptionMapper.introspectFaultDataType(exceptionType);
            }
        }
    }

    private <T extends Annotation> T getAnnotation(Method method, int index, Class<T> annotationType) {
        Annotation[] annotations = method.getParameterAnnotations()[index];
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationType) {
                return annotationType.cast(annotation);
            }
        }
        return null;
    }

    private static String getValue(String value, String defaultValue) {
        return "".equals(value) ? defaultValue : value;
    }

}
