package org.apache.tuscany.binding.celtix.handler;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import javax.jws.soap.SOAPBinding.Style;
import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceProvider;

import commonj.sdo.helper.TypeHelper;

import org.apache.tuscany.binding.celtix.assembly.WebServiceBinding;
import org.apache.tuscany.binding.celtix.handler.io.SCAServerDataBindingCallback;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.context.ContextInitException;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.impl.EntryPointContextImpl;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.objectweb.celtix.Bus;
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
import org.osoa.sca.annotations.Destroy;
import org.xmlsoap.schemas.wsdl.http.AddressType;

public class CeltixEntryPointContextImpl extends EntryPointContextImpl
    implements ServerBindingEndpointCallback {
    
    EntryPoint entry;
    private Bus bus;
    private Port port;
    private TypeHelper typeHelper;
    private WSDLMetaDataCache wsdlCache;
    private ServerBinding serverBinding;
    private Object entryPointProxy;
    
    private Map<QName, ServerDataBindingCallback> opMap = 
        new ConcurrentHashMap<QName, ServerDataBindingCallback>(); 

    
    public CeltixEntryPointContextImpl(EntryPoint entry,
                                       SourceWireFactory sourceWireFactory,
                                       MessageFactory messageFactory)
        throws ContextInitException {
        
        super(entry.getName(), sourceWireFactory, messageFactory);
        this.entry = entry;
    }

    public void start() throws ContextInitException {
        // TODO Auto-generated method stub
        super.start();
        System.out.println("In start");
        
        entryPointProxy = getInstance(null);
        WebServiceBinding wsBinding = (WebServiceBinding)entry.getBindings().get(0);
        bus = wsBinding.getBus();
        typeHelper = wsBinding.getTypeHelper();
        Definition wsdlDef = wsBinding.getWSDLDefinition();
        port = wsBinding.getWSDLPort();
        wsdlCache = new WSDLMetaDataCache(wsdlDef, wsBinding.getWSDLPort());
        
        initOperationMap(wsdlDef);
        
        String key = wsdlDef.getDocumentBaseURI();
        URL url;
        try {
            url = new URL(key);
        } catch (MalformedURLException e) {
            throw new ContextInitException(e);
        }

        EndpointReferenceType reference = EndpointReferenceUtils.getEndpointReference(url,
                wsBinding.getWSDLService().getQName(),
                wsBinding.getWSDLPort().getName());
        
        AttributedURIType address = new AttributedURIType();

        String bindingId = null;
        Binding binding = wsBinding.getWSDLPort().getBinding();
        if (null != binding) {
            List list = binding.getExtensibilityElements();
            if (!list.isEmpty()) {
                bindingId = ((ExtensibilityElement)list.get(0)).getElementType().getNamespaceURI();
            }
        }
        List<?> list = wsBinding.getWSDLPort().getExtensibilityElements();
        for (Object ep : list) {
            ExtensibilityElement ext = (ExtensibilityElement)ep;
            if (ext instanceof SOAPAddress) {
                if (bindingId == null) {
                    bindingId = ((SOAPAddress)ext).getLocationURI();
                }
                address.setValue(((SOAPAddress)ext).getLocationURI());
            }
            if (ext instanceof AddressType) {
                if (bindingId == null) {
                    bindingId = ((AddressType)ext).getLocation();
                }
                address.setValue(((AddressType)ext).getLocation());
            }
        }
        if (reference.getAddress() == null) {
            //REVIST - bug in Celtix that the HTTP transport won't find the address correctly
            reference.setAddress(address);
        }
        
        try {
            serverBinding = bus.getBindingManager().getBindingFactory(bindingId).createServerBinding(
                    reference, this);
            serverBinding.activate();
        } catch (Exception e) {
            throw new ContextInitException(e);
        }        
    }

    @Destroy
    public void stop() throws CoreRuntimeException {
        System.out.println("In stop");
        super.stop();
    }

    private void initOperationMap(Definition def) {
        List ops = port.getBinding().getBindingOperations();
        Iterator opIt = ops.iterator();
        while (opIt.hasNext()) {
            BindingOperation op = (BindingOperation)opIt.next();
            BindingInput bindingInput = op.getBindingInput();
            List elements = bindingInput.getExtensibilityElements();
            QName qn = new QName(def.getTargetNamespace(), op.getName());
            for (Iterator i = elements.iterator(); i.hasNext();) {
                Object element = i.next();
                if (SOAPBody.class.isInstance(element)) {
                    SOAPBody body = (SOAPBody)element;
                    if (body.getNamespaceURI() != null) {
                        qn = new QName(body.getNamespaceURI(), op.getName());
                    }
                }
            }
            
            ServerDataBindingCallback cb = getDataBindingCallback(qn, null,
                                                                  DataBindingCallback.Mode.PARTS);
            opMap.put(qn, cb);
            if (!"".equals(cb.getRequestWrapperQName().getLocalPart())) {
                opMap.put(cb.getRequestWrapperQName(), cb);
            }
        }        
    }
    public ServerDataBindingCallback getDataBindingCallback(QName operationName,
                                                            ObjectMessageContext objContext,
                                                            DataBindingCallback.Mode mode) {
        if (opMap.containsKey(operationName)) {
            return opMap.get(operationName);
        }
        WSDLOperationInfo opInfo = wsdlCache.getOperationInfo(operationName.getLocalPart());
        boolean inout = false;
        
        
        Class<?> serviceInterface = getServiceInterface();
        System.out.println(serviceInterface.getName());
        Method meth = getMethod(serviceInterface, operationName.getLocalPart());
        System.out.println(meth);
        
        ServerDataBindingCallback scb = new SCAServerDataBindingCallback(opInfo,
                                                                         typeHelper,
                                                                         inout,
                                                                         meth,
                                                                         entryPointProxy);
        // TODO Auto-generated method stub
        return scb;
    }
    protected Method getMethod(Class<?> serviceInterface, String operationName) {
        // Note: this doesn't support overloaded operations
        Method[] methods = serviceInterface.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(operationName)) {
                return m;
            }
            // tolerate WSDL with capatalized operation name
            StringBuilder sb = new StringBuilder(operationName);
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
            if (m.getName().equals(sb.toString())) {
                return m;
            }
        }
        throw new BuilderConfigException("no operation named " + operationName
                                         + " found on service interface: " + serviceInterface.getName());
    }

    
    public DataBindingCallback getFaultDataBindingCallback(ObjectMessageContext objContext) {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<QName, ? extends DataBindingCallback> getOperations() {
        return opMap;
    }

    public Style getStyle() {
        // TODO Auto-generated method stub
        return wsdlCache.getStyle();
    }
    public DataBindingCallback.Mode getServiceMode() {
        return DataBindingCallback.Mode.PARTS;
    }

    public WebServiceProvider getWebServiceProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    public Executor getExecutor() {
        //Let the transport handle it (or it goes to the Bus default wq 
        //if the transport cannot handle it
        return null;
    }

    
    
}

