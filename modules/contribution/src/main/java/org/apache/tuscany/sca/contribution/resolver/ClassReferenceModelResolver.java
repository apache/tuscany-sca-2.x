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

package org.apache.tuscany.sca.contribution.resolver;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;

/**
 * A Model Resolver for WSDL models.
 *
 * @version $Rev: 557916 $ $Date: 2007-07-20 01:04:40 -0700 (Fri, 20 Jul 2007) $
 */
public class ClassReferenceModelResolver implements ModelResolver {
    private Contribution contribution;
    protected WeakReference<ClassLoader> classLoader;
    private Map<String, ClassReference> map = new HashMap<String, ClassReference>();
    
    public ClassReferenceModelResolver(Contribution contribution) {
        this.contribution = contribution;
        this.classLoader = new WeakReference<ClassLoader>(Thread.currentThread().getContextClassLoader());
    }

    public void addModel(Object resolved) {
        ClassReference clazz = (ClassReference)resolved;
        map.put(clazz.getClassName(), clazz);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(((ClassReference)resolved).getClassName());
    }
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = map.get(unresolved);
        
        if (resolved != null ){
            return (T)resolved;
        } 
        
        //Load a class on demand
        Class clazz;
        try {
            clazz = Class.forName(((ClassReference)unresolved).getClassName(), true, classLoader.get());
        } catch (ClassNotFoundException e) {
            
            // Return the unresolved object
            return unresolved;
        }
        
        // Store a new ClassReference wrappering the loaded class
        ClassReference classReference = new ClassReference(clazz);
        map.put(getPackageName(classReference), classReference);
        
        // Return the resolved ClassReference
        return (T)classReference;
    }
    
    /***************
     * Helper methods
     ***************/
    
    private String getPackageName(ClassReference clazz) {
        int pos = clazz.getClassName().lastIndexOf(".");
        return clazz.getClassName().substring(0, pos - 1 );
    }
}
