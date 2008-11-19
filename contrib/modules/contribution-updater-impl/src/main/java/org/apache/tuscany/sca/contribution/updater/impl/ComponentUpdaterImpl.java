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
package org.apache.tuscany.sca.contribution.updater.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.updater.ComponentNotFoundException;
import org.apache.tuscany.sca.contribution.updater.ComponentUpdater;
import org.apache.tuscany.sca.contribution.updater.ComponentUpdaterException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.CompositeActivatorImpl;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.contribution.updater.impl.ArtifactsFinder;
import org.apache.tuscany.sca.assembly.xml.MetaComponentProcessor;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.introspect.impl.JavaIntrospectionHelper;

public class ComponentUpdaterImpl implements ComponentUpdater {

    private String contribURI;
    private String compositeURI;
    private String componentName;
    private JavaInterfaceFactory javaFactory;
    private CompositeBuilder compositeBuilder;
    private CompositeActivator compositeActivator;
    private ContributionService contributionService;
    private ExtensionPointRegistry registry;
    private AssemblyFactory assemblyFactory;
    private InterfaceContractMapper interfaceContractMapper;
    private Contribution contrib;

    public ComponentUpdaterImpl(String contribURI, String compositeURI,
            String componentName, AssemblyFactory assembly,
            JavaInterfaceFactory javaFactory,
            CompositeBuilder compositeBuilder,
            CompositeActivator compositeActivator,
            ContributionService contribService,
            ExtensionPointRegistry registry, InterfaceContractMapper mapper) {
        this.contribURI = contribURI;
        this.compositeURI = compositeURI;
        this.componentName = componentName;
        this.javaFactory = javaFactory;
        this.compositeBuilder = compositeBuilder;
        this.compositeActivator = compositeActivator;
        this.contributionService = contribService;
        this.registry = registry;
        this.assemblyFactory = assembly;
        this.interfaceContractMapper = mapper;
        this.contrib = this.contributionService.getContribution(contribURI);
    }

    private void reconcileReference(Reference reference,
            ComponentReference targetReference, String componentName) {

        targetReference.setReference(reference);
        targetReference.setIsCallback(reference.isCallback());
        targetReference.setMultiplicity(reference.getMultiplicity());
        if (targetReference.getInterfaceContract() != null) {
            if (!targetReference.getInterfaceContract().equals(
                    reference.getInterfaceContract())) {
                if (!interfaceContractMapper.isCompatible(reference
                        .getInterfaceContract(), targetReference
                        .getInterfaceContract())) {
                    System.err
                            .println("Component reference interface incompatible with reference interface: "
                                    + componentName
                                    + "/"
                                    + targetReference.getName());
                }
            }
        } else {
            targetReference.setInterfaceContract(reference
                    .getInterfaceContract());
        }
        if (targetReference.getBindings().isEmpty()) {
            targetReference.getBindings().addAll(reference.getBindings());
        }

        // Reconcile callback bindings
        if (targetReference.getCallback() == null) {
            targetReference.setCallback(reference.getCallback());
            if (targetReference.getCallback() == null) {
                targetReference.setCallback(assemblyFactory.createCallback());
            }

        } else if (targetReference.getCallback().getBindings().isEmpty()
                && reference.getCallback() != null) {
            targetReference.getCallback().getBindings().addAll(
                    reference.getCallback().getBindings());
        }
        Contribution contrib = contributionService.getContribution(contribURI);
        List<DeployedArtifact> artifacts = contrib.getArtifacts();
        // RuntimeComponent source = null;
        Composite composite = ArtifactsFinder.findComposite(compositeURI,
                artifacts);
        Component c = ArtifactsFinder.findComponent(composite, componentName);
        if (targetReference.getAutowire() == null) {
            targetReference.setAutowire(c.getAutowire());
        }

        // Reconcile targets
        if (targetReference.getTargets().isEmpty()) {
            targetReference.getTargets().addAll(reference.getTargets());
        }

    }

    public org.apache.tuscany.sca.assembly.Reference buildReference(
            String name, String className, Class<?> businessInterface,
            boolean required) throws Exception {

        Class<?> rawType = contrib.getClassLoader().loadClass(className);

        if (rawType == null)
            return null;

        org.apache.tuscany.sca.assembly.Reference reference = assemblyFactory
                .createReference();
        JavaInterfaceContract interfaceContract = javaFactory
                .createJavaInterfaceContract();
        reference.setInterfaceContract(interfaceContract);
        reference.setName(name);

        if (required) {
            reference.setMultiplicity(Multiplicity.ONE_ONE);
        } else {
            reference.setMultiplicity(Multiplicity.ZERO_ONE);
        }
        Type[] interfaces = rawType.getGenericInterfaces();
        Type genericType = null;

        for (int i = 0; i < interfaces.length; ++i) {
            Type tmp = interfaces[i];
            if (interfaces[i] instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) tmp;
                tmp = type.getRawType();
            }
            if (tmp.getClass().getName().equals(
                    businessInterface.getClass().getName())) {
                genericType = tmp;
            }
        }
        if (genericType == null)
            throw new ComponentUpdaterException(
                    "User has specified a wrong businessInterface:"
                            + businessInterface);
        // baseType = JavaIntrospectionHelper.getBusinessInterface(baseType,
        // genericType);
        /*
         * Class<?> baseType = getBaseType(rawType, genericType); if
         * (CallableReference.class.isAssignableFrom(baseType)) { if
         * (Collection.class.isAssignableFrom(rawType)) { genericType =
         * JavaIntrospectionHelper.getParameterType(genericType); } baseType =
         * JavaIntrospectionHelper.getBusinessInterface(baseType, genericType); }
         */
        try {

            JavaInterface callInterface = javaFactory
                    .createJavaInterface(JavaIntrospectionHelper
                            .getErasure(genericType));
            reference.getInterfaceContract().setInterface(callInterface);
            if (callInterface.getCallbackClass() != null) {
                JavaInterface callbackInterface = javaFactory
                        .createJavaInterface(callInterface.getCallbackClass());
                reference.getInterfaceContract().setCallbackInterface(
                        callbackInterface);
            }
        } catch (InvalidInterfaceException e) {
            throw new IntrospectionException(e);
        }
        return reference;
    }

    /*
     * private org.apache.tuscany.sca.assembly.Reference
     * createReference(JavaElementImpl element, String name) throws
     * IntrospectionException { org.apache.tuscany.sca.assembly.Reference
     * reference = assemblyFactory.createReference(); JavaInterfaceContract
     * interfaceContract = javaFactory.createJavaInterfaceContract();
     * reference.setInterfaceContract(interfaceContract);
     *  // reference.setMember((Member)element.getAnchor()); boolean required =
     * true; Reference ref = element.getAnnotation(Reference.class); if (ref !=
     * null) { required = ref.required(); } // reference.setRequired(required);
     * reference.setName(name); Class<?> rawType = element.getType(); if
     * (rawType.isArray() || Collection.class.isAssignableFrom(rawType)) { if
     * (required) { reference.setMultiplicity(Multiplicity.ONE_N); } else {
     * reference.setMultiplicity(Multiplicity.ZERO_N); } } else { if (required) {
     * reference.setMultiplicity(Multiplicity.ONE_ONE); } else {
     * reference.setMultiplicity(Multiplicity.ZERO_ONE); } } Type genericType =
     * element.getGenericType(); Class<?> baseType = getBaseType(rawType,
     * genericType); if (CallableReference.class.isAssignableFrom(baseType)) {
     * if (Collection.class.isAssignableFrom(rawType)) { genericType =
     * JavaIntrospectionHelper.getParameterType(genericType); } baseType =
     * JavaIntrospectionHelper.getBusinessInterface(baseType, genericType); }
     * try { JavaInterface callInterface =
     * javaFactory.createJavaInterface(baseType);
     * reference.getInterfaceContract().setInterface(callInterface); if
     * (callInterface.getCallbackClass() != null) { JavaInterface
     * callbackInterface =
     * javaFactory.createJavaInterface(callInterface.getCallbackClass());
     * reference.getInterfaceContract().setCallbackInterface(callbackInterface); } }
     * catch (InvalidInterfaceException e) { throw new
     * IntrospectionException(e); } return reference; }
     */
    public ComponentReference addReferenceWire(String referenceName,
            String className, Class<?> interfaceName, String targetComponent)
            throws ComponentUpdaterException, ComponentNotFoundException {

        StAXArtifactProcessorExtensionPoint staxProcessors = registry
                .getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        MetaComponentProcessor processor = (MetaComponentProcessor) staxProcessors
                .getProcessor(Component.class);

        Contribution contrib = contributionService.getContribution(contribURI);

        List<DeployedArtifact> artifacts = contrib.getArtifacts();
        RuntimeComponent source = null;
        Composite composite = ArtifactsFinder.findComposite(compositeURI,
                artifacts);
        // TODO error handling
        if (composite != null)
            source = (RuntimeComponent) ArtifactsFinder.findComponent(
                    composite, componentName);
        else {
            throw new ComponentNotFoundException("Not found component "
                    + componentName + " for update");
        }
        if (source != null) {
            // Debig this
            RuntimeComponentReference targetReference = (RuntimeComponentReference) assemblyFactory
                    .createComponentReference();
            targetReference.setName(referenceName);
            targetReference.setUnresolved(true);
            Reference reference = null;
            try {
                reference = buildReference(referenceName, className,
                        interfaceName, true);
            } catch (Exception e) {
                throw new ComponentUpdaterException(e.getMessage());
            }
            source.getImplementation().getReferences().add(reference);

            // targetService.
            ComponentService targetService = assemblyFactory
                    .createComponentService();
            targetService.setUnresolved(true);
            targetService.setName(targetComponent);
            targetReference.getTargets().add(targetService);

            // reconciliate
            reconcileReference(reference, targetReference, componentName);
            // create component reference for the reference
            source.getReferences().add(targetReference);
            try {
                processor.resolveReference(targetReference, contrib
                        .getModelResolver());
            } catch (ContributionResolveException e) {
                throw new ComponentUpdaterException(
                        "Contribution Resolving Exception while updating..");
            }

            CompositeActivatorImpl activator = (CompositeActivatorImpl) compositeActivator;
            compositeBuilder.attachWire(source, composite, targetReference);
            activator.activate(source, targetReference);
            synchronized (source) {
                activator.configureComponentContext(source);
            }
            // RuntimeComponentReference runtimeRef =
            // ((RuntimeComponentReference)ref);
            // runtimeRef.setComponent(component);
            for (Binding binding : targetReference.getBindings()) {
                ReferenceBindingProvider bindingProvider = targetReference
                        .getBindingProvider(binding);
                if (bindingProvider != null) {
                    bindingProvider.start();
                }

            }

            ImplementationProvider implementationProvider = source
                    .getImplementationProvider();
            if (implementationProvider != null) {
                /*
                 * con la reflection cosi tolgo la dipendneza if
                 * ((implementationProvider.getClass().getName().equals("JavaImplementationProvider")) {
                 * Clazz cget } implementationProvider.getClass(). if
                 * (implementationProvider instanceof
                 * JavaImplementationProvider) { ((JavaImplementationProvider)
                 * implementationProvider). startReference(targetReference); }
                 */
            }
            return targetReference;
        } else {
            throw new ComponentNotFoundException("Not found component "
                    + componentName + " for update");
        }
    }

    public ComponentReference removeReferenceWire(String referenceName,
            String targetComponent) throws ComponentUpdaterException,
            ComponentNotFoundException {
        List<DeployedArtifact> artifacts = contributionService.getContribution(
                contribURI).getArtifacts();
        RuntimeComponent source = null;
        Composite composite = ArtifactsFinder.findComposite(compositeURI,
                artifacts);
        ComponentReference toRemove = null;

        if (composite != null) {
            source = (RuntimeComponent) ArtifactsFinder.findComponent(
                    composite, this.componentName);
        } else {
            throw new ComponentNotFoundException("Not found component "
                    + componentName + " for update");
        }
        if ((source != null) && (targetComponent != null)) {
            /* source target refenence */

            List<ComponentReference> references = source.getReferences();
            for (ComponentReference ref : references) {
                if (ref.getName().equals(targetComponent)) {
                    toRemove = ref;
                    break;
                }
            }
            if (toRemove != null) {
                CompositeActivatorImpl activator = (CompositeActivatorImpl) compositeActivator;
                activator.stop(source, toRemove);
                activator.deactivate(source,
                        (RuntimeComponentReference) toRemove);
                compositeBuilder.detachWire(composite, source, toRemove);
            } else {
                throw new ComponentNotFoundException("Not found component "
                        + componentName + " for update");
            }

        }
        return toRemove;
    }
}
