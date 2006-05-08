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
package org.apache.tuscany.spi.context;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetWireFactory;

/**
 * A runtime entity that manages an atomic (i.e. leaf-type) artifact.
 *
 * @version $Rev$ $Date$
 */
public interface AtomicContext extends Context {

    /**
     * Returns the context implementation scope
     */
    public Scope getScope();

    /**
     * Returns whether the context should be eagerly initialized
     */
    public boolean isEagerInit();

    /**
     * Notifies the given instance of an initialization event
     *
     * @throws TargetException
     */
    public void init(Object instance) throws TargetException;

    /**
     * Notifies the given instance of a destroy event
     *
     * @throws TargetException
     */
    public void destroy(Object instance) throws TargetException;

    /**
     * Returns the target instance associated with the context. A target instance is the actual object a
     * request is dispatched to sans proxy wire chain.
     *
     * @throws TargetException
     */
    public Object getTargetInstance() throws TargetException;

    /**
     * Creates a new implementation instance, generally used as a callback by a {@link ScopeContext}
     *
     * @throws ObjectCreationException
     */
    public InstanceContext createInstance() throws ObjectCreationException;

    /**
     * Adds a target-side wire factory for the given service name. Target-side wire factories contain the
     * invocation chains associated with the destination service of a wire and are responsible for generating
     * proxies
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
     * Adds a source-side wire factory for the given reference. Source-side wire factories contain the
     * invocation chains for a reference in the implementation associated with the instance context created by
     * this configuration. Source-side wire factories also produce proxies that are injected on a reference in
     * a component implementation.
     *
     * @param referenceName
     * @param factory
     */
    public void addSourceWireFactory(String referenceName, SourceWireFactory factory);

    /**
     * Adds a set of source-side wire multiplicity factories for the given reference. Source-side wire factories contain
     * the invocation chains for a reference in the implementation associated with the instance context
     * created by this configuration. Source-side wire factories also produce proxies that are injected on a
     * reference in a component implementation.
     *
     * @param referenceName
     */
    public void addSourceWireFactories(String referenceName, Class<?> multiplicityClass, List<SourceWireFactory> factories);
    /**
     * Returns a collection of source-side wire factories for references. There may 1..n wire factories per
     * reference.
     */
    public List<SourceWireFactory> getSourceWireFactories();

    /**
     * Called to signal to the configuration that its parent context has been activated and that it shoud
     * perform any required initialization steps
     */
    public void prepare();

}
