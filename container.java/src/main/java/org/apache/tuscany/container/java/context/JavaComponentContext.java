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
package org.apache.tuscany.container.java.context;

import java.util.Iterator;

import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.ContextInitException;
import org.apache.tuscany.core.context.LifecycleEventListener;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.SimpleComponentContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.ObjectCallbackException;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.injection.ObjectFactory;

/**
 * Manages Java service component implementation instances
 * 
 * @version $Rev$ $Date$
 */
public class JavaComponentContext extends AbstractContext implements SimpleComponentContext {

    private boolean eagerInit;

    private EventInvoker initInvoker;

    private EventInvoker destroyInvoker;

    private Injector componentName;

    private Injector moduleContext;

    private boolean stateless;

    // the cached target instance
    private Object cachedTargetInstance;

    // creates a new implementation instance with injected references and properties
    private ObjectFactory objectFactory;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public JavaComponentContext(String name, ObjectFactory objectFactory, boolean eagerInit, EventInvoker initInvoker,
            EventInvoker destroyInvoker, boolean stateless) {
        super(name);
        assert (objectFactory != null) : "Object factory was null";
        if (eagerInit == true && initInvoker == null) {
            ContextInitException e = new ContextInitException("No intialization method found for implementation");
            e.setIdentifier(getName());
            throw e;
        }
        this.objectFactory = objectFactory;

        this.eagerInit = eagerInit;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
        this.stateless = stateless;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void setName(String name) {
        super.setName(name);
    }

    protected int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public synchronized Object getInstance(QualifiedName qName) throws TargetException {
        return getInstance(qName, true);
    }

    public synchronized Object getInstance(QualifiedName qName, boolean notify) throws TargetException {
        if (cachedTargetInstance != null) {
            return cachedTargetInstance; // already cached, just return
        }

        if (getLifecycleState() == ERROR || getLifecycleState() == CONFIG_ERROR) {
            return null;
        }
        synchronized (this) {
            try {
                Object instance = objectFactory.getInstance();
                startInstance(instance);
                if (notify) {
                    for (Iterator iter = contextListener.iterator(); iter.hasNext();) {
                        LifecycleEventListener listener = (LifecycleEventListener) iter.next();
                        listener.onInstanceCreate(this);
                    }
                }
                setLifecycleState(RUNNING);
                if (stateless) {
                    return instance;
                } else {
                    // cache the actual instance
                    cachedTargetInstance = instance;
                    return cachedTargetInstance;
                }
            } catch (ObjectCreationException e) {
                setLifecycleState(Context.ERROR);
                TargetException te = new TargetException("Error creating instance for component", e);
                te.setIdentifier(getName());
                throw te;
            }
        }

    }

    public boolean isEagerInit() {
        return eagerInit;
    }

    public boolean isDestroyable() {
        return (destroyInvoker != null);
    }

    // ----------------------------------
    // Lifecycle methods
    // ----------------------------------

    public void start() throws ContextInitException {
        if (getLifecycleState() != UNINITIALIZED && getLifecycleState() != STOPPED) {
            throw new IllegalStateException("Component must be in UNINITIALIZED state [" + getLifecycleState() + "]");
        }
        if (objectFactory == null) {
            setLifecycleState(ERROR);
            ContextInitException e = new ContextInitException("Object factory not found");
            e.setIdentifier(getName());
            throw e;
        }
        setLifecycleState(INITIALIZED);
    }

    public void stop() {
        if (cachedTargetInstance != null) {
            if (destroyInvoker != null) {
                try {
                    destroyInvoker.invokeEvent(cachedTargetInstance);
                } catch (ObjectCallbackException e) {
                    TargetException te = new TargetException(e.getCause());
                    te.setIdentifier(getName());
                    throw te;
                }
            }
        }
        setLifecycleState(STOPPED);
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------
    private void startInstance(Object instance) throws TargetException {
        try {
            // handle @Init
            if (initInvoker != null) {
                initInvoker.invokeEvent(instance);
            }
        } catch (ObjectCallbackException e) {
            TargetException te = new TargetException("Error initializing instance", e);
            te.setIdentifier(getName());
            throw te;
        }
    }

}
