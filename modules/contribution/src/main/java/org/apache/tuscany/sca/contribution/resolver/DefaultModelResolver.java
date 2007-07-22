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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;

/**
 * A default implementation of an artifact resolver, based on a map.
 *
 * @version $Rev: 548560 $ $Date: 2007-06-18 19:25:19 -0700 (Mon, 18 Jun 2007) $
 */
public class DefaultModelResolver implements ModelResolver {
    private static final long serialVersionUID = -7826976465762296634L;
    
    private Map<Object, Object> map = new HashMap<Object, Object>();
    
    protected WeakReference<ClassLoader> classLoader;
    protected Contribution contribution;
    
    public DefaultModelResolver(ClassLoader classLoader, Contribution contribution) {
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.contribution = contribution;
    }
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = map.get(unresolved);
        if (resolved != null) {
            
            // Return the resolved object
            return modelClass.cast(resolved);
            
        } else if (unresolved instanceof ClassReference) {
            
            // Load a class on demand
            ClassReference classReference = (ClassReference)unresolved;
            Class clazz;
            try {
                clazz = Class.forName(classReference.getClassName(), true, classLoader.get());
            } catch (ClassNotFoundException e) {
                
                // Return the unresolved object
                return unresolved;
            }
            
            // Store a new ClassReference wrappering the loaded class
            resolved = new ClassReference(clazz);
            map.put(resolved, resolved);
            
            // Return the resolved ClassReference
            return modelClass.cast(resolved);
                
        } else {
            
            // Return the unresolved object
            return unresolved;
        }
    }
    
    public void addModel(Object resolved) {
        map.put(resolved, resolved);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(resolved);
    }
    
    public Collection<Object> getModels() {
        return map.values();
    }
    
}
