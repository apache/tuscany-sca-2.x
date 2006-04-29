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
package org.apache.tuscany.core.config.processor;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;

/**
 * Processes the {@link org.osoa.sca.annotations.Scope} annotation
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScopeProcessor extends ImplementationProcessorSupport {

    public ScopeProcessor() {
    }

    public ScopeProcessor(AssemblyFactory factory) {
        super(factory);
    }

    @Override
    public void visitEnd(Class<?> clazz, ComponentInfo type) throws ConfigurationLoadException {
        Scope scope = null;
        org.osoa.sca.annotations.Scope annotation = clazz.getAnnotation(org.osoa.sca.annotations.Scope.class);
        if (annotation != null) {
            scope = ProcessorHelper.getScope(annotation);
        } else {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                scope = recurseScope(superClass);
            }
        }
        if (scope == null) {
            scope = Scope.INSTANCE;
        }
        //FIXME hack for now - set scope to implementation scope
        //This will be clean up with spec change
        for (Service service : type.getServices()) {
            Scope serviceScope = service.getServiceContract().getScope();
            if (serviceScope == Scope.INSTANCE || serviceScope == null) {
                service.getServiceContract().setScope(scope);
            }
        }

    }

    /**
     * Walks the class hierarchy until a scope annotation is found
     */
    private Scope recurseScope(Class<?> superClass) {
        if (Object.class.equals(superClass)) {
            return null;
        }
        org.osoa.sca.annotations.Scope scope = superClass.getAnnotation(org.osoa.sca.annotations.Scope.class);
        if (scope == null) {
            superClass = superClass.getSuperclass();
            if (superClass != null) {
                return recurseScope(superClass);
            }
        }
        return ProcessorHelper.getScope(scope);
    }

}
