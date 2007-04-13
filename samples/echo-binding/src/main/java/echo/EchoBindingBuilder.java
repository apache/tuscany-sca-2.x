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
package echo;

import java.net.URI;

import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;

/**
 * @version $Rev$ $Date$
 */
public class EchoBindingBuilder extends BindingBuilderExtension<EchoBinding> {
    @Override
    public ServiceBinding build(CompositeService serviceDefinition,
                                EchoBinding bindingDefinition,
                                DeploymentContext context) throws BuilderException {
        return new EchoService(URI.create(context.getComponentId() + "#" + serviceDefinition.getName()));
    }

    @Override
    public ReferenceBinding build(CompositeReference referenceDefinition,
                                  EchoBinding bindingDefinition,
                                  DeploymentContext context) throws BuilderException {
        URI targetURI = bindingDefinition.getURI() != null ? URI.create(bindingDefinition.getURI()) : null;
        return new EchoReference(URI.create(context.getComponentId() + "#" + referenceDefinition.getName()), targetURI);
    }

    @Override
    protected Class<EchoBinding> getBindingType() {
        return EchoBinding.class;
    }
}
