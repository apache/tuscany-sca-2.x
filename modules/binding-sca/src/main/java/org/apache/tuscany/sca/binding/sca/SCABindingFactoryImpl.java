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

package org.apache.tuscany.sca.binding.sca;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.distributed.host.SCADomainNode;

/**
 * A factory for the SCA binding model.
 * 
 * @version $Rev$ $Date$
 */
public class SCABindingFactoryImpl implements SCABindingFactory {
    private SCADomainNode domainNode;
    private ExtensionPointRegistry registry;
    
    public SCABindingFactoryImpl(SCADomainNode domainNode,
                                 ExtensionPointRegistry registry) {
        this.domainNode = domainNode;
        this.registry = registry;
    }
    public SCABinding createSCABinding() {     
        return new SCABindingImpl(domainNode,
                                  registry);
    }
    
}
