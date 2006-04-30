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
package org.apache.tuscany.binding.axis2.builder;

import java.lang.reflect.Method;

import org.apache.tuscany.binding.axis2.config.WSExternalServiceContextFactory;
import org.apache.tuscany.binding.axis2.handler.ExternalServiceTargetInvoker;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.extension.WireBuilderSupport;
import org.apache.tuscany.core.wire.TargetInvoker;
import org.osoa.sca.annotations.Scope;

@Scope("MODULE")
public class ExternalWebServiceWireBuilder extends WireBuilderSupport {

    public ExternalWebServiceWireBuilder() {
    }

    @Override
    protected boolean handlesTargetType(Class targetType) {
        return WSExternalServiceContextFactory.class.isAssignableFrom(targetType);
    }

    @Override
    protected TargetInvoker createInvoker(QualifiedName targetName, Method operation, ScopeContext context, boolean downScope) {
        return new ExternalServiceTargetInvoker(targetName, operation, context);
    }
}
