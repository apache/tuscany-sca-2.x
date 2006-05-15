/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.core.system.context;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyObject;

@Scope("MODULE")
public class TestBuilder implements ContextFactoryBuilder {
    private ContextFactoryBuilderRegistry builderRegistry;

    private boolean invoked = false;

    public TestBuilder() {
        super();
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(this);
    }

    @Autowire
    public void setBuilderRegistry(ContextFactoryBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    public void build(AssemblyObject object) throws BuilderException {
        invoked = true;
    }

    public boolean invoked() {
        return invoked;
    }
}
