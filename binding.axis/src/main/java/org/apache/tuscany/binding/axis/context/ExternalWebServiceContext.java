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
package org.apache.tuscany.binding.axis.context;

import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.context.impl.ExternalServiceImpl;
import org.apache.tuscany.core.injection.ObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;

public class ExternalWebServiceContext extends ExternalServiceImpl implements ExternalServiceContext {

    /**
     * Creates an external service context
     * 
     * @param name the name of the external service
     * @param targetProxyFactory the target proxy factory which creates proxies implementing the configured service
     *        interface for the entry point. There is always only one proxy factory as an external service is configured
     *        with one service
     * @param targetInstanceFactory the object factory that creates an artifact capabile of communicating over the
     *        binding transport configured on the external service. The object factory may implement a caching strategy.
     */
    public ExternalWebServiceContext(String name, ProxyFactory targetProxyFactory, ObjectFactory targetInstanceFactory) {
        super(name, targetProxyFactory, targetInstanceFactory);
    }
}
