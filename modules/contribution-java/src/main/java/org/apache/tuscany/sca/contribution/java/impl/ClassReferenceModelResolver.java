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

package org.apache.tuscany.sca.contribution.java.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * A Model Resolver for ClassReferences.
 *
 * @version $Rev: 557916 $ $Date: 2007-07-20 01:04:40 -0700 (Fri, 20 Jul 2007) $
 */
public class ClassReferenceModelResolver implements ModelResolver {
    private Contribution contribution;
    private WeakReference<ClassLoader> classLoader;
    private Map<String, ClassReference> map = new HashMap<String, ClassReference>();

    private ModelResolver osgiResolver;

    public ClassReferenceModelResolver(final Contribution contribution, ModelFactoryExtensionPoint modelFactories) {
        this.contribution = contribution;
        if (this.contribution != null) {
            // Allow privileged access to get ClassLoader. Requires RuntimePermission in security policy.
            // ClassLoader cl = contribution.getClassLoader();
            ClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                    return contribution.getClassLoader();
                }
            });           

            if (cl == null) {
                // Allow privileged access to get ClassLoader. Requires RuntimePermission in security policy.
                //ClassLoader contextClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                //    public ClassLoader run() {
                //        return Thread.currentThread().getContextClassLoader();
                //    }
                //});
                ClassLoader contextClassLoader = ServiceDiscovery.getInstance().getServiceDiscoverer().getClass().getClassLoader();
                cl = new ContributionClassLoader(contribution, contextClassLoader);
                contribution.setClassLoader(cl);
            }
            this.classLoader = new WeakReference<ClassLoader>(cl);
        } else {
            // This path should be used only for unit testing.
            // Allow privileged access to get ClassLoader. Requires RuntimePermission in security policy.
            // this.classLoader = new WeakReference<ClassLoader>(this.getClass().getClassLoader());
            ClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                    return this.getClass().getClassLoader();
                }
            });           
            this.classLoader = new WeakReference<ClassLoader>( cl );
        }

        try {
            Class<?> osgiResolverClass =
                Class.forName("org.apache.tuscany.sca.contribution.osgi.impl.OSGiClassReferenceModelResolver");
            if (osgiResolverClass != null) {
                Constructor constructor =
                    osgiResolverClass.getConstructor(Contribution.class, ModelFactoryExtensionPoint.class);
                this.osgiResolver = (ModelResolver)constructor.newInstance(contribution, modelFactories);
            }
        } catch (Throwable e) {
            // Ignore error, non-OSGi classloading is used in this case
        }
    }

    public void addModel(Object resolved) {
        ClassReference clazz = (ClassReference)resolved;
        map.put(clazz.getClassName(), clazz);
    }

    public Object removeModel(Object resolved) {
        return map.remove(((ClassReference)resolved).getClassName());
    }

  

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        if (!(unresolved instanceof ClassReference)) {
            return unresolved;
        }
        Object resolved = map.get(((ClassReference)unresolved).getClassName());

        if (resolved != null) {
            return modelClass.cast(resolved);
        }

        //Load a class on demand
        Class clazz = null;
        
        if (osgiResolver != null) {
            resolved = osgiResolver.resolveModel(modelClass, unresolved);
            clazz = ((ClassReference)resolved).getJavaClass();
        }
        
        if (clazz == null) {
            try {
            	// Search contribution ClassLoader (which has visibility of classes in the contribution
            	// as well as explicitly imported packages from other contributions)
                clazz = Class.forName(((ClassReference)unresolved).getClassName(), true, classLoader.get());
            } catch (ClassNotFoundException e) {
            } catch (NoClassDefFoundError e) {
            }
        }

        if (clazz != null) {
            //if we load the class            
            // Store a new ClassReference wrapping the loaded class
            ClassReference classReference = new ClassReference(clazz);
            map.put(clazz.getName(), classReference);

            // Return the resolved ClassReference
            return modelClass.cast(classReference);
        } else {
            return unresolved;
        }

    }

    
    /***************
     * Helper methods
     ***************/

    private String getPackageName(ClassReference clazz) {
        int pos = clazz.getClassName().lastIndexOf(".");
        return clazz.getClassName().substring(0, pos);
    }
    
}
