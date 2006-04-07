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
package org.apache.tuscany.core.context.scope;

import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.model.assembly.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements a {@link org.apache.tuscany.core.context.ScopeStrategy} for the default module scopes: stateless, request, session,
 * and module.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultScopeStrategy  extends AbstractScopeStrategy  {

    public DefaultScopeStrategy() {
    }

    public Map<Scope,ScopeContext> createScopes(EventContext eventContext) {
        ScopeContext moduleScope = new ModuleScopeContext(eventContext);
        ScopeContext sessionScope = new HttpSessionScopeContext(eventContext);
        ScopeContext requestScope = new RequestScopeContext(eventContext);
        ScopeContext statelessScope = new StatelessScopeContext(eventContext);
        ScopeContext aggregrateScope = new CompositeScopeContext(eventContext);
        Map<Scope,ScopeContext> scopes = new HashMap<Scope,ScopeContext>();
        scopes.put(Scope.MODULE,moduleScope);
        scopes.put(Scope.SESSION,sessionScope);
        scopes.put(Scope.REQUEST,requestScope);
        scopes.put(Scope.INSTANCE,statelessScope);
        scopes.put(Scope.AGGREGATE,aggregrateScope);
        return scopes;
    }

}
