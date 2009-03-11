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

package calculator.operations;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * 
 */
public class OperationsActivator implements BundleActivator {
    private Logger logger = Logger.getLogger(OperationsActivator.class.getName());

    private List<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();

    public void start(BundleContext context) throws Exception {
        Dictionary<String, Object> props = new Hashtable<String, Object>();

        logger.info("Registering " + AddService.class.getName());
        props.put("sca.service", "AddComponent#service-name(Add)");
        registrations.add(context.registerService(AddService.class.getName(), new AddServiceImpl(), props));

        logger.info("Registering " + SubtractService.class.getName());
        props.put("sca.service", "SubtractComponent#service-name(Subtract)");
        registrations.add(context.registerService(SubtractService.class.getName(), new SubtractServiceImpl(), props));

        logger.info("Registering " + MultiplyService.class.getName());
        props.put("sca.service", "MultiplyComponent#service-name(Multiply)");
        registrations.add(context.registerService(MultiplyService.class.getName(), new MultiplyServiceImpl(), props));

        logger.info("Registering " + DivideService.class.getName());
        props.put("sca.service", "DivideComponent#service-name(Divide)");
        registrations.add(context.registerService(DivideService.class.getName(), new DivideServiceImpl(), props));
    }

    public void stop(BundleContext context) throws Exception {
        for (ServiceRegistration registration : registrations) {
            logger.info("Unregistering " + registration);
            registration.unregister();
        }
    }

}
