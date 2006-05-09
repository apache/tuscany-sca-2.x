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
package org.apache.tuscany.binding.celtix.config;

import org.apache.tuscany.binding.celtix.handler.CeltixEntryPointContextImpl;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.extension.EntryPointContextFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.webapp.ServletHost;
import org.apache.tuscany.model.assembly.EntryPoint;

/**
 * Creates instances of {@link org.apache.tuscany.core.context.EntryPointContext} configured with the
 * appropriate invocation chains and bindings. This implementation serves as a marker for
 *
 * @version $Rev$ $Date$
 */
public class WebServiceEntryPointContextFactory extends EntryPointContextFactory {
    MessageFactory messageFactory;
    EntryPoint entryPoint;
    ServletHost servlet;
    
    public WebServiceEntryPointContextFactory(ServletHost tomcatHost,
                                            EntryPoint entryPoint,
                                            MessageFactory mf) {
        super(entryPoint.getName(), mf);
        messageFactory = mf;
        this.entryPoint = entryPoint;
        servlet = tomcatHost;
    }
    
    public EntryPointContext createContext() throws ContextCreationException {
        if (servlet == null) {
            return new CeltixEntryPointContextImpl(entryPoint,
                                               getSourceWireFactories().get(0),
                                               messageFactory);
        } else {
            //REVISIT - running in tomcat
            return null;
        }
    }

}
