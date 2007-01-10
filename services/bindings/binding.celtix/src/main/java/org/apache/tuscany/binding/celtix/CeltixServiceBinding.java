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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import javax.jws.soap.SOAPBinding;
import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceProvider;

import org.osoa.sca.annotations.Destroy;
import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceBindingExtension;

import commonj.sdo.helper.TypeHelper;
import org.apache.tuscany.binding.celtix.io.SCAServerDataBindingCallback;
import org.objectweb.celtix.Bus;
import org.objectweb.celtix.bindings.BindingFactory;
import org.objectweb.celtix.bindings.DataBindingCallback;
import org.objectweb.celtix.bindings.ServerBinding;
import org.objectweb.celtix.bindings.ServerBindingEndpointCallback;
import org.objectweb.celtix.bindings.ServerDataBindingCallback;
import org.objectweb.celtix.bus.bindings.WSDLMetaDataCache;
import org.objectweb.celtix.bus.bindings.WSDLOperationInfo;
import org.objectweb.celtix.context.ObjectMessageContext;
import org.objectweb.celtix.ws.addressing.AttributedURIType;
import org.objectweb.celtix.ws.addressing.EndpointReferenceType;
import org.objectweb.celtix.wsdl.EndpointReferenceUtils;
import org.xmlsoap.schemas.wsdl.http.AddressType;


/**
 * An implementation of a {@link Service} configured with the Celtix binding
 *
 * @version $Rev$ $Date$
 */
public class CeltixServiceBinding extends ServiceBindingExtension implements ServerBindingEndpointCallback {
    private static final QName BINDING_WS = new QName(XML_NAMESPACE_1_0, "binding.ws");

    private Bus bus;
    private Port port;
    private Definition wsdlDef;
    private Service wsdlService;
    private WSDLMetaDataCache wsdlCache;
    private TypeHelper typeHelper;

    private Map<QName, ServerDataBindingCallback> opMap =
        new ConcurrentHashMap<QName, ServerDataBindingCallback>();


    public CeltixServiceBinding(String theName,
                         CompositeComponent parent,
                         WebServiceBindingDefinition binding,
                         Bus bus,
                         TypeHelper theTypeHelper) {
        super(theName, parent);
        this.wsdlDef = binding.getWSDLDefinition();
        this.port = binding.getWSDLPort();
        this.wsdlService = binding.getWSDLService();
        this.bus = bus;
        this.wsdlCache = new WSDLMetaDataCache(wsdlDef, port);
        this.typeHelper = theTypeHelper;
    }

    public void start() {
        super.start();
        initOperationMap();
        startServerBinding();
    }

    private void startServerBinding() {
        String key = wsdlDef.getDocumentBaseURI();
        URL url;
        try {
            url = new URL(key);
        } catch (MalformedURLException e) {
            throw new CeltixServiceInitException(e);
        }

        QName qName = wsdlService.getQName();
        String portName = port.getName();
        EndpointReferenceType reference = EndpointReferenceUtils.getEndpointReference(url, qName, portName);

        AttributedURIType address = new AttributedURIType();

        String bindingId = null;
        Binding binding = port.getBinding();
        if (null != binding) {
            List list = binding.getExtensibilityElements();
            if (!list.isEmpty()) {
                bindingId = ((ExtensibilityElement) list.get(0)).getElementType().getNamespaceURI();
            }
        }
        List<?> list = port.getExtensibilityElements();
        for (Object ep : list) {
            ExtensibilityElement ext = (ExtensibilityElement) ep;
            if (ext instanceof SOAPAddress) {
                if (bindingId == null) {
                    bindingId = ((SOAPAddress) ext).getLocationURI();
                }
                address.setValue(((SOAPAddress) ext).getLocationURI());
            }
            if (ext instanceof AddressType) {
                if (bindingId == null) {
                    bindingId = ((AddressType) ext).getLocation();
                }
                address.setValue(((AddressType) ext).getLocation());
            }
        }
        if (reference.getAddress() == null) {
            //REVIST - bug in Celtix that the HTTP transport won't find the address correctly
            reference.setAddress(address);
        }
        ClassLoader previousLoader = null;
        try {
            //FIXME: This hack is because SAAJImpl uses Thread.currentThread().getContextClassLoader(),
            //this classloader is different from current classLoader.
            previousLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            BindingFactory bindingFactory = bus.getBindingManager().getBindingFactory(bindingId);
            ServerBinding serverBinding = bindingFactory.createServerBinding(reference, this);
            serverBinding.activate();
        } catch (Exception e) {
            throw new CeltixServiceInitException(e);
        } finally {
            if (previousLoader != null) {
                Thread.currentThread().setContextClassLoader(previousLoader);
            }
        }
    }

    private void initOperationMap() {
        List ops = port.getBinding().getBindingOperations();
        for (Object op1 : ops) {
            BindingOperation op = (BindingOperation) op1;
            BindingInput bindingInput = op.getBindingInput();
            List elements = bindingInput.getExtensibilityElements();
            QName qn = new QName(wsdlDef.getTargetNamespace(), op.getName());

            //In case soap:body namespace is different from targetNamespace
            for (Object element : elements) {
                if (SOAPBody.class.isInstance(element)) {
                    SOAPBody body = (SOAPBody) element;
                    if (body.getNamespaceURI() != null) {
                        qn = new QName(body.getNamespaceURI(), op.getName());
                    }
                }
            }

            ServerDataBindingCallback cb = getDataBindingCallback(qn, null, DataBindingCallback.Mode.PARTS);
            opMap.put(qn, cb);
            if (!"".equals(cb.getRequestWrapperQName().getLocalPart())) {
                opMap.put(cb.getRequestWrapperQName(), cb);
            }
        }
    }

    @Destroy
    public void stop() throws CoreRuntimeException {
        super.stop();
    }

    public ServerDataBindingCallback getDataBindingCallback(QName operationName,
                                                            ObjectMessageContext objContext,
                                                            DataBindingCallback.Mode mode) {
        if (opMap.containsKey(operationName)) {
            return opMap.get(operationName);
        }
        WSDLOperationInfo opInfo = wsdlCache.getOperationInfo(operationName.getLocalPart());
        if (opInfo == null) {
            //REVISIT - really map the operation name to a WSDL operation
            for (String opName : wsdlCache.getAllOperationInfo().keySet()) {
                if (operationName.getLocalPart().equalsIgnoreCase(opName)) {
                    opInfo = wsdlCache.getOperationInfo(opName);
                    break;
                }
            }
        }
        boolean inout = false;

        //       Class<?> serviceInterface = this.getInterface();
        // Method meth = getMethod(serviceInterface, operationName.getLocalPart());

        // Object proxy = null;
//        try {
//         //   proxy = this.getServiceInstance();
//        } catch (TargetException e) {
//            throw new CeltixServiceInitException(e);
//        }

        return new SCAServerDataBindingCallback(opInfo,
            inout,
            operationName.getLocalPart(),
            getInboundWire(),
            typeHelper);
    }

//    protected Method getMethod(Class<?> serviceInterface, String operationName) {
//        // Note: this doesn't support overloaded operations
//        Method[] methods = serviceInterface.getMethods();
//        for (Method m : methods) {
//            if (m.getName().equals(operationName)) {
//                return m;
//            }
//            // tolerate WSDL with capatalized operation name
//            StringBuilder sb = new StringBuilder(operationName);
//            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
//            if (m.getName().equals(sb.toString())) {
//                return m;
//            }
//        }
//        // FIXME
//        throw new CeltixServiceInitException("no operation named " + operationName
//            + " found on service interface: " + serviceInterface.getName());
//    }

    public DataBindingCallback getFaultDataBindingCallback(ObjectMessageContext objContext) {
        // REVISIT - what to do about faults
        return null;
    }

    public Map<QName, ? extends DataBindingCallback> getOperations() {
        return opMap;
    }

    public SOAPBinding.Style getStyle() {
        return wsdlCache.getStyle();
    }

    public DataBindingCallback.Mode getServiceMode() {
        return DataBindingCallback.Mode.PARTS;
    }

    public WebServiceProvider getWebServiceProvider() {
        //not needed I think
        return null;
    }

    public Executor getExecutor() {
        //Let the transport handle it (or it goes to the Bus default wq
        //if the transport cannot handle it
        return null;
    }


    public QName getBindingType() {
        return BINDING_WS;
    }
}
