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
package org.apache.tuscany.container.script;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

/**
 * ScriptFactory creates ScriptInstances for a script
 */
public class ScriptInstanceFactory implements ObjectFactory<ScriptInstance> {
    protected String resourceName;
    protected ClassLoader classLoader;
    private String className;
    private String scriptSource;
    private Map<String, ObjectFactory> contextObjects;

    public ScriptInstanceFactory(String resourceName,
                                 String className,
                                 String scriptSource,
                                 ClassLoader classLoader) {
        this.resourceName = resourceName;
        this.classLoader = classLoader;
        this.className = className;
        this.scriptSource = scriptSource;
        this.contextObjects = new HashMap<String, ObjectFactory>();
    }

    /**
     * Create a new invokeable instance of the script
     * <p/>
     * objects to add to scope of the script instance
     *
     * @return a RhinoScriptInstance
     */
    //public ScriptInstanceImpl createInstance(List<Class<?>> serviceBindings, Map<String, Object> context) {
    public ScriptInstance getInstance() throws ObjectCreationException {
        try {

            //TODO: this uses a new manager and recompiles the scrip each time, may be able to optimize
            // but need to be careful about instance scoping

            BSFManager bsfManager = new BSFManager();
            bsfManager.setClassLoader(BSFManager.class.getClassLoader());

            // TODO: hack to get Ruby working with the standalone launcher
            Thread.currentThread().setContextClassLoader(BSFManager.class.getClassLoader());

//            // register any context objects (SCA properties and references)
//            for (String beanName : context.keySet()) {
//                bsfManager.registerBean(beanName, context.get(beanName));
//            }

            // register any context objects (SCA properties and references)
            for (Map.Entry<String, ObjectFactory> entry : contextObjects.entrySet()) {
                bsfManager.registerBean(entry.getKey(), entry.getValue().getInstance());
            }

            String scriptLanguage = BSFManager.getLangFromFilename(resourceName);
            BSFEngine bsfEngine = bsfManager.loadScriptingEngine(scriptLanguage);
            bsfEngine.exec(resourceName, 0, 0, scriptSource);

            // if there's a className then get the class object
            Object clazz = null;
            if (className != null) {
                // special case for Ruby which requires a .new call
                if ("ruby".equals(scriptLanguage)) {
                    clazz = bsfEngine.eval(null, 1, 1, className + ".new");
                } else {
                    clazz = bsfEngine.call(null, className, null);
                }
            }

            return new ScriptInstanceImpl(bsfEngine, clazz);

        } catch (BSFException e) {
            if (e.getTargetException() != null) {
                throw new ObjectCreationException(e.getTargetException());
            }
            throw new ObjectCreationException(e.getTargetException());
        }
    }

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

    public void addContextObjectFactory(String name, ObjectFactory<?> factory) {
        contextObjects.put(name, factory);
    }

}
