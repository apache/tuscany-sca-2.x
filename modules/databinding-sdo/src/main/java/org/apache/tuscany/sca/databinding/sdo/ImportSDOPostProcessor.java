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

package org.apache.tuscany.sca.databinding.sdo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.processor.ContributionPostProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sdo.api.SDOUtil;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

/**
 * PostProcessor resposible for identifying SDO Factories and register them with
 * SDO Helper Context
 * 
 * @version $Rev$ $Date$
 */
public class ImportSDOPostProcessor implements ContributionPostProcessor {
    private static final String URI_SEPARATOR = "/";
    private static final String JAVA_SEPARATOR = ".";

    private HelperContextRegistry helperContextRegistry;

    public ImportSDOPostProcessor(HelperContextRegistry helperContextRegistry) {
        super();
        this.helperContextRegistry = helperContextRegistry;
    }

    public void visit(Contribution contribution) {
        for (DeployedArtifact artifact : contribution.getArtifacts()) {
            String artifactURI = artifact.getURI();
            if (artifactURI.endsWith("Factory.class")) {
                //load the factory and prepare to register the class with SDO
                String factoryName = getFactoryClassName(artifactURI);
                ClassReference clazz = new ClassReference(factoryName);
                clazz = contribution.getModelResolver().resolveModel(ClassReference.class, clazz);
                if (clazz.getJavaClass() != null) {
                    try {
                        //check if it's a SDO factory by introspecting INSTANCE field
                        if (isSDOFactory(clazz.getJavaClass())) {
                            register(clazz.getJavaClass(), this.getHelperContext(contribution));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Transform class artifact URI into a java class name for proper loading by
     * the class loader
     * 
     * @param factoryURI
     * @return
     */
    private String getFactoryClassName(String factoryURI) {
        factoryURI = factoryURI.replace(URI_SEPARATOR, JAVA_SEPARATOR);
        int pos = factoryURI.lastIndexOf(JAVA_SEPARATOR);
        return factoryURI.substring(0, pos);
    }

    /**
     * Check if a specific class is a SDO Factory by checking INSTANCE field
     * 
     * @param factoryClass
     * @return
     */
    private boolean isSDOFactory(Class factoryClass) {
        try {
            // The factory interface has a constant "INSTANCE" field
            Field field = factoryClass.getField("INSTANCE");
            // A public method: register(HelperContext scope)
            Method method = factoryClass.getMethod("register", HelperContext.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * Get a SDO HelperContext reference
     * 
     * @return
     */
    private HelperContext getHelperContext(Contribution contribution) {
        HelperContext helperContext = null;

        // FIXME: [rfeng] Should we scope the HelperContext by contribution URI?
        String id = contribution.getURI();
        synchronized (helperContextRegistry) {
            helperContext = helperContextRegistry.getHelperContext(id);
            if (helperContext == null) {
                helperContext = SDOUtil.createHelperContext();
                helperContextRegistry.register(id, helperContext);
            }
        }

        return helperContext;
    }

    /**
     * Register an SDO Factory with the helper context
     * 
     * @param factoryClass
     * @param helperContext
     * @throws Exception
     */
    private static void register(Class factoryClass, HelperContext helperContext) throws Exception {
        Field field = factoryClass.getField("INSTANCE");
        Object factory = field.get(null);
        Method method = factory.getClass().getMethod("register", new Class[] {HelperContext.class});
        method.invoke(factory, new Object[] {helperContext});

        // FIXME: How do we associate the application HelperContext with the one imported by the composite
        HelperContext defaultContext = HelperProvider.getDefaultContext();
        method.invoke(factory, new Object[] {defaultContext});
    }
}
