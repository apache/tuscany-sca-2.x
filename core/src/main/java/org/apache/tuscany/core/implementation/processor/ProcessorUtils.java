/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.implementation.processor;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.model.InteractionScope;

import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * Contains various utility methods for <code>ImplementationProcessor</code>s
 *
 * @version $Rev$ $Date$
 */
public final class ProcessorUtils {

    private ProcessorUtils() {
    }

    /**
     * Convenience method for creating a mapped service from the given interface
     */
    public static JavaMappedService createService(Class<?> interfaze) {
        JavaMappedService service = new JavaMappedService();
        service.setName(JavaIntrospectionHelper.getBaseName(interfaze));
        service.setRemotable(interfaze.getAnnotation(Remotable.class) != null);
        service.setServiceInterface(interfaze);
        JavaServiceContract contract = new JavaServiceContract();
        contract.setInterfaceClass(interfaze);
        Scope interactionScope = interfaze.getAnnotation(Scope.class);
        if (interactionScope == null) {
            contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
        } else {
            if ("CONVERSATIONAL".equalsIgnoreCase(interactionScope.value())) {
                contract.setInteractionScope(InteractionScope.CONVERSATIONAL);
            } else {
                contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
            }
        }
        Callback callback = interfaze.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            Class<?> callbackClass = callback.value();
            contract.setCallbackClass(callbackClass);
            contract.setCallbackName(JavaIntrospectionHelper.getBaseName(callbackClass));
        }
        service.setServiceContract(contract);
        return service;
    }

    /**
     * Determines if all the members of a collection have unique types
     *
     * @param collection the collection to analyze
     * @return true if the types are unique
     */
    public static boolean areUnique(Class[] collection) {
        if (collection.length == 0) {
            return true;
        }
        return areUnique(collection, 0);
    }

    /**
     * Determines if all the members of a collection have unique types
     *
     * @param collection the collection to analyze
     * @param start      the position in the collection to start
     * @return true if the types are unique
     */
    private static boolean areUnique(Class[] collection, int start) {
        Object compare = collection[start];
        for (int i = start + 1; i < collection.length; i++) {
            if (compare.equals(collection[i])) {
                return false;
            }
        }
        if (start + 1 < collection.length) {
            return areUnique(collection, start + 1);
        } else {
            return true;
        }
    }

}
