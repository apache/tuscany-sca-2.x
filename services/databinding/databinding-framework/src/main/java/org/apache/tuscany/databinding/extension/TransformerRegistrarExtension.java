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

import java.util.Collection;

import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.TransformerRegistry;
import org.apache.tuscany.spi.annotation.Autowire;
import org.osoa.sca.annotations.Init;

/**
 * A base class responsible to register a collection of transformers. The purpose is to group
 * a list of transformer contributions in one system component
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class TransformerRegistrarExtension {
    protected TransformerRegistry registry;

    public TransformerRegistrarExtension() {
        super();
    }

    @Autowire
    public void setTransformerRegistry(TransformerRegistry registry) {
        this.registry = registry;
    }

    @Init(eager = true)
    public void init() {
        for (Transformer t : getTransformers()) {
            registry.registerTransformer(t);
        }
    }

    protected abstract Collection<Transformer> getTransformers();

}
