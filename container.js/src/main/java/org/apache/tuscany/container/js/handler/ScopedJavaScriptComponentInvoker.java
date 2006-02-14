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
package org.apache.tuscany.container.js.handler;

import java.lang.reflect.Method;

import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.TargetException;

/**
 * Uses a scope container to resolve an implementation instance based on the current thread context
 */
public class ScopedJavaScriptComponentInvoker extends AbstractJavaScriptComponentInvoker {

    private ScopeContext container;
    private QualifiedName name;

    public ScopedJavaScriptComponentInvoker(String serviceName, Method operation, ScopeContext container) {
        super(operation);
        assert (serviceName != null) : "No service name specified";
        assert (container != null) : "No scope container specified";
        name = new QualifiedName(serviceName);
        this.container = container;
    }

    protected Object getInstance() throws TargetException {
        return container.getInstance(name);
    }

    public boolean isCacheable() {
        return false;
    }

}
