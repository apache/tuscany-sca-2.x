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
package org.apache.tuscany.binding.celtix.handler;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.jws.WebParam.Mode;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.ws.Holder;
import javax.xml.ws.ProtocolException;

import commonj.sdo.helper.TypeHelper;
import org.apache.tuscany.binding.celtix.assembly.WebServiceBinding;
import org.apache.tuscany.binding.celtix.handler.io.SCADataBindingCallback;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.BuilderInitException;
import org.apache.tuscany.model.assembly.ExternalService;
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
 * An ExternalWebServiceClient using Celtix
 */
public class ExternalWebServiceClient {

    private Bus bus;
    private TypeHelper typeHelper;
    private WSDLMetaDataCache wsdlCache;
    private ClientBinding clientBinding;
    private WebServiceBinding wsBinding;

    
    
    public ExternalWebServiceClient(ExternalService externalService) throws BuilderException {
        wsBinding = (WebServiceBinding)externalService.getBindings().get(0);
        bus = wsBinding.getBus();
        typeHelper = wsBinding.getTypeHelper();
        Definition wsdlDef = wsBinding.getWSDLDefinition();
        wsdlCache = new WSDLMetaDataCache(wsdlDef, wsBinding.getWSDLPort());

        try {
            String key = wsdlDef.getDocumentBaseURI();
            URL url = new URL(key);

            EndpointReferenceType reference = EndpointReferenceUtils.getEndpointReference(url,
                    wsBinding.getWSDLService().getQName(),
                    wsBinding.getWSDLPort().getName());

            String bindingId = null;
            Binding binding = wsBinding.getWSDLPort().getBinding();
            if (null != binding) {
                List list = binding.getExtensibilityElements();
                if (!list.isEmpty()) {
                    bindingId = ((ExtensibilityElement)list.get(0)).getElementType().getNamespaceURI();
                }
            }
            if (bindingId == null) {
                List<?> list = wsBinding.getWSDLPort().getExtensibilityElements();
                for (Object ep : list) {
                    ExtensibilityElement ext = (ExtensibilityElement)ep;
                    if (ext instanceof SOAPAddress) {
                        bindingId = ((SOAPAddress)ext).getLocationURI();
                    }
                    if (ext instanceof AddressType) {
                        bindingId = ((AddressType)ext).getLocation();
                    }
                }

            }
            clientBinding = bus.getBindingManager().getBindingFactory(bindingId).createClientBinding(
                    reference);
        } catch (MalformedURLException e) {
            throw new BuilderInitException(e);
        } catch (BusException e) {
            throw new BuilderInitException(e);
        } catch (WSDLException e) {
            throw new BuilderInitException(e);
        } catch (IOException e) {
            throw new BuilderInitException(e);
        }
    }

    /**
     * Invoke an operation on the external Web service.
     *
     * @param operationName the name of the WS operation to invoke
     * @param args          the Java object arguments to the WS operation
     * @return the response from the WS as a Java object
     */
    public Object invoke(String operationName, Object[] args) {
        WSDLOperationInfo opInfo = wsdlCache.getOperationInfo(operationName);
        if (opInfo == null) {
            //REVISIT - really map the operation name to a WSDL operation
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
        Object realArgs[] = new Object[args.length];
        if (opInfo.getParamsLength() == 0) {
            //REVISIT - opInfo doesn't return the needed info for the wrapped doc/lit case.
            //Bug in Celtix
            realArgs = args;
        } else {
            for (int x = 0; x < args.length; x++) {
                if (opInfo.getWebParam(x).mode() == Mode.IN) {
                    realArgs[x] = args[x];
                } else {
                    realArgs[x] = new Holder<Object>(args[x]);
                    inOutCount++;
                    hasInOut = true;
                }
            }
        }

        objMsgContext.setMessageObjects(realArgs);

        boolean isOneway = opInfo.isOneWay();
        DataBindingCallback callback = new SCADataBindingCallback(opInfo, typeHelper,
                                                                  wsBinding.getResourceLoader(),
                                                                  hasInOut);

        try {
            if (isOneway) {
                clientBinding.invokeOneWay(objMsgContext,
                        callback);
            } else {
                objMsgContext = clientBinding.invoke(objMsgContext,
                        callback);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (objMsgContext.getException() != null) {
            //REVISIT - Exceptions
            /*
            if (isValidException(objMsgContext)) {
                throw (Exception)objMsgContext.getException();
            } else {
                throw new ProtocolException(objMsgContext.getException());
            }
            */
            throw new ProtocolException(objMsgContext.getException());
        }

        if (hasInOut) {
            Object ret[] = new Object[inOutCount + 1];
            ret[0] = objMsgContext.getReturn();
            inOutCount = 1;
            for (int x = 0; x < args.length; x++) {
                if (opInfo.getWebParam(x).mode() != Mode.IN) {
                    Holder<?> holder = (Holder<?>)realArgs[x];
                    ret[inOutCount] = holder.value;
                    inOutCount++;
                }
            }
            return ret;
        }
        return objMsgContext.getReturn();
    }
}
