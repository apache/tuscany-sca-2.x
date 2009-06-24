/**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements. See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership. The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing,
  * software distributed under the License is distributed on an
  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied. See the License for the
  * specific language governing permissions and limitations
  * under the License.
  */
package org.apache.tuscany.sca.dosgi.discovery;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.discovery.Discovery;

public class DiscoveryActivator implements BundleActivator {
    private List<AbstractDiscoveryService> discoveryServices = new ArrayList<AbstractDiscoveryService>();
    private List<ServiceRegistration> discoveryServiceRegistrations = new ArrayList<ServiceRegistration>();

    public void start(BundleContext context) {
        discoveryServices.add(new LocalDiscoveryService(context));

        discoveryServices.add(new DomainDiscoveryService(context));

        for (AbstractDiscoveryService service : discoveryServices) {
            ServiceRegistration registration =
                context.registerService(Discovery.class.getName(), service, new Hashtable<String, Object>());
            discoveryServiceRegistrations.add(registration);
        }
    }

    public void stop(BundleContext context) {
        for (ServiceRegistration registration : discoveryServiceRegistrations) {
            registration.unregister();
        }
        for (AbstractDiscoveryService service : discoveryServices) {
            service.stop();
        }
    }
}
