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

import java.util.logging.Logger;

import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Bundle Activator that creates the Equinox-based service discoverer 
 *
 * @version $Rev: $ $Date: $
 */
public class EquinoxServiceDiscoveryActivator implements BundleActivator {
    private static Logger logger = Logger.getLogger(EquinoxServiceDiscoveryActivator.class.getName());

    public void start(BundleContext context) throws Exception {
        logger.info("Installing service discovery");
        EquinoxServiceDiscoverer discoverer = new EquinoxServiceDiscoverer(context);
        ServiceDiscovery.getInstance().setServiceDiscoverer(discoverer);
        logger.info("Installed service discovery");
    }

    public void stop(BundleContext context) throws Exception {
    }

}
