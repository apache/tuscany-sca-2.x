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

import static org.apache.tuscany.core.context.ContextConstants.AGGREGATE_SCOPE;
import static org.apache.tuscany.core.context.ContextConstants.MODULE_SCOPE;
import static org.apache.tuscany.core.context.ContextConstants.REQUEST_SCOPE;
import static org.apache.tuscany.core.context.ContextConstants.SESSION_SCOPE;
import static org.apache.tuscany.core.context.ContextConstants.STATELESS;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ScopeContext;

/**
 * Implements a {@link org.apache.tuscany.core.context.ScopeStrategy} for the default module scopes: stateless, request, session,
 * and module.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultScopeStrategy  extends AbstractScopeStrategy  {

    public DefaultScopeStrategy() {
    }

    public Map<Integer,ScopeContext> createScopes(EventContext eventContext) {
        ScopeContext moduleScope = new ModuleScopeContext(eventContext);
        ScopeContext sessionScope = new HttpSessionScopeContext(eventContext);
        ScopeContext requestScope = new RequestScopeContext(eventContext);
        ScopeContext statelessScope = new StatelessScopeContext(eventContext);
        ScopeContext aggregrateScope = new AggregateScopeContext(eventContext);
        Map<Integer,ScopeContext> scopes = new HashMap();
        scopes.put(MODULE_SCOPE,moduleScope);
        scopes.put(SESSION_SCOPE,sessionScope);
        scopes.put(REQUEST_SCOPE,requestScope);
        scopes.put(STATELESS,statelessScope);
        scopes.put(AGGREGATE_SCOPE,aggregrateScope);
        return scopes;
    }

}
