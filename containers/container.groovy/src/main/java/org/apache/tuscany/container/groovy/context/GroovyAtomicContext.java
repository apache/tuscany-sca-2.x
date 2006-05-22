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
package org.apache.tuscany.container.groovy.context;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.container.groovy.invoker.GroovyScript;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.extension.AtomicContextExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.core.context.PojoInstanceWrapper;

/**
 * Groovy component context.
 */
public class GroovyAtomicContext extends AtomicContextExtension {

    private GroovyScript script;

    /**
     * Initializes the context.
     */
    public GroovyAtomicContext(String name, GroovyScript script) {
        this.name = name;
        this.script = script;
    }

    public GroovyScript getScript(){
        return script;
    }

    public Object getService(String s) throws TargetException {
        return null;
    }

    public List getServiceInterfaces() {
        return null;
    }

    public TargetInvoker createTargetInvoker(String s, Method method) {
        return null;
    }

    public InstanceWrapper createInstance() throws ObjectCreationException {
        return new PojoInstanceWrapper(this,script);
    }

    public Object getService() throws TargetException {
        return script;
    }

}
