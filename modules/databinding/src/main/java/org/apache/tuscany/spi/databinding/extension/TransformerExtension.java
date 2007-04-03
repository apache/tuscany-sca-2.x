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
package org.apache.tuscany.spi.databinding.extension;

import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.TransformerRegistry;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Base Implementation of Transformer which provides the registration to the transformer registry
 *
 * @version $Rev$ $Date$
 */
@Service(Transformer.class)
@Scope("COMPOSITE")
@EagerInit
public abstract class TransformerExtension<S, T> implements Transformer {

    protected TransformerRegistry registry;

    protected TransformerExtension() {
        super();
    }

    @Reference
    public void setTransformerRegistry(TransformerRegistry registry) {
        this.registry = registry;
    }

    @Init
    public void init() {
        registry.registerTransformer(this);
    }

    protected abstract Class getSourceType();

    protected abstract Class getTargetType();

    public String getSourceDataBinding() {
        return getSourceType().getName();
    }

    public String getTargetDataBinding() {
        return getTargetType().getName();
    }

    public int getWeight() {
        // default to 50
        return 50;
    }

}
