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

package org.apache.tuscany.sca.extensibility.equinox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * A test host that starts/stops Equinox.
 *
 * @version $Rev: $ $Date: $
 */
public class TestEquinoxHost {
    private Framework framework;

    private BundleContext init() throws Exception {
        if (framework != null) {
            throw new IllegalStateException("The framework is started already");
        }
        ServiceDeclaration sd = ServiceDiscovery.getInstance().getServiceDeclaration(FrameworkFactory.class.getName());
        Class<?> factoryCls = sd.loadClass();
        FrameworkFactory factory = (FrameworkFactory)factoryCls.newInstance();
        Map<Object, Object> props = new HashMap<Object, Object>();
        props.put("osgi.clean", "true");
        props.put("osgi.instance.area", new File("target/workspace").toURI().toString());
        props.put("osgi.install.area", new File("target/eclipse").toURI().toString());
        props.put("osgi.configuration.area", new File("target/eclipse").toURI().toString());
        framework = factory.newFramework(props);
        framework.start();
        return framework.getBundleContext();
    }

    public BundleContext start() {
        try {
            return init();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void stop() {
        try {
            if (framework != null) {
                framework.stop();
                framework.waitForStop(2000);
                framework = null;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
