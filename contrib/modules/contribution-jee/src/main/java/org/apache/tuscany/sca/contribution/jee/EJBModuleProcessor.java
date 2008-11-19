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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EjbRef;
import org.apache.openejb.jee.EjbRefType;
import org.apache.openejb.jee.EjbReference;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.MessageDrivenBean;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.SessionType;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.implementation.ejb.EJBImplementation;
import org.apache.tuscany.sca.implementation.ejb.EJBImplementationFactory;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;

/**
 * @version $Rev$ $Date$
 */
public class EJBModuleProcessor {
    private EjbModule ejbModule;
    private AssemblyHelper helper;
    private Map<String, List<String>> intfToBean = new HashMap<String, List<String>>();
    private List<String> statefulBeans = new ArrayList<String>();

    public EJBModuleProcessor(EjbModule ejbModule, AssemblyHelper helper) {
        super();
        this.ejbModule = ejbModule;
        this.helper = helper;
    }

    public EJBModuleProcessor(EjbModule ejbModule) {
        this.ejbModule = ejbModule;
        this.helper = new AssemblyHelper();
    }

    public Map<String, ComponentType> getEjbComponentTypes() throws ContributionException {
        intfToBean.clear();
        statefulBeans.clear();
        Map<String, ComponentType> ejbComponentTypes = new HashMap<String, ComponentType>();

        EjbJar ejbJar = ejbModule.getEjbJar();
        if (!ejbJar.getVersion().startsWith("3")) {
            // Not an EJB3 module
            // TODO: should throw an exception
        }

        Map<String, EnterpriseBean> beansMap = ejbJar.getEnterpriseBeansByEjbName();
        for (Map.Entry<String, EnterpriseBean> entry : beansMap.entrySet()) {
            EnterpriseBean bean = entry.getValue();
            ComponentType ct = null;
            if (bean instanceof SessionBean) {
                SessionBean sbean = (SessionBean)bean;
                ct = getEjbComponentType(sbean, ejbModule.getClassLoader());
            } else if (bean instanceof MessageDrivenBean) {
                MessageDrivenBean mdbean = (MessageDrivenBean)bean;
                ct = getEjbComponentType(mdbean, ejbModule.getClassLoader());
            } else {
                continue;
            }
            if (ct != null) {
                // Bean is an EJB3 bean
                ejbComponentTypes.put(bean.getEjbName(), ct);
            }
        }

        // Adjust the references to STATEFUL beans
        for (Map.Entry<String, ComponentType> entry : ejbComponentTypes.entrySet()) {
            ComponentType ct = entry.getValue();
            for (Reference reference : ct.getReferences()) {
                String intf = ((JavaInterface)reference.getInterfaceContract().getInterface()).getName();
                for (String bean : intfToBean.get(intf)) {
                    if (statefulBeans.contains(bean)) {
                        reference.getRequiredIntents().add(AssemblyHelper.CONVERSATIONAL_INTENT);
                        break;
                    }
                }
            }
        }

        return ejbComponentTypes;
    }

    public ComponentType getEjbAppComponentType() throws ContributionException {
        ComponentType componentType = helper.createComponentType();

        Map<String, ComponentType> ejbComponentTypes = getEjbComponentTypes();

        for (Map.Entry<String, ComponentType> entry : ejbComponentTypes.entrySet()) {
            String beanName = entry.getKey();
            ComponentType ejbComponentType = entry.getValue();

            for (Service service : ejbComponentType.getServices()) {
                Service service2 = helper.createComponentService();
                service2.setName(beanName + "_" + service.getName());
                service2.setInterfaceContract(service.getInterfaceContract());
                service2.getRequiredIntents().addAll(service.getRequiredIntents());

                componentType.getServices().add(service2);
            }

            for (Reference reference : ejbComponentType.getReferences()) {
                Reference reference2 = helper.createComponentReference();
                reference2.setName(beanName + "_" + reference.getName());
                reference2.setInterfaceContract(reference.getInterfaceContract());
                reference2.getRequiredIntents().addAll(reference.getRequiredIntents());

                componentType.getReferences().add(reference2);
            }
        }

        return componentType;
    }

    public Composite getEjbAppComposite() throws ContributionException {
        Composite composite = helper.createComposite();

        Map<String, ComponentType> ejbComponentTypes = getEjbComponentTypes();

        ModelFactoryExtensionPoint mfep = new DefaultModelFactoryExtensionPoint();
        EJBImplementationFactory eif = mfep.getFactory(EJBImplementationFactory.class);

        for (Map.Entry<String, ComponentType> entry : ejbComponentTypes.entrySet()) {
            String ejbName = entry.getKey();
            ComponentType componentType = entry.getValue();

            EJBImplementation impl = eif.createEJBImplementation();
            impl.setEJBLink(ejbModule.getModuleId() + "#" + ejbName);
            // Create component
            Component component = helper.createComponent();
            String componentName = ejbName;
            component.setName(componentName);
            component.setImplementation(impl);

            // Add services
            for (Service service : componentType.getServices()) {
                ComponentService componentService = helper.createComponentService();
                componentService.setService(service);
                componentService.setInterfaceContract(service.getInterfaceContract());
                component.getServices().add(componentService);
            }

            // Add references
            for (Reference reference : componentType.getReferences()) {
                ComponentReference componentReference = helper.createComponentReference();
                componentReference.setReference(reference);
                componentReference.setInterfaceContract(reference.getInterfaceContract());
                componentReference.getRequiredIntents().addAll(reference.getRequiredIntents());
                component.getReferences().add(componentReference);
            }

            // Add properties
            for (Property property : componentType.getProperties()) {
                ComponentProperty componentProperty = helper.createComponentProperty();
                componentProperty.setProperty(property);
                component.getProperties().add(componentProperty);
            }

            // Add component
            composite.getComponents().add(component);

            // Add composite services
            for (ComponentService service : component.getServices()) {
                CompositeService compositeService = helper.createCompositeService();
                compositeService.setInterfaceContract(service.getInterfaceContract());
                compositeService.setPromotedComponent(component);
                compositeService.setPromotedService(service);
                composite.getServices().add(compositeService);
            }

            // Add composite references
            for (ComponentReference reference : component.getReferences()) {
                CompositeReference compositeReference = helper.createCompositeReference();
                compositeReference.setInterfaceContract(reference.getInterfaceContract());
                compositeReference.getRequiredIntents().addAll(reference.getRequiredIntents());
                compositeReference.getPromotedReferences().add(reference);
                composite.getReferences().add(compositeReference);
            }
        }
        return composite;
    }

    private ComponentType getEjbComponentType(SessionBean bean, ClassLoader cl) throws ContributionException {
        if(bean.getBusinessRemote().size() == 0 && bean.getBusinessLocal().size() == 0) {
            // Not an EJB3 Session bean
            return null;
        }
        ComponentType componentType = helper.createComponentType();

        boolean conversational = bean.getSessionType().equals(SessionType.STATEFUL);
        if (conversational) {
            statefulBeans.add(bean.getEjbName());
        }

        // Process Remote Business interfaces of the SessionBean
        for (String intfName : bean.getBusinessRemote()) {
            // This code is added to take care of EJB references to STATEFUL beans that are injected
            // without a beanname in @EJB annotation
            List<String> beansList = intfToBean.get(intfName);
            if (beansList == null) {
                beansList = new ArrayList<String>();
                intfToBean.put(intfName, beansList);
            }
            beansList.add(bean.getEjbName());

            String serviceName =
                intfName.lastIndexOf(".") != -1 ? intfName.substring(intfName.lastIndexOf(".") + 1) : intfName;
            Service service = helper.createComponentService();
            service.setName(serviceName);
            InterfaceContract ic = null;
            try {
                Class<?> clazz = cl.loadClass(intfName);
                ic = helper.createInterfaceContract(clazz);
                ic.getInterface().setConversational(conversational);
                ic.getInterface().setRemotable(true);
            } catch (Exception e) {
                throw new ContributionException(e);
            }
            service.setInterfaceContract(ic);
            if (conversational) {
                service.getRequiredIntents().add(AssemblyHelper.CONVERSATIONAL_INTENT);
            }
            componentType.getServices().add(service);
        }

        // Process Local Business interfaces of the SessionBean
        for (String intfName : bean.getBusinessLocal()) {
            String serviceName =
                intfName.lastIndexOf(".") != -1 ? intfName.substring(intfName.lastIndexOf(".") + 1) : intfName;
            Service service = helper.createComponentService();
            service.setName(serviceName);
            InterfaceContract ic = null;
            try {
                Class<?> clazz = cl.loadClass(intfName);
                ic = helper.createInterfaceContract(clazz);
                ic.getInterface().setConversational(conversational);
            } catch (Exception e) {
                throw new ContributionException(e);
            }
            service.setInterfaceContract(ic);
            if (conversational) {
                service.getRequiredIntents().add(AssemblyHelper.CONVERSATIONAL_INTENT);
            }
            componentType.getServices().add(service);
        }

        // Process Remote EJB References
        for (Map.Entry<String, EjbRef> entry : bean.getEjbRefMap().entrySet()) {
            EjbRef ejbRef = entry.getValue();
            if(ejbRef.getHome() != null) {
                // References to only EJB3 beans need to be considered.
                // Skip the current on as it is not a reference to an EJB3 bean.
                continue;
            }
            if (ejbRef.getRefType().compareTo(EjbReference.Type.REMOTE) != 0) {
                // Only Remote EJB references need to be considered.
                // Skip the current one as it is not a remote reference.
                continue;
            }
            //FIXME: ejbRef.getEjbRefType() is null sometimes.  Need a different way to figure the type.
            if(ejbRef.getEjbRefType() != null && ejbRef.getEjbRefType().compareTo(EjbRefType.SESSION) != 0) {
                // Only references to Session beans need to be considered.
                // Skip the current one as it is not a Session bean.
                continue;
            }
            String referenceName = entry.getKey();
            referenceName = referenceName.replace("/", "_");
            Reference reference = helper.createComponentReference();
            reference.setName(referenceName);
            InterfaceContract ic = null;
            try {
                Class<?> clazz = cl.loadClass(ejbRef.getInterface());
                ic = helper.createInterfaceContract(clazz);
            } catch (Exception e) {
                throw new ContributionException(e);
            }
            reference.setInterfaceContract(ic);
            reference.getRequiredIntents().add(AssemblyHelper.EJB_INTENT);
            componentType.getReferences().add(reference);
        }

        // Process env-entries to compute properties
        for (Map.Entry<String, EnvEntry> entry : bean.getEnvEntryMap().entrySet()) {
            EnvEntry envEntry = entry.getValue();
            String type = envEntry.getEnvEntryType();
            if (!AssemblyHelper.ALLOWED_ENV_ENTRY_TYPES.containsKey(type)) {
                continue;
            }
            String propertyName = entry.getKey();
            propertyName = propertyName.replace("/", "_");
            String value = envEntry.getEnvEntryValue();
            Property property = helper.createComponentProperty();
            property.setName(propertyName);
            property.setXSDType(AssemblyHelper.ALLOWED_ENV_ENTRY_TYPES.get(type));
            property.setValue(value);
            componentType.getProperties().add(property);
        }

        return componentType;
    }

    private ComponentType getEjbComponentType(MessageDrivenBean bean, ClassLoader cl) throws ContributionException {
        try {
            if(javax.ejb.MessageDrivenBean.class.isAssignableFrom(cl.loadClass(bean.getEjbClass()))) {
                // Not an EJB3 bean
                return null;
            }
        } catch (ClassNotFoundException ignored) {
            // Should not happen
        }
        ComponentType componentType = helper.createComponentType();

        // Process Remote EJB References
        for (Map.Entry<String, EjbRef> entry : bean.getEjbRefMap().entrySet()) {
            EjbRef ejbRef = entry.getValue();
            if(ejbRef.getHome() != null) {
                // References to only EJB3 beans need to be considered.
                // Skip the current on as it is not a reference to an EJB3 bean.
                continue;
            }
            if (ejbRef.getRefType().compareTo(EjbReference.Type.REMOTE) != 0) {
                // Only Remote EJB references need to be considered.
                // Skip the current one as it is not a remote reference.
                continue;
            }
            //FIXME: ejbRef.getEjbRefType() is null sometimes.  Need a different way to figure the type.
            if(ejbRef.getEjbRefType() != null && ejbRef.getEjbRefType().compareTo(EjbRefType.SESSION) != 0) {
                // Only references to Session beans need to be considered.
                // Skip the current one as it is not a Session bean.
                continue;
            }
            String referenceName = entry.getKey();
            referenceName = referenceName.replace("/", "_");
            Reference reference = helper.createComponentReference();
            reference.setName(referenceName);
            InterfaceContract ic = null;
            try {
                Class<?> clazz = cl.loadClass(ejbRef.getInterface());
                ic = helper.createInterfaceContract(clazz);
            } catch (Exception e) {
                throw new ContributionException(e);
            }
            reference.setInterfaceContract(ic);
            reference.getRequiredIntents().add(AssemblyHelper.EJB_INTENT);
            componentType.getReferences().add(reference);
        }

        // Process env-entries to compute properties
        for (Map.Entry<String, EnvEntry> entry : bean.getEnvEntryMap().entrySet()) {
            EnvEntry envEntry = entry.getValue();
            String type = envEntry.getEnvEntryType();
            if (!AssemblyHelper.ALLOWED_ENV_ENTRY_TYPES.containsKey(type)) {
                continue;
            }
            String propertyName = entry.getKey();
            propertyName = propertyName.replace("/", "_");
            String value = envEntry.getEnvEntryValue();
            Property property = helper.createComponentProperty();
            property.setName(propertyName);
            property.setXSDType(AssemblyHelper.ALLOWED_ENV_ENTRY_TYPES.get(type));
            property.setValue(value);
            componentType.getProperties().add(property);
        }

        return componentType;
    }
}
