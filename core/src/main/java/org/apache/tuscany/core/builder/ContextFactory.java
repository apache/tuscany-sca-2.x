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

import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.model.assembly.Scope;

import java.util.List;
import java.util.Map;

/**
 * Implementations create {@link org.apache.tuscany.core.context.Context}s based on an assembly configuration.
 * <p/>
 * Context factories are "built" in two phases. {@link ContextFactoryBuilder}s analyze an assembly, producing
 * <code>ContextFactory</code>s for {@link org.apache.tuscany.model.assembly.Component}s, {@link
 * org.apache.tuscany.model.assembly.EntryPoint}s, and {@link org.apache.tuscany.model.assembly.ExternalService}s. During this
 * phase, {@link org.apache.tuscany.core.wire.WireFactory}s for source- and target-side wires are produced for the
 * <code>ContextFactory</code>s.  Ê
 * <p/>
 * The second build phase connects the source- and target-side <code>WireFactories</code>, thereby completing wire configuration.
 * <p/>
 * At runtime, <code>ContextFactory</code>s are called to create new <code>Context</code>s when a new implementation instance is
 * required for a component, entry point, or external service. The <code>Context</code> is then responsible for instantiating and
 * managing the actual implementation instance. When a <code>Context</code> creates a new instance, the previously configured
 * <code>WireFactory</code>s are used to create wires to and from the instance. A wire is a collection of stateless invocation
 * chains that are managed by the <code>Context</code>'s <code>ContextFactory</code>.
 *
 * @version $Rev: 385747 $ $Date: 2006-03-13 22:12:53 -0800 (Mon, 13 Mar 2006) $
 */
public interface ContextFactory<T extends Context> {

    /**
     * Creates a <code>Context</code> based on configuration supplied by a logical model assembly
     *
     * @return a new instance context
     * @throws ContextCreationException if an error occurs creating the context
     */
    public T createContext() throws ContextCreationException;

    /**
     * Returns the scope identifier associated with the type of contexts produced by the current factory
     */
    public Scope getScope();

    /**
     * Returns the name of the <code>Context</code> produced by the current factory
     */
    public String getName();

    /**
     * Adds a property to the context
     */
    public void addProperty(String propertyName, Object value);

    /**
     * Adds a target-side wire factory for the given service name. Target-side wire factories contain the invocation chains
     * associated with the destination service of a wire and are responsible for generating proxies
     */
    public void addTargetWireFactory(String serviceName, TargetWireFactory factory);

    /**
     * Returns the target-side wire factory associated with the given service name
     */
    public TargetWireFactory getTargetWireFactory(String serviceName);

    /**
     * Returns a collection of target-side wire factories keyed by service name
     */
    public Map<String, TargetWireFactory> getTargetWireFactories();

    /**
     * Adds a source-side wire factory for the given reference. Source-side wire factories contain the invocation chains for a
     * reference in the implementation associated with the instance context created by this configuration. Source-side wire
     * factories also produce proxies that are injected on a reference in a component implementation.
     *
     * @param referenceName
     * @param factory
     */
    public void addSourceWireFactory(String referenceName, SourceWireFactory factory);

    /**
     * Adds a set of source-side wire factories for the given reference. Source-side wire factories contain the invocation chains
     * for a reference in the implementation associated with the instance context created by this configuration. Source-side wire
     * factories also produce proxies that are injected on a reference in a component implementation.
     *
     * @param referenceName
     * @param referenceInterface
     * @param factory
     * @param multiplicity
     */
    public void addSourceWireFactories(String referenceName, Class referenceInterface, List<SourceWireFactory> factory, boolean multiplicity);

    /**
     * Returns a collection of source-side wire factories for references. There may 1..n wire factories per reference.
     */
    public List<SourceWireFactory> getSourceWireFactories();

    /**
     * Called to signal to the configuration that its parent context has been activated and that it shoud perform any required
     * initialization steps
     *
     * @param parent the parent context
     */
    public void prepare(CompositeContext parent);

}
