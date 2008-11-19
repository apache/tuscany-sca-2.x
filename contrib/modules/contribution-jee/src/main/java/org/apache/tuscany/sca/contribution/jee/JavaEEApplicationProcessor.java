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

import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.service.ContributionException;

/**
 * @version $Rev$ $Date$
 */
public class JavaEEApplicationProcessor {
    private AppModule appModule;
    private ComponentType componentType;
    private AssemblyHelper helper;

    public JavaEEApplicationProcessor(AppModule appModule, AssemblyHelper helper) {
        super();
        this.appModule = appModule;
        this.helper = helper;
    }

    public JavaEEApplicationProcessor(AppModule module) {
        appModule = module;
        helper = new AssemblyHelper();
    }

    public ComponentType getJavaEEAppComponentType() throws ContributionException {
        if (componentType != null) {
            return componentType;
        }
        componentType = helper.createComponentType();
        
        // Process all EJB modules
        for(EjbModule ejbModule : appModule.getEjbModules()) {
            EJBModuleProcessor emp = new EJBModuleProcessor(ejbModule, helper);
            Map<String, ComponentType> ejbComponentTypes = emp.getEjbComponentTypes();
            for(Map.Entry<String, ComponentType> entry : ejbComponentTypes.entrySet()) {
                String beanName = entry.getKey();
                ComponentType ct = entry.getValue();
                EnterpriseBean bean = ejbModule.getEjbJar().getEnterpriseBeansByEjbName().get(beanName);
                String mappedName = bean.getMappedName() != null ? bean.getMappedName() : beanName;

                String mappedName2 = mappedName.replace("/", "_");
                // Add all services from the bean
                for(Service service : ct.getServices()) {
                    Service service2 = helper.createComponentService();
                    String serviceName = mappedName2 + "_"+service.getName();
                    service2.setName(serviceName);
                    service2.setInterfaceContract(service.getInterfaceContract());
                    componentType.getServices().add(service2);
                }
                
                String beanName2 = beanName.replace("/", "_");
                // Add all references
                for(Reference reference : ct.getReferences()) {
                    Reference reference2 = helper.createComponentReference();
                    String referenceName = beanName2+"_"+reference.getName();
                    reference2.setName(referenceName);
                    reference2.setInterfaceContract(reference.getInterfaceContract());
                    reference2.getRequiredIntents().addAll(reference.getRequiredIntents());
                    componentType.getReferences().add(reference2);
                }
            }
            emp.getEjbAppComponentType();
        }

        // Process web modules (?)
        // FIXME: SCA JEE Spec 1.0 - Sec 7.1.3 says nothing about web modules

        return componentType;
    }
}
