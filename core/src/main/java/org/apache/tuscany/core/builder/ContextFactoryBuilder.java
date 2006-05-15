/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.builder;

import org.apache.tuscany.model.assembly.AssemblyObject;

/**
 * The extension point for component types in the runtime. Implementations perform the first phase of converting an assembly model
 * into a series of runtime artifacts. Specifically, <code>ContextFactoryBuilder</code>s are responsible for analyzing the
 * assembly model and producing {@link ContextFactory}s that are used to generate executable artifacts such as an {@link
 * org.apache.tuscany.core.context.Context}. In the case of components, the <code>ContextFactory</code> will typically contain
 * configuration for instantiating implementation instances with injected properties and references.
 * <p/>
 * As the assembly model is analyzed, <code>ContextFactoryBuilder</code>s are guaranteed to be called first and are expected to
 * decorate the assembly model with <code>ContextFactory</code>s.
 * <p/>
 * The second phase uses {@link WireBuilder}s to connect the source and target wire chains held in these
 * <code>ContextFactory</code>s to form a completed wire. <code>WireBuilder<code>s may use a similar delegation strategy and
 * perform various optimizations.
 *
 * @version $Rev$ $Date$
 * @see ContextFactory
 * @see WireBuilder
 */
public interface ContextFactoryBuilder {

    /**
     * Creates or updates a context factory based on configuration contained in the given model object. The model object is
     * decorated with the factory.
     *
     * @param object the logical configuration model node
     * @throws BuilderException
     */
    public void build(AssemblyObject object) throws BuilderException;

}
