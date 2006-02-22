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
package org.apache.tuscany.container.js.builder;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;

/**
 * Adds a handler to a source or target proxy configuration
 * 
 * @version $Rev$ $Date$
 */
public class MockHandlerBuilder implements RuntimeConfigurationBuilder {

    private MessageHandler handler;

    private boolean source;

    private boolean request;

    /**
     * Creates the builder.
     * 
     * @param handler the handler to add to the source or target proxy configuration
     * @param source true if the handler should be added on the source side; false if the handler should be added to the
     *        target side
     * @param request true if the handler is a request handler; false if the handler is a response handler
     */
    public MockHandlerBuilder(MessageHandler handler, boolean source, boolean request) {
        this.handler = handler;
        this.source = source;
        this.request = request;
    }

    public void build(AssemblyModelObject modelObject, Context context) throws BuilderException {
        if (source) {
            if (!(modelObject instanceof ConfiguredReference)) {
                return;
            } else {
                ConfiguredReference cref = (ConfiguredReference) modelObject;
                ProxyFactory pFactory = (ProxyFactory) cref.getProxyFactory();
                for (InvocationConfiguration config : pFactory.getProxyConfiguration().getInvocationConfigurations().values()) {
                    if (request) {
                        config.addRequestHandler(handler);
                    } else {
                        config.addResponseHandler(handler);
                    }
                }
            }
        } else {
            if (!(modelObject instanceof ConfiguredService)) {
                return;
            } else {
                ConfiguredService cservice = (ConfiguredService) modelObject;
                ProxyFactory pFactory = (ProxyFactory) cservice.getProxyFactory();
                for (InvocationConfiguration config : pFactory.getProxyConfiguration().getInvocationConfigurations().values()) {
                    if (request) {
                        config.addRequestHandler(handler);
                    } else {
                        config.addResponseHandler(handler);
                    }
                }
            }

        }
    }

}
