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

import org.apache.tuscany.sca.contribution.java.JavaExport;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A model resolver for Java exports.
 *
 * @version $Rev$ $Date$
 */
public class JavaExportModelResolver implements ModelResolver {

    private JavaExport export;
    private ModelResolver resolver;
    
    public JavaExportModelResolver(JavaExport export, ModelResolver resolver) {
        this.export = export;
        this.resolver = resolver;
    }
    
    public void addModel(Object resolved) {
        throw new IllegalStateException();
    }

    public Object removeModel(Object resolved) {
        throw new IllegalStateException();
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        if (!(unresolved instanceof ClassReference)) {
            return unresolved;
        }
        
        // Filter package name
        ClassReference classReference = (ClassReference)unresolved;
        String className = classReference.getClassName();
        int d = className.lastIndexOf('.');
        String packageName;
        if (d != -1) {
            packageName = className.substring(0, d);
        } else {
            packageName = "";
        }
        if (export.getPackage().equals(packageName)) {
            
            // Package matches the exported package, delegate to the
            // contribution's resolver
            return resolver.resolveModel(modelClass, unresolved);
        } else {
            
            // Package is not exported, return the unresolved object 
            return unresolved;
        }
    }

}
