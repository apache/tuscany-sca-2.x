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
import java.io.StringWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.context.ServiceReferenceExt;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.runtime.Invocable;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Default implementation of a ServiceReference.
 *
 * @version $Rev$ $Date$
 * @param <B> the type of the business interface
 */
public class ServiceReferenceImpl<B> implements ServiceReferenceExt<B> {
    private static final long serialVersionUID = 6763709434194361540L;

    protected transient ProxyFactory proxyFactory;
    protected transient Class<B> businessInterface;
    protected transient B proxy;

    protected Object callbackID; // The callbackID should be serializable

    protected transient RuntimeEndpointReference endpointReference;

//    protected String scdl;
//
//    private transient XMLStreamReader xmlReader;

    protected transient CompositeContext compositeContext;
    protected ExtensionPointRegistry registry;
    protected FactoryExtensionPoint modelFactories;
    protected RuntimeAssemblyFactory assemblyFactory;
    protected StAXArtifactProcessorExtensionPoint staxProcessors;
    protected StAXArtifactProcessor<EndpointReference> staxProcessor;
    protected XMLInputFactory xmlInputFactory;
    protected XMLOutputFactory xmlOutputFactory;
    protected BuilderExtensionPoint builders;

    /*
     * Public constructor for Externalizable serialization/deserialization
     */
    public ServiceReferenceImpl() {
        super();
    }

    public ServiceReferenceImpl(Class<B> businessInterface,
                                Invocable endpointReference,
                                CompositeContext compositeContext) {
        this.businessInterface = businessInterface;
        this.endpointReference = (RuntimeEndpointReference) endpointReference;
        if (compositeContext == null) {
            compositeContext = endpointReference.getCompositeContext();
        }
        bind(compositeContext);
    }
    
    public ServiceReferenceImpl(Class<B> businessInterface,
                                Invocable endpointReference) {
        this(businessInterface, endpointReference, null);
    }

    protected void bind(CompositeContext context) {
        this.compositeContext = context;
        this.registry = compositeContext.getExtensionPointRegistry();
        this.modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
        this.xmlInputFactory = modelFactories.getFactory(XMLInputFactory.class);
        this.xmlOutputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        this.staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        this.staxProcessor = staxProcessors.getProcessor(EndpointReference.class);
        this.builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
        this.proxyFactory = ExtensibleProxyFactory.getInstance(registry);
    }

    public RuntimeEndpointReference getEndpointReference() {
        return endpointReference;
    }

    public B getProxy() throws ObjectCreationException {
        try {
            if (proxy == null) {
                proxy = createProxy();
            }
            return proxy;
        } catch (Exception e) {
            throw new ObjectCreationException(e);
        }
    }

    public void setProxy(B proxy) {
        this.proxy = proxy;
    }

    protected B createProxy() throws Exception {
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
        out.writeObject(endpointReference);
        /*
        try {
            String xml = null;
            if (scdl == null) {
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
        */
    }

    /**
     * write the endpoint reference into an xml string
     */
    public String toXMLString() throws IOException, XMLStreamException, ContributionWriteException {
        StringWriter writer = new StringWriter();
        XMLStreamWriter streamWriter = xmlOutputFactory.createXMLStreamWriter(writer);
        staxProcessor.write(endpointReference, streamWriter, new ProcessorContext(registry));
        return writer.toString();
    }

    /**
     * Read the reference from a stream
     * 
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.endpointReference = (RuntimeEndpointReference) in.readObject();
        // Force resolve
        endpointReference.getComponent();
        bind(endpointReference.getCompositeContext());

        RuntimeComponentReference reference = (RuntimeComponentReference)endpointReference.getReference();
        reference.setComponent((RuntimeComponent)endpointReference.getComponent());

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
                JavaInterfaceFactory javaInterfaceFactory = getJavaInterfaceFactory(compositeContext);

                try {
                    javaInterfaceFactory.createJavaInterface(javaInterface, javaInterface.getJavaClass());
                } catch (InvalidInterfaceException e) {
                    throw new ServiceRuntimeException(e);
                }
                //FIXME: If the interface needs XSDs to be loaded (e.g., for static SDO),
                // this needs to be done here.  We usually search for XSDs in the current
                // contribution at resolve time.  Is it possible to locate the current
                // contribution at runtime?
            }
            this.businessInterface = (Class<B>)javaInterface.getJavaClass();
        }

        Binding binding = endpointReference.getBinding();
        if (binding != null) {
            BindingBuilder bindingBuilder = builders.getBindingBuilder(binding.getType());
            if (bindingBuilder != null) {
                org.apache.tuscany.sca.assembly.builder.BuilderContext context = new BuilderContext(registry);
                bindingBuilder.build(endpointReference.getComponent(), reference, endpointReference.getBinding(), context);
            }
        }

        this.proxyFactory = getProxyFactory(this.compositeContext);

        /*
        endpointReference.bind(CompositeContext.getCurrentCompositeContext());
        endpointReference.rebuild();
        */
        /*
        final boolean hasSCDL = in.readBoolean();
        if (hasSCDL) {
            this.scdl = in.readUTF();
        } else {
            this.scdl = null;
        }
        */
    }

    /**
     * Read xml string into the endpoint reference
     */
    /*
    public void fromXMLString() throws IOException, XMLStreamException, ContributionReadException {

        XMLStreamReader streamReader = xmlReader;

        if (scdl != null) {
            Reader reader = new StringReader(scdl);

            if (xmlInputFactory == null) {
                // this is a reference being read from a external stream
                // so set up enough of the reference in order to resolved the
                // xml being read
                bind(CompositeContext.getCurrentCompositeContext());
            }

            streamReader = xmlInputFactory.createXMLStreamReader(reader);
        }

        endpointReference = (RuntimeEndpointReference) staxProcessor.read(streamReader, new ProcessorContext(registry));

        // ok to GC
        xmlReader = null;
        scdl = null;
    }
    */

    /**
     * @throws IOException
     */
    private synchronized void resolve() throws Exception {
        /*
        if ((scdl != null || xmlReader != null) && endpointReference == null) {
            fromXMLString();

            compositeContext.bindComponent((RuntimeComponent) endpointReference.getComponent());

            RuntimeComponentReference reference = (RuntimeComponentReference)endpointReference.getReference();
            reference.setComponent((RuntimeComponent)endpointReference.getComponent());

            ReferenceParameters parameters = null;
            for (Object ext : reference.getExtensions()) {
                if (ext instanceof ReferenceParameters) {
                    parameters = (ReferenceParameters)ext;
                    break;
                }
            }

            if (parameters != null) {
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
                    JavaInterfaceFactory javaInterfaceFactory = getJavaInterfaceFactory(compositeContext);

                    javaInterfaceFactory.createJavaInterface(javaInterface, javaInterface.getJavaClass());
                    //FIXME: If the interface needs XSDs to be loaded (e.g., for static SDO),
                    // this needs to be done here.  We usually search for XSDs in the current
                    // contribution at resolve time.  Is it possible to locate the current
                    // contribution at runtime?
                }
                this.businessInterface = (Class<B>)javaInterface.getJavaClass();
            }

            Binding binding = endpointReference.getBinding();
            if (binding != null) {
                BindingBuilder bindingBuilder = builders.getBindingBuilder(binding.getType());
                if (bindingBuilder != null) {
                    BuilderContext context = new BuilderContext(registry);
                    bindingBuilder.build(endpointReference.getComponent(), reference, endpointReference.getBinding(), context);
                }
            }

            this.proxyFactory = getProxyFactory(this.compositeContext);
        } else if (compositeContext == null) {
            this.compositeContext = CompositeContext.getCurrentCompositeContext();
            if (this.compositeContext != null) {
                this.proxyFactory = getProxyFactory(this.compositeContext);
            }
        }
        */
    }

    private JavaInterfaceFactory getJavaInterfaceFactory(CompositeContext compositeContext) {
        ExtensionPointRegistry extensionPointRegistry = compositeContext.getExtensionPointRegistry();
        FactoryExtensionPoint factories = extensionPointRegistry.getExtensionPoint(FactoryExtensionPoint.class);
        JavaInterfaceFactory javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
        return javaInterfaceFactory;
    }

    private ProxyFactory getProxyFactory(CompositeContext compositeContext) {
        ExtensionPointRegistry extensionPointRegistry = compositeContext.getExtensionPointRegistry();
        return ExtensibleProxyFactory.getInstance(extensionPointRegistry);
    }

    // ==================================================================================

    /*
    public XMLStreamReader getXMLReader() {
        return xmlReader;
    }
    */
}
