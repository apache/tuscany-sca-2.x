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
package org.apache.tuscany.core.system.assembly.impl;

import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.SystemModule;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * The default implementation of the system assembly factory
 *
 * @version $Rev$ $Date$
 */
public class SystemAssemblyFactoryImpl extends AssemblyFactoryImpl implements SystemAssemblyFactory {

    public SystemAssemblyFactoryImpl() {
    }

    public SystemImplementation createSystemImplementation() {
        return new SystemImplementationImpl();
    }

    public SystemBinding createSystemBinding() {
        return new SystemBindingImpl();
    }

    public <T> Component createSystemComponent(String name, Class<T> service, Class<? extends T> impl, Scope scope) {
        JavaServiceContract jsc = createJavaServiceContract();
        jsc.setInterface(service);
        jsc.setScope(scope);
        Service s = createService();
        s.setServiceContract(jsc);

        ComponentInfo componentType = createComponentInfo();
        componentType.getServices().add(s);

        SystemImplementation sysImpl = createSystemImplementation();
        sysImpl.setImplementationClass(impl);
        sysImpl.setComponentInfo(componentType);

        Component sc = createSimpleComponent();
        sc.setName(name);
        sc.setImplementation(sysImpl);
        return sc;
    }

    public EntryPoint createSystemEntryPoint(String entryPointName, Class<?> serviceContract, String targetName) {
        // create the system binding
        SystemBinding systemBinding = createSystemBinding();

        // define the EP's service contract
        JavaServiceContract javaServiceContract = createJavaServiceContract();
        javaServiceContract.setInterface(serviceContract);

        return createEntryPoint(entryPointName, javaServiceContract, systemBinding, targetName);
    }

    public SystemModule createSystemModule() {
        return new SystemModuleImpl();
    }

}
