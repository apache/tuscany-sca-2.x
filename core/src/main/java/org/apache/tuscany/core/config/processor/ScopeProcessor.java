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

import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ComponentInfo;

import java.lang.annotation.Annotation;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScopeProcessor extends AnnotationProcessorSupport {

    public void visitImplementationClass(Class clazz, Annotation annotation, ComponentInfo type) {
        if (!(annotation instanceof org.osoa.sca.annotations.Scope)) {
            return;
        }
        Scope scope = getScope(annotation); // hack for now - set scope to implementation scope
        for(Service service: type.getServices()){
            Scope serviceScope = service.getServiceContract().getScope();
            if (serviceScope == Scope.INSTANCE || serviceScope == null){
                service.getServiceContract().setScope(scope);
            }
        }
    }

    @Override
    public void visitServiceInterface(Class clazz, Annotation annotation, Service service) {
        if (!(annotation instanceof org.osoa.sca.annotations.Scope)) {
            return;
        }
        service.getServiceContract().setScope(getScope(annotation));
    }

    private Scope getScope(Annotation annotation){
        org.osoa.sca.annotations.Scope scopeAnnotation = (org.osoa.sca.annotations.Scope) annotation;
        if ("MODULE".equalsIgnoreCase(scopeAnnotation.value())) {
           return Scope.MODULE;
        } else if ("SESSION".equalsIgnoreCase(scopeAnnotation.value())) {
            return Scope.SESSION;
        } else if ("REQUEST".equalsIgnoreCase(scopeAnnotation.value())) {
            return Scope.REQUEST;
        } else {
            return Scope.INSTANCE;
        }

    }
}
