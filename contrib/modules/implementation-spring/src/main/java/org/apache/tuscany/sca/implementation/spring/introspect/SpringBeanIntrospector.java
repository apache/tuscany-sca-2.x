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
package org.apache.tuscany.sca.implementation.spring.introspect;

import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.spring.SpringConstructorArgElement;
import org.apache.tuscany.sca.implementation.spring.SpringImplementation;

/**
 * Provides introspection functions for Spring beans
 * This version leans heavily on the implementation-java classes
 *
 * @version $Rev$ $Date$
 */
public class SpringBeanIntrospector {

    private JavaImplementationFactory javaImplementationFactory;

    /**
     * The constructor sets up the various visitor elements that will be used to introspect
     * the Spring bean and extract SCA information.
     *
     * @param assemblyFactory The Assembly Factory to use
     * @param javaFactory The Java Interface Factory to use
     * @param policyFactory The Policy Factory to use.
     */
    public SpringBeanIntrospector(ExtensionPointRegistry registry, List<SpringConstructorArgElement> conArgs) {

        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        javaImplementationFactory = factories.getFactory(JavaImplementationFactory.class);
    } // end constructor 

    /**
     * Introspect a Spring Bean and extract the features important to SCA
     * @param beanClass the Spring Bean class to introspect
     * @param componentType the componentType that is filled in through the introspection
     * process (assumed empty on invocation, filled on return
     * @return a Map of property names to JavaElementImpl
     * @throws ContributionResolveException - if there was a problem resolving the
     * Spring Bean or its componentType
     *
     */
    public JavaImplementation introspectBean(Class<?> beanClass, ComponentType componentType)
        throws ContributionResolveException {
        if (componentType == null)
            throw new ContributionResolveException("Introspect Spring bean: supplied componentType is null");

        // Create a Java implementation ready for the introspection
        JavaImplementation javaImplementation = javaImplementationFactory.createJavaImplementation();
        // Set the type to be implementation.spring to avoid heuristic introspection
        javaImplementation.setType(SpringImplementation.TYPE);

        try {
            // Introspect the bean...the results of the introspection are placed into the Java implementation
            javaImplementationFactory.createJavaImplementation(javaImplementation, beanClass);

            // Extract the services, references & properties found through introspection
            // put the services, references and properties into the component type
            componentType.getServices().addAll(javaImplementation.getServices());
            componentType.getReferences().addAll(javaImplementation.getReferences());
            componentType.getProperties().addAll(javaImplementation.getProperties());

        } catch (IntrospectionException e) {
            throw new ContributionResolveException(e);
        } // end try

        return javaImplementation;

    } // end method introspectBean

} // end class SpringBeanIntrospector
