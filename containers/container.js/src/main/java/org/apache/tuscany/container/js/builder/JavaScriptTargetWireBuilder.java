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
package org.apache.tuscany.container.js.builder;

import org.apache.tuscany.container.js.config.JavaScriptContextFactory;
import org.apache.tuscany.container.js.rhino.RhinoTargetInvoker;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.extension.WireBuilderSupport;
import org.apache.tuscany.core.wire.TargetInvoker;
import org.osoa.sca.annotations.Scope;

import java.lang.reflect.Method;

/**
 * Responsible for bridging source- and target-side invocations chains when the target type is a JavaScript implementation
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class JavaScriptTargetWireBuilder extends WireBuilderSupport {

    protected boolean handlesTargetType(Class targetType) {
        return JavaScriptContextFactory.class.isAssignableFrom(targetType);
    }

    protected TargetInvoker createInvoker(QualifiedName targetName, Method operation, ScopeContext context, boolean downScope) {
        String serviceName = targetName.getPartName();
        return new RhinoTargetInvoker(serviceName, operation, context);
    }
}
