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
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A Model Resolver for ClassReferences.
 *
 * @version $Rev: 557916 $ $Date: 2007-07-20 01:04:40 -0700 (Fri, 20 Jul 2007) $
 */
public class ClassReferenceModelResolver implements ModelResolver {
    private Contribution contribution;
    protected WeakReference<ClassLoader> classLoader;
    private Map<String, ClassReference> map = new HashMap<String, ClassReference>();
    
    private ModelResolver osgiResolver;
    
    public ClassReferenceModelResolver(Contribution contribution, ModelFactoryExtensionPoint modelFactories) {
        this.contribution = contribution;
        //FIXME The classloader should be passed in
        this.classLoader = new WeakReference<ClassLoader>(Thread.currentThread().getContextClassLoader());
        
        try {
            Class osgiResolverClass = Class.forName("org.apache.tuscany.sca.contribution.osgi.impl.OSGiClassReferenceModelResolver");
            if (osgiResolverClass != null) {
                Constructor constructor = osgiResolverClass.getConstructor(Contribution.class, ModelFactoryExtensionPoint.class);
                this.osgiResolver = (ModelResolver)constructor.newInstance(contribution, modelFactories);
            }
        } catch (Exception e) {
        }
    }

    public void addModel(Object resolved) {
        ClassReference clazz = (ClassReference)resolved;
        map.put(clazz.getClassName(), clazz);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(((ClassReference)resolved).getClassName());
    }
    
    /**
     * Handle artifact resolution when the specific class reference is imported from another contribution
     * @param unresolved
     * @return
     */
    private ClassReference resolveImportedModel(ClassReference unresolved) {
        ClassReference resolved = unresolved;

        if( this.contribution != null) {
            for (Import import_ : this.contribution.getImports()) {
                if (import_ instanceof JavaImport) {
                    JavaImport javaImport = (JavaImport) import_;
                    String packageName = javaImport.getPackage();
                    if (javaImport.getPackage().equals(packageName)) {
                        // Delegate the resolution to the import resolver
                        resolved = import_.getModelResolver().resolveModel(ClassReference.class, unresolved);
                    }
                }
            }
            
        }
        return resolved;
    }
    
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = map.get(unresolved);
        
        if (resolved != null ){
            return modelClass.cast(resolved);
        } 
        
        //Load a class on demand
        Class clazz = null;
        
        if (osgiResolver != null)
            resolved = osgiResolver.resolveModel(modelClass, unresolved);
        
        if (unresolved == resolved || resolved == null) {
            try {
                clazz = Class.forName(((ClassReference)unresolved).getClassName(), true, classLoader.get());
            } catch (ClassNotFoundException e) {
                //we will later try to delegate to imported model resolvers
            }
        }
        else
            clazz = ((ClassReference)resolved).getJavaClass();


        if (clazz != null) {
            //if we load the class            
            // Store a new ClassReference wrappering the loaded class
            ClassReference classReference = new ClassReference(clazz);
            map.put(getPackageName(classReference), classReference);
            
            // Return the resolved ClassReference
            return modelClass.cast(classReference);            
        } else {
            //delegate resolution of the class
            resolved = this.resolveImportedModel((ClassReference)unresolved);
            return modelClass.cast(resolved);
        }
        

    }
    
    /***************
     * Helper methods
     ***************/
    
    private String getPackageName(ClassReference clazz) {
        int pos = clazz.getClassName().lastIndexOf(".");
        return clazz.getClassName().substring(0, pos - 1 );
    }
}
