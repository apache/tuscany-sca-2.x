/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.core.scope;

import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.scope.Scope;
import org.apache.tuscany.sca.scope.ScopeContainer;
import org.apache.tuscany.sca.scope.ScopeContainerFactory;
import org.apache.tuscany.sca.store.Store;

/**
 * @version $Rev$ $Date$
 */
public class ConversationalScopeContainerFactory implements ScopeContainerFactory {
    private Store store;

    public ConversationalScopeContainerFactory(Store store) {
        super();
        this.store = store;
    }

    public ScopeContainer createScopeContainer(RuntimeComponent component) {
        return new ConversationalScopeContainer(store, component);
    }

    public Scope getScope() {
        return Scope.CONVERSATION;
    }

}
