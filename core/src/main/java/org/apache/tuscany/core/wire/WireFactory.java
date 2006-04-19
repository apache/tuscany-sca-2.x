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
package org.apache.tuscany.core.wire;

/**
 * Implementations are responsible for managing source or target sides of a wire, including creation of service proxies.
 * Source-side wires are injected on references and may contain policy interceptors and/or handlers specified by them. Target-side
 * wires may contain policy interceptors and/or handlers specified by the service the wire is targeted to or one of its
 * operations. Source- and target-side <code>WireFactory</code>s are held in the {@link org.apache.tuscany.core.builder.ContextFactory}
 * associated with the source reference or target service.
 * <p/>
 * When an assembly is built by the runtime, source-side and target-side wires are "bridged" on the source side (i.e. a reference
 * to the target-side is stored in the source-side). This bridging process is done by a series of {@link
 * org.apache.tuscany.core.builder.WireBuilder}s configured in the runtime. When a new component implementation instance is
 * created, it will be injected with a proxy for each reference containing the bridged source- and target-side wires.
 * <p/>
 * Unmanaged code, i.e. clients that are not components, that perform a locate operation are handled differently. In this case, a
 * target-side proxy will be returned by the locate operation created by the <code>WireFactory</code> associated with the target
 * service. This target-side proxy will only contain the target-side wire and its handlers/interceptors.Ê
 * <p/>
 * Wires are structured by operation; that is, they contain an invocation chain per operation on a service. Note that the service
 * specified by a reference may differ in type from the target service specified by the wire. In this case, a mediation may be
 * performed by the runtime. Hence, source-to-target bridging is done on a per operation basis. Source- and target-side Invocation
 * chains are accessible through the subtypes of <code>WireFactory</code>.
 *
 * @version $Rev$ $Date$
 */
public interface WireFactory<T> {

    /**
     * Prepares the factory. This will typically be called at build time, after bridging source- and target-side invocation chains.
     *
     * @throws ProxyInitializationException if an error is encountered during initialization
     */
    public void initialize() throws ProxyInitializationException;

    /**
     * Returns a proxy for a service specified by a reference or target
     */
    public T createProxy() throws ProxyCreationException;

    /**
     * Sets the primary interface type generated proxies implement
     */
    public void setBusinessInterface(Class interfaze);

    /**
     * Returns the primary interface type implemented by generated proxies
     */
    public Class getBusinessInterface();

    /**
     * Adds an interface type generated proxies implement
     */
    public void addInterface(Class claz);

    /**
     * Returns an array of all interfaces implemented by generated proxies
     */
    public Class[] getImplementatedInterfaces();

}
