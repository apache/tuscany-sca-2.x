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

package calculator.dosgi.operations.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

import calculator.dosgi.operations.AddService;
import calculator.dosgi.operations.DivideService;
import calculator.dosgi.operations.MultiplyService;
import calculator.dosgi.operations.SubtractService;

/**
 *
 */
public class OperationsActivator implements BundleActivator {
    private Logger logger = Logger.getLogger(OperationsActivator.class.getName());

    public void start(BundleContext context) throws Exception {
        logger.info("Starting " + context.getBundle());

        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put("service.exported.configs", new String[] {"org.osgi.sca"});
        props.put("service.exported.interfaces", new String[] {"*"});
        
        logger.info("Registering " + AddService.class.getName());
        props.put("sca.service", "AddComponent#service-name(Add)");
        props.put("org.osgi.sca.bindings", new String[] {"{http://sample}Add"});
        context.registerService(AddService.class.getName(), new AddServiceImpl(), props);

        logger.info("Registering " + SubtractService.class.getName());
        props.put("sca.service", "SubtractComponent#service-name(Subtract)");
        props.put("org.osgi.sca.bindings", new String[] {"{http://sample}Subtract"});
        context.registerService(SubtractService.class.getName(), new SubtractServiceImpl(), props);

        logger.info("Registering " + MultiplyService.class.getName());
        props.put("sca.service", "MultiplyComponent#service-name(Multiply)");
        props.put("org.osgi.sca.bindings", new String[] {"{http://sample}Multiply"});
        context.registerService(MultiplyService.class.getName(), new MultiplyServiceImpl(), props);

        logger.info("Registering " + DivideService.class.getName());
        props.put("sca.service", "DivideComponent#service-name(Divide)");
        props.put("org.osgi.sca.bindings", new String[] {"{http://sample}Divide"});
        context.registerService(DivideService.class.getName(), new DivideServiceImpl(), props);

        getBundle(context, AddService.class);
    }

    public void stop(BundleContext context) throws Exception {
        logger.info("Stopping " + context.getBundle());
        // Registered services will be automatically unregistered
    }

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

}
