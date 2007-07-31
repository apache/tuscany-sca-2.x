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

package org.apache.tuscany.sca.spi.impl;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.spi.ImplementationActivator;
import org.apache.tuscany.sca.spi.utils.DefaultPropertyValueObjectFactory;
import org.apache.tuscany.sca.spi.utils.PropertyValueObjectFactory;

/**
 * A Tuscany ModuleActivator which acitvates all the ImplementationActivators
 */
public class ImplementationsActivator implements ModuleActivator {

    protected List<ImplementationActivator> implementationActivators;
    
    public void start(ExtensionPointRegistry registry) {

        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);

        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class); 
        MediatorImpl mediator = new MediatorImpl(dataBindings, transformers);
        
        //FIXME Pass this factory differently as it's not an extension point
        PropertyValueObjectFactory propertyFactory = new DefaultPropertyValueObjectFactory(mediator);
        registry.addExtensionPoint(propertyFactory);

        this.implementationActivators = DiscoveryUtils.discoverActivators(ImplementationActivator.class, getClass().getClassLoader(), registry);

        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);

        for (final ImplementationActivator implementationActivator : implementationActivators) {

            Class<Implementation> implClass = implementationActivator.getImplementationClass();
            QName scdlQName = getSCDLQName(implClass);
            staxProcessors.addArtifactProcessor(new SCDLProcessor(assemblyFactory, scdlQName, implClass, registry, factories));

            if (implementationActivator.getImplementationClass() != null && providerFactories != null) {
                addImplementationProvider(implementationActivator, providerFactories);
            }
        }
    }

    public void stop(ExtensionPointRegistry registry) {
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);

        for (final ImplementationActivator implementationActivator : implementationActivators) {
            if (staxProcessors != null) {
                StAXArtifactProcessor processor = staxProcessors.getProcessor(getSCDLQName(implementationActivator.getImplementationClass()));
                if (processor != null) {
                    staxProcessors.removeArtifactProcessor(processor);
                }
            }
        }
    }

    private void addImplementationProvider(final ImplementationActivator implementationActivator, ProviderFactoryExtensionPoint providerFactories) {

        providerFactories.addProviderFactory(new ImplementationProviderFactory() {
            public ImplementationProvider createImplementationProvider(final RuntimeComponent rc, final Implementation impl) {
                if (impl instanceof PojoImplementation) {
                    return new ImplementationImplementationProvider(implementationActivator, rc, impl, ((PojoImplementation)impl).getUserImpl());
                } else {
                    return new ImplementationImplementationProvider(implementationActivator, rc, impl, impl);
                }
            }
            public Class getModelType() {
                Class c = implementationActivator.getImplementationClass();

                if (Implementation.class.isAssignableFrom(c)) {
                    return c;
                } else {
                    return PojoImplementation.class;
                }
            }
        });
    }

    protected QName getSCDLQName(Class implementationClass) {
        String localName = implementationClass.getName();
        if (localName.lastIndexOf('.') > -1) {
            localName = localName.substring(localName.lastIndexOf('.') + 1);
        }
        if (localName.endsWith("Implementation")) {
            localName = localName.substring(0, localName.length() - 14);
        }
        StringBuilder sb = new StringBuilder(localName);
        for (int i=0; i<sb.length(); i++) {
            if (Character.isUpperCase(sb.charAt(i))) {
                sb.setCharAt(i, Character.toLowerCase(sb.charAt(i)));
            } else {
                break;
            }
        }
        return new QName(Constants.SCA10_NS, "implementation." + sb.toString());
    }


    public Object[] getExtensionPoints() {
        return null;
    }
}
