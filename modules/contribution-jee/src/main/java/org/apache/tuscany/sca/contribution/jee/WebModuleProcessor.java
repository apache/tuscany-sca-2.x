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

package org.apache.tuscany.sca.contribution.jee;

import java.util.Map;

import org.apache.openejb.config.WebModule;
import org.apache.openejb.jee.EjbRef;
import org.apache.openejb.jee.EjbReference;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.WebApp;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.implementation.web.WebImplementation;
import org.apache.tuscany.sca.implementation.web.WebImplementationFactory;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;

public class WebModuleProcessor {
    private WebModule webModule;
    private ComponentType componentType;

    public WebModuleProcessor(WebModule module) {
        webModule = module;
    }

    public ComponentType getWebAppComponentType() throws ContributionException {
        if (componentType != null) {
            return componentType;
        }
        componentType = AssemblyHelper.createComponentType();

        WebApp webApp = webModule.getWebApp();
        ClassLoader classLoader = webModule.getClassLoader();

        // Process Remote EJB References
        for (Map.Entry<String, EjbRef> entry : webApp.getEjbRefMap().entrySet()) {
            EjbRef ejbRef = entry.getValue();
            if (ejbRef.getRefType().compareTo(EjbReference.Type.REMOTE) != 0) {
                // Only Remote EJB references need to be considered.
                // Skip the current one as it is a remote reference.
                continue;
            }
            String referenceName = entry.getKey();
            referenceName = referenceName.replace("/", "_");
            Reference reference = AssemblyHelper.createComponentReference();
            reference.setName(referenceName);
            InterfaceContract ic = null;
            try {
                Class<?> clazz = classLoader.loadClass(ejbRef.getInterface());
                ic = AssemblyHelper.createInterfaceContract(clazz);
            } catch (Exception e) {
                componentType = null;
                throw new ContributionException(e);
            }
            reference.setInterfaceContract(ic);
            componentType.getReferences().add(reference);
        }

        // Process env-entries to compute properties
        for (Map.Entry<String, EnvEntry> entry : webApp.getEnvEntryMap().entrySet()) {
            EnvEntry envEntry = entry.getValue();
            String type = envEntry.getEnvEntryType();
            if (!AssemblyHelper.ALLOWED_ENV_ENTRY_TYPES.containsKey(type)) {
                continue;
            }
            String propertyName = entry.getKey();
            propertyName = propertyName.replace("/", "_");
            String value = envEntry.getEnvEntryValue();
            Property property = AssemblyHelper.createComponentProperty();
            property.setName(propertyName);
            property.setXSDType(AssemblyHelper.ALLOWED_ENV_ENTRY_TYPES.get(type));
            property.setValue(value);
            componentType.getProperties().add(property);
        }

        return componentType;
    }

    public Composite getWebAppComposite() throws ContributionException {
        getWebAppComponentType();

        Composite composite = AssemblyHelper.createComposite();

        ModelFactoryExtensionPoint mfep = new DefaultModelFactoryExtensionPoint();
        WebImplementationFactory wif = mfep.getFactory(WebImplementationFactory.class);
        WebImplementation impl = wif.createWebImplementation();
        impl.setWebURI(webModule.getModuleId());

        // Create component
        Component component = AssemblyHelper.createComponent();
        String componentName = webModule.getModuleId();
        component.setName(componentName);
        component.setImplementation(impl);

        // Add references
        for (Reference reference : componentType.getReferences()) {
            ComponentReference componentReference = AssemblyHelper.createComponentReference();
            componentReference.setReference(reference);
            component.getReferences().add(componentReference);
        }

        // Add properties
        for (Property property : componentType.getProperties()) {
            ComponentProperty componentProperty = AssemblyHelper.createComponentProperty();
            componentProperty.setProperty(property);
            component.getProperties().add(componentProperty);
        }

        // Add component to composite
        composite.getComponents().add(component);

        // Add composite references
        for (ComponentReference reference : component.getReferences()) {
            CompositeReference compositeReference = AssemblyHelper.createCompositeReference();
            compositeReference.setInterfaceContract(reference.getInterfaceContract());
            compositeReference.getPromotedReferences().add(reference);
            composite.getReferences().add(compositeReference);
        }

        return composite;
    }
}
