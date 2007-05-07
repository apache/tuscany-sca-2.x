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
package org.apache.tuscany.implementation.java.monitor;


import java.util.Map;

/**
 * Helper for creating MonitorFactory instances.
 * 
 * @version $$Rev$$ $$Date$$
 */

public final class MonitorFactoryUtil {
    /**
     * Hide the constructor
     */
    private MonitorFactoryUtil() {
    }

    /**
     * Creates a MonitorFactory instance of the specified type.
     * @param name fully qualified classname of the desired MonitorFactory type
     * @param props collection of initialization properties
     * @return a configured MonitorFactory instance, or null if the factory could not be instantiated.
     */
    @SuppressWarnings("unchecked")
    public static MonitorFactory createMonitorFactory(String name, Map<String, Object> props) {
        Class<? extends MonitorFactory> clazz;
        try {
            clazz = (Class<? extends MonitorFactory>) Class.forName(name);
        } catch (ClassNotFoundException cnfe) {
            return null;
        } catch (ClassCastException cce) {
            return null;
        }

        return createMonitorFactory(clazz, props);
    }

    /**
     * Creates a MonitorFactory instance of the specified type.
     * @param mfc class of the desired MonitorFactory type
     * @param props collection of initialization properties
     * @return a configured MonitorFactory instance, or null if the factory could not be instantiated.
     */
    public static MonitorFactory createMonitorFactory(Class<? extends MonitorFactory> mfc, Map<String, Object> props) {
        MonitorFactory mf;
        try {
            mf = mfc.newInstance();
            mf.initialize(props);
        } catch (InstantiationException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
        // allow IllegalArgumentException to propogate out

        return mf;
    }
}
