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

package org.apache.tuscany.sca.core.component;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class ComponentContextHelper {

    private final AssemblyFactory assemblyFactory;
    private final JavaInterfaceFactory javaInterfaceFactory;
    private final StAXArtifactProcessorExtensionPoint staxProcessors;

    /**
     * @param assemblyFactory The factory to create assembly models
     * @param processors The extension point for stax artifact processors
     */
    public ComponentContextHelper(AssemblyFactory assemblyFactory,
                           JavaInterfaceFactory javaInterfaceFactory,
                           StAXArtifactProcessorExtensionPoint processors) {
        this.assemblyFactory = assemblyFactory;
        this.javaInterfaceFactory = javaInterfaceFactory;
        this.staxProcessors = processors;
    }

    /**
     * Create a self-reference for a component service
     * @param component
     * @param service
     * @throws CloneNotSupportedException 
     * @throws InvalidInterfaceException 
     */
    public ComponentReference createSelfReference(Component component,
                                                  ComponentService service,
                                                  Class<?> businessInterface) throws CloneNotSupportedException,
        InvalidInterfaceException {
        ComponentReference componentReference = assemblyFactory.createComponentReference();
        componentReference.setName("$self$." + service.getName());
        for (Binding binding : service.getBindings()) {
            if (binding instanceof WireableBinding) {
                WireableBinding wireableBinding = (WireableBinding)((WireableBinding)binding).clone();
                wireableBinding.setTargetBinding(binding);
                wireableBinding.setTargetComponent(component);
                wireableBinding.setTargetComponentService(service);
                wireableBinding.setRemote(false);
                componentReference.getBindings().add(wireableBinding);
            } else {
                componentReference.getBindings().add(binding);
            }
        }

        componentReference.setCallback(service.getCallback());
        componentReference.getTargets().add(service);
        componentReference.getPolicySets().addAll(service.getPolicySets());
        componentReference.getRequiredIntents().addAll(service.getRequiredIntents());

        InterfaceContract interfaceContract = service.getInterfaceContract();
        Service componentTypeService = service.getService();
        if (componentTypeService != null && componentTypeService.getInterfaceContract() != null) {
            interfaceContract = componentTypeService.getInterfaceContract();
        }
        interfaceContract = getInterfaceContract(interfaceContract, businessInterface);
        componentReference.setInterfaceContract(interfaceContract);
        componentReference.setMultiplicity(Multiplicity.ONE_ONE);
        // component.getReferences().add(componentReference);
        return componentReference;
    }

    /**
     * @param interfaceContract
     * @param businessInterface
     * @return
     * @throws CloneNotSupportedException
     * @throws InvalidInterfaceException
     */
    private InterfaceContract getInterfaceContract(InterfaceContract interfaceContract, Class<?> businessInterface)
        throws CloneNotSupportedException, InvalidInterfaceException {
        Interface interfaze = interfaceContract.getInterface();
        if (interfaze instanceof JavaInterface) {
            Class<?> cls = ((JavaInterface)interfaze).getJavaClass();
            if (!businessInterface.isAssignableFrom(cls)) {
                // The interface is not assignable from the interface contract
                interfaceContract = (InterfaceContract)interfaceContract.clone();
                interfaceContract.setInterface(javaInterfaceFactory.createJavaInterface(businessInterface));
            }
        }
        return interfaceContract;
    }

    /**
     * Bind a component reference to a component service
     * @param <B>
     * @param businessInterface
     * @param reference
     * @param service
     * @return
     * @throws CloneNotSupportedException
     * @throws InvalidInterfaceException
     */
    public RuntimeComponentReference bindComponentReference(Class<?> businessInterface,
                                                            RuntimeComponentReference reference,
                                                            RuntimeComponent component,
                                                            RuntimeComponentService service)
        throws CloneNotSupportedException, InvalidInterfaceException {
        RuntimeComponentReference ref = (RuntimeComponentReference)reference.clone();
        InterfaceContract interfaceContract = reference.getInterfaceContract();
        Reference componentTypeReference = reference.getReference();
        if (componentTypeReference != null && componentTypeReference.getInterfaceContract() != null) {
            interfaceContract = componentTypeReference.getInterfaceContract();
        }
        InterfaceContract refInterfaceContract = getInterfaceContract(interfaceContract, businessInterface);
        if (refInterfaceContract != interfaceContract) {
            ref = (RuntimeComponentReference)reference.clone();
            ref.setInterfaceContract(interfaceContract);
        }
        ref.setComponent(component);
        ref.getTargets().add(service);
        ref.getBindings().clear();
        for (Binding binding : service.getBindings()) {
            if (binding instanceof WireableBinding) {
                WireableBinding wireableBinding = (WireableBinding)((WireableBinding)binding).clone();
                wireableBinding.setTargetBinding(binding);
                wireableBinding.setTargetComponent(component);
                wireableBinding.setTargetComponentService(service);
                wireableBinding.setRemote(false);
                ref.getBindings().add(wireableBinding);
            } else {
                ref.getBindings().add(binding);
            }
        }
        return ref;
    }

    public void write(Component component, ComponentReference reference, Writer writer) throws IOException {
        try {
            StAXArtifactProcessor<Composite> processor = staxProcessors.getProcessor(Composite.class);
            Composite composite = assemblyFactory.createComposite();
            composite.setName(new QName("http://tuscany.apache.org/xmlns/sca/1.0", "default"));
            Component comp = assemblyFactory.createComponent();
            comp.setName("default");
            comp.setURI(component.getURI());
            composite.getComponents().add(comp);
            comp.getReferences().add(reference);

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(writer);
            processor.write(composite, streamWriter);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public String toXML(Component component, ComponentReference reference) throws IOException {
        StringWriter writer = new StringWriter();
        write(component, reference, writer);
        return writer.toString();
    }

    public RuntimeComponent read(Reader reader) throws IOException {
        try {
            StAXArtifactProcessor<Composite> processor = staxProcessors.getProcessor(Composite.class);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader streamReader = inputFactory.createXMLStreamReader(reader);
            Composite composite = processor.read(streamReader);
            RuntimeComponent component = (RuntimeComponent)composite.getComponents().get(0);
            return component;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public Component fromXML(String xml) throws IOException {
        return read(new StringReader(xml));
    }

    public static RuntimeComponent getCurrentComponent() {
        Message message = ThreadMessageContext.getMessageContext();
        if (message != null) {
            EndpointReference to = message.getTo();
            if (to == null) {
                return null;
            }
            RuntimeComponent component = message.getTo().getComponent();
            return component;
        }
        return null;
    }
    
    public static CompositeActivator getCurrentCompositeActivator() {
        RuntimeComponent component = getCurrentComponent();
        if (component != null) {
            ComponentContextImpl context = (ComponentContextImpl)component.getComponentContext();
            return context.getCompositeActivator();
        }
        return null;
    }
    
    public static ComponentContextHelper getCurrentComponentContextHelper() {
        CompositeActivator activator = getCurrentCompositeActivator();
        if (activator != null) {
            return activator.getComponentContextHelper();
        }
        return null;
    }

    /**
     * @param component
     */
    public static ComponentService getSingleService(Component component) {
        ComponentService targetService;
        List<ComponentService> services = component.getServices();
        List<ComponentService> regularServices = new ArrayList<ComponentService>();
        for (ComponentService service : services) {
            if (service.isCallback()) {
                continue;
            }
            String name = service.getName();
            if (!name.startsWith("$") || name.startsWith("$dynamic$")) {
                regularServices.add(service);
            }
        }
        if (regularServices.size() != 1) {
            throw new ServiceRuntimeException("The component doesn't have exactly one service");
        }
        targetService = regularServices.get(0);
        return targetService;
    }

}
