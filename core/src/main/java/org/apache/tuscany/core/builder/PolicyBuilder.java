/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.builder;

/**
 * A marker for policy extensions in the runtime. Implementations evaluate wire-related policy metadata on a {@link
 * org.apache.tuscany.model.assembly.ConfiguredReference} or {@link org.apache.tuscany.model.assembly.ConfiguredService} and
 * contribute {@link org.apache.tuscany.core.wire.Interceptor}s or {@link org.apache.tuscany.core.wire.MessageHandler}s
 * implementing a policy to {@link org.apache.tuscany.core.wire.InvocationConfiguration}s that are part of a {@link
 * org.apache.tuscany.core.wire.WireConfiguration}. The contributed <code>Interceptor</code>s or <code>Handler</code>s will be
 * called as part of an invocation over a wire.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface PolicyBuilder {

}
