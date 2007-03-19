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

package org.apache.tuscany.core.model.physical.instancefactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;

/**
 * Reflection based instance factory.
 * 
 * @version $Revision$ $Date$
 */
public class ReflectiveIFProviderDefinition extends InstanceFactoryProviderDefinition {
    
    // Implementation class
    private String implementationClass;
    
    // Constructor arguments
    private List<String> constructorArguments = new LinkedList<String>();
    
    // Init method
    private String initMethod;
    
    // Destroy method
    private String destroyMethod;
    
    // Constructor injection sites
    private List<InjectionSource> cdiSources = new LinkedList<InjectionSource>();
    
    // Injection sites
    private List<InjectionSiteMapping> injectionSites = new LinkedList<InjectionSiteMapping>();
    
    // Property sites
    private Map<InjectionSource, String> properties = new HashMap<InjectionSource, String>();

    /**
     * returns the constructor argument.
     * @return the constructorArguments Fully qualified names of the constructor 
     * atgument types.
     */
    public List<String> getConstructorArguments() {
        return Collections.unmodifiableList(constructorArguments);
    }

    /**
     * Adds a constructor argument type.
     * @param constructorArgument the constructorArguments to set
     */
    public void addConstructorArgument(String constructorArgument) {
        constructorArguments.add(constructorArgument);
    }

    /**
     * Returns constructor injection names.
     * @return the constructorNames Constructor injection names.
     */
    public List<InjectionSource> getCdiSources() {
        return Collections.unmodifiableList(cdiSources);
    }

    /**
     * Adds a constructor injection name.
     * @param cdiSource Constructor injection name.
     */
    public void addCdiSource(InjectionSource cdiSource) {
        cdiSources.add(cdiSource);
    }

    /**
     * Gets the destroy method.
     * @return Destroy method name.
     */
    public String getDestroyMethod() {
        return destroyMethod;
    }

    /**
     * Sets the destroy method.
     * @param destroyMethod Destroy method name.
     */
    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    /**
     * Gets the implementation class.
     * @return Implementation class.
     */
    public String getImplementationClass() {
        return implementationClass;
    }

    /**
     * Sets the implementation class.
     * @param implementationClass Implementation class.
     */
    public void setImplementationClass(String implementationClass) {
        this.implementationClass = implementationClass;
    }

    /**
     * Gets the init method.
     * @return Init method name.
     */
    public String getInitMethod() {
        return initMethod;
    }

    /**
     * Sets the init method.
     * @param initMethod Init method name.
     */
    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    /**
     * Gets the injection sites.
     * @return Injection sites.
     */
    public List<InjectionSiteMapping> getInjectionSites() {
        return Collections.unmodifiableList(injectionSites);
    }

    /**
     * Adds an injection site.
     * @param injectionSite site.
     */
    public void addInjectionSite(InjectionSiteMapping injectionSite) {
        injectionSites.add(injectionSite);
    }

    /**
     * Returns a read-only view of properties.
     * @return Read-only view of properties.
     */
    public Map<InjectionSource, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Adds a property to the definition.
     * @param injectionSource Injection source for the property.
     * @param property String value of the property.
     */
    public void addProperty(InjectionSource injectionSource, String property) {
        properties.put(injectionSource, property);
    }

}
