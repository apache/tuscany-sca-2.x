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
package org.apache.tuscany.spi.builder;

import org.apache.tuscany.model.BoundReference;
import org.apache.tuscany.model.BoundService;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetWireFactory;

/**
 * @version $Rev$ $Date$
 */
public interface BuilderRegistry {
    void register(ComponentBuilder<?> builder);

    void register(WireBuilder builder);

    Context build(CompositeContext parent, Component component);

    Context build(CompositeContext parent, BoundService boundService);

    Context build(CompositeContext parent, BoundReference boundReference);

    void connect(SourceWireFactory<?> source, TargetWireFactory<?> target);

    void completeChain(TargetWireFactory<?> target);
}
