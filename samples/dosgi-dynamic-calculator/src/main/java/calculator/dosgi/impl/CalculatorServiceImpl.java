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

import static org.osgi.framework.Constants.OBJECTCLASS;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

import calculator.dosgi.CalculatorService;
import calculator.dosgi.operations.AddService;
import calculator.dosgi.operations.DivideService;
import calculator.dosgi.operations.MultiplyService;
import calculator.dosgi.operations.SubtractService;

/**
 * An implementation of the Calculator service.
 */
public class CalculatorServiceImpl implements CalculatorService {
    private ServiceTracker remoteServices;
    private ServiceTracker localServices;

    public CalculatorServiceImpl() {
        super();
    }

    public CalculatorServiceImpl(BundleContext context) {
        super();
        Filter remoteFilter = null, localFilter = null;
        try {
            remoteFilter =
                context.createFilter("(&(" + OBJECTCLASS + "=calculator.dosgi.operations.*) (service.imported=*))");
            localFilter =
                context.createFilter("(&(" + OBJECTCLASS + "=calculator.dosgi.operations.*) (!(service.imported=*)))");
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        this.remoteServices = new ServiceTracker(context, remoteFilter, null);
        remoteServices.open();
        this.localServices = new ServiceTracker(context, localFilter, null);
        localServices.open();
    }

    private <T> T getService(Class<T> cls) {
        try {
            // Wait for 10 seconds until the remote services are imported
            remoteServices.waitForService(10000);
        } catch (InterruptedException e) {
            throw new IllegalStateException(cls.getSimpleName() + " is not available");
        }
        Object[] remoteObjects = remoteServices.getServices();
        if (remoteObjects != null) {
            for (Object s : remoteObjects) {
                if (cls.isInstance(s)) {
                    System.out.println("Remote service: " + s);
                    return cls.cast(s);
                }
            }
        }
        Object[] localObjects = localServices.getServices();
        if (localObjects != null) {
            for (Object s : localObjects) {
                if (cls.isInstance(s)) {
                    System.out.println("Local service: " + s);
                    return cls.cast(s);
                }
            }
        }
        throw new IllegalStateException(cls.getSimpleName() + " is not available");
    }

    public double add(double n1, double n2) {
        return getService(AddService.class).add(n1, n2);
    }

    public double subtract(double n1, double n2) {
        return getService(SubtractService.class).subtract(n1, n2);
    }

    public double multiply(double n1, double n2) {
        return getService(MultiplyService.class).multiply(n1, n2);
    }

    public double divide(double n1, double n2) {
        return getService(DivideService.class).divide(n1, n2);
    }
}
