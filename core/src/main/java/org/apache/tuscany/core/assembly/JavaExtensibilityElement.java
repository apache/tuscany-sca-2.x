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
package org.apache.tuscany.core.assembly;

import java.lang.reflect.AccessibleObject;
import java.util.List;

import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;

/**
 * Serves as a holder for metadata associated with SCA Java component types
 *
 * @version $$Rev$$ $$Date$$
 */
public interface JavaExtensibilityElement {

    /**
     * Returns the {@link EventInvoker} associated with the intialize callback
     */
    EventInvoker getInit();

    /**
     * Sets the {@link EventInvoker} associated with the intialize callback
     */
    void setInit(EventInvoker init);

    /**
     * Returns true if the implementation must be eagerly initialized
     */
    boolean isEagerInit();

    /**
     * Determines if the implementation must be eagerly initialized
     */
    boolean setEagerInit(boolean val);

    /**
     * Returns the {@link EventInvoker} associated with the destroy callback
     */
    EventInvoker getDestroy();

    /**
     * Sets the {@link EventInvoker} associated with the destroy callback
     */
    void setDestroy(EventInvoker destroy);

    /**
     * Returns the field, method, or constructor marked with an {@link org.osoa.sca.annotations.ComponentName}
     * annotation
     */
    AccessibleObject getComponentName();

    /**
     * Sets the field, method, or constructor marked with an {@link org.osoa.sca.annotations.ComponentName}
     * annotation
     */
    void setComponentName(AccessibleObject componentName);

    /**
     * Returns the field, method, or constructor marked with an {@link org.osoa.sca.annotations.Context}
     * annotation
     */
    AccessibleObject getContext();

    /**
     * Sets the field, method, or constructor marked with an {@link org.osoa.sca.annotations.Context}
     * annotation
     */
    void setContext(AccessibleObject context);

    /**
     * Returns a list of generic extensibility {@link EventInvoker}s. Extension processors that add support
     * for custom annotations types may add an <code>EventInvoker</code> to this collection. The invoker will
     * be available to the component type's {@link org.apache.tuscany.core.builder.ContextFactoryBuilder} to
     * pass to a {@link org.apache.tuscany.core.builder.ContextFactory}.
     */
    List<EventInvoker> getInvokers();

    /**
     * Returns a list of generic extensibility {@link Injector}s. Extension processors that add support
     * for custom annotations types may add an <code>Injector</code> to this collection. The injector will
     * be available to the component type's {@link org.apache.tuscany.core.builder.ContextFactoryBuilder} to
     * pass to a {@link org.apache.tuscany.core.builder.ContextFactory}.
     */
    List<Injector> getInjectors();
}
