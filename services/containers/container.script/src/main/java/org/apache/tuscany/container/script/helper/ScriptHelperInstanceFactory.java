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
package org.apache.tuscany.container.script.helper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScriptInstanceFactory creates ScriptInstances for a script
 */
public abstract class ScriptHelperInstanceFactory<T extends ScriptHelperInstance> {

    protected String resourceName;

    protected ClassLoader classLoader;

    public ScriptHelperInstanceFactory(String resourceName, ClassLoader classLoader) {
        this.resourceName = resourceName;
        this.classLoader = classLoader;
    }

    /**
     * Create a new invokeable instance of the script
     * @param services 
     * 
     * @param context
     *            objects to add to scope of the script instance
     * @return a RhinoScriptInstance
     * TODO: services should be on the constructor not on this method
     */
    public abstract T createInstance(List<Class> services, Map<String, Object> context);

    public String getResourceName() {
        return resourceName;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    protected Map<String, Class> getResponseClasses(List<Class> services) {
        Map<String, Class> responseClasses = new HashMap<String, Class>();
        if (services != null) {
            for (Class s : services) {
                for (Method m : s.getMethods()) {
                    responseClasses.put(m.getName(), m.getReturnType());
                }
            }
        }
        return responseClasses;
    }

}
