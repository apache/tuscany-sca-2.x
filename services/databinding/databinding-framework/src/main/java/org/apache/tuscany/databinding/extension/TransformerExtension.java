/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.databinding.extension;

import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.TransformerRegistry;
import org.apache.tuscany.spi.annotation.Autowire;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Service;

/**
 * Base Implementation of Transformer which provides the registration to the transformer registry
 */
@org.osoa.sca.annotations.Scope("MODULE")
@Service(Transformer.class)
public abstract class TransformerExtension<S, T> implements Transformer {

    protected TransformerRegistry registry;

    protected TransformerExtension() {
        super();
    }
    
    @Autowire
    public void setTransformerRegistry(TransformerRegistry registry) {
        this.registry = registry;
    }

    @Init(eager = true)
    public void init() {
        registry.registerTransformer(this);
    }

    protected abstract Class getSourceType();
    protected abstract Class getTargetType();
    
    public String getSourceBinding() {
        return getSourceType().getName();
    }

    public String getTargetBinding() {
        return getTargetType().getName();
    }

    public int getWeight() {
        // default to 50
        return 50;
    }    
    
}
