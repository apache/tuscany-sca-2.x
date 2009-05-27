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

package org.apache.tuscany.sca.contribution.osgi.impl;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.osgi.framework.Bundle;

/**
 * A Model Resolver for ClassReferences.
 *
 * @version $Rev$ $Date$
 */
public class OSGiClassReferenceModelResolver implements ModelResolver {
    // private Contribution contribution;
    private Bundle bundle;

    public OSGiClassReferenceModelResolver(Contribution contribution, FactoryExtensionPoint modelFactories) {
        // this.contribution = contribution;
        this.bundle = OSGiBundleActivator.findBundle(contribution.getLocation());
    }

    public void addModel(Object resolved) {
    }

    public Object removeModel(Object resolved) {
        return resolved;
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        //Load a class on demand
        Class<?> clazz = null;
        if (bundle != null) {
            try {
                clazz = bundle.loadClass(((ClassReference)unresolved).getClassName());
            } catch (Exception e) {
                // Ignore
            }
        }

        if (clazz != null) {
            //if we load the class
            // Store a new ClassReference wrapping the loaded class
            ClassReference classReference = new ClassReference(clazz);

            // Return the resolved ClassReference
            return modelClass.cast(classReference);
        } else {
            return unresolved;
        }

    }
}
