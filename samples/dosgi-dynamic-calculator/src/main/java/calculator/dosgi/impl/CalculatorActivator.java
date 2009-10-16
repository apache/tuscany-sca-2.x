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

package calculator.dosgi.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

import calculator.dosgi.CalculatorService;
import calculator.dosgi.operations.AddService;

/**
 * 
 */
public class CalculatorActivator implements BundleActivator {
    private Logger logger = Logger.getLogger(CalculatorActivator.class.getName());

    private Bundle getBundle(BundleContext bundleContext, Class<?> cls) {
        PackageAdmin packageAdmin = null;
        // PackageAdmin is used to resolve bundles 
        ServiceReference ref = bundleContext.getServiceReference("org.osgi.service.packageadmin.PackageAdmin");
        if (ref != null) {
            packageAdmin = (PackageAdmin)bundleContext.getService(ref);
            Bundle bundle = packageAdmin.getBundle(cls);
            if (bundle != null) {
                logger.info(cls.getName() + " is loaded by bundle: " + bundle.getSymbolicName());
            }
            bundleContext.ungetService(ref);
            return bundle;
        }
        return null;
    }

    public void start(BundleContext context) throws Exception {
        logger.info("Starting " + context.getBundle());
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put("sca.service", "CalculatorComponent#service-name(Calculator)");
        props.put("calculator", "Calculator");
        props.put("service.exported.configs", new String[] {"org.osgi.sca"});
        props.put("sca.bindings", new String[] {"OSGI-INF/sca/calculator-service.bindings"});
        props.put("service.exported.interfaces", new String[] {"*"});
        logger.info("Registering " + CalculatorService.class.getName());
        CalculatorService calculator = new CalculatorServiceImpl(context);
        context.registerService(CalculatorService.class.getName(), calculator, props);

        getBundle(context, AddService.class);

    }

    public void stop(BundleContext context) throws Exception {
        logger.info("Stopping " + context.getBundle());
        // Registered services will be automatically unregistered
    }

}
