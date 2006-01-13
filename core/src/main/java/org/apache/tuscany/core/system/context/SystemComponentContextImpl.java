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
package org.apache.tuscany.core.system.context;

import org.apache.tuscany.core.context.LifecycleEventListener;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.SimpleComponentContext;
import org.apache.tuscany.core.context.TargetException;

/**
 * @author delfinoj
 */
public class SystemComponentContextImpl implements SimpleComponentContext {

    private Object instance;

    /**
     * Constructor
     */
    public SystemComponentContextImpl(Object instance) {
        super();
        this.instance = instance;
    }

    public boolean isEagerInit() {
        return false;
    }

    public boolean isDestroyable() {
        return false;
    }

    /**
     * @see org.apache.tuscany.core.context.SimpleComponentContext#getInstance(QualifiedName,
     *      boolean)
     */
    public Object getInstance(QualifiedName componentName, boolean notify) throws TargetException {
        return instance;
    }

    /**
     * @see org.apache.tuscany.core.context.SimpleComponentContext#getInstance(QualifiedName)
     */
    public Object getInstance(QualifiedName componentName) throws TargetException {
        return instance;
    }


    /**
     * @see org.apache.tuscany.core.context.SimpleComponentContext#getScope()
     */
    public int getScope() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.apache.tuscany.core.context.SimpleComponentContext#releaseInstance()
     */
    public void releaseInstance() throws TargetException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.tuscany.core.context.Context#addContextListener(org.apache.tuscany.core.context.LifecycleEventListener)
     */
    public void addContextListener(LifecycleEventListener listener) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.tuscany.core.context.Context#getLifecycleState()
     */
    public int getLifecycleState() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.apache.tuscany.core.context.Context#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.tuscany.core.context.Context#removeContextListener(org.apache.tuscany.core.context.LifecycleEventListener)
     */
    public void removeContextListener(LifecycleEventListener listener) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.tuscany.core.context.Context#setLifecycleState(int)
     */
    public void setLifecycleState(int state) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.tuscany.core.context.Context#setName(java.lang.String)
     */
    public void setName(String name) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.tuscany.core.context.Context#start()
     */
    public void start() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.tuscany.core.context.Context#stop()
     */
    public void stop() {
        // TODO Auto-generated method stub

    }

}
