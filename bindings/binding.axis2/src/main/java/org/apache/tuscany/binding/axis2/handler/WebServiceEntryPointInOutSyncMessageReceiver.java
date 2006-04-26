/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.axis2.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import commonj.sdo.helper.TypeHelper;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver;

import org.apache.tuscany.binding.axis2.util.AxiomHelper;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.model.assembly.EntryPoint;

import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.soap.SOAPBody;
import org.apache.ws.commons.soap.SOAPEnvelope;
import org.apache.ws.commons.soap.SOAPFactory;
import org.apache.wsdl.WSDLConstants;


public class WebServiceEntryPointInOutSyncMessageReceiver extends AbstractInOutSyncMessageReceiver {
    public static final String MEP_URL = WSDLConstants.MEP_URI_IN_OUT;

    private final EntryPointContext entryPointContext;
    private final TypeHelper typeHelper;
    private final ClassLoader classLoader;
    private final Map<String, QName> responseTypeMap;
    private final Map<String, Method> methodMap;

    /**
     * Constructor WebServiceEntryPointInOutSyncMessageReceiver
     *
     * @param entryPoint
     * @param context
     * @param wsdlPortInfo
     */
    @SuppressWarnings("unchecked")
    public WebServiceEntryPointInOutSyncMessageReceiver(EntryPoint entryPoint,
                                                        EntryPointContext context,
                                                        WebServicePortMetaData wsdlPortInfo,
                                                        TypeHelper typeHelper,
                                                        ClassLoader classLoader) {
        this.entryPointContext = context;

        Class<?> serviceInterface = entryPoint.getConfiguredService().getPort()
                .getServiceContract().getInterface();
        Method[] methods = serviceInterface.getMethods();
        Map<String, Method> map = new HashMap<String, Method>(methods.length);
        for (Method method : methods) {
            map.put(method.getName(), method);
        }

        // initialize maps
        List<WebServiceOperationMetaData> operations = wsdlPortInfo.getAllOperationMetaData();
        responseTypeMap = new HashMap<String, QName>(operations.size());
        methodMap = new HashMap<String, Method>(operations.size());
        for (WebServiceOperationMetaData operation : operations) {
            String opName = operation.getBindingOperation().getOperation().getName();
            QName qname = operation.getOutputPart(0).getElementName();
            responseTypeMap.put(opName, qname);

            Method method = map.get(opName);
            methodMap.put(opName, method);
        }

        this.typeHelper = typeHelper;
        this.classLoader = classLoader;
    }

    public void invokeBusinessLogic(MessageContext msgContext,
                                    MessageContext outMsgContext)
        throws AxisFault {
        
        // set application classloader onto the thread
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            // get operation name from request message
            AxisOperation axisOperation = msgContext.getAxisOperation();
            String axisOperationName = axisOperation.getName().getLocalPart();

            // de-serialize request parameters to objects
            OMElement requestOM = msgContext.getEnvelope().getBody().getFirstElement();
            Object[] args = AxiomHelper.toObjects(typeHelper, requestOM);

            // invoke the proxy's InvocationHandler
            // FIXME we should be invoking the Tuscany pipeline rather than the proxy
            Method operationMethod = methodMap.get(axisOperationName);
            InvocationHandler handler = (InvocationHandler) entryPointContext.getHandler();
            Object response = handler.invoke(null, operationMethod, args);

            // construct the response message
            SOAPFactory fac = getSOAPFactory(msgContext);
            SOAPEnvelope soapenv = fac.getDefaultEnvelope();
            SOAPBody soapbody = soapenv.getBody();

            // serialize the wire respose into the message
            QName responseTypeQN = responseTypeMap.get(axisOperationName);
            OMElement responseOM = AxiomHelper.toOMElement(typeHelper,
                                                           new Object[]{response},
                                                           responseTypeQN);
            soapbody.addChild(responseOM);

            outMsgContext.setEnvelope(soapenv);
            outMsgContext.getOperationContext().setProperty(Constants.RESPONSE_WRITTEN, Constants.VALUE_TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AxisFault("Error creating DataObject from Soapenvelope. "
                                + e.getClass() + ' ' + e.getMessage(), e);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new AxisFault("Error creating DataObject from Soapenvelope. "
                                + e.getClass() + ' ' + e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }
}
