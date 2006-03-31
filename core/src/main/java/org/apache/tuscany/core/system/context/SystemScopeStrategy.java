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
package org.apache.tuscany.core.system.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.scope.AbstractScopeStrategy;
import org.apache.tuscany.core.context.scope.AggregateScopeContext;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.context.scope.StatelessScopeContext;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Implements a {@link org.apache.tuscany.core.context.ScopeStrategy} for a system aggregate context with the following scopes:
 * <ul>
 * <li>{@link org.apache.tuscany.model.assembly.Scope#AGGREGATE}</li>
 * <li>{@link org.apache.tuscany.model.assembly.Scope#MODULE}</li>
 * <li>{@link org.apache.tuscany.model.assembly.Scope#INSTANCE}</li>
 * </ul>
 * 
 * @version $Rev$ $Date$
 */
public class SystemScopeStrategy extends AbstractScopeStrategy {

    public SystemScopeStrategy() {
    }

    public Map<Scope, ScopeContext> createScopes(EventContext eventContext) {
        ScopeContext aggregrateScope = new AggregateScopeContext(eventContext);
        ScopeContext moduleScoper = new ModuleScopeContext(eventContext);
        ScopeContext statelessScope = new StatelessScopeContext(eventContext);
        Map<Scope, ScopeContext> scopes = new HashMap<Scope, ScopeContext>();
        scopes.put(Scope.AGGREGATE, aggregrateScope);
        scopes.put(Scope.MODULE, moduleScoper);
        scopes.put(Scope.INSTANCE, statelessScope);
        return scopes;
    }

}
