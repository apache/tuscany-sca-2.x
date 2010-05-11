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

package org.apache.tuscany.sca.base;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * A bundle activator that delegates to others
 */
public class AggregatedBundleActivator implements BundleActivator {
    public static final String BUNDLE_ACTIVATOR_LIST = "Tuscany-Bundle-Activator-List";
    private List<BundleActivator> activators = new ArrayList<BundleActivator>();

    public void start(BundleContext context) throws Exception {
        String list = (String)context.getBundle().getHeaders().get(BUNDLE_ACTIVATOR_LIST);
        if (list == null) {
            return;
        }
        for (String cls : list.split(",")) {
            Object i = context.getBundle().loadClass(cls).newInstance();
            if (i instanceof BundleActivator) {
                ((BundleActivator)i).start(context);
                activators.add((BundleActivator)i);
            }
        }
    }

    public void stop(BundleContext context) throws Exception {
        for (BundleActivator a : activators) {
            a.stop(context);
        }

    }

}
