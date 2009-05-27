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

package org.apache.tuscany.sca.node.osgi.impl;

import java.io.StringReader;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.equinox.OSGiExtensionPointRegistry;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfigurationFactory;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The OSGi based NodeFactory
 *
 * @version $Rev$ $Date$
 */
public class OSGiNodeFactoryImpl extends NodeFactoryImpl {
    private ServiceRegistration registration;
    private BundleContext bundleContext;

    /**
     * Constructs a new Node controller
     */
    public OSGiNodeFactoryImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        autoDestroy = false;
        setNodeFactory(this);
    }

    protected NodeConfiguration getConfiguration(Bundle bundle, String compositeContent) {
        init();

        // Create a node configuration
        NodeConfigurationFactory configurationFactory = modelFactories.getFactory(NodeConfigurationFactory.class);
        NodeConfiguration configuration = configurationFactory.createNodeConfiguration();

        URL location = bundle.getEntry("/");
        String uri = bundle.getSymbolicName();
        configuration.setURI(uri).addContribution(uri, location);

        if (compositeContent != null) {
            configuration.addDeploymentComposite(uri, new StringReader(compositeContent));
        } else {
            String compositeURI = (String)bundle.getHeaders().get("SCA-Composite");
            if (compositeURI == null) {
                compositeURI = "OSGI-INF/sca/bundle.composite";
            }
            if (compositeURI != null) {
                configuration.addDeploymentComposite(uri, compositeURI);
            }
        }
        // Set the bundle
        configuration.getExtensions().add(bundle);
        return configuration;
    }

    public synchronized void init() {
        if (!inited) {
            super.init();

            // Register the ExtensionPointRegistry as an OSGi service
            Dictionary<Object, Object> props = new Hashtable<Object, Object>();
            registration =
                bundleContext.registerService(ExtensionPointRegistry.class.getName(), extensionPoints, props);
        }
    }

    public synchronized void destroy() {
        if (inited) {
            if (registration != null) {
                registration.unregister();
                registration = null;
            }
            super.destroy();
        }

    }

    @Override
    protected Object getNodeKey(NodeConfiguration configuration) {
        // Use the bundle as the key
        return configuration.getExtensions().get(0);
    }

    @Override
    protected ExtensionPointRegistry createExtensionPointRegistry() {
        return new OSGiExtensionPointRegistry(bundleContext);
    }

}
