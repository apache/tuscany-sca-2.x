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

package org.apache.tuscany.implementation.spi;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.contribution.resolver.ModelResolver;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;

/**
 * TODO: couldn't something like this class be provided by the runtime?
 * Each impl shouldn't have to have their own .componentType merging code
 */
public abstract class AbstractStAXArtifactProcessor<I extends Implementation> implements StAXArtifactProcessor<I> {

    private AssemblyFactory assemblyFactory;

    public AbstractStAXArtifactProcessor(AssemblyFactory assemblyFactory) {
        this.assemblyFactory = assemblyFactory;
    }

    public void resolve(I model, ModelResolver resolver) throws ContributionResolveException {
      addSideFileComponentType(model.getURI(), model, resolver);
      model.setUnresolved(false);
    }

    protected void addSideFileComponentType(String name, Implementation impl, ModelResolver resolver) {
        int lastDot = name.lastIndexOf('.');
        if (lastDot < 0) {
            return;
        }
        String sideFileName = name.substring(0, lastDot) + ".componentType";

        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setURI(sideFileName);
        componentType.setUnresolved(true);

        componentType = resolver.resolveModel(ComponentType.class, componentType);

        if (!componentType.isUnresolved()) {
            for (Reference reference : componentType.getReferences()) {
                impl.getReferences().add(reference);
            }
            for (Service service : componentType.getServices()) {
                impl.getServices().add(service);
            }
            for (Property property : componentType.getProperties()) {
                impl.getProperties().add(property);
            }
            if (componentType.getConstrainingType() != null) {
                impl.setConstrainingType(componentType.getConstrainingType());
            }
        }
    }

}
