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

package org.apache.tuscany.sca.contribution.groovy;

import groovy.lang.GroovyClassLoader;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A ModelResolver that compiles Groovy scripts into the contribution 
 * class loader so they can be used just like a regular java class.
 *
 * @version $Rev$ $Date$
 */
public class GroovyModelResolver implements ModelResolver {

    protected ModelResolver modelresolver;
    protected Contribution contribution;

    public GroovyModelResolver(Contribution contribution, ModelFactoryExtensionPoint modelFactories) {
        modelresolver = contribution.getModelResolver();
        this.contribution = contribution;
    }

    public void addModel(Object model) {
        ClassLoader cl = contribution.getClassLoader();
        if (!(cl instanceof GroovyClassLoader)) {
            // replace the contribution class loader with a Groovy one
        	// If the contribution does not have a ClassLoader, use this ClassLoader as parent
        	if (cl == null) cl = this.getClass().getClassLoader();            
            cl = new GroovyClassLoader(cl);
            contribution.setClassLoader(cl);
        }
        try {

            ((GroovyClassLoader)cl).parseClass(((GroovyArtifact)model).getArtifactURL().openStream());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object removeModel(Object arg0) {
        return null;
    }

    public <T> T resolveModel(Class<T> arg0, T arg1) {
        return null;
    }
}
