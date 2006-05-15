/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.core.wire.impl;

import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.wire.WireConfiguration;
import org.apache.tuscany.core.wire.ProxyCreationException;
import org.apache.tuscany.core.wire.WireFactory;
import org.apache.tuscany.core.wire.WireFactoryInitException;

/**
 * Returns an actual implementation instance as opposed to a proxy. Used in cases where proxying may be optimized away.
 * 
 * @version $Rev: 379957 $ $Date: 2006-02-22 14:58:24 -0800 (Wed, 22 Feb 2006) $
 */
public class NullWireFactory implements WireFactory {

    private CompositeContext parentContext;

    private String targetName;

    private Class businessInterface;

    public NullWireFactory(String componentName, CompositeContext parentContext) {
        assert (parentContext != null) : "Parent context was null";
        this.targetName = componentName;
        this.parentContext = parentContext;
    }

    public void initialize(Class businessInterface, WireConfiguration config) throws WireFactoryInitException {
        this.businessInterface = businessInterface;
    }

    public Object createProxy() throws ProxyCreationException {
        return parentContext.getContext(targetName);
    }

    public void initialize() throws WireFactoryInitException {
    }

    public void setBusinessInterface(Class interfaze) {
        businessInterface = interfaze;
    }

    public Class getBusinessInterface() {
        return businessInterface;
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }

    public Class[] getImplementatedInterfaces() {
        throw new UnsupportedOperationException();
    }

}
