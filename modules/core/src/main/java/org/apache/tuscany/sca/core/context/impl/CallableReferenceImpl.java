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
package org.apache.tuscany.sca.core.context.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.UUID;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.BindingBuilderExtension;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.assembly.impl.ReferenceParametersImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeWireImpl;
import org.apache.tuscany.sca.core.context.ComponentContextExt;
import org.apache.tuscany.sca.core.context.CompositeContext;
import org.apache.tuscany.sca.core.context.ServiceReferenceExt;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * The old base class for implementations of service and callback references. We have maintained
 * this in Tuscany 2.x for the time being even though SCA 1.1 only defines service references and 
 * not callable reference
 *
 * @version $Rev$ $Date$
 * @param <B> the type of the business interface
 */
public class CallableReferenceImpl<B> implements ServiceReferenceExt<B> {
    static final long serialVersionUID = -521548304761848325L;
    
    protected transient ProxyFactory proxyFactory;
    protected transient Class<B> businessInterface;
    protected transient Object proxy;

    protected Object callbackID; // The callbackID should be serializable

    protected transient RuntimeComponent component;
    protected transient RuntimeComponentReference reference;
    protected transient EndpointReference endpointReference;

    protected String scdl;

    private transient RuntimeComponentReference clonedRef;
    private transient ReferenceParameters refParams;
    private transient XMLStreamReader xmlReader;

    protected transient CompositeActivator compositeActivator;
    private ExtensionPointRegistry registry;
    private FactoryExtensionPoint modelFactories;
    protected RuntimeAssemblyFactory assemblyFactory;
    private StAXArtifactProcessorExtensionPoint staxProcessors;
    private StAXArtifactProcessor<EndpointReference> staxProcessor; 
    private XMLInputFactory xmlInputFactory;
    private XMLOutputFactory xmlOutputFactory;

    /*
     * Public constructor for Externalizable serialization/deserialization
     */
    public CallableReferenceImpl() {
        super();
    }

    /*
     * Public constructor for use by XMLStreamReader2CallableReference
     */
    // TODO - EPR - Is this required
    public CallableReferenceImpl(XMLStreamReader xmlReader) throws Exception {
        this.xmlReader = xmlReader;
        resolve();
    }

    protected CallableReferenceImpl(Class<B> businessInterface,
                                    RuntimeComponent component,
                                    RuntimeComponentReference reference,
                                    EndpointReference endpointReference,
                                    ProxyFactory proxyFactory,
                                    CompositeActivator compositeActivator) {
        this.proxyFactory = proxyFactory;
        this.businessInterface = businessInterface;
        this.component = component;
        this.reference = reference;
        this.endpointReference = endpointReference;
        this.compositeActivator = compositeActivator;
        
        getExtensions();

        // FIXME: The SCA Specification is not clear how we should handle multiplicity
        // for CallableReference
        if (this.endpointReference == null) {

            // TODO - EPR - If no endpoint reference specified assume the first one
            //        This will happen when a self reference is created in which case the
            //        the reference should only have one endpointReference so use that
            if (this.reference.getEndpointReferences().size() == 0){
                throw new ServiceRuntimeException("The reference " + reference.getName() + " in component " +
                        component.getName() + " has no endpoint references");
            }

            if (this.reference.getEndpointReferences().size() > 1){
                throw new ServiceRuntimeException("The reference " + reference.getName() + " in component " +
                        component.getName() + " has more than one endpoint reference");
            }

            this.endpointReference = this.reference.getEndpointReferences().get(0);
        }

        // FIXME: Should we normalize the componentName/serviceName URI into an absolute SCA URI in the SCA binding?
        // sca:component1/component11/component112/service1?
        this.compositeActivator = compositeActivator;
        initCallbackID();
    }
    
    private void getExtensions() {
        this.registry = compositeActivator.getCompositeContext().getExtensionPointRegistry();
        this.modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
        this.xmlInputFactory = modelFactories.getFactory(XMLInputFactory.class);
        this.xmlOutputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        this.staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        this.staxProcessor = staxProcessors.getProcessor(EndpointReference.class);
    }

    public CallableReferenceImpl(Class<B> businessInterface, RuntimeWire wire, ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
        this.businessInterface = businessInterface;
        //ExtensionPointRegistry registry = ((RuntimeWireImpl)wire).getExtensionPoints();
        //this.modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        //this.assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
        bind(wire);
    }

    public RuntimeWire getRuntimeWire() {
        try {
            resolve();
            if (endpointReference != null){
                return reference.getRuntimeWire(endpointReference);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }
    
    public EndpointReference getEndpointReference() {
        return endpointReference;
    }

    protected void bind(RuntimeWire wire) {
        if (wire != null) {
            this.component = (RuntimeComponent)wire.getEndpointReference().getComponent();
            this.reference = (RuntimeComponentReference)wire.getEndpointReference().getReference();
            this.endpointReference = wire.getEndpointReference();
            this.compositeActivator = ((ComponentContextExt)component.getComponentContext()).getCompositeActivator();
            initCallbackID();
        }
    }

    protected void initCallbackID() {
        if (reference.getInterfaceContract() != null) {
            if (reference.getInterfaceContract().getCallbackInterface() != null) {
                this.callbackID = createCallbackID();
            }
        }
    }

    public B getProxy() throws ObjectCreationException {
        try {
            if (proxy == null) {
                proxy = createProxy();
            }
            return businessInterface.cast(proxy);
        } catch (Exception e) {
            throw new ObjectCreationException(e);
        }
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }

    protected Object createProxy() throws Exception {
        return proxyFactory.createProxy(this);
    }

    public B getService() {
        try {
            resolve();
            return getProxy();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public Class<B> getBusinessInterface() {
        try {
            resolve();
            return businessInterface;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public boolean isConversational() {
        try {
            resolve();
            return reference == null ? false : reference.getInterfaceContract().getInterface().isConversational();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public Object getCallbackID() {
        try {
            resolve();
            return callbackID;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Follow a service promotion chain down to the inner most (non composite)
     * component service.
     *
     * @param topCompositeService
     * @return
     */
    private ComponentService getPromotedComponentService(CompositeService compositeService) {
        ComponentService componentService = compositeService.getPromotedService();
        if (componentService != null) {
            Service service = componentService.getService();
            if (componentService.getName() != null && service instanceof CompositeService) {

                // Continue to follow the service promotion chain
                return getPromotedComponentService((CompositeService)service);

            } else {

                // Found a non-composite service
                return componentService;
            }
        } else {

            // No promoted service
            return null;
        }
    }

    // ============ WRITE AND READ THE REFERENCE TO EXTERNAL XML ========================
    
    /**
     * write the reference to a stream
     * 
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        try {
            String xml = null;
            if (scdl == null){
                xml = toXMLString();
            } else {
                xml = scdl;
            }
            
            if (xml == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                out.writeUTF(xml);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            throw new IOException(e.toString());
        }
    }  
    
    /**
     * write the endpoint reference into an xml string
     */
    public String toXMLString() throws IOException, XMLStreamException, ContributionWriteException{
        StringWriter writer = new StringWriter();
        XMLStreamWriter streamWriter = xmlOutputFactory.createXMLStreamWriter(writer);
        staxProcessor.write(endpointReference, streamWriter);
        return writer.toString();
    }   
    
    /**
     * Read the reference from a stream
     * 
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final boolean hasSCDL = in.readBoolean();
        if (hasSCDL) {
            this.scdl = in.readUTF();
        } else {
            this.scdl = null;
        }
    }  
    
    /**
     * Read xml string into the endpoint reference
     */
    public void fromXMLString() throws IOException, XMLStreamException, ContributionReadException {
        
        XMLStreamReader streamReader = xmlReader;
        
        if (scdl != null ){
            Reader reader = new StringReader(scdl);
            
            if (xmlInputFactory == null){
                // this is a reference being read from a external stream
                // so set up enough of the reference in order to resolved the
                // xml being read
                CompositeContext componentContextHelper = CompositeContext.getCurrentCompositeContext();
                this.compositeActivator = CompositeContext.getCurrentCompositeActivator();
                getExtensions();
            }
            
            streamReader = xmlInputFactory.createXMLStreamReader(reader);
        }
        
        endpointReference = staxProcessor.read(streamReader);
        
        // ok to GC
        xmlReader = null;
        scdl = null;
    }
    
    /**
     * @throws IOException
     */
    private synchronized void resolve() throws Exception {
        if ((scdl != null || xmlReader != null) && component == null && reference == null) {
            fromXMLString();
            
            this.component = (RuntimeComponent)endpointReference.getComponent();
            compositeActivator.configureComponentContext(this.component);
            
            this.reference = (RuntimeComponentReference)endpointReference.getReference();
            this.reference.setComponent(this.component);
            clonedRef = reference;
            
            ReferenceParameters parameters = null;
            for (Object ext : reference.getExtensions()) {
                if (ext instanceof ReferenceParameters) {
                    parameters = (ReferenceParameters)ext;
                    break;
                }
            }
            
            if (parameters != null) {
                refParams = parameters;
                this.callbackID = parameters.getCallbackID();
            }

            Interface i = reference.getInterfaceContract().getInterface();
            if (i instanceof JavaInterface) {
                JavaInterface javaInterface = (JavaInterface)i;
                if (javaInterface.isUnresolved()) {
                    // Allow privileged access to get ClassLoader. Requires RuntimePermission in
                    // security policy.
                    ClassLoader classLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                        public ClassLoader run() {
                            return Thread.currentThread().getContextClassLoader();
                        }
                    });
                    javaInterface.setJavaClass(classLoader.loadClass(javaInterface.getName()));
                    compositeActivator.getCompositeContext().getJavaInterfaceFactory()
                        .createJavaInterface(javaInterface, javaInterface.getJavaClass());
                    //FIXME: If the interface needs XSDs to be loaded (e.g., for static SDO),
                    // this needs to be done here.  We usually search for XSDs in the current
                    // contribution at resolve time.  Is it possible to locate the current
                    // contribution at runtime?
                }
                this.businessInterface = (Class<B>)javaInterface.getJavaClass();
            }
            
            if (endpointReference.getBinding() instanceof BindingBuilderExtension) {
                ((BindingBuilderExtension)endpointReference.getBinding()).getBuilder().build(component, reference, endpointReference.getBinding(), null);
            }            

            this.proxyFactory = compositeActivator.getCompositeContext().getProxyFactory();       
        } else if (compositeActivator == null) {
            this.compositeActivator = CompositeContext.getCurrentCompositeActivator();
            if (this.compositeActivator != null) {
                this.proxyFactory = this.compositeActivator.getCompositeContext().getProxyFactory();
            }
        }       
    }
    
    // ==================================================================================

    /**
     * Create a callback id
     *
     * @return the callback id
     */
    private String createCallbackID() {
        return UUID.randomUUID().toString();
    }

    public void attachCallbackID(Object callbackID) {
        this.callbackID = callbackID;
    }

    protected ReferenceParameters getReferenceParameters() {
        ReferenceParameters parameters = new ReferenceParametersImpl();
        parameters.setCallbackID(callbackID);
        return parameters;
    }

    public XMLStreamReader getXMLReader() {
        return xmlReader;
    }

}
