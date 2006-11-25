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
package org.apache.tuscany.binding.celtix;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.jws.WebParam;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

import commonj.sdo.helper.TypeHelper;
import org.apache.tuscany.binding.celtix.io.SCADataBindingCallback;
import org.objectweb.celtix.Bus;
import org.objectweb.celtix.BusException;
import org.objectweb.celtix.bindings.ClientBinding;
import org.objectweb.celtix.bindings.DataBindingCallback;
import org.objectweb.celtix.bus.bindings.WSDLMetaDataCache;
import org.objectweb.celtix.bus.bindings.WSDLOperationInfo;
import org.objectweb.celtix.context.ObjectMessageContext;
import org.objectweb.celtix.ws.addressing.EndpointReferenceType;
import org.objectweb.celtix.wsdl.EndpointReferenceUtils;
import org.xmlsoap.schemas.wsdl.http.AddressType;


/**
 * Responsible for dispatching a service operation invocation on a reference to the active Celtix <code>Bus</code>
 *
 * @version $Rev$ $Date$
 */
public class CeltixInvoker implements TargetInvoker {

    private WSDLMetaDataCache wsdlCache;

    private ClientBinding clientBinding;

    private String operationName;

    private TypeHelper typeHelper;

    public CeltixInvoker(String operationName,
                         Bus bus,
                         Port port,
                         Service wsdlService,
                         Definition wsdlDef,
                         TypeHelper theTypeHelper) throws BuilderException {
        this.wsdlCache = new WSDLMetaDataCache(wsdlDef, port);
        this.operationName = operationName;
        this.typeHelper = theTypeHelper;
        // Definition wsdlDef = wsBinding.getWSDLDefinition();
        // wsdlCache = new WSDLMetaDataCache(wsdlDef, wsBinding.getWSDLPort());

        try {
            String key = wsdlDef.getDocumentBaseURI();
            URL url = new URL(key);

            QName qName = wsdlService.getQName();
            EndpointReferenceType reference = EndpointReferenceUtils.getEndpointReference(url, qName, port.getName());

            String bindingId = null;
            Binding binding = port.getBinding();
            if (null != binding) {
                List list = binding.getExtensibilityElements();
                if (!list.isEmpty()) {
                    bindingId = ((ExtensibilityElement) list.get(0)).getElementType().getNamespaceURI();
                }
            }
            if (bindingId == null) {
                List<?> list = port.getExtensibilityElements();
                for (Object ep : list) {
                    ExtensibilityElement ext = (ExtensibilityElement) ep;
                    if (ext instanceof SOAPAddress) {
                        bindingId = ((SOAPAddress) ext).getLocationURI();
                    }
                    if (ext instanceof AddressType) {
                        bindingId = ((AddressType) ext).getLocation();
                    }
                }

            }
            clientBinding = bus.getBindingManager().getBindingFactory(bindingId).createClientBinding(reference);
        } catch (MalformedURLException e) {
            throw new InvokerCreationException(e);
        } catch (BusException e) {
            throw new InvokerCreationException(e);
        } catch (WSDLException e) {
            throw new InvokerCreationException(e);
        } catch (IOException e) {
            throw new InvokerCreationException(e);
        }
    }

    /**
     * Invoke an operation on the external Web service.
     *
     * @param args the Java object arguments to the WS operation
     * @return the response from the WS as a Java object
     */
    public Object invokeTarget(final Object args, final short sequence) throws InvocationTargetException {
        WSDLOperationInfo opInfo = wsdlCache.getOperationInfo(operationName);
        if (opInfo == null) {
            // REVISIT - really map the operation name to a WSDL operation
            for (String opName : wsdlCache.getAllOperationInfo().keySet()) {
                if (operationName.equalsIgnoreCase(opName)) {
                    opInfo = wsdlCache.getOperationInfo(opName);
                    break;
                }
            }
        }

        ObjectMessageContext objMsgContext = clientBinding.createObjectContext();

        boolean hasInOut = false;
        int inOutCount = 0;
        Object realArgs[];
        Object argsArray[];
        if (args.getClass().isArray()) {
            argsArray = (Object[]) args;
            realArgs = new Object[Array.getLength(args)];
        } else {
            argsArray = new Object[0];
            realArgs = new Object[0];
        }

        if (opInfo.getParamsLength() == 0) {
            // REVISIT - opInfo doesn't return the needed info for the wrapped doc/lit case.
            // Bug in Celtix
            realArgs = argsArray;
        } else {
            for (int x = 0; x < argsArray.length; x++) {
                if (opInfo.getWebParam(x).mode() == WebParam.Mode.IN) {
                    realArgs[x] = argsArray[x];
                } else {
                    realArgs[x] = new Holder<Object>(argsArray[x]);
                    inOutCount++;
                    hasInOut = true;
                }
            }
        }
        objMsgContext.setMessageObjects(realArgs);
        boolean isOneway = opInfo.isOneWay();
        DataBindingCallback callback = new SCADataBindingCallback(opInfo, hasInOut, typeHelper);
        try {
            if (isOneway) {
                clientBinding.invokeOneWay(objMsgContext, callback);
            } else {
                objMsgContext = clientBinding.invoke(objMsgContext, callback);
            }
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        }

        if (objMsgContext.getException() != null) {
            // REVISIT - Exceptions
            /*
             * if (isValidException(objMsgContext)) { throw
             * (Exception)objMsgContext.getException(); } else { throw new
             * ProtocolException(objMsgContext.getException()); }
             */
            throw new InvocationTargetException(objMsgContext.getException());
        }

        if (hasInOut) {
            Object ret[] = new Object[inOutCount + 1];
            ret[0] = objMsgContext.getReturn();
            inOutCount = 1;
            for (int x = 0; x < argsArray.length; x++) {
                if (opInfo.getWebParam(x).mode() != WebParam.Mode.IN) {
                    Holder<?> holder = (Holder<?>) realArgs[x];
                    ret[inOutCount] = holder.value;
                    inOutCount++;
                }
            }
            return ret;
        }
        return objMsgContext.getReturn();
    }

    public Message invoke(Message msg) {
        try {
            Object resp = invokeTarget(msg.getBody(), TargetInvoker.NONE);
            msg.setBody(resp);
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
        }
        return msg;
    }

    public void setNext(Interceptor next) {
        throw new UnsupportedOperationException();
    }

    public CeltixInvoker clone() throws CloneNotSupportedException {
        try {
            return (CeltixInvoker) super.clone();
        } catch (CloneNotSupportedException e) {
            // will not happen
            return null;
        }
    }

    public boolean isCacheable() {
        return true;
    }

    public void setCacheable(boolean cacheable) {

    }

    public boolean isOptimizable() {
        return false;
    }

}
