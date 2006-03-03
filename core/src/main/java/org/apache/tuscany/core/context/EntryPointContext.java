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
package org.apache.tuscany.core.context;

/**
 * The runtime artifact representing an entry point, <code>EntryPointContext</code> manages invocation handler
 * instances that expose service operations offered by a component in the parent aggregate. The invocation handler
 * instance is responsible for dispatching the request down an invocation chain to the target instance. The invocation
 * chain may contain {@link org.apache.tuscany.core.invocation.Interceptor}s and
 * {@link org.apache.tuscany.core.invocation.MessageHandler}s that implement policies or perform mediations on the
 * invocation.
 * <p>
 * Entry point contexts are used by transport binding artifacts to invoke an operation on a service. The transport
 * binding uses an {@link java.lang.reflect.InvocationHandler} instance obtained from the <code>EntryPointContext</code>
 * to perform the invocation as in:
 * 
 * <pre>
 *              AggregateContext aggregateContext = ...
 *              EntryPointContext ctx = (EntryPointContext) aggregateContext.getContext(&quot;source&quot;);
 *              Assert.assertNotNull(ctx);
 *              InvocationHandler handler = (InvocationHandler) ctx.getImplementationInstance();
 *              Object response = handler.invoke(null, operation, new Object[] { param });
 * </pre>
 * 
 * The <code>Proxy</code> instance passed to <code>InvocationHandler</code> may be null as the client is invoking
 * directly on the handler.
 * <p>
 * Alternatively, the following will return a proxy implementing the service interface exposed by the entry point:
 * 
 * <pre>
 *              AggregateContext aggregateContext = ...
 *              EntryPointContext ctx = (EntryPointContext) aggregateContext.getContext(&quot;source&quot;);
 *              Assert.assertNotNull(ctx);
 *              HelloWorld proxy = (Helloworld) ctx.getInstance(null); // service name not necessary
 * </pre>
 * 
 * The proxy returned will be backed by the entry point invocation chain.
 * 
 * @version $Rev$ $Date$
 */
public interface EntryPointContext extends InstanceContext {

}
