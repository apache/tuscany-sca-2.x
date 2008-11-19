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

package org.apache.tuscany.sca.assembly.xml;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A Model Resolver for ComponentType models.
 *
 * @version $Rev$ $Date$
 */
public class ComponentTypeModelResolver implements ModelResolver {
	private Contribution contribution;
    private Map<String, ComponentType> map = new HashMap<String, ComponentType>();
    
    public ComponentTypeModelResolver(Contribution contribution, ModelFactoryExtensionPoint modelFactories) {
    	this.contribution = contribution;
    }

    public void addModel(Object resolved) {
        ComponentType componentType = (ComponentType)resolved;
        map.put(componentType.getURI(), componentType);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(((ComponentType)resolved).getURI());
    }
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {

    	//get componentType artifact URI
        String uri = ((ComponentType)unresolved).getURI();
        if (uri == null) {
        	return (T)unresolved;
        }
        
        //lookup the componentType
        ComponentType resolved = (ComponentType) map.get(uri);
        if (resolved != null) {
            return modelClass.cast(resolved);
        } 
        
        //If not found, delegate the resolution to the imports (in this case based on the java imports)
        //compute the package name from the componentType URI
        if (unresolved instanceof ComponentType) {
            //FIXME The core assembly model now depends on java imports to 
            // resolve componentTypes of all kinds, this is not right at all!!!
            int s = uri.lastIndexOf('/');
            if (s != -1) {
                String packageName = uri.substring(0, uri.lastIndexOf("/"));
                for (Import import_ : this.contribution.getImports()) {
                    if (import_ instanceof JavaImport) {
                    	JavaImport javaImport = (JavaImport)import_;
                    	//check the import location against the computed package name from the componentType URI
                        if (javaImport.getPackage().equals(packageName)) {
                            // Delegate the resolution to the import resolver
                            resolved = javaImport.getModelResolver().resolveModel(ComponentType.class, (ComponentType)unresolved);
                            if (!resolved.isUnresolved()) {
                                return modelClass.cast(resolved);
                            }
                        }
                    }
                }
            }
        }

        return (T)unresolved;
    }
    
}
