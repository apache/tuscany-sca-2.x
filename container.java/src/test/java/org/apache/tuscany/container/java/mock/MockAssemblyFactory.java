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
package org.apache.tuscany.container.java.mock;

import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.assembly.pojo.PojoJavaImplementation;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.pojo.PojoSystemImplementation;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.apache.tuscany.model.assembly.pojo.PojoAggregateComponent;
import org.apache.tuscany.model.assembly.pojo.PojoComponent;
import org.apache.tuscany.model.assembly.pojo.PojoJavaInterface;
import org.apache.tuscany.model.assembly.pojo.PojoService;
import org.apache.tuscany.model.assembly.pojo.PojoSimpleComponent;
import org.apache.tuscany.model.types.java.JavaInterface;

/**
 * Generates test components and module assemblies
 * 
 * @version $Rev$ $Date$
 */
public class MockAssemblyFactory {

    public static SimpleComponent createComponent(String name, Class type, ScopeEnum scope) throws NoSuchMethodException {
        SimpleComponent sc = new PojoSimpleComponent();
        JavaImplementation impl = new PojoJavaImplementation();
        impl.setClass(type.getName());
        sc.setComponentImplementation(impl);
        Service s = new PojoService();
        JavaInterface ji = new PojoJavaInterface();
        s.setInterfaceContract(ji);
        ji.setScope(scope);
        impl.getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
    }

    public static Component createSystemComponent(String name, String type, ScopeEnum scope) throws NoSuchMethodException,
            ClassNotFoundException {

        Class claz = JavaIntrospectionHelper.loadClass(type);
        PojoComponent sc = null;
        if (AggregateContext.class.isAssignableFrom(claz)) {
            sc = new PojoAggregateComponent();
        } else {
            sc = new PojoSimpleComponent();
        }
        SystemImplementation impl = new PojoSystemImplementation();
        impl.setClass(type);
        sc.setComponentImplementation(impl);
        Service s = new PojoService();
        JavaInterface ji = new PojoJavaInterface();
        s.setInterfaceContract(ji);
        ji.setScope(scope);
        impl.getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
    }

}
