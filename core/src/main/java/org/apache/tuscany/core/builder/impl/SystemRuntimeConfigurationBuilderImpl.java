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
package org.apache.tuscany.core.builder.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.core.addressing.AddressingConstants;
import org.apache.tuscany.core.addressing.AddressingFactory;
import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.builder.SimpleComponentRuntimeConfiguration;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.config.SystemRuntimeConfigurationImpl;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.types.InterfaceType;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @author delfinoj
 */
public class SystemRuntimeConfigurationBuilderImpl {

    private AssemblyFactory assemblyFactory;
    private AddressingFactory addressingFactory;
    private ResourceLoader resourceLoader;
    
    /**
     * A model visitor that builds system components.
     * @author delfinoj
     *
     */
    private class Builder implements AssemblyModelVisitor {
        
        /**
         * @see org.apache.tuscany.model.assembly.AssemblyModelVisitor#visit(org.apache.tuscany.model.assembly.AssemblyModelObject)
         */
        public boolean visit(AssemblyModelObject modelObject) {
            if (modelObject instanceof Component) {
                Component component = (Component) modelObject;
                ComponentImplementation implementation = component.getComponentImplementation();
                if (implementation instanceof SystemImplementation) {
                    SystemImplementation systemImplementation = (SystemImplementation) implementation;
                    buildExtensionComponent(component, systemImplementation);
                }
            }
            return true;
        }

    }

    /**
     * A model visitor that assembles system components.
     * @author delfinoj
     *
     */
    private class Assembler implements AssemblyModelVisitor {
        
        /**
         * @see org.apache.tuscany.model.assembly.AssemblyModelVisitor#visit(org.apache.tuscany.model.assembly.AssemblyModelObject)
         */
        public boolean visit(AssemblyModelObject modelObject) {
            if (modelObject instanceof Component) {
                Component component = (Component) modelObject;
                ComponentImplementation implementation = component.getComponentImplementation();
                if (implementation instanceof SystemImplementation) {
                    SystemImplementation systemImplementation = (SystemImplementation) implementation;
                    assembleExtensionComponent(component, systemImplementation);
                }
            }
            return true;
        }

    }

    /**
     * Constructor
     */
    public SystemRuntimeConfigurationBuilderImpl(AssemblyFactory assemblyFactory, AddressingFactory addressingFactory,
            ResourceLoader resourceLoader) {
        super();
        this.assemblyFactory = assemblyFactory;
        this.addressingFactory = addressingFactory;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Build all system components in the given module.
     * 
     * @param module
     */
    public void build(Module module) {
        Builder builder=new Builder();
        module.accept(builder);
        for (ModuleFragment moduleFragment : module.getModuleFragments()) {
            moduleFragment.accept(builder);
        }
        Assembler assembler=new Assembler();
        module.accept(assembler);
        for (ModuleFragment moduleFragment : module.getModuleFragments()) {
            moduleFragment.accept(assembler);
        }
    }

    /**
     * Build the configuration of the given system component.
     * 
     * @param component
     * @param implementation
     */
    private void buildExtensionComponent(Component component, SystemImplementation implementation) {

        // Load the component implementation class. The component
        // implementation class must
        // implement the MessageHandler interface
        final String className = implementation.getClass_();
        Class implementationClass;
        try {
            // SECURITY
            implementationClass = (Class) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws ClassNotFoundException {
                    return resourceLoader.loadClass(className);
                }
            });
        } catch (PrivilegedActionException e1) {
            throw new ServiceRuntimeException(e1.getException());
        }

        // Create a new instance of the system component implementation class
        Object implementationInstance;
        try {
            implementationInstance = (MessageHandler) implementationClass.newInstance();
        } catch (InstantiationException e1) {
            throw new ServiceRuntimeException(e1);
        } catch (IllegalAccessException e1) {
            throw new ServiceRuntimeException(e1);
        }

        // Create a new runtime configuration
        SystemRuntimeConfigurationImpl runtimeConfiguration = new SystemRuntimeConfigurationImpl(implementationClass, implementationInstance);

        // Store it in the implementation model object
        implementation.setRuntimeConfiguration(runtimeConfiguration);
    }

    /**
     * Assemble the given component 
     * @param component
     * @param implementation
     */
    private void assembleExtensionComponent(Component component, SystemImplementation implementation) {
        
        // Get the component implementation class
        SystemRuntimeConfigurationImpl runtimeConfiguration = (SystemRuntimeConfigurationImpl) implementation.getRuntimeConfiguration();
        Class implementationClass=runtimeConfiguration.getInstanceClass();
        
        for (Iterator<ConfiguredReference> i = component.getConfiguredReferences().iterator(); i.hasNext();) {
            ConfiguredReference configuredReference = i.next();
            
            // Get the reference interface
            InterfaceType interfaceType=configuredReference.getReference().getInterfaceContract().getInterfaceType();
            Class interfaceClass=interfaceType.getInstanceClass();
            
            // Determine if we have multiplicity here
            boolean isMultiplicityN=configuredReference.getReference().isMultiplicityN();

            // Look for a setter method for each reference
            String referenceName = configuredReference.getReference().getName();
            String propertyName;
            if (referenceName.length() == 1)
                propertyName = "set" + Character.toUpperCase(referenceName.charAt(0));
            else
                propertyName = "set" + Character.toUpperCase(referenceName.charAt(0)) + referenceName.substring(1);
            Method setter;
            try {

                // Determine the setter method signature
                Class[] signature=null;
                if (isMultiplicityN) {
                    signature=new Class[]{List.class};
                } else {
                    if (interfaceClass==MessageHandler.class) {
                        signature=new Class[]{EndpointReference.class};
                    } else {
                        signature=new Class[]{interfaceClass};
                    }
                }
                
                // Get the setter method
                setter = implementationClass.getMethod(propertyName, signature);
                
            } catch (NoSuchMethodException e) {
                continue;
            }

            // Get the implementation instance
            Object implementationInstance=runtimeConfiguration.createComponentContext().getInstance(null);
            
            // Get the value of the reference
            Object configuredReferenceValue=getConfiguredReferenceValue(implementationInstance, configuredReference, isMultiplicityN, interfaceClass);
            
            // Invoke it
            try {
                setter.invoke(implementationInstance, new Object[] { configuredReferenceValue });
            
            } catch (IllegalArgumentException e) {
                throw new ServiceRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ServiceRuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new ServiceRuntimeException(e);
            }
        }
    }
    
    /**
     * Returns a list of endpoint references
     * 
     * @param fromMessageHandler
     * @param configuredReference
     * @return
     */
    private Object getConfiguredReferenceValue(Object fromServiceInstance, ConfiguredReference configuredReference, boolean isMultiplicityN, Class interfaceClass) {
        List<ConfiguredService> configuredServices;
        if (configuredReference != null) {
            configuredServices = configuredReference.getConfiguredServices();
        } else {
            configuredServices = null;
        }
        if (configuredServices == null) {
            throw new ServiceRuntimeException("Cannot find service for " + configuredReference.getReference().getName());
        }
        if (interfaceClass==MessageHandler.class) {
            if (isMultiplicityN) {
                List values = new ArrayList();
                for (ConfiguredService configuredService : configuredServices) {
                    values.add(getConfiguredServiceEndpointReference(configuredReference, (MessageHandler)fromServiceInstance, configuredService));
                }
                return values;
            } else {
                ConfiguredService configuredService=configuredServices.get(0);
                return getConfiguredServiceEndpointReference(configuredReference, (MessageHandler)fromServiceInstance, configuredService);
            }
        } else {
            if (isMultiplicityN) {
                List values = new ArrayList();
                for (ConfiguredService configuredService : configuredServices) {
                    values.add(getConfiguredServiceInstance(configuredService));
                }
                return values;
            } else {
                ConfiguredService configuredService=configuredServices.get(0);
                return getConfiguredServiceInstance(configuredService);
            }
        }
    }

    /**
     * Returns an endpoint reference for the given configured service.
     * @param fromConfiguredReference
     * @param fromMessageHandler
     * @param configuredService
     * @return
     */
    private Object getConfiguredServiceEndpointReference(ConfiguredReference fromConfiguredReference, MessageHandler fromMessageHandler, ConfiguredService configuredService) {
        
        // Create a new service reference for this service
        EndpointReference toEndpointReference = addressingFactory.createEndpointReference();
        ServiceURI serviceAddress = assemblyFactory.createServiceURI(null, configuredService);
        toEndpointReference.setAddress(serviceAddress.getAddress());
        toEndpointReference.setConfiguredPort(configuredService);
        
        // Get the target message handler
        MessageHandler toMessageHandler=(MessageHandler)getConfiguredServiceInstance(configuredService);
        toEndpointReference.setMessageHandler(toMessageHandler);

        // Create a service reference for the source reference
        // and store it as a parameter of the target endpoint reference
        EndpointReference fromEndpointReference = addressingFactory.createEndpointReference();
        ServiceURI fromAddress = assemblyFactory.createServiceURI(null, fromConfiguredReference);
        fromEndpointReference.setAddress(fromAddress.getAddress());
        fromEndpointReference.setConfiguredPort(fromConfiguredReference);
        fromEndpointReference.setMessageHandler(fromMessageHandler);
        toEndpointReference.getReferenceParameters().put(AddressingConstants.FROM_HEADER_NAME, fromEndpointReference);
        
        return toEndpointReference;
    }

    /**
     * Returns the implementation instance for the given service.
     * @param configuredService
     * @return
     */
    private Object getConfiguredServiceInstance(ConfiguredService configuredService) {
        Component toComponent = (Component) configuredService.getPart();
        SystemImplementation toImplementation = (SystemImplementation) toComponent.getComponentImplementation();
        SimpleComponentRuntimeConfiguration runtimeConfiguration = (SimpleComponentRuntimeConfiguration) toImplementation.getRuntimeConfiguration();
        if (runtimeConfiguration == null) {
            buildExtensionComponent(toComponent, toImplementation);
            runtimeConfiguration = (SimpleComponentRuntimeConfiguration) toImplementation.getRuntimeConfiguration();
        }
        try {
            return runtimeConfiguration.createComponentContext().getInstance(null);
        } catch (TargetException e) {
            throw new ServiceRuntimeException(e);
        }
    }
    
}
