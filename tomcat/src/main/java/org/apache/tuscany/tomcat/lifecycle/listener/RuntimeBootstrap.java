/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.tomcat.lifecycle.listener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.ConfigurationLoader;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.system.builder.SystemComponentContextBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;

/**
 * Bootstraps an SCA runtime hosted by a Tomcat instance. The runtime will be bound to JNDI and accessed by
 * {@link org.apache.tuscany.tomcat.lifecycle.listener.WebAppLifecycleListener}s that deploy web archives as module
 * components.
 * <p>
 * Tomcat is configured by adding the following to <code>server.xml</code> under the
 * <code>GlobalNamingResources</code> element:
 * 
 * <pre>
 *   &lt;Resource name=&quot;TuscanyRuntime&quot; auth=&quot;Container&quot; type=&quot;org.apache.tuscany.core.runtime.RuntimeContext&quot;
 *          description=&quot;Tuscany Runtime&quot;
 *          factory=&quot; org.apache.tuscany.tomcat.lifecycle.listener.RuntimeBootstrap&quot;
 *          monitor=&quot;[monitor factory]&quot; loader=&quot;[configuration loader]&quot;/&gt;
 * </pre>
 * 
 * @version $Rev$ $Date$
 */
public class RuntimeBootstrap implements ObjectFactory {

    public static final String RUNTIME_NAME = "TuscanyRuntime";

    public RuntimeBootstrap() {
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
            throws BootstrapConfigurationException {
        if (!RUNTIME_NAME.equals(name)) {
            throw new BootstrapConfigurationException("Tuscany Runtime must be configured with JNDI address of " + RUNTIME_NAME);
        }
        MonitorFactory monitorFactory = null;
        ConfigurationLoader configurationLoader = null;

        Reference ref = (Reference) obj;
        if (!RuntimeContext.class.getName().equals(ref.getClassName())) {
            throw new BootstrapConfigurationException("Invalid runtime configuration");
        }
        RefAddr monitorAddr = ref.get("monitor");
        RefAddr loaderAddr = ref.get("loader");
        if (loaderAddr == null) {
            throw new BootstrapConfigurationException("No configuration laoder specified in Tomcat configuration");
        }
        if (monitorAddr != null) {
            try {
                Class monitorFactoryClass = JavaIntrospectionHelper.loadClass(monitorAddr.getContent().toString());
                monitorFactory = (MonitorFactory) monitorFactoryClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new BootstrapConfigurationException("Unable to find monitor factory class specified in Tomcat configuration", e);
            } catch (InstantiationException e) {
                throw new BootstrapConfigurationException("Error initializing monitor factory in Tomcat configuration", e);
            } catch (IllegalAccessException e) {
                throw new BootstrapConfigurationException("Error initializing monitor factory in Tomcat configuration", e);
            }
        }
//        try {
//            Class configurationLoaderClass = JavaIntrospectionHelper.loadClass(loaderAddr.getContent().toString());
//            configurationLoader = (ConfigurationLoader) configurationLoaderClass.newInstance();
//        } catch (ClassNotFoundException e) {
//            throw new BootstrapConfigurationException("Unable to find configuration loader class specified in Tomcat configuration", e);
//        } catch (InstantiationException e) {
//            throw new BootstrapConfigurationException("Error initializing configuration loader in Tomcat configuration", e);
//        } catch (IllegalAccessException e) {
//            throw new BootstrapConfigurationException("Error initializing configuration loader in Tomcat configuration", e);
//        }
        // create the SCA Runtime and have it bound in the global JNDI context
        return new RuntimeContextImpl(monitorFactory, createBuilders(),null);
    }

    /**
     * Creates a collection of bootstrap builders
     */
    private List<RuntimeConfigurationBuilder> createBuilders() {
        List<RuntimeConfigurationBuilder> builders = new ArrayList();
        builders.add((new SystemComponentContextBuilder()));
        builders.add(new SystemEntryPointBuilder());
        builders.add(new SystemExternalServiceBuilder());
        return builders;
    }
}
